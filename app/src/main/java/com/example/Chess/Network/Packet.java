package com.example.Chess.Network;

public abstract class Packet
{
    /**
     * Converts the packet to a byte array
     * !!IMPORTANT!! All strings should be at the end of the array
     * @return the packet in a byte array
     */
    public abstract byte[] PacketToByte();

    /**
     * Converts a byte array to a packet
     * Will throw an error if data does not match the packet type
     * @param data the byte array that will be converted
     */
    public abstract void ByteToPacket(byte[] data);

    /**
     * Gets the type of packet
     * @return the packet type
     */
    public abstract PacketTypes GetType();
}
