package org.share.clienttoserver;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import org.share.HeaderPacket;
import org.share.PacketType;

//0~3 타입 + 4~7 타입을 뺀 바디길이 + 8~11 이름길이 + 12~이름
@Getter
@JsonTypeName("CLIENT_CONNECT")
public class ClientConnectPacket extends HeaderPacket {
    private final String name;

    @JsonCreator
    public ClientConnectPacket(@JsonProperty("name") String name) { //디스커넥트는 message가 필요없음
        super(PacketType.CLIENT_CONNECT);
        this.name = name;
    }
}
