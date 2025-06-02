package com.example.Chess.UI;

import com.example.Chess.Chess.ChessBoard;
import com.example.Chess.Vector2;
import com.raylib.Raylib;
import static com.example.Chess.UI.UI.IsMenuOpen;
import static com.example.Chess.UI.UI.IsMutiMenuOpen;
import static com.example.Chess.UI.UiButton.CheckIsSoundenabled;

public class MutiUi {
    public static UiButton[] MutiMenuButtons;
    public static boolean Gamestart = false;
    public static boolean IsSoundenabled = true;
    public static void Initialize() {
        MutiMenuButtons = new UiButton[6];
        MutiMenuButtons[0] = new UiButton(new Vector2(640, 0), new Vector2(360, 100), "Quit MutiMenu");
        MutiMenuButtons[1] = new UiButton(new Vector2(640, 100), new Vector2(360, 100), "Disable Sound");
        MutiMenuButtons[2] = new UiButton(new Vector2(640, 200), new Vector2(360, 100), "Option");
        MutiMenuButtons[3] = new UiButton(new Vector2(640, 300), new Vector2(180, 100), "Surrender");
        MutiMenuButtons[4] = new UiButton(new Vector2(820, 300), new Vector2(180, 100), "Offer Draw");



    }
    public static void updateButtons(){
        if(MutiMenuButtons[0].CheckStartButtonClicked())
        {
            IsMutiMenuOpen = false;

        }
        if(MutiMenuButtons[1].CheckStartButtonClicked())
        {


        }
        if(MutiMenuButtons[2].CheckStartButtonClicked())
        {

        }

        if(MutiMenuButtons[3].CheckStartButtonClicked())
        {

        }
        if(MutiMenuButtons[4].CheckStartButtonClicked())
        {

        }


    }
    public static void RenderButtons()
    {

        for(UiButton button : MutiMenuButtons)
        {
            if (button != null) {
                button.DrawButton();
            }
        }
    }
}
