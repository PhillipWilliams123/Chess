package com.example.Chess.Network;

import com.example.Chess.Globals;
import com.example.Chess.Network.Packets.Pong;
import com.example.Chess.Network.Packets.Version;

import java.io.*;
import java.net.Socket;

public class Client
{

    public String ip;
    public int port;
    public Socket socket;
    public boolean connected;

    public void Init()
    {

    }

    public void Connect(String ip, int port)
    {
        try {
            socket = new Socket(ip, port);
            connected = true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void SendPacket(Packet packet)
    {
        //get the output stream so we can write to it
        try
        {
            OutputStream outStream = socket.getOutputStream();
            outStream.write(packet.PacketToByte());
            outStream.flush();
            outStream.close();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void Update()
    {
        //check if we have connected to a server before receiving packets
        if(connected)
        {
            //get the input stream so we can access the data the server is sending
            InputStream inputStream;
            try
            {
                inputStream = socket.getInputStream();
                HandlePacket(inputStream.readAllBytes());
            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }

        }
    }

    /**
     * Will run whatever behavior for the data sent in
     * @param data the packet in bytes
     */
    private void HandlePacket(byte[] data)
    {
        //data is not a packet as they all start with an int
        //which is size 4
        if(data.length < 4)
            return;

        //get the type of packet
        int type = -1;
        ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
        DataInputStream inputStream = new DataInputStream(byteStream);
        try
        {
            type = inputStream.readInt();
            inputStream.close();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        switch (type)
        {
            case -1:
                break;
            case 0:
            {
                //ping packet
                //behavior is to return a pong packet
                Pong packet = new Pong();
                SendPacket(packet);
                break;
            }
            case 1:
            {
                //pong packet
                //behavior is something
                break;
            }
            case 2:
            {
                //version packet
                //behavior is to check version match
                Version packet = new Version();
                packet.ByteToPacket(data);
                System.out.println("Server version: " + packet.version);
                if(!packet.version.equals(Globals.Version))
                {
                    //create an error if they don't match
                    throw new RuntimeException("Server and Client versions do not match");
                }
                break;
            }
        }
    }

    public void Stop()
    {
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
