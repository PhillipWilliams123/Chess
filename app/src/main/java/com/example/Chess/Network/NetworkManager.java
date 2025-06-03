package com.example.Chess.Network;

import com.example.Chess.Network.Packets.IdentifierPacket;
import com.raylib.Raylib;

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
        
        if(server.Init("127.0.0.1", globalPort))
        {
            isServer = true;
            initialized = true;

            //we will also want to start a client to run along the server on the same instance

            InitClient();
        }
    }

    /**
     * Starts the client
     */
    public static void InitClient()
    {
        if(isClient)
            return;

        client = new Client();
        //init does nothing currently
        client.Init();
        //the ip is just on localhost for testing but should be set
        //by and input to allow separate clients to connect
        if(client.Connect("127.0.0.1", globalPort))
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

    static float updateAccum = 1;
    /**
     * Will update server or client
     */
    public static void Update()
    {
        updateAccum += Raylib.GetFrameTime();

        if(!isLocaterClient && updateAccum > 1f)
        {
            updateAccum = 0;
            InitLocaterClient();
        }

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
