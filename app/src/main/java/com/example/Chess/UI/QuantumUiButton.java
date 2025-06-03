package com.example.Chess.UI;

import com.example.Chess.Chess.ChessBoard;
import com.raylib.Raylib;

import com.example.Chess.Vector2;
import static com.example.Chess.Chess.ChessBoard.GetChessPieceAtPos;
import static com.raylib.Colors.LIGHTGRAY;
import static com.raylib.Raylib.DrawRectangle;

public class QuantumUiButton {
    public Vector2 position;
    public static boolean IsQuantumUiOpen = false;
    public static UiButton[] Quantumbuttons;

    public static void Initialize() {
        int mouseX = (int) Raylib.GetMousePosition().x();
        int mouseY = (int) Raylib.GetMousePosition().y();
        Quantumbuttons = new UiButton[6];
        Quantumbuttons[0] = new UiButton(new Vector2(mouseX, mouseY + 50), new Vector2(200, 100), "Quantum Action");
        Quantumbuttons[1] = new UiButton(new Vector2(mouseX, mouseY), new Vector2(200, 50), "Quit");
    }

    public static void updateButtons() {
        if (Quantumbuttons[0].IsButtonClicked()) {
//Quantum Action
        }

        if (Quantumbuttons[1].IsButtonClicked()) {

            IsQuantumUiOpen = false;
        }
    }

    public static boolean IsMouseOverPiece() {
        int mouseX = (int) Raylib.GetMousePosition().x();
        int mouseY = (int) Raylib.GetMousePosition().y();

        if (GetChessPieceAtPos(new Vector2(mouseX, mouseY)) != null) {
            return true;
        }

        return false;
    }

    public static void CheckRightClick() {
        if (Raylib.IsMouseButtonPressed(Raylib.MOUSE_BUTTON_RIGHT)) {
            IsQuantumUiOpen = !IsQuantumUiOpen; // toggle UI
            if (IsQuantumUiOpen) {
                FindButtonslocation(); // set position only once
            }
        }
    }
public static int CheckOutofBound (int num){
        if(num+150 > 640){
            return num - 150;
        }
        return num;
}
    public static void FindButtonslocation() {
        int mouseX = (int) Raylib.GetMousePosition().x();
        int mouseY = (int) Raylib.GetMousePosition().y();
        Quantumbuttons[0] = new UiButton(new Vector2(mouseX, CheckOutofBound(mouseY) + 50), new Vector2(200, 100), "Quantum Action");
        Quantumbuttons[1] = new UiButton(new Vector2(mouseX, CheckOutofBound(mouseY)), new Vector2(200, 50), "Quit");
    }

    public static void RenderButtons() {
        if (IsQuantumUiOpen) {
            for (UiButton button : Quantumbuttons) {
                if (button != null) {
                    button.DrawButton();
                }
            }
        }
    }
}



