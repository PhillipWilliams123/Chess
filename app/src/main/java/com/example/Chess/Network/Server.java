package com.example.Chess.Network;

import com.example.Chess.Chess.ChessBoard;
import com.example.Chess.Interaction;
import com.example.Chess.Network.Packets.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server
{
    public static int MaxClients = 2;
    public ServerSocket socket;
    public List<Socket> clients;
    public int port;
    public String ip;
    public ServerListenThread[] listenThreads;
    public Thread joinThread;

    public int currentClients;

    /**
     * Starts the server on a port
     * @param ip the ip on which it will run (not needed for the server)
     * @param port the port on which it will run
     */
    public boolean Init(String ip, int port)
    {
        this.ip = ip;
        this.port = port;
        currentClients = 0;
        boolean startedServer = false;
        int startCount = 0;
        String err = "";
        while (!startedServer )
        {
            try
            {
                startCount++;
                socket = new ServerSocket(port);
                startedServer = true;
                System.out.println("[SERVER] Started server on " + ip + ":" + port);
            } catch (IOException e)
            {
                port++;
                err = e.getMessage();
            }

            if(startCount >= 50)
                break;
        }

        if(!startedServer)
        {
            System.out.println("[SERVER] Could not start server " + err);
            return false;
        }

        clients = new ArrayList<>();
        listenThreads = new ServerListenThread[MaxClients];
        //create the listen threads for each client
        for (int i = 0; i < MaxClients; i++)
        {
            listenThreads[i] = new ServerListenThread(i);
        }
        joinThread = new Thread(this::AcceptClients);
        joinThread.start();

        //send information to the locater server
        if(NetworkManager.isLocaterClient)
        {
            //tell the server we are a server
            NetworkManager.locaterClient.SendPacket(new IdentifierPacket(true));
            NetworkManager.locaterClient.SendPacket(new ServerInfoPacket(port, currentClients, 0, ip));
        }
        else
        {
            System.out.println("[SERVER] Started without connection to Locater Server");
        }

        Interaction.Init();
        ChessBoard.Init();

        return true;
    }

    public void Update()
    {
        for (int i = 0; i < clients.size(); i++)
        {
            //restart the thread if it stopped
            if(listenThreads[i].thread.isInterrupted())
            {
                if(listenThreads[i].bufferData != null)
                {
                    //since the thread stopped this means we got data and we can then parse it
                    HandlePacket(listenThreads[i].bufferData, listenThreads[i].bufferClient);
                }
                try
                {
                    //dont reset the thread if the client is null
                    if(i == clients.size() || clients.get(i) == null)
                        continue;

                    //reset the thread
                    listenThreads[i].Start(currentClients, clients.get(i).getInputStream());
                } catch (IOException e)
                {
                    System.out.println("[SERVER] Could not start listen thread");
                    continue;
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
                clients.add(socket.accept());
                System.out.println("[SERVER] got client " + clients.get(currentClients).getInetAddress());
                //start the listen thread for this client
                listenThreads[currentClients].Start(currentClients, clients.get(currentClients).getInputStream());
                //send out what version this server is so we don't have weird behavior happening
                //with different versions of the code interacting
                SendPacket(new VersionPacket(), currentClients);
                currentClients++;
                //update locater server with our information
                if(NetworkManager.isLocaterClient)
                {
                    //tell the server we are a server
                    NetworkManager.locaterClient.SendPacket(new IdentifierPacket(true));
                    NetworkManager.locaterClient.SendPacket(new ServerInfoPacket(port, currentClients, 0, ip));
                }

                joinThread.interrupt();

                //if it is not our turn and someone joins then tell them it is their turn
                if(!Interaction.isOurTurn && currentClients == 2)
                    SendPacket(new StartGamePacket(Interaction.isOurTurn), currentClients);
            } catch (IOException e)
            {
                System.out.println("[SERVER] Could not accept client");
                return;
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
            if(!NetworkManager.disablePacketLogging)
                System.out.println("[SERVER] sending packet " + packet.GetType());
            DataOutputStream outStream = new DataOutputStream(clients.get(client).getOutputStream());
            byte[] data = packet.PacketToByte();
            outStream.writeInt(data.length);
            outStream.write(data);
            outStream.flush();
        } catch (IOException e)
        {
            System.out.println("[SERVER] Could not send packet");
            return;
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
            System.out.println("[SERVER] Could not read packet");
            return;
        }

        if(!NetworkManager.disablePacketLogging)
            System.out.println("[SERVER] got packet " + PacketTypes.types[type]);

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
                break;
            }
            case 4:
            {
                //disconnect packet
                //behavior is to remove the client that disconnected
                //stop the thread for the client

                SendPacket(new DisconnectPacket(), client);

                try {
                    listenThreads[client].thread.join(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                listenThreads[client].thread.interrupt();
                clients.remove(client);
                currentClients--;

                System.out.println("[SERVER] Client disconnected");

                //restart the join thread to allow new clients to join
                joinThread.interrupt();
                if(joinThread.isInterrupted())
                {
                    joinThread = new Thread(this::AcceptClients);
                    joinThread.start();
                }

                //update locater server with our information
                if(NetworkManager.isLocaterClient)
                {
                    //tell the server we are a server
                    NetworkManager.locaterClient.SendPacket(new IdentifierPacket(true));
                    NetworkManager.locaterClient.SendPacket(new ServerInfoPacket(port, currentClients, 0, ip));
                }

                break;
            }
        }
    }

    /**
     * Stops the server and cleans up streams
     */
    public void Stop()
    {
        if(NetworkManager.isLocaterClient)
        {
            NetworkManager.locaterClient.SendPacket(new ServerInfoPacket(0, 0, 0, ""));
        }
        NetworkManager.isServer = false;

        try {

            for (int i = 0; i < currentClients; i++)
            {
                //clients[i].close();
            }

            socket.close();
        } catch (Exception e)
        {

        }
    }
}
