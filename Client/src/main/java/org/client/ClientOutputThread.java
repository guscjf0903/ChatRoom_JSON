package org.client;

import lombok.Getter;
import lombok.Setter;
import org.share.*;
import org.share.clienttoserver.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import static org.share.HeaderPacket.*;

@Getter
@Setter
public class ClientOutputThread extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(ClientOutputThread.class);

    Socket socket;
    OutputStream out = null;
    Scanner scanner = new Scanner(System.in);
    String clientName;
    public ClientOutputThread(Socket socket,String clientname) {
        this.socket = socket;
        this.clientName = clientname;
    }


    @Override
    public void run() {
        try {
            out = socket.getOutputStream();
            startChat();
          } catch (IOException e) {
            logger.error("IOException",e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                logger.error("IOException",e);
            }
        }
    }

    public void startChat(){
        try{
            while(true){
                String message;
                message = scanner.nextLine();
                if(message == null){
                    continue;
                }
                if(message.startsWith("/")){
                    ClientCommand(message);
                }else{
                    ClientMessagePacket clientMessagePacket = new ClientMessagePacket(message, clientName);
                    sendToJsonString(clientMessagePacket);
                }
                if(message.equals("/quit")){
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void sendFilePacketToByte(File file) {
        try{
            InputStream fileInputStream = new FileInputStream(file); // 파일로만 구성된 데이터에서 이름을 계속추가
            byte[] chunk = new byte[4096];
            int byteRead;
            int chunknumber = 0;
            int lastChunknumber = (int) Math.ceil((double) file.length() / 4096) - 1;

            while((byteRead = fileInputStream.read(chunk)) != -1){
                byte[] actualChunk = new byte[byteRead];
                System.arraycopy(chunk, 0, actualChunk, 0, byteRead);
                System.out.println("actual Chunk : " + actualChunk.length);
                ClientFilePacket clientFilePacket = new ClientFilePacket(file.getName(),chunknumber,actualChunk,lastChunknumber);
                //sendPacketToByte(clientFilePacket);
                chunknumber++;
                this.sleep(10);
            }
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void sendToJsonString(HeaderPacket packet) throws IOException {
        byte[] packetByteData = packetToJson(packet).getBytes();
        out.write(packetByteData);
        out.flush();
    }

    public synchronized void ClientCommand(String message) throws IOException {
        if ("/quit".equals(message)) {
            ClientDisconnectPacket clientDisconnectPacket = new ClientDisconnectPacket(clientName);
            sendToJsonString(clientDisconnectPacket);
        } else if("/namechange".equals(message)){
            System.out.print("Please enter a name to change :");
            String changeName = scanner.nextLine();
            ClientChangeNamePacket clientChangeNamePacket = new ClientChangeNamePacket(clientName,changeName);
            sendToJsonString(clientChangeNamePacket);
        } else if("/w".equals(message)){
            System.out.print("Please enter a name to whisper :");
            String whisperName = scanner.nextLine();
            System.out.print("Please enter a message to whisper :");
            String whisperMessage = scanner.nextLine();
            ClientWhisperPacket clientWhisperPacket = new ClientWhisperPacket(whisperMessage, whisperName);
            sendToJsonString(clientWhisperPacket);
        }else if("/f".equals(message)) {
            System.out.print("Please enter a file name to send : ");
            String filepath = scanner.nextLine();
            File file = new File(filepath);
            if (file.exists()) { // 파일이 존재하는지 확인
                sendFilePacketToByte(file);
            } else { //파일이 없을때 예외처리.
                System.out.println("File does not exist. Please provide a valid file path.");
            }
        } else{
            System.out.println("Invalid command");
        }
    }
}