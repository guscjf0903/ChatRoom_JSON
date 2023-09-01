package org.server;

import org.share.clienttoserver.*;
import org.share.servertoclient.*;

import java.io.*;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.out;
import static org.server.ServerThread.clientMap;
import static org.share.HeaderPacket.*;

public class SeverMessageHandler {
    static Map<String, RandomAccessFile> fileMap = new HashMap<>();

    public static void sendAllMessage(ClientMessagePacket messagepacket) throws IOException { //모두에게 전송하는 메세지 (lock 걸어야함)
        ServerMessagePacket serversendpacket = new ServerMessagePacket(messagepacket.getMessage(), messagepacket.getName());
        byte[] sendAllByte = packetToJson(serversendpacket).getBytes();
        try {
            for (Map.Entry<OutputStream, String> entry : clientMap.entrySet()) {
                String receiverName = entry.getValue();
                OutputStream clientStream = entry.getKey();
                if (messagepacket.getName().equals(receiverName)) {
                    continue;
                }
                try {
                    clientStream.write(sendAllByte);
                    clientStream.flush();
                } catch (IOException e) {
                    // 클라이언트와의 연결이 끊어진 경우, 해당 클라이언트를 제거합니다.
                    clientMap.remove(clientStream);
                    out.println("[" + receiverName + " Disconnected]");
                }
            }
        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
        }
    }

    public static void sendWhisperMessage(ClientWhisperPacket whisperPacket, String sendName) throws IOException {
        ServerMessagePacket serversendpacket = new ServerMessagePacket(whisperPacket.getMessage(), sendName);
        byte[] sendAllbyte = packetToJson(serversendpacket).getBytes();
        try {
            for (Map.Entry<OutputStream, String> entry : clientMap.entrySet()) {
                String receiverName = entry.getValue();
                OutputStream clientStream = entry.getKey();
                if (receiverName.equals(whisperPacket.getWhispername())) {
                    try {
                        clientStream.write(sendAllbyte);
                        clientStream.flush();
                    } catch (IOException e) {// 클라이언트와의 연결이 끊어진 경우, 해당 클라이언트를 제거합니다.
                        clientMap.remove(clientStream);
                        out.println("[" + receiverName + " Disconnected]");
                    }
                    return;
                }
            }
            for (Map.Entry<OutputStream, String> entry : clientMap.entrySet()) { //만약 전송되지 않았을때 예외처리
                String receiverName = entry.getValue();
                OutputStream clientStream = entry.getKey();
                if(receiverName.equals(sendName)){
                    try {
                        exceptionMessage(clientStream,"There is no user with that name.");
                    } catch (IOException e) {// 클라이언트와의 연결이 끊어진 경우, 해당 클라이언트를 제거합니다.
                        clientMap.remove(clientStream);
                        out.println("[" + receiverName + " Disconnected]");
                    }
                    return;
                }

            }
        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
        }
    }


    public static void clientChangeName(ClientChangeNamePacket clientChangeNamePacket) throws IOException {
        ServerNameChangePacket serverNameChangePacket = new ServerNameChangePacket(clientChangeNamePacket.getName(),clientChangeNamePacket.getChangename());
        byte[] serverNameChangePacketbyte = packetToJson(serverNameChangePacket).getBytes();
        for (Map.Entry<OutputStream,String> entry : clientMap.entrySet()) {
            String receiverName = entry.getValue();
            OutputStream clientStream = entry.getKey();
            try {
                clientStream.write(serverNameChangePacketbyte);
                clientStream.flush();
            } catch (IOException e) {
                // 클라이언트와의 연결이 끊어진 경우, 해당 클라이언트를 제거합니다.
                clientMap.remove(clientStream);
                out.println("[" + receiverName + " Disconnected]");
            }
            if(clientChangeNamePacket.getName().equals(receiverName)){
                clientMap.put(clientStream,clientChangeNamePacket.getChangename());
            }
        }
    }
//패킷을 받아서 해쉬맵안에 같은 이름을 가진 밸류를 찾아서 바꾼 이름으로 바꿔줌

    public static void sendAllNotify(String message) throws IOException { //서버 공지 (lock 걸어야함)
        ServerNotifyPacket packet = new ServerNotifyPacket(message);
        byte[] sendNotifybyte = packetToJson(packet).getBytes();

        try {
            for (Map.Entry<OutputStream, String> entry : clientMap.entrySet()) {
                String receiverName = entry.getValue();
                OutputStream clientStream = entry.getKey();
                try {
                    clientStream.write(sendNotifybyte);
                    clientStream.flush();
                } catch (IOException e) {
                    // 클라이언트와의 연결이 끊어진 경우, 해당 클라이언트를 제거합니다.
                    clientMap.remove(receiverName);
                    out.println("[" + receiverName + " Disconnected]");
                }
            }
        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void disconnectClient(ClientDisconnectPacket disconnectPacket,String ThreadName) throws IOException {
        ServerDisconnectPacket disconnectpacket = new ServerDisconnectPacket(disconnectPacket.getName());
        byte[] disconnectpacketbyte = packetToJson(disconnectpacket).getBytes();

        try {
            for (Map.Entry<OutputStream, String> entry : clientMap.entrySet()) {
                String receiverName = entry.getValue();
                OutputStream clientStream = entry.getKey();
                try {
                    clientStream.write(disconnectpacketbyte);
                    clientStream.flush();
                    if (receiverName.equals(ThreadName)) {
                        clientMap.remove(out);
                    }
                } catch (IOException e) {
                    // 클라이언트와의 연결이 끊어진 경우, 해당 클라이언트를 제거합니다.
                    clientMap.remove(clientStream);
                    out.println("[" + receiverName + "Disconnected]");
                }
            }
        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
        }
        out.println("[" + disconnectpacket.getName() + " Disconnected]"); //서버에 띄우는 메세지.
    }

    public static synchronized void exceptionMessage(OutputStream out,String message) throws IOException { //원하는 사람 한명에게만 서버 공지전송
        ServerExceptionPacket exceptionPacket = new ServerExceptionPacket(message);
        byte[] exceptionPacketByte = packetToJson(exceptionPacket).getBytes();
        out.write(exceptionPacketByte);
        out.flush();
    }

    public static synchronized void sendFile(ClientFilePacket Packet, String clientName) throws IOException {
        byte[] sendAllbyte;
        ServerFilePacket serverFilePacket = new ServerFilePacket(clientName,Packet.getFilename(),Packet.getChunknumber(),Packet.getChunk(), Packet.getLastChunknumber());
        //sendAllbyte = packetToByte(serverFilePacket);
        try {
            for (Map.Entry<OutputStream, String> entry : clientMap.entrySet()) {
                String receiverName = entry.getValue();
                OutputStream clientStream = entry.getKey();
                if (clientName.equals(receiverName)) {
                    continue;
                }
                try {
                    //clientStream.write(sendAllbyte);
                    clientStream.flush();
                } catch (IOException e) {
                    // 클라이언트와의 연결이 끊어진 경우, 해당 클라이언트를 제거합니다.
                    clientMap.remove(clientStream);
                    out.println("[" + receiverName + " Disconnected]");
                }
            }
        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
        }
    }
}
