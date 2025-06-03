package com.example.Chess.UI;

import com.example.Chess.Chess.ChessBoard;
import com.example.Chess.Network.LocaterServer;
import com.example.Chess.Network.NetworkManager;
import com.example.Chess.Network.Packets.ServerInfoRequestPacket;
import com.example.Chess.Vector2;
import com.raylib.Raylib;
import static com.example.Chess.UI.UiButton.CheckIsSoundenabled;
import static com.raylib.Colors.*;

public class MutiUi {
    public static UiButton[] buttons;

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
        }

        if(buttons[0].CheckStartButtonClicked())
        {
            UI.IsMutiMenuOpen = false;

        }
        if(buttons[1].CheckStartButtonClicked())
        {
            if(!NetworkManager.isServer && !NetworkManager.isClient)
            {
                NetworkManager.InitServer();
                buttons[1].text = "Stop Server";

                if(!NetworkManager.isLocaterClient)
                {
                    LocaterServer.ips[0] = NetworkManager.server.ip;
                    LocaterServer.ports[0] = NetworkManager.server.port;
                }
            }
            else
            {
                NetworkManager.server.Stop();
                buttons[1].text = "Start Server";
            }
        }
        if(buttons[2].CheckStartButtonClicked())
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

            if(buttons[i + 3].CheckStartButtonClicked())
            {
                if(!NetworkManager.isClient)
                {
                    NetworkManager.InitClient();
                    buttons[i + 3].text = "Leave";
                }
                else
                {
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
