package com.example.Chess.UI;




import com.example.Chess.Chess.ChessBoard;
import com.example.Chess.Vector2;
import com.raylib.Raylib;

import static com.example.Chess.Chess.ChessSound.*;
import static com.raylib.Raylib.SetSoundVolume;

public class UI {

    public static UiButton[] buttons;
public static boolean Gamestart = false;
    public static void Initialize()
    {
        buttons = new UiButton[6];
        buttons[0] = new UiButton(new Vector2(640, 0), new Vector2(360, 100), "Start Game");
        buttons[1] = new UiButton(new Vector2(640, 100), new Vector2(360, 100), "Disable Sound");
        buttons[2] = new UiButton(new Vector2(640, 200), new Vector2(360, 100), "Enable Sound");
        buttons[3] = new UiButton(new Vector2(640, 300), new Vector2(180, 100), "Surrender");
        buttons[4] = new UiButton(new Vector2(820, 300), new Vector2(180, 100), "Offer Draw");
        buttons[5] = new UiButton(new Vector2(640, 400), new Vector2(360, 100), "Quit Game");


    }
//    SetSoundVolume(Sound sound, float volume);

    public static void updateButtons(){
        if(buttons[0].CheckStartButtonClicked())
        {
            ChessBoard.Init();
            ChessBoard.InitStandardGame();
        }
        if(buttons[1].CheckStartButtonClicked())
        {
            SetSoundVolume(CaptureSound, 0);
            SetSoundVolume(MoveSound, 0);
            SetSoundVolume(NotifySound, 0);
        }
        if(buttons[2].CheckStartButtonClicked())
        {
            SetSoundVolume(CaptureSound, 100);
            SetSoundVolume(MoveSound, 100);
            SetSoundVolume(NotifySound, 100);
        }
        if(buttons[3].CheckStartButtonClicked())
        {

        }
        if(buttons[4].CheckStartButtonClicked())
        {

        }
        if(buttons[5].CheckStartButtonClicked())
        {
            Raylib.CloseWindow();
        }
    }

    public static void RenderButtons()
    {
        for(UiButton button : buttons)
        {
            if (button != null) {
                button.DrawButton();
            }
        }
    }


}



