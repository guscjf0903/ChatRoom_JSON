package org.share.clienttoserver;

import lombok.Getter;
import org.share.HeaderPacket;
import org.share.PacketType;

//0~3 타입 + 4~7 타입을 뺀 바디길이 + 8~11 이름길이 + 12~이름
@Getter
public class ClientConnectPacket extends HeaderPacket {
    private final String name;

    public ClientConnectPacket(String name) { //디스커넥트는 message가 필요없음
        super(PacketType.CLIENT_CONNECT);
        this.name = name;
    }
}
