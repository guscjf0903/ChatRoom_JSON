package org.share.servertoclient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import org.share.HeaderPacket;
import org.share.PacketType;

@Getter
@JsonTypeName("SERVER_EXCEPTION")
public class ServerExceptionPacket extends HeaderPacket {
    private final String message;

    @JsonCreator
    public ServerExceptionPacket(@JsonProperty("message") String message) { //헤더내용 삽입
        super(PacketType.SERVER_EXCEPTION);
        this.message = message;
    }
}
