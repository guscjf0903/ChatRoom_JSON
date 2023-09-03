package org.client;

import org.share.HeaderPacket;
import org.share.PacketType;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Objects;

import org.share.servertoclient.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.share.servertoclient.ServerDisconnectPacket.*;

public class ClientInputThread extends Thread {
    static final int MAXBUFFERSIZE = 8000;
    Socket socket;
    InputStream in = null;
    ClientOutputThread clientOutputThread;
    HashMap<String, RandomAccessFile> fileMap = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(ClientInputThread.class);

    public ClientInputThread(Socket socket, ClientOutputThread clientOutputThread) {
        this.socket = socket;
        this.clientOutputThread = clientOutputThread;
    }

    @Override
    public void run() {
        try {
            in = socket.getInputStream();

            while (true) {
                byte[] serverByteData = new byte[MAXBUFFERSIZE];
                int serverByteLength = in.read(serverByteData);
                String jsonString = new String(serverByteData, 0, serverByteLength);
                boolean disconnectCheck;
                if (serverByteLength >= 0) {
                    HeaderPacket packet = jsonToPacket(jsonString);
                    disconnectCheck = packetCastingAndPrint(Objects.requireNonNull(packet));
                    if (!disconnectCheck) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            logger.error("IOException", e);
            System.out.println("[IOException]");
        } finally {
            try {
                System.out.println("Disconnected from server.");
                socket.close();
            } catch (IOException e) {
                logger.error("IOException", e);
            }
        }
    }

    public boolean packetCastingAndPrint(HeaderPacket packet) throws IOException {
        if (packet.getPacketType() == PacketType.SERVER_NOTIFY) {
            ServerNotifyPacket notifyPacket = (ServerNotifyPacket) packet;
            System.out.println("[SERVER] " + notifyPacket.getMessage()); //서버의 Notify 메세지 출력
            return true;
        } else if (packet.getPacketType() == PacketType.SERVER_EXCEPTION) {
            ServerExceptionPacket exceptionPacket = (ServerExceptionPacket) packet;
            System.out.println("[SERVER] " + exceptionPacket.getMessage()); //서버의 Exception 메세지 출력
            return true;
        } else if (packet.getPacketType() == PacketType.SERVER_MESSAGE) {
            ServerMessagePacket messagePacket = (ServerMessagePacket) packet;
            System.out.println("[" + messagePacket.getName() + "] : " + messagePacket.getMessage());//클라이언트가 보낸 메세지
            return true;
        } else if (packet.getPacketType() == PacketType.SERVER_DISCONNECT) {
            ServerDisconnectPacket disconnectPacket = (ServerDisconnectPacket) packet;
            if (disconnectPacket.getName().equals(clientOutputThread.getClientName())) { //본인이 나가기 된 경우.
                return false;
            }
            System.out.println("[SERVER] " + disconnectPacket.getName() + " left the server.");
        } else if (packet.getPacketType() == PacketType.SERVER_CHANGENAME) {
            ServerNameChangePacket nameChangePacket = (ServerNameChangePacket) packet;
            System.out.println("[SERVER] " + nameChangePacket.getName() + "->" + nameChangePacket.getChangeName());
            if (clientOutputThread.getClientName().equals(nameChangePacket.getName())) {
                clientOutputThread.setClientName(nameChangePacket.getChangeName());
            }
            return true;
        } else if (packet.getPacketType() == PacketType.SERVER_FILE) {
            ServerFilePacket serverFilePacket = (ServerFilePacket) packet;
            saveFile(serverFilePacket);
            return true;
        }
        return true;
    }

    public void saveFile(ServerFilePacket packet) throws IOException {
        try {
            String filename = packet.getFileName();
            int chunkNumber = packet.getChunkNumber();
            byte[] chunk = packet.getChunk();
            if (!fileMap.containsKey(filename)) {
                File file = new File("/Users/hyunchuljung/Desktop/ClientFolder/" + filename);
                RandomAccessFile rfile = new RandomAccessFile(file, "rw");
                fileMap.put(filename, rfile);
            }
            RandomAccessFile rfile = fileMap.get(filename);
            rfile.seek(chunkNumber * 4096L);
            rfile.write(chunk);
        } catch (IOException e) {
            fileMap.remove(packet.getFileName());
            logger.error("IOException", e);
        }
        if (packet.getChunkNumber() == packet.getLastChunkNumber()) {
            fileMap.remove(packet.getFileName());
            System.out.println("File Download Complete");
        } else if (packet.getChunkNumber() == 0) {
            System.out.println("File Download Start");
        }
    }


}