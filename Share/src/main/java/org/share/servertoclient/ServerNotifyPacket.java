package org.share.servertoclient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import org.share.HeaderPacket;
import org.share.PacketType;

@Getter
@JsonTypeName("SERVER_NOTIFY")
public class ServerNotifyPacket extends HeaderPacket {
    private final String message;

    @JsonCreator
    public ServerNotifyPacket(@JsonProperty("message") String message) { //헤더내용 삽입
        super(PacketType.SERVER_NOTIFY);
        this.message = message;
    }
}
