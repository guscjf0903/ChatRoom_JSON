package org.share.servertoclient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import org.share.HeaderPacket;
import org.share.PacketType;

@Getter
@JsonTypeName("SERVER_FILE")
public class ServerFilePacket extends HeaderPacket {
    private final String name;
    private final String fileName;
    private final int chunkNumber;
    private final byte[] chunk;
    private final int lastChunkNumber;
    @JsonCreator
    public ServerFilePacket(@JsonProperty("type")String name, String fileName, int chunkNumber, byte[] chunk, int lastChunkNumber) {
        super(PacketType.SERVER_FILE);
        this.name = name;
        this.fileName = fileName;
        this.chunkNumber = chunkNumber;
        this.chunk = chunk;
        this.lastChunkNumber = lastChunkNumber;
    }
}
