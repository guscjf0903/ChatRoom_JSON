package org.share;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PacketType {
    CLIENT_MESSAGE(1),
    CLIENT_CONNECT(2),
    CLIENT_DISCONNECT(3),
    SERVER_NOTIFY(4),
    SERVER_MESSAGE(5),
    SERVER_EXCEPTION(6),
    SERVER_DISCONNECT(7),
    CLIENT_CHANGENAME(8),
    SERVER_CHANGENAME(9),
    CLIENT_WHISPERMESSAGE(10),
    CLIENT_FILE(11),
    SERVER_FILE(12);

    private final int value;
    //서버와 클라이언트는 메세지를 필요로하고 커넥트와 디스커넥트는 메세지는 필요하지 않음
}
