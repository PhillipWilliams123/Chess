package com.example.Chess.Network.Packets;

import com.example.Chess.Globals;
import com.example.Chess.Network.Packet;
import com.example.Chess.Network.PacketTypes;

import java.io.*;

public class ServerInfoPacket extends Packet
{

    public int port;
    public int players;
    public int id;
    public String ip;

    public ServerInfoPacket()
    {

    }

    public ServerInfoPacket(int port, int players, int id, String ip)
    {
        this.port = port;
        this.id = id;
        this.players = players;
        this.ip = ip;
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
            outStream.writeInt(port);
            outStream.writeInt(id);
            outStream.writeInt(players);
            outStream.write(ip.getBytes());
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
            port = inputStream.readInt();
            id = inputStream.readInt();
            players = inputStream.readInt();
            ip = new String(inputStream.readAllBytes());
            inputStream.close();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PacketTypes GetType()
    {
        return PacketTypes.serverInfo;
    }
}
