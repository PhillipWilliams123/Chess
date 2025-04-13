package com.example.Chess.Network;

import com.example.Chess.Network.Packets.PieceMovePacket;
import com.example.Chess.Network.Packets.PongPacket;
import com.example.Chess.Network.Packets.VersionPacket;

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
    public ServerListenThread[] listenThreads;
    public Thread joinThread;

    private int currentClients;
    private int clientIncr;

    /**
     * Starts the server on a port
     * @param ip the ip on which it will run (not needed for the server)
     * @param port the port on which it will run
     */
    public void Init(String ip, int port)
    {
        this.ip = ip;
        this.port = port;
        currentClients = 0;
        try
        {
            socket = new ServerSocket(port);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        clients = new Socket[MaxClients];
        listenThreads = new ServerListenThread[MaxClients];
        for (int i = 0; i < MaxClients; i++)
        {
            listenThreads[i] = new ServerListenThread(i, currentClients);
        }
        joinThread = new Thread(this::AcceptClients);
        joinThread.start();
    }

    public void Update()
    {
        for (int i = 0; i < currentClients; i++)
        {
            //restart the thread if it stopped
            if(listenThreads[i].thread.isInterrupted())
            {
                if(listenThreads[i].bufferData != null)
                {
                    clientIncr++;
                    //store an incrementer so the thread can process all clients
                    if(clientIncr >= currentClients)
                        clientIncr = 0;
                    HandlePacket(listenThreads[i].bufferData, listenThreads[i].bufferClient);
                }
                try
                {
                    listenThreads[i].Start(currentClients, clients[i].getInputStream());
                } catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }

        //restart the join thread if it is stopped
        if(joinThread.isInterrupted())
        {
            joinThread = new Thread(this::AcceptClients);
            joinThread.start();
        }
    }

    /**
     * Will accept clients on a thread
     */
    public void AcceptClients()
    {
        //continue accepting clients until we fill the max allowed clients
        if(currentClients < MaxClients)
        {
            try
            {
                //this is blocking so it will wait here until a client connects
                clients[currentClients] = socket.accept();
                //start the listen thread for this client
                listenThreads[currentClients].Start(currentClients, clients[currentClients].getInputStream());
                //send out what version this server is so we don't have weird behavior happening
                //with different versions of the code interacting
                SendPacket(new VersionPacket(), currentClients);
                currentClients++;
                joinThread.interrupt();
            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }
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
            System.out.println("[SERVER] sending packet " + packet.GetType());
            DataOutputStream outStream = new DataOutputStream(clients[client].getOutputStream());
            byte[] data = packet.PacketToByte();
            outStream.writeInt(data.length);
            outStream.write(data);
            outStream.flush();
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
        for (int i = 0; i < currentClients; i++)
        {
            SendPacket(packet, i);
        }
    }

    /**
     * Send a packet to all clients excluding a client
     * @param packet the packet to send
     * @param client the client to exclude
     */
    public void SendPacketAllExclude(Packet packet, int client)
    {
        for (int i = 0; i < currentClients; i++)
        {
            if(i == client)
                continue;

            SendPacket(packet, i);
        }
    }


    /**
     * Will run whatever behavior for the data sent in
     * @param data the packet in bytes
     * @param client the index of the client if we need to return any packets
     */
    private void HandlePacket(byte[] data, int client)
    {
        //we need to stop the thread when we reach here because we have data to be processed

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

        System.out.println("[SERVER] got packet " + type);

        switch (type)
        {
            case -1:
                break;
            case 0:
            {
                //ping packet
                //behavior is to return a pong packet
                PongPacket packet = new PongPacket();
                SendPacket(packet, client);
                break;
            }
            case 1:
            {
                //pong packet
                break;
            }
            case 3:
            {
                //piece move packet
                //behavior is that we send this to all other clients excluding the sender
                PieceMovePacket packet = new PieceMovePacket();
                packet.ByteToPacket(data);
                SendPacketAllExclude(packet, client);
            }
        }
    }

    public void Stop()
    {
        try {

            for (int i = 0; i < currentClients; i++)
            {
                clients[i].getInputStream().close();
                clients[i].getOutputStream().close();
                clients[i].close();
            }

            socket.close();
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
