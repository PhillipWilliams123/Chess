package com.example.Chess.Network;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class ServerListenThread implements Runnable
{
    public Thread thread;
    public byte[] bufferData;
    public int bufferClient;

    //store what its index is
    private int threadIndex;
    private int currentClients;
    private DataInputStream inputStream;

    public ServerListenThread(int threadIndex, int currentClients)
    {
        this.threadIndex = threadIndex;
    }

    public void Start(int currentClients, InputStream inputStream)
    {
        this.currentClients = currentClients;
        this.inputStream = new DataInputStream(inputStream);
        thread = new Thread(this);
        thread.start();
    }

    public void run()
    {
        try
        {
            //if there are no clients then just stop
            if(currentClients == 0)
            {
                thread.interrupt();
                return;
            }

            System.out.println("[SERVER] start receive on thread " + threadIndex);
            int dataLength = inputStream.readInt();
            bufferData = inputStream.readNBytes(dataLength);
            bufferClient = threadIndex;
            System.out.println("[SERVER] " + Arrays.toString(bufferData) + " server receive");
            thread.interrupt();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
