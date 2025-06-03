package com.example.Chess.UI;




import com.example.Chess.Chess.ChessBoard;
import com.example.Chess.Vector2;
import com.raylib.Raylib;



public class UI {

    public static UiButton[] buttons;
public static boolean Gamestart = false;
public static boolean IsSoundenabled = true;

    public static boolean IsMenuOpen;
    public static boolean IsMutiMenuOpen;
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
            if(buttons[0].CheckStartButtonClicked())
            {

                ChessBoard.Init();
                ChessBoard.InitStandardGame();

            }

            if(buttons[1].CheckStartButtonClicked())
            {
                IsMenuOpen = true;

            }
            if(buttons[2].CheckStartButtonClicked())
            {
                MutiUi.UpdateServerInfos();
                IsMutiMenuOpen = true;
            }
            if(buttons[3].CheckStartButtonClicked())
            {
                Raylib.CloseWindow();
            }
        }

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
    }


}



