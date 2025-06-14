package com.example.Chess.Network;

import com.example.Chess.Network.Packets.*;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class LocaterServer
{
    public static int MaxClients = 20;
    public ServerSocket socket;
    public List<Socket> clients;
    public static int locaterPort = 58300;
    //will need to be set to a known ip all clients have
    public static String locaterIp = "127.0.0.1";
    public ServerListenThread[] listenThreads;
    public Thread joinThread;

    private int currentClients;

    public static int[] ports;
    public static int[] players;
    public static String[] ips;

    private int[] serverPorts;
    private int[] serverPlayers;
    private String[] serverIps;
    private int[] serverIndexOffsets;

    public static void InitServerLists()
    {
        ports = new int[MaxClients];
        players = new int[MaxClients];
        ips = new String[MaxClients];

        for (int i = 0; i < MaxClients; i++)
        {
            ips[i] = "";
        }
    }

    /**
     * Starts the server on a port
     */
    public boolean Init()
    {
        serverPorts = new int[MaxClients];
        serverPlayers = new int[MaxClients];
        serverIps = new String[MaxClients];
        serverIndexOffsets = new int[MaxClients];

        for (int i = 0; i < MaxClients; i++)
        {
            serverIps[i] = "";
        }

        currentClients = 0;
        boolean startedServer = false;
        int startCount = 0;
        String err = "";
        while (!startedServer )
        {
            try
            {
                startCount++;
                socket = new ServerSocket(locaterPort);
                startedServer = true;
            } catch (IOException e)
            {
                locaterPort++;
                err = e.getMessage();
            }

            if(startCount >= 50)
                break;
        }

        if(!startedServer)
        {
            System.out.println("[LOC SERVER] Could not start server " + err);
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
        return true;
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
                    //since the thread stopped this means we got data and we can then parse it
                    HandlePacket(listenThreads[i].bufferData, listenThreads[i].bufferClient);
                }
                try
                {
                    //dont reset the thread if the client is null
                    if(clients.isEmpty() || clients.get(i) == null)
                        continue;

                    //reset the thread
                    listenThreads[i].Start(currentClients, clients.get(i).getInputStream());
                } catch (IOException e)
                {
                    System.out.println("[LOC SERVER] Could not start listen thread");
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
                System.out.println("[LOC SERVER] got client " + clients.get(currentClients).getInetAddress());
                //start the listen thread for this client
                listenThreads[currentClients].Start(currentClients, clients.get(currentClients).getInputStream());
                currentClients++;
                joinThread.interrupt();
            } catch (IOException e)
            {
                System.out.println("[LOC SERVER] Could not accept client");
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
                System.out.println("[LOC SERVER] sending packet " + packet.GetType());
            DataOutputStream outStream = new DataOutputStream(clients.get(client).getOutputStream());
            byte[] data = packet.PacketToByte();
            outStream.writeInt(data.length);
            outStream.write(data);
            outStream.flush();
        } catch (IOException e)
        {
            System.out.println("[LOC SERVER] Could not send packet " + packet.GetType());
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
            System.out.println("[LOC SERVER] Could not read packet");
            return;
        }

        if(!NetworkManager.disablePacketLogging)
            System.out.println("[LOC SERVER] got packet " + PacketTypes.types[type]);

        switch (type)
        {
            case 0:
            {
                //ping packet
                //behavior is to return a pong packet
                PongPacket packet = new PongPacket();
                SendPacket(packet, client);
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

                //restart the join thread to allow new clients to join
                joinThread.interrupt();
                if(joinThread.isInterrupted())
                {
                    joinThread = new Thread(this::AcceptClients);
                    joinThread.start();
                }

                break;
            }
            case 7:
            {
                //server info packet
                ServerInfoPacket packet = new ServerInfoPacket();
                packet.ByteToPacket(data);

                //we need to remove any index from a client
                int sum = 0;
                for (int i = 0; i < client; i++)
                {
                    sum += serverIndexOffsets[i];
                }

                serverIps[client - sum] = packet.ip;
                serverPorts[client - sum] = packet.port;
                serverPlayers[client - sum] = packet.players;
                serverIndexOffsets[client] = 0;


                break;
            }
            case 8:
            {
                //server info request packet
                ServerInfoRequestPacket packet = new ServerInfoRequestPacket();
                packet.ByteToPacket(data);

                SendPacket(new ServerInfoPacket(serverPorts[packet.id], serverPlayers[packet.id], packet.id, serverIps[packet.id]), client);
                break;
            }
            case 9:
            {
                //identifier packet
                IdentifierPacket packet = new IdentifierPacket();
                packet.ByteToPacket(data);

                if(!packet.isServer)
                {
                    serverIndexOffsets[client] = 1;
                }
                else
                {
                    serverIndexOffsets[client] = 0;
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
        try {


            socket.close();
        } catch (Exception e)
        {

        }
    }
}
