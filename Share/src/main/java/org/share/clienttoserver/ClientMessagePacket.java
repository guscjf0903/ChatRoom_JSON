package org.share.clienttoserver;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import org.share.HeaderPacket;
import org.share.PacketType;

@Getter
@JsonTypeName("CLIENT_MESSAGE")
public class ClientMessagePacket extends HeaderPacket {
    private final String message;
    private final String name;
    @JsonCreator
    public ClientMessagePacket(@JsonProperty("message") String message,@JsonProperty("name") String name) {
        super(PacketType.CLIENT_MESSAGE);
        this.message = message;
        this.name = name;
    }
}
