package com.example.Chess.Network;

import com.example.Chess.Network.Packets.IdentifierPacket;
import com.raylib.Raylib;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class NetworkManager
{
    public static final int globalPort = 58301;

    public static Server server;
    public static Client client;
    public static boolean isServer;
    public static boolean isClient;
    public static boolean isLocaterClient;
    public static boolean isLocaterServer;
    public static boolean initialized;

    public static Client locaterClient;
    public static LocaterServer locaterServer;
    public static boolean disablePacketLogging = true;

    public static void Init()
    {
        LocaterServer.InitServerLists();
    }

    /**
     * Starts the server
     */
    public static void InitServer()
    {
        if(isServer)
            return;

        //start on localhost and some port that is never in use
        //since it is localhost we just point all clients to the ip
        //the server runs on the port
        server = new Server();
        String serverIp = "127.0.0.1";
        Enumeration e = null;
        try {
            e = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException ex) {
            throw new RuntimeException(ex);
        }
        while(e.hasMoreElements())
        {
            NetworkInterface n = (NetworkInterface) e.nextElement();
            Enumeration ee = n.getInetAddresses();
            while (ee.hasMoreElements())
            {
                InetAddress i = (InetAddress) ee.nextElement();
                String ip = i.getHostAddress();
                if((ip.charAt(0) == '1' && ip.charAt(1) == '9') || (ip.charAt(0) == '1' && ip.charAt(1) == '0'))
                    serverIp = ip;
            }
        }
        if(server.Init(serverIp, globalPort))
        {
            isServer = true;
            initialized = true;

            //we will also want to start a client to run along the server on the same instance

            InitClient("127.0.0.1", server.port);
        }
    }

    /**
     * Starts the client
     */
    public static void InitClient(String ip, int port)
    {
        if(isClient)
            return;

        client = new Client();
        //init does nothing currently
        client.Init();
        //the ip is just on localhost for testing but should be set
        //by and input to allow separate clients to connect
        if(client.Connect(ip, port))
        {
            isClient = true;
            initialized = true;
        }
    }

    public static void InitLocaterClient()
    {
        if(isLocaterClient)
            return;

        locaterClient = new Client();
        locaterClient.Init();

        if(locaterClient.Connect(LocaterServer.locaterIp, LocaterServer.locaterPort))
        {
            isLocaterClient = true;
            //tell the server we are not a server
            locaterClient.SendPacket(new IdentifierPacket(false));
        }
    }

    public static void InitLocaterServer()
    {
        //check if there is already a locater server
        if(isLocaterClient || isLocaterServer)
        {
            //there is a locater server if the locater client is connected
            return;
        }
        locaterServer = new LocaterServer();
        if(locaterServer.Init())
        {
            isLocaterServer = true;
        }
    }
    /**
     * Will update server or client
     */
    public static void Update()
    {
        if(isServer)
        {
            server.Update();
            //update the client on the server instance
        }
        if(isClient)
        {
            client.Update();
        }
        if(isLocaterClient)
        {
            locaterClient.Update();
        }
        if(isLocaterServer)
        {
            locaterServer.Update();
        }
    }

    /**
     * Cleans up server and client, whichever is started
     */
    public static void CleanUp()
    {
        initialized = false;

        if(isClient)
        {
            client.Stop();
        }
        if(isServer)
        {
            server.Stop();
        }
        if(isLocaterClient)
        {
            locaterClient.Stop();
        }
        if(isLocaterServer)
        {
            locaterServer.Stop();
        }
    }
}
