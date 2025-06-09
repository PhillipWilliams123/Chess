package com.example.Chess.Network;

public enum PacketTypes
{
    ping,
    pong,
    version,
    pieceMove,
    disconnect,
    draw,
    surrender,
    serverInfo,
    serverInfoRequest,
    identifier,
    startGame,
    state;

    public static PacketTypes[] types = PacketTypes.values();
}
