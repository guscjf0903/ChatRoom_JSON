package org.share.servertoclient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import org.share.HeaderPacket;
import org.share.PacketType;
@Getter
@JsonTypeName("SERVER_DISCONNECT")
public class ServerDisconnectPacket extends HeaderPacket {
    private final String name;
    @JsonCreator
    public ServerDisconnectPacket(@JsonProperty("name")String name) { //헤더내용 삽입
        super(PacketType.SERVER_DISCONNECT);
        this.name = name;
    }
}
