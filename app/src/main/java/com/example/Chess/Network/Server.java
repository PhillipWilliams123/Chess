package com.example.Chess.Network;

import com.example.Chess.Network.Packets.Ping;
import com.example.Chess.Network.Packets.Pong;
import com.example.Chess.Network.Packets.Version;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server
{
    public static int MaxClients = 2;
    public ServerSocket socket;
    public Socket[] clients;
    public int port;
    public String ip;

    private int currentClients;

    /**
     * Starts the server on a port
     * @param ip the ip on which it will run (not needed for the server)
     * @param port the port on which it will run
     */
    public void Init(String ip, int port)
    {
        this.ip = ip;
        this.port = port;
        try
        {
            socket = new ServerSocket(port);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        clients = new Socket[MaxClients];
    }

    public void Update()
    {
        //continue accepting clients until we fill the max allowed clients
        //this is blocking so it will wait here until a client connects
        if(currentClients < MaxClients)
        {
            try
            {

                clients[currentClients] = socket.accept();
                //send out what version this server is so we don't have weird behavior happening
                //with different versions of the code interacting
                SendPacket(new Version(), currentClients);
            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }

            currentClients++;
        }
        else
        {
            //all clients are connected
        }


    }

    /**
     * Will send a packet to a client
     * @param packet the packet to be sent
     * @param client the index of the client
     */
    public void SendPacket(Packet packet, int client)
    {
        //get the output stream so we can write to it
        try
        {
            OutputStream outStream = clients[client].getOutputStream();
            outStream.write(packet.PacketToByte());
            outStream.flush();
            outStream.close();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Send a packet to all clients
     * @param packet the packet to be sent
     */
    public void SendPacketAll(Packet packet)
    {
        for (int i = 0; i < clients.length; i++)
        {
            //get the output stream so we can write to it
            try
            {
                OutputStream outStream = clients[i].getOutputStream();
                outStream.write(packet.PacketToByte());
                outStream.flush();
                outStream.close();
            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
    }


    /**
     * Will run whatever behavior for the data sent in
     * @param data the packet in bytes
     * @param client the index of the client if we need to return any packets
     */
    private void HandlePacket(byte[] data, int client)
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
                SendPacket(packet, client);
                break;
            }
            case 1:
            {
                //pong packet
                break;
            }
        }
    }
}
