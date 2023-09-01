package org.share.clienttoserver;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import org.share.HeaderPacket;
import org.share.PacketType;

@Getter
@JsonTypeName("CLIENT_CHANGENAME")
public class ClientChangeNamePacket extends HeaderPacket {
    private final String name;
    private final String changename;
    @JsonCreator
    public ClientChangeNamePacket(@JsonProperty("name") String name,@JsonProperty("changename") String changename){
        super(PacketType.CLIENT_CHANGENAME);
        this.name = name;
        this.changename = changename;
    }
}
