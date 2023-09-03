package org.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int SERVER_PORT = 8888;
    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    public void start() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(SERVER_PORT); // 포트번호와 서버 소켓 생성
            System.out.println("[Server Start]");
            while (true) {
                System.out.println("[Client Waiting]");
                Socket socket = serverSocket.accept(); //클라이언트 연결 수락
                //연결이 들어올때마다 새로운 소켓 생성
                //클라이언트가 접속하면 새로운 스레드 생성.
                ServerThread ServerThread = new ServerThread(socket);
                ServerThread.start();
            }
        } catch (IOException e) {
            logger.error("IOException", e);
        }
    }
}
