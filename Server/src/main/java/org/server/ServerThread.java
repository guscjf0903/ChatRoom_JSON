package org.server;

import org.share.*;
import org.share.clienttoserver.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.*;

import static org.server.SeverMessageHandler.*;
import static org.share.HeaderPacket.*;
import static org.share.PacketType.*;

public class ServerThread extends Thread {
    static final int MAXBUFFERSIZE = 8000;
    private static final Logger logger = LoggerFactory.getLogger(ServerThread.class);


    public static Map<OutputStream, String> clientMap = Collections.synchronizedMap(new HashMap<OutputStream, String>());
    public String clientName;

    private final Socket socket;
    private InputStream in;
    private OutputStream out;

    public ServerThread(Socket socket) {
        this.socket = socket;
        try {
            out = socket.getOutputStream(); // 클라에게 보내는 메세지
            in = socket.getInputStream(); //클라에서 오는 메세지
        } catch (IOException e) {
            logger.error("IOException", e);
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                byte[] clientByteData = new byte[MAXBUFFERSIZE];
                int clientByteLength = in.read(clientByteData);
                String jsonString = new String(clientByteData, 0, clientByteLength);
                boolean disconnectcheck = true;
                if (clientByteLength >= 0) {
                    HeaderPacket packet = jsonToPacket(jsonString);
                    disconnectcheck = packetCastingAndSend(packet);
                }
                if (!disconnectcheck) {
                    break;
                }
            }
        } catch (IOException e) {
            logger.error("IOException", e);
            System.out.println("[" + clientName + "Disconnected]");
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                logger.error("IOException", e);
            }
        }
    }


    private synchronized void connectClient(ClientConnectPacket connectPacket) throws IOException { // 커넥트 요청 들어올시 동작
        if (clientMap.containsValue(connectPacket.getName())) {
            exceptionMessage(out, "Duplicate name. Please enter another name");
            return;
        }
        clientName = connectPacket.getName();
        clientMap.put(out, clientName);
        sendAllNotify(clientName + " is Connected");
        System.out.println("[" + clientName + " Connected]"); //서버에 띄우는 메세지.
    }

    public synchronized boolean packetCastingAndSend(HeaderPacket packet) throws IOException {
        if (packet != null) {
            if (packet.getPacketType() == PacketType.CLIENT_CONNECT) {
                ClientConnectPacket connectPacket = (ClientConnectPacket) packet;
                connectClient(connectPacket);
            } else if (packet.getPacketType() == PacketType.CLIENT_MESSAGE) {
                ClientMessagePacket messagePacket = (ClientMessagePacket) packet;
                sendAllMessage(messagePacket);
            } else if (packet.getPacketType() == CLIENT_CHANGENAME) {
                ClientChangeNamePacket changeNamePacket = (ClientChangeNamePacket) packet;
                boolean containsValue = clientMap.containsValue(changeNamePacket.getChangeName());
                if (containsValue) {
                    exceptionMessage(out, "Duplicate name. Please enter another name");
                } else {
                    clientChangeName(changeNamePacket);
                    clientName = changeNamePacket.getChangeName();
                    exceptionMessage(out, "Your name has been changed to " + changeNamePacket.getChangeName());
                }
            } else if (packet.getPacketType() == CLIENT_WHISPERMESSAGE) {
                ClientWhisperPacket whisperPacket = (ClientWhisperPacket) packet;
                sendWhisperMessage(whisperPacket, clientName);
            } else if (packet.getPacketType() == CLIENT_FILE) {
                ClientFilePacket filePacket = (ClientFilePacket) packet;
                System.out.println("packetCasting chunk :" + filePacket.getChunk().length);
                sendFile(filePacket, clientName);
                return true;
            } else if (packet.getPacketType() == PacketType.CLIENT_DISCONNECT) {
                ClientDisconnectPacket disconnectPacket = (ClientDisconnectPacket) packet;
                disconnectClient(disconnectPacket, clientName);
                return !disconnectPacket.getName().equals(clientName);
            }
        }
        return true;
    }
}



