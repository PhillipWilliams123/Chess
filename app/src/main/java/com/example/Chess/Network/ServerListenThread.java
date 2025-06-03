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

    /**
     * Creates a server Listen Thread
     * @param threadIndex the index that the thread is
     */
    public ServerListenThread(int threadIndex)
    {
        this.threadIndex = threadIndex;
    }

    /**
     * Will Start the thread to start listening
     * @param currentClients what the current clients number is at
     * @param inputStream the input stream for the client of this thread
     */
    public void Start(int currentClients, InputStream inputStream)
    {
        this.currentClients = currentClients;
        this.inputStream = new DataInputStream(inputStream);
        thread = new Thread(this);
        thread.start();
    }

    /**
     * Runs the stuff for the thread to run async from the main thread
     */
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

            if(!NetworkManager.disablePacketLogging)
                System.out.println("[SERVER] start receive on thread " + threadIndex);
            int dataLength = inputStream.readInt();
            bufferData = inputStream.readNBytes(dataLength);
            bufferClient = threadIndex;
            if(!NetworkManager.disablePacketLogging)
                System.out.println("[SERVER] " + Arrays.toString(bufferData) + " server receive");
            thread.interrupt();
        } catch (IOException e)
        {
            //the stream has stopped for some reason so terminate
            thread.interrupt();
            return;
        }
    }
}
