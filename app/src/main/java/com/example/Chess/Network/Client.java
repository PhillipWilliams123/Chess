package com.example.Chess.Network;

import com.example.Chess.Chess.ChessBoard;
import com.example.Chess.Globals;
import com.example.Chess.Interaction;
import com.example.Chess.Network.Packets.*;
import com.example.Chess.UI.UiButton;
import com.example.Chess.Vector2;
import com.raylib.Raylib;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

import static com.example.Chess.UI.UI.buttons;

public class Client implements Runnable
{

    public String ip;
    public int port;
    public Socket socket;
    public boolean connected;
    public Thread listenThread;

    private InputStream serverStream;
    private OutputStream clientStream;
    private DataInputStream serverInputStream;
    private DataOutputStream clientOutStream;

    private byte[] bufferData;

    /**
     * Will initialize anything in the client
     * Currently does nothing
     */
    public void Init()
    {

    }

    /**
     * Will connect the client to a server socket
     * @param ip the ip of the server
     * @param port the port of the server
     */
    public boolean Connect(String ip, int port)
    {
        try
        {
            socket = new Socket(ip, port);
            serverStream = socket.getInputStream();
            serverInputStream = new DataInputStream(serverStream);
            clientStream = socket.getOutputStream();
            clientOutStream = new DataOutputStream(clientStream);
            connected = true;
            System.out.println("[CLIENT] Started connect to " + ip + ":" + port);

            //start the "Listening" thread to get the data
            listenThread = new Thread(this);
            listenThread.start();

            this.ip = ip;
            this.port = port;

        } catch (IOException e)
        {
            System.out.println("[CLIENT] Could not connect to " + ip + ":" + port);
            return false;
        }
        return true;
    }

    /**
     * Will send a packet to the server
     * @param packet the packet we want to send
     */
    public void SendPacket(Packet packet)
    {
        //get the output stream so we can write to it
        try
        {
            if(!NetworkManager.disablePacketLogging)
                System.out.println("[CLIENT] sending packet " + packet.GetType());
            byte[] data = packet.PacketToByte();
            clientOutStream.writeInt(data.length);
            clientOutStream.write(data);
            clientOutStream.flush();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Will update the client
     */
    public void Update()
    {
        if(listenThread.isInterrupted())
        {
            //handle the data
            if(bufferData != null)
                HandlePacket(bufferData);
            //restart the thread
            listenThread = new Thread(this);
            listenThread.start();
        }
    }

    /**
     * What will be run in a different thread
     */
    public void run()
    {
        //get the input stream so we can access the data the server is sending
        try
        {
            int dataLength = serverInputStream.readInt();
            bufferData = serverInputStream.readNBytes(dataLength);
            if(!NetworkManager.disablePacketLogging)
                System.out.println("[CLIENT] " + Arrays.toString(bufferData) + " client receive");
            //we have received data so we stop the thread and process the data
            listenThread.interrupt();
        } catch (IOException e)
        {
            System.out.println("[CLIENT] " + e.getMessage());
        }
    }

    /**
     * Will run whatever behavior for the data sent in
     * @param data the packet in bytes
     */
    private void HandlePacket(byte[] data)
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

        if(!NetworkManager.disablePacketLogging)
            System.out.println("[CLIENT] got packet " + PacketTypes.types[type]);

        switch (type)
        {
            case -1:
                break;
            case 0:
            {
                //ping packet
                //behavior is to return a pong packet
                PongPacket packet = new PongPacket();
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
                VersionPacket packet = new VersionPacket();
                packet.ByteToPacket(data);
                if(!NetworkManager.disablePacketLogging)
                    System.out.println("[CLIENT] Server version: " + packet.version);
                if(!packet.version.equals(Globals.Version))
                {
                    //create an error if they don't match
                    throw new RuntimeException("[CLIENT] Server and Client versions do not match");
                }
                break;
            }
            case 3:
            {
                //piece move packet
                //behavior is to move the piece to the position
                PieceMovePacket packet = new PieceMovePacket();
                packet.ByteToPacket(data);
                ChessBoard.chessPieces[packet.piece].TryMove(packet.position);

                //we have received a move and can set our player to be able to move
                Interaction.isOurTurn = true;
                Interaction.SetTurn(packet.turn);

                break;
            }
            case 4:
            {
                //if the client gets a disconnect we stop the client
                Stop();
                break;
            }
            case 5:
            {
                //Draw packet
                //behavior is to move the piece to the position
                DrawPacket packet = new DrawPacket();
                packet.ByteToPacket(data);

                break;
            }
            case 6:
            {
                //piece move packet
                //behavior is to move the piece to the position
                SurrenderPacket packet = new SurrenderPacket();
                packet.ByteToPacket(data);

                break;
            }
            case 7:
            {
                //server info packet
                ServerInfoPacket packet = new ServerInfoPacket();
                packet.ByteToPacket(data);

                LocaterServer.ips[packet.id] = packet.ip;
                LocaterServer.ports[packet.id] = packet.port;
                LocaterServer.players[packet.id] = packet.players;

                break;
            }
            case 8:
            {
                //server packet request
                break;
            }
            case 10:
            {
                StartGamePacket packet = new StartGamePacket();
                packet.ByteToPacket(data);

                Interaction.isOurTurn = !packet.otherSide;

                //start game packet
                ChessBoard.Init();
                ChessBoard.InitStandardGame();
                break;
            }
        }
    }

    public void Disconnect()
    {
        NetworkManager.client.SendPacket(new DisconnectPacket());
    }

    /**
     * Will clean up and stop any streams
     */
    public void Stop()
    {
        NetworkManager.isClient = false;

        try
        {
            listenThread.interrupt();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
