package com.example.Chess;
import static com.raylib.Colors.*;
import static com.raylib.Raylib.*;

public class App
{
    public static void main(String[] args)
    {
        //starts our window (should move to its own file for the setup)
        InitWindow(1280, 720, "Chess");
        SetTargetFPS(60);

        while (!WindowShouldClose())
        {            
            //This tells Raylib that we want to start drawing something to the screen
            BeginDrawing();
            //sets the background to a color which are constants starting with RAY"color"
            ClearBackground(RAYWHITE);
            
            DrawText("Quantum Chess", 0, 0, 1, VIOLET);
            DrawFPS(20, 20);
            
            //Tells raylib that we have stopped drawing stuff
            EndDrawing();
        }
        
        //does some cleanup
        CloseWindow();
    }
}
