package com.example.Chess.UI;

import com.example.Chess.Network.LocaterServer;
import com.example.Chess.Network.NetworkManager;
import com.example.Chess.Network.Packets.ServerInfoRequestPacket;
import com.example.Chess.Vector2;
import com.raylib.Raylib;

import static com.raylib.Colors.*;

public class MutiUi {
    public static UiButton[] buttons;

    public static ToolTip locaterServerToolTip = new ToolTip(new Vector2(640 + (360 / 2), 100), new Vector2(360 / 2, 100), "Loc Server\nAlready Started");
    public static ToolTip serverToolTip = new ToolTip(new Vector2(640, 100), new Vector2(360 / 2, 100), "Needs Locater\nServer Connection");

    public static void Initialize()
    {
        buttons = new UiButton[23];
        buttons[0] = new UiButton(new Vector2(640, 0), new Vector2(360, 100), "Back");
        buttons[1] = new UiButton(new Vector2(640, 100), new Vector2(360 / 2, 100), "Start Server");
        buttons[2] = new UiButton(new Vector2(640 + (360 / 2), 100), new Vector2(360 / 2, 100), "Start Loc Server");

        for (int i = 0; i < LocaterServer.MaxClients; i++)
        {
            buttons[i + 3] = new UiButton(new Vector2(1000 - 70, i * 20 + 200), new Vector2(70, 20), "Join");
        }
    }

    static float updateAccum = 1;
    public static void updateButtons()
    {
        updateAccum += Raylib.GetFrameTime();
        if(updateAccum > 1)
        {
            updateAccum = 0;
            UpdateServerInfos();
            if(!NetworkManager.isLocaterClient)
            {
                NetworkManager.InitLocaterClient();
            }
        }

        buttons[2].lock = NetworkManager.isLocaterClient;
        buttons[1].lock = !NetworkManager.isLocaterClient || (NetworkManager.isClient && !NetworkManager.isServer);
        if(buttons[2].lock)
        {
            locaterServerToolTip.show = true;
            locaterServerToolTip.Update();
        }
        else
        {
            locaterServerToolTip.show = false;
        }

        if(buttons[1].lock)
        {
            if(NetworkManager.isClient)
            {
                serverToolTip.SetText("In a Server");
            }
            else
            {
                serverToolTip.SetText("Needs Locater\nServer Connection");
            }
            serverToolTip.show = true;
            serverToolTip.Update();
        }
        else
            serverToolTip.show = false;

        if(buttons[0].IsButtonClicked())
        {
            UI.IsMutiMenuOpen = false;

        }
        if(buttons[1].IsButtonClicked())
        {
            if(!NetworkManager.isServer && !NetworkManager.isClient)
            {
                NetworkManager.InitServer();
                buttons[1].text = "Stop Server";
            }
            else
            {
                NetworkManager.server.Stop();
                NetworkManager.client.Stop();
                buttons[1].text = "Start Server";
            }
        }
        if(buttons[2].IsButtonClicked())
        {
            NetworkManager.InitLocaterServer();
        }

        for (int i = 0; i < LocaterServer.MaxClients; i++)
        {
            if(NetworkManager.isClient)
            {
                if(LocaterServer.ips[i].equals(NetworkManager.client.ip) && LocaterServer.ports[i] == NetworkManager.client.port)
                {
                    buttons[i + 3].text = "Leave";
                }
            }

            buttons[i + 3].lock = NetworkManager.isServer;

            if(buttons[i + 3].IsButtonClicked())
            {
                if(!NetworkManager.isClient)
                {
                    String ip = NetworkManager.locaterServer.ips[i];
                    int port = NetworkManager.locaterServer.ports[i];
                    NetworkManager.InitClient(ip, port);
                    buttons[i + 3].text = "Leave";
                }
                else
                {
                    if(NetworkManager.isServer)
                    {
                        break;
                    }
                    System.out.println("Disconnect");
                    NetworkManager.client.Disconnect();
                    buttons[i + 3].text = "Join";
                }
            }
        }

    }

    public static void RenderButtons()
    {

        for (int i = 0; i < buttons.length; i++)
        {
            if(buttons[i] == null)
                continue;

            if(i < 3)
            {
                buttons[i].DrawButton();
            }
            else
            {
                if(!LocaterServer.ips[i - 3].isEmpty())
                {
                    buttons[i].DrawButton();
                }
            }
        }

        for (int i = 0; i < LocaterServer.MaxClients; i++)
        {
            if(LocaterServer.ips[i].isEmpty())
                continue;

            Raylib.DrawText(LocaterServer.ips[i] + " Players: " + LocaterServer.players[i], 640, 20 * i + 200, 20, BLACK);
        }

        locaterServerToolTip.Draw();
        serverToolTip.Draw();
    }

    public static void UpdateServerInfos()
    {
        if(NetworkManager.isLocaterClient)
        {
            for (int i = 0; i < LocaterServer.MaxClients; i++)
            {
                NetworkManager.locaterClient.SendPacket(new ServerInfoRequestPacket(i));
            }
        }
    }
}
