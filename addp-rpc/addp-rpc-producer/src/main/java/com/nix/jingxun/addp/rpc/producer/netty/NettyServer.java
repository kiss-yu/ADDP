package com.nix.jingxun.addp.rpc.producer.netty;

import com.nix.jingxun.addp.rpc.common.config.CommandCode;
import com.nix.jingxun.addp.rpc.common.config.RPCServerConfig;
import com.nix.jingxun.addp.rpc.common.processor.HeartProcessor;
import com.nix.jingxun.addp.rpc.remoting.RemotingService;
import com.nix.jingxun.addp.rpc.remoting.netty.NettyRemotingServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author keray
 * @date 2018/12/07 19:45
 */
@Component
public class NettyServer {
    @Autowired
    private HeartProcessor heartProcessor;
    @Autowired
    private RPCInvokeProcessor rpcInvokeProcessor;
    private RemotingService remotingService;
    private final ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(100,100,60, TimeUnit.SECONDS,new LinkedBlockingQueue<>(), (ThreadFactory) Thread::new);
    private static boolean start = false;
    @PostConstruct
    public synchronized void start() {
        if (start) {
            return;
        }
        remotingService = new NettyRemotingServer(new RPCServerConfig());
        start = true;
        remotingService.registerProcessor(CommandCode.HELLO.getCode(),heartProcessor,poolExecutor);
        remotingService.registerProcessor(CommandCode.SYNC_EXEC_METHOD.getCode(),rpcInvokeProcessor,poolExecutor);
        remotingService.start();
    }
}
