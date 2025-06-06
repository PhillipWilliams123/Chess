package com.example.Chess.UI;

import com.example.Chess.Chess.ChessBoard;
import com.example.Chess.Vector2;
import com.raylib.Raylib;
import static com.example.Chess.UI.UI.IsMenuOpen;
import static com.example.Chess.UI.UI.IsMutiMenuOpen;
import static com.example.Chess.UI.UiButton.CheckIsSoundenabled;

public class UiMenu {
    public static UiButton[] MenuButtons;
    public static boolean Gamestart = false;
    public static boolean IsSoundenabled = true;
    public static void Initialize() {
        MenuButtons = new UiButton[6];
        MenuButtons[0] = new UiButton(new Vector2(640, 0), new Vector2(360, 100), "Back");
        MenuButtons[1] = new UiButton(new Vector2(640, 100), new Vector2(360, 100), "Disable Sound");
        MenuButtons[2] = new UiButton(new Vector2(640, 200), new Vector2(180, 100), "Surrender");
        MenuButtons[3] = new UiButton(new Vector2(820, 200), new Vector2(180, 100), "Offer Draw");



    }
    public static void updateButtons(){
        if(MenuButtons[0].CheckStartButtonClicked())
        {
            IsMenuOpen = false;

        }
        if(MenuButtons[1].CheckStartButtonClicked())
        {
            CheckIsSoundenabled();
        }
        if(MenuButtons[3].CheckStartButtonClicked())
        {

        }

    }
    public static void RenderButtons()
    {
        for(UiButton button : MenuButtons)
        {
            if (button != null) {
                button.DrawButton();
            }
        }
    }
    }
