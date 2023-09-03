package org.share.clienttoserver;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import org.share.HeaderPacket;
import org.share.PacketType;

@Getter
@JsonTypeName("CLIENT_DISCONNECT")
public class ClientDisconnectPacket extends HeaderPacket {
    private final String name;

    @JsonCreator
    public ClientDisconnectPacket(@JsonProperty("name") String name) { //디스커넥트는 message가 필요없음
        super(PacketType.CLIENT_DISCONNECT);
        this.name = name;
    }
}
