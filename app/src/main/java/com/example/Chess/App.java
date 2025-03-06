package com.example.Chess;
import com.example.Chess.Rendering.Renderer;

import static com.raylib.Colors.*;
import static com.raylib.Raylib.*;

public class App
{
    public static void main(String[] args)
    {
        //Important classes
        Renderer mainRenderer = new Renderer();

        //starts our window (should move to its own file for the setup)
        InitWindow(640, 480, "Chess");
        SetTargetFPS(60);

        while (!WindowShouldClose())
        {            
            //This tells Raylib that we want to start drawing something to the screen
            BeginDrawing();
            //sets the background to a color which are constants starting with RAY"color"
            ClearBackground(RAYWHITE);
            
            DrawText("Quantum Chess", 0, 0, 1, VIOLET);
            DrawFPS(20, 20);
            
            mainRenderer.DrawChessBoard();

            //Tells raylib that we have stopped drawing stuff
            EndDrawing();
        }
        
        //does some cleanup
        CloseWindow();
    }
}
