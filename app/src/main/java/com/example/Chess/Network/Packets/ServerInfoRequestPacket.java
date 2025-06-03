package com.example.Chess.Network.Packets;

import com.example.Chess.Network.Packet;
import com.example.Chess.Network.PacketTypes;

import java.io.*;

public class ServerInfoRequestPacket extends Packet
{

    public int id;

    public ServerInfoRequestPacket()
    {

    }

    public ServerInfoRequestPacket(int id)
    {
        this.id = id;
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
            outStream.writeInt(id);
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
            id = inputStream.readInt();
            inputStream.close();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PacketTypes GetType()
    {
        return PacketTypes.serverInfoRequest;
    }
}
