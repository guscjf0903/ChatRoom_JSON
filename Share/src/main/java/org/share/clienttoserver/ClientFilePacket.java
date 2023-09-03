package org.share.clienttoserver;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import org.share.HeaderPacket;
import org.share.PacketType;

import java.io.*;

@Getter
@JsonTypeName("CLIENT_FILE")
public class ClientFilePacket extends HeaderPacket {
    private final String filename;
    byte[] chunk;
    int chunkNumber;
    int lastChunkNumber;

    @JsonCreator
    public ClientFilePacket(@JsonProperty("filename") String filename, @JsonProperty("chunknumber") int chunknumber, @JsonProperty("chunk") byte[] chunk, @JsonProperty("lastChunknumber") int lastChunkNumber) throws IOException {
        super(PacketType.CLIENT_FILE);
        this.filename = filename;
        this.chunkNumber = chunknumber;
        this.chunk = chunk;
        this.lastChunkNumber = lastChunkNumber;
    }
}
