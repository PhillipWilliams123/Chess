package com.example.Chess.Network.Packets;

import com.example.Chess.Globals;
import com.example.Chess.Network.Packet;
import com.example.Chess.Network.PacketTypes;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Version extends Packet
{
    public String version;

    @Override
    public byte[] PacketToByte()
    {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream outStream = new DataOutputStream(byteStream);

        //since all that is needed for the ping is just the type we turn this into the bytes
        try
        {
            outStream.writeInt(GetType().ordinal());
            outStream.write(Globals.Version.getBytes());
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
            version = new String(inputStream.readAllBytes());
            inputStream.close();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PacketTypes GetType()
    {
        return PacketTypes.version;
    }
}
