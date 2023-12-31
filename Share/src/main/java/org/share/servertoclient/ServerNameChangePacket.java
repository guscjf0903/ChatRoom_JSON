package org.share.servertoclient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import org.share.HeaderPacket;
import org.share.PacketType;

@Getter
@JsonTypeName("SERVER_CHANGENAME")
public class ServerNameChangePacket extends HeaderPacket {
    private final String changeName;
    private final String name;

    @JsonCreator
    public ServerNameChangePacket(@JsonProperty("name") String name, @JsonProperty("changename") String changeName) {
        super(PacketType.SERVER_CHANGENAME);
        this.name = name;
        this.changeName = changeName;
    }
}
