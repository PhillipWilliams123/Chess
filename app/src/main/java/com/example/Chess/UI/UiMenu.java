package com.example.Chess.UI;

import com.example.Chess.Network.NetworkManager;
import com.example.Chess.Vector2;

import static com.example.Chess.UI.UI.IsMenuOpen;
import static com.example.Chess.UI.UiButton.CheckIsSoundenabled;

public class UiMenu {
    public static UiButton[] MenuButtons;
    public static boolean Gamestart = false;
    public static boolean IsSoundenabled = true;
    public static void Initialize() {
        MenuButtons = new UiButton[6];
        MenuButtons[0] = new UiButton(new Vector2(640, 0), new Vector2(360, 100), "Back");
        MenuButtons[1] = new UiButton(new Vector2(640, 100), new Vector2(360, 100), "Disable Sound");
        MenuButtons[2] = new UiButton(new Vector2(640, 200), new Vector2(360, 100), "Option");
        MenuButtons[3] = new UiButton(new Vector2(640, 300), new Vector2(180, 100), "Surrender");
        MenuButtons[4] = new UiButton(new Vector2(820, 300), new Vector2(180, 100), "Offer Draw");



    }
    public static void updateButtons(){
        if(MenuButtons[0].IsButtonClicked())
        {
            IsMenuOpen = false;

        }
        if(MenuButtons[1].IsButtonClicked())
        {
            CheckIsSoundenabled();

        }
        if(MenuButtons[2].IsButtonClicked())
        {

        }

        MenuButtons[3].lock = !NetworkManager.isClient;
        MenuButtons[4].lock = !NetworkManager.isClient;
        MenuButtons[3].draw = NetworkManager.isClient;
        MenuButtons[4].draw = NetworkManager.isClient;

        if(MenuButtons[3].IsButtonClicked())
        {

        }
        if(MenuButtons[4].IsButtonClicked())
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
