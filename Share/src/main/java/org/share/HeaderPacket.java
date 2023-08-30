package org.share;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static org.share.PacketType.*;

@AllArgsConstructor
@Getter
public abstract class HeaderPacket {
    protected PacketType packetType;

    public static String packetToJson(HeaderPacket packet) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(packet);
    }

    public static HeaderPacket jsonToPacket(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, HeaderPacket.class);
    }
}


