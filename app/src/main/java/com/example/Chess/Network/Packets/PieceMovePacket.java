package com.example.Chess.Network.Packets;

import com.example.Chess.Network.Packet;
import com.example.Chess.Network.PacketTypes;
import com.example.Chess.Vector2;

import java.io.*;

public class PieceMovePacket extends Packet
{
    public Vector2 position;
    public int piece;
    public boolean turn;

    public PieceMovePacket()
    {

    }

    public PieceMovePacket(Vector2 position, int piece, boolean turn)
    {
        this.position = position;
        this.piece = piece;
        this.turn = turn;
    }

    @Override
    public byte[] PacketToByte()
    {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream outStream = new DataOutputStream(byteStream);

        //since all that is needed for the ping is just the type we turn this into the bytes
        try
        {
            outStream.writeInt(GetType().ordinal());
            outStream.writeDouble(position.x);
            outStream.writeDouble(position.y);
            outStream.writeInt(piece);
            outStream.writeBoolean(turn);
            outStream.flush();
            outStream.close();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        return byteStream.toByteArray();
    }

    @Override
    public void ByteToPacket(byte[] data)
    {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
        DataInputStream inputStream = new DataInputStream(byteStream);

        //get the data and assign it
        try
        {
            int type = inputStream.readInt();
            if(type != GetType().ordinal())
                throw new RuntimeException("Byte data does not match packet" + type + " " + GetType());
            position = new Vector2(inputStream.readDouble(), inputStream.readDouble());
            piece = inputStream.readInt();
            turn = inputStream.readBoolean();
            inputStream.close();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PacketTypes GetType()
    {
        return PacketTypes.pieceMove;
    }
}
