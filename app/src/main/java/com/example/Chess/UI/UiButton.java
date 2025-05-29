package com.example.Chess.UI;

import com.example.Chess.Vector2;
import com.raylib.Raylib;

import static com.raylib.Colors.*;
import static com.raylib.Raylib.*;
import static com.raylib.Raylib.DrawText;

public class UiButton {

    public Vector2 position;
    public Vector2 size;
    public String text;

    public UiButton(Vector2 position, Vector2 size, String text)
    {
        this.position = position;
        this.size = size;
        this.text = text;
    }

    public void DrawButton() {
        // Draw button rectangle
        DrawRectangle((int) position.x, (int) position.y, (int) size.x, (int) size.y, LIGHTGRAY);
        int mouseX = (int) Raylib.GetMousePosition().x();
        int mouseY = (int) Raylib.GetMousePosition().y();
            if (mouseX >= position.x && mouseX <= position.x + size.x && mouseY >= position.y && mouseY <= position.y + size.y) {

                DrawRectangle((int) position.x, (int) position.y, (int) size.x, (int) size.y, DARKGRAY);
            }
        // Draw button border
        DrawRectangleLines((int) position.x, (int) position.y, (int) size.x, (int) size.y, BLACK);

        // Draw button text centered
        int textWidth = MeasureText(text, 20);
        int textX = (int)position.x + ((int)size.x - textWidth) / 2;
        int textY = (int)position.y + ((int)size.y - 20) / 2;
        DrawText(text, textX, textY, 20, BLACK);
    }

    public boolean CheckStartButtonClicked() {
        int mouseX = (int) Raylib.GetMousePosition().x();
        int mouseY = (int) Raylib.GetMousePosition().y();
        if (Raylib.IsMouseButtonPressed(Raylib.MOUSE_BUTTON_LEFT)) {
            if (mouseX >= position.x && mouseX <= position.x + size.x && mouseY >= position.y && mouseY <= position.y + size.y) {

                return true;
            }
        }
        return false;
    }
}
