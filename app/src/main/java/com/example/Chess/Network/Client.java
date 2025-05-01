package com.example.Chess.Network;

import com.example.Chess.Chess.ChessBoard;
import com.example.Chess.Globals;
import com.example.Chess.Network.Packets.DisconnectPacket;
import com.example.Chess.Network.Packets.PieceMovePacket;
import com.example.Chess.Network.Packets.PongPacket;
import com.example.Chess.Network.Packets.VersionPacket;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

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
    public void Connect(String ip, int port)
    {
        try
        {
            socket = new Socket(ip, port);
            serverStream = socket.getInputStream();
            serverInputStream = new DataInputStream(serverStream);
            clientStream = socket.getOutputStream();
            clientOutStream = new DataOutputStream(clientStream);
            connected = true;
            System.out.println("[CLIENT] Started on " + socket.getInetAddress());

            //start the "Listening" thread to get the data
            listenThread = new Thread(this);
            listenThread.start();

        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
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
            System.out.println("[CLIENT] " + Arrays.toString(bufferData) + " client receive");
            //we have received data so we stop the thread and process the data
            listenThread.interrupt();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
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

        System.out.println("[CLIENT] got packet " + type);

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
            }
        }
    }

    /**
     * Will clean up and stop any streams
     */
    public void Stop()
    {
        NetworkManager.client.SendPacket(new DisconnectPacket());

        try
        {
            listenThread.interrupt();
            serverStream.close();
            clientStream.close();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
