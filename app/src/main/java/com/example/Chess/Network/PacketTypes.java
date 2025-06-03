package com.example.Chess.Network;

public enum PacketTypes
{
    ping,
    pong,
    version,
    pieceMove,
    disonnect,
    draw,
    surrender,
    serverInfo,
    serverInfoRequest,
    identifier;

    public static PacketTypes[] types = PacketTypes.values();
}
