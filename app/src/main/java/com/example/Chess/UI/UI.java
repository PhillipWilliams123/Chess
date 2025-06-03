package com.example.Chess.UI;




import com.example.Chess.Chess.ChessBoard;
import com.example.Chess.Interaction;
import com.example.Chess.Network.NetworkManager;
import com.example.Chess.Network.Packets.StartGamePacket;
import com.example.Chess.Vector2;
import com.raylib.Raylib;



public class UI {

    public static UiButton[] buttons;
public static boolean Gamestart = false;
public static boolean IsSoundenabled = true;

    public static boolean IsMenuOpen;
    public static boolean IsMutiMenuOpen;

    static ToolTip startGameToolTip = new ToolTip(new Vector2(640, 0), new Vector2(180, 100), "test");

    public static void Initialize()
    {
        buttons = new UiButton[6];
        buttons[0] = new UiButton(new Vector2(640, 0), new Vector2(180, 100), "Start Game");

        buttons[1] = new UiButton(new Vector2(640, 100), new Vector2(360, 100), "Menu");
        buttons[2] = new UiButton(new Vector2(640, 200), new Vector2(360, 100), "Multiplayer");

        buttons[3] = new UiButton(new Vector2(820, 0), new Vector2(180, 100), "Quit Game");



    }
//    SetSoundVolume(Sound sound, float volume);

    public static void updateButtons()
    {
        //lock the start game if we are connected to a server
        buttons[0].lock = NetworkManager.isClient && !NetworkManager.isServer;
        if(NetworkManager.isServer)
        {
            buttons[0].lock = NetworkManager.server.currentClients != 2;
        }

        if(IsMenuOpen)
        {
            UiMenu.updateButtons();
        }
        else if(IsMutiMenuOpen)
        {
            MutiUi.updateButtons();
        }
        else
        {
            if(buttons[0].IsButtonClicked())
            {

                ChessBoard.Init();
                ChessBoard.InitStandardGame();

                if(NetworkManager.isServer)
                {
                    //tell the other client we are the one starting
                    NetworkManager.server.SendPacketAllExclude(new StartGamePacket(true), 0);
                    Interaction.isOurTurn = true;
                }

            }

            if(buttons[1].IsButtonClicked())
            {
                IsMenuOpen = true;

            }
            if(buttons[2].IsButtonClicked())
            {
                MutiUi.UpdateServerInfos();
                IsMutiMenuOpen = true;
            }
            if(buttons[3].IsButtonClicked())
            {
                Raylib.CloseWindow();
            }

            if(buttons[0].lock)
            {
                if(NetworkManager.isClient)
                {
                    startGameToolTip.SetText("Not Server Owner");;
                }
                if(NetworkManager.isServer)
                {
                    startGameToolTip.SetText("Not Enough Players");
                }

                startGameToolTip.show = true;
            }
            else
                startGameToolTip.show = false;
        }

        startGameToolTip.Update();

    }

    public static void RenderButtons()
    {
        if(IsMenuOpen)
        {
            UiMenu.RenderButtons();
        }
        else if(IsMutiMenuOpen)
        {
            MutiUi.RenderButtons();
        }
        else
        {
            for(UiButton button : buttons)
            {
                if (button != null) {
                    button.DrawButton();
                }
            }
        }

        startGameToolTip.Draw();
    }


}



