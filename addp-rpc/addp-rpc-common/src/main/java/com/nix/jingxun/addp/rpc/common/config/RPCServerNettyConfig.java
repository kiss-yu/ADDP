package com.nix.jingxun.addp.rpc.common.config;

import com.nix.jingxun.addp.rpc.remoting.netty.NettyServerConfig;
import com.nix.jingxun.addp.rpc.remoting.netty.NettySystemConfig;
import lombok.Data;
import org.springframework.context.annotation.Configuration;

/**
 * @author keray
 * @date 2018/12/08 21:08
 */
@Data
@Configuration
public class RPCServerNettyConfig extends NettyServerConfig {

    private int listenPort = 15100;
    private int serverWorkerThreads = 8;
    private int serverCallbackExecutorThreads = 0;
    private int serverSelectorThreads = 3;
    private int serverAsyncSemaphoreValue = 64;
    private int serverChannelMaxIdleTimeSeconds = 120;

    private int serverSocketSndBufSize = NettySystemConfig.socketSndbufSize;
    private int serverSocketRcvBufSize = NettySystemConfig.socketRcvbufSize;
    private boolean serverPooledByteBufAllocatorEnable = true;

    private boolean useEpollNativeSelector = false;
}
