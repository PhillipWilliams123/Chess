package com.example.Chess.Network;

public class NetworkManager
{
    public static final int globalPort = 58301;

    public static Server server;
    public static Client client;
    public static boolean isServer;
    public static boolean isClient;
    public static boolean initialized;

    /**
     * Starts the server
     */
    public static void InitServer()
    {
        //start on localhost and some port that is never in use
        //since it is localhost we just point all clients to the ip
        //the server runs on the port
        server = new Server();
        server.Init("127.0.0.1", globalPort);
        isServer = true;
        initialized = true;

        //we will also want to start a client to run along the server on the same instance

        InitClient();
    }

    /**
     * Starts the client
     */
    public static void InitClient()
    {
        client = new Client();
        //init does nothing currently
        client.Init();
        //the ip is just on localhost for testing but should be set
        //by and input to allow separate clients to connect
        client.Connect("127.0.0.1", globalPort);
        isClient = true;
        initialized = true;
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
    }

    /**
     * Cleans up server and client, whichever is started
     */
    public static void CleanUp()
    {
        if(isServer)
        {
            server.Stop();
        }
        if(isClient)
        {
            client.Stop();
        }
    }
}
