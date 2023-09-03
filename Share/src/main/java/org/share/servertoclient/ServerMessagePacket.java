package org.share.servertoclient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import org.share.HeaderPacket;
import org.share.PacketType;

@Getter
@JsonTypeName("SERVER_MESSAGE")
public class ServerMessagePacket extends HeaderPacket {
    private final String message;
    private final String name;

    @JsonCreator
    public ServerMessagePacket(@JsonProperty("message") String message, @JsonProperty("name") String name) { //헤더내용 삽입
        super(PacketType.SERVER_MESSAGE);
        this.message = message;
        this.name = name;
    }
}
