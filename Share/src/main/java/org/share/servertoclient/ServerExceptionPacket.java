package org.share.servertoclient;

import lombok.Getter;
import org.share.HeaderPacket;
import org.share.PacketType;
@Getter
public class ServerExceptionPacket extends HeaderPacket {
    private final String message;

    public ServerExceptionPacket(String message) { //헤더내용 삽입
        super(PacketType.SERVER_EXCEPTION);
        this.message = message;
    }
}
