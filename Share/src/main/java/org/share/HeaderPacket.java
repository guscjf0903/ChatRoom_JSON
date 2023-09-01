package org.share;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.share.clienttoserver.*;
import org.share.servertoclient.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ClientConnectPacket.class, name = "CLIENT_CONNECT"),
        @JsonSubTypes.Type(value = ClientMessagePacket.class, name = "CLIENT_MESSAGE"),
        @JsonSubTypes.Type(value = ClientDisconnectPacket.class, name = "CLIENT_DISCONNECT"),
        @JsonSubTypes.Type(value = ClientChangeNamePacket.class, name = "CLIENT_CHANGENAME"),
        @JsonSubTypes.Type(value = ClientWhisperPacket.class, name = "CLIENT_WHISPERMESSAGE"),
        @JsonSubTypes.Type(value = ServerDisconnectPacket.class, name = "SERVER_DISCONNECT"),
        @JsonSubTypes.Type(value = ServerExceptionPacket.class, name = "SERVER_EXCEPTION"),
        @JsonSubTypes.Type(value = ServerMessagePacket.class, name = "SERVER_MESSAGE"),
        @JsonSubTypes.Type(value = ServerNameChangePacket.class, name = "SERVER_CHANGENAME"),
        @JsonSubTypes.Type(value = ServerNotifyPacket.class, name = "SERVER_NOTIFY"),
        @JsonSubTypes.Type(value = ClientFilePacket.class, name = "CLIENT_FILE"),
        @JsonSubTypes.Type(value = ServerFilePacket.class, name = "SERVER_FILE")
})

public abstract class HeaderPacket {
    protected PacketType packetType;
    private static final Logger logger = LoggerFactory.getLogger(HeaderPacket.class);

    public static String packetToJson(HeaderPacket packet) throws JsonProcessingException {
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(packet);
        }catch (JsonProcessingException e) {
            logger.error("JsonProcessingException", e);
            return null;
        }
    }
    public static HeaderPacket jsonToPacket(String json) throws JsonProcessingException {
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, HeaderPacket.class);
        }catch (JsonProcessingException e){
            logger.error("JsonProcessingException",e);
            return null;
        }

    }
}


