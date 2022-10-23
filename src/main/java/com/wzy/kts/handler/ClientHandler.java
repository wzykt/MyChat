package com.wzy.kts.handler;

import com.wzy.kts.service.ClientHandlerCallback;
import com.wzy.kts.util.CloseUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author yu.wu
 * @description 客户端链接处理类
 * @date 2022/10/19 23:26
 */
@Component
@Slf4j
public class ClientHandler {

    private final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    /*
    唯一id
     */
    private String clientHandlerId;

    /*
    套接字
     */
    private Socket socket;

    /*
    读处理器
     */
    private ReadHandler readHandler;

    /*
    写处理器
     */
    private WriteHandler writeHandler;

    private ClientHandlerCallback clientHandlerCallBack;

    private String clientInfo;

    public ClientHandler(Socket socket, ClientHandlerCallback clientHandlerCallBack) throws IOException {
        this.clientHandlerId = UUID.randomUUID().toString();
        this.socket = socket;
        this.readHandler = new ReadHandler(socket.getInputStream());
        this.writeHandler = new WriteHandler(socket.getOutputStream());
        this.clientHandlerCallBack = clientHandlerCallBack;
        this.clientInfo = setClientInfo(socket);
        log.info("客户端链接：{}", clientInfo);
    }

    //调用写处理器发送message
    public void send(String message) {
        writeHandler.send(message);
    }

    public void read(){
        readHandler.start();
    }

    private String setClientInfo(Socket socket) {
        return "[ " + socket.getInetAddress().getHostAddress() + " : " + socket.getPort() + "]";
    }

    public String getClientInfo() {
        return clientInfo;
    }

    public String getClientHandlerId() {
        return clientHandlerId;
    }


    private void close() {
        readHandler.exit();
        writeHandler.exit();
    }

    public void exitBySelf(){
        close();
        /** 回调，告知服务器有客户端下线*/
        clientHandlerCallBack.closeClient(this);
    }

    //读处理器
    class ReadHandler extends Thread {
        /**
         * socket输入流
         */
        private InputStream inputStream;

        /**
         * 标识线程是否终止，即客户端是否断开
         */
        private boolean stop = false;

        /**
         * 用线程池将读到的信息回调ChatServer,提高并发
         */
        private ThreadPoolExecutor readHandlerThreadPool = new ThreadPoolExecutor(1, 3, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(10));

        public ReadHandler(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        public void setStop(boolean stop) {
            this.stop = stop;
        }

        @Override
        public void run() {
            read();
        }

        public void read() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                do {
                    String message = reader.readLine();
                    log.info("message : {}", message);
                    // TODO: 2022/10/20 回调ChatServer
                    readHandlerThreadPool.execute(() -> {
                        clientHandlerCallBack.receiveMessage(ClientHandler.this, message);
                    });
                } while (!stop);
            } catch (Exception e) {
                if (!stop) {
                    logger.error("客户端: {} ,异常断开连接, e : {}", clientInfo, e.getMessage());
                    ClientHandler.this.exitBySelf();
                }
            }
        }

        /**
         * @description 客户端下线，释放处理器资源
         */
        public void exit(){
            stop = true;
            CloseUtil.close(inputStream);
            readHandlerThreadPool.shutdown();
        }
    }

    //写处理器
    class WriteHandler extends Thread {

        private BufferedWriter writer;

        private boolean stop = false;

        /** 用线程池将发送信息,提高并发 */
        private ThreadPoolExecutor writeHandlerThreadPool = new ThreadPoolExecutor(1,3,60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(10));

        public WriteHandler(OutputStream outputStream){
            writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        }

        public void send(String message){
            try{
                writeHandlerThreadPool.execute(()->{
                    if(stop){
                        return;
                    }
                    try {
                        writer.write(message);
                        writer.flush();
                    }catch (IOException e){
                        log.error("发送数据失败, e : {}",e.getMessage());
                    }
                });
            }catch (Exception e){
                if (!stop){
                    log.error("客户端: {} ,异常断开连接, e : {}",clientInfo,e.getMessage());
                    ClientHandler.this.exitBySelf();
                }
            }
        }

        public void exit() {
            stop = true;
            CloseUtil.close(writer);
            writeHandlerThreadPool.shutdownNow();
        }
    }
}
