package org.share.clienttoserver;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import org.share.HeaderPacket;
import org.share.PacketType;

@Getter
@JsonTypeName("CLIENT_WHISPERMESSAGE")
public class ClientWhisperPacket extends HeaderPacket {
    private final String message;
    private final String whispername;
    @JsonCreator
    public ClientWhisperPacket(@JsonProperty("message")String message,@JsonProperty("whispername") String whispername) {
        super(PacketType.CLIENT_WHISPERMESSAGE);
        this.message = message;
        this.whispername = whispername;
    }
}
