package com.example.Chess.UI;


import static com.raylib.Colors.BLACK;
import static com.raylib.Colors.LIGHTGRAY;
import com.raylib.Raylib;
import static com.raylib.Raylib.*;
public class UI {



    // Call this in your main game loop's draw method
    public void DrawStartButton() {
        int x1 = 640;
        int y1 = 640;
        int x2 = 1000;
        int y2 = 680;  // Add height for clickable area (40 px)

        // Draw button rectangle
        DrawRectangle(x1, y1, x2 - x1, y2 - y1, LIGHTGRAY);

        // Draw button border
        DrawRectangleLines(x1, y1, x2 - x1, y2 - y1, BLACK);

        // Draw button text centered
        int textWidth = MeasureText("Start Game", 20);
        int textX = x1 + ((x2 - x1) - textWidth) / 2;
        int textY = y1 + ((y2 - y1) - 20) / 2;
        DrawText("Start Game", textX, textY, 20, BLACK);
    }

    // Call this in your main game loop's update method to detect click
public static boolean CheckStartButtonClicked() {
    int mouseX = (int) Raylib.GetMousePosition().x();
    int mouseY = (int) Raylib.GetMousePosition().y();
    if (Raylib.IsMouseButtonPressed(Raylib.MOUSE_BUTTON_LEFT)) {
        if (mouseX >= 640 && mouseX <= 1000 && mouseY >= 640 && mouseY <= 680) {
            return true;
        }
    }
    return false;
}
}



