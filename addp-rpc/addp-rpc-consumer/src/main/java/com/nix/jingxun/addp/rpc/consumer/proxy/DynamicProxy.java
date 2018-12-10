package com.nix.jingxun.addp.rpc.consumer.proxy;

import com.alibaba.fastjson.JSON;
import com.alipay.remoting.exception.RemotingException;
import com.nix.jingxun.addp.rpc.common.*;
import com.nix.jingxun.addp.rpc.common.config.CommonConfig;
import com.nix.jingxun.addp.rpc.common.protocol.RPCPackage;
import com.nix.jingxun.addp.rpc.common.protocol.RPCPackageCode;
import com.nix.jingxun.addp.rpc.consumer.RPCContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author keray
 * @date 2018/12/07 22:39
 */
@Slf4j
public class DynamicProxy implements InvocationHandler {

    @Override
    public Object invoke(Object object, Method method, Object[] args) throws Throwable{
        Class<?> proxyInterface = method.getDeclaringClass();
        RPCInterfaceAnnotation consumer = proxyInterface.getAnnotation(RPCInterfaceAnnotation.class);
        if (consumer == null) {
            throw new RuntimeException("非rpc代理接口 执行失败");
        }
        // 到注册中心去找服务提供方法
        String producerHost = getProducerHost(RPCMethodParser.getMethodKey(proxyInterface.getName(),consumer.appName(),consumer.group(),consumer.version()));
        RPCPackage responsePackage = null;
        RPCRequest request = createInvokeRPCRequest(proxyInterface,method,args);
        RPCPackage rpcPackage = RPCPackage.createRequestMessage(RPCPackageCode.RPC_INVOKE);
        rpcPackage.setObject(request);
        switch (consumer.type()) {
            case SYNC_EXEC_METHOD:{
                if (consumer.timeout() == 0) {
                    throw new RuntimeException("同步rpc调用timeout 必须大于 0");
                }
                responsePackage = RPCRemotingClient.CLIENT.invokeSync(producerHost,rpcPackage,consumer.timeout());
            }break;
            case ASYNC_EXEC_METHOD:return RPCRemotingClient.CLIENT.invokeWithFuture(producerHost,responsePackage,0);
            default:break;
        }
        if (responsePackage.getCmdCode() == RPCPackageCode.RESPONSE_SUCCESS) {
            RPCResponse rpcResponse = (RPCResponse) responsePackage.getObject();
            if (rpcResponse.getResult() != null) {
                rpcResponse.getResult().setData(JSON.parseObject(JSON.toJSONString(rpcResponse.getResult().getData()),rpcResponse.getResult().getClazz()));
            }
            if (rpcResponse.getCode() == RPCResponse.ResponseCode.SUCCESS) {
                if (rpcResponse.getResult() != null) {
                    return rpcResponse.getResult().getData();
                }
                return null;
            } else {
                throw rpcResponse.getError().getException();
            }
        } else {
            throw new RuntimeException("rpc调用失败");
        }
    }
    private RPCRequest createInvokeRPCRequest(Class<?> proxyInterface, Method method, Object[] args) {
        RPCRequest request = new RPCRequest();
        request.setContext(RPCContext.getContext());
        request.setInterfaceName(proxyInterface.getName());
        request.setMethod(method.getName());
        request.setDate(new Date());
        if (args != null && args.length > 0) {
            request.setParamData(Stream.of(args).map(item -> new RPCRequest.ParamsData(item.getClass(), item)).collect(Collectors.toList()));
        }
        return request;
    }

    private String getProducerHost(String interfaceKey) throws RemotingException, InterruptedException {
        log.info("rpc 调用 {}",interfaceKey);
        RPCPackage request = RPCPackage.createRequestMessage(RPCPackageCode.CONSUMER_GET_MSG);
        request.setObject(interfaceKey);
        return RPCRemotingClient.CLIENT.invokeSync(CommonConfig.SERVER_HOST,request,1000).getObject().toString();
    }
}