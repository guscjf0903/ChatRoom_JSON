package org.client;

import org.share.HeaderPacket;
import org.share.PacketType;
import org.share.clienttoserver.ClientConnectPacket;
import org.share.servertoclient.ServerExceptionPacket;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import static org.share.HeaderPacket.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Client {
    private static final int SERVER_PORT = 8888;
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }

    public void start() {

        Socket socket;
        try {
            //socket 연결 후 클라이언트 인풋,아웃풋 생성
            socket = new Socket("localhost", SERVER_PORT);
            String connectName = duplicateNameCheck(socket);

            ClientOutputThread clientoutputThread = new ClientOutputThread(socket,connectName);
            ClientInputThread clientinputThread = new ClientInputThread(socket,clientoutputThread);


            clientoutputThread.start();
            clientinputThread.start();
        } catch (Exception e) {
            logger.error("IOException",e);
        }
    }

    public String duplicateNameCheck(Socket socket) throws IOException {
        OutputStream out = socket.getOutputStream();
        InputStream in = socket.getInputStream();
        Scanner scanner = new Scanner(System.in);
        ClientConnectPacket connectPacket = null;
        while(true){
            System.out.print("please enter your name :");
            String name = scanner.nextLine(); // 중복확인 기능 추가해야함.
            connectPacket = new ClientConnectPacket(name);

            byte[] packetByteData = packetToJson(connectPacket).getBytes();
            out.write(packetByteData);

            byte[] serverByteData = new byte[1024];
            int serverByteLength = in.read(serverByteData);
            String serverJsonString = new String(serverByteData, 0, serverByteLength);
            HeaderPacket serverPacket = jsonToPacket(serverJsonString);
            if(serverPacket.getPacketType() == PacketType.SERVER_EXCEPTION){
                ServerExceptionPacket serverExceptionPacket = (ServerExceptionPacket) serverPacket;
                System.out.println("[SERVER] " + serverExceptionPacket.getMessage());
            }else{
               break;
            }
        }
        return connectPacket.getName();
    }

}

