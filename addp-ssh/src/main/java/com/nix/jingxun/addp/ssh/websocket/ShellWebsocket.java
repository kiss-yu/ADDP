package com.nix.jingxun.addp.ssh.websocket;
import com.alibaba.fastjson.JSON;
import com.jcraft.jsch.JSchException;
import com.nix.jingxun.addp.ssh.util.ShellUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author keray
 * @date 2018/12/04 下午3:15
 */
@Slf4j

@ServerEndpoint(value = "/shell/socket/")
@Component
public class ShellWebsocket {

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(50,50,10, TimeUnit.SECONDS,new LinkedBlockingDeque<>(1024), (ThreadFactory) Thread::new);
    private Map<String, ShellUtil> shellUtilMap = new ConcurrentHashMap<>(32);
    private Map<Session, String> sessionStringMap = new ConcurrentHashMap<>(32);

    private final static String SOCKET_CREATE = "socket_create";
    private final static String SOCKET_CONNECT = "socket_connect";

    @OnOpen
    public void onOpen(Session session) {

    }
    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {

    }

    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("websocket on message {},session {}",message,message);
        HashMap<String,String> data = JSON.parseObject(message, HashMap.class);
        String key = data.get("key");
        String id = data.get("id");
        String command = data.get("command");
        if (SOCKET_CONNECT.equalsIgnoreCase(key)) {
            sessionStringMap.put(session,id);
            shellUtilMap.put(id,new ShellUtil(data.get("ip"),data.get("username"),data.get("password")));
        } else if (SOCKET_CREATE.equalsIgnoreCase(key)){
            sessionStringMap.put(session,session.getId());
            shellUtilMap.put(session.getId(),new ShellUtil(data.get("ip"),data.get("username"),data.get("password")));
        } else {
            if (command == null) {
                return;
            }
            final ShellUtil shellUtil = shellUtilMap.get(id);
            final BlockingQueue<String> out = new LinkedBlockingQueue<>();
            final AtomicBoolean stop = new AtomicBoolean(false);
            executor.execute(() -> shellUtil.execute(command,out,stop));
            while (!stop.get() || !out.isEmpty()) {
                try {
                    String result = out.poll(10,TimeUnit.MILLISECONDS);
                    if (result != null) {
                        if (ShellUtil.SHELL_EXEC_FAIL.equalsIgnoreCase(result)) {
                            result = "command exec fail";
                        }
                        sendMessage(result, session);
                    }
                } catch (InterruptedException e) {
                    sendMessage("ssh command exec error",session);
                    break;
                }
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("websocket error.",error);
        log.warn("websocket error.session {} ",session);
        shellUtilMap.remove(sessionStringMap.remove(session));
    }

    private void sendMessage(String message,Session session) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            log.error("websocket send message fail.",e);
            log.warn("websocket send message fail.session {} message {}",session,message);
            shellUtilMap.remove(sessionStringMap.remove(session));
        }
    }
}
