package com.example.Chess;
import com.example.Chess.Chess.ChessBoard;
import com.example.Chess.Rendering.Renderer;
import org.bytedeco.javacpp.BytePointer;

import static com.raylib.Colors.*;
import static com.raylib.Raylib.*;

public class App
{
    public static void main(String[] args)
    {
        //Important classes
        Renderer mainRenderer = new Renderer();

        //starts our window (should move to its own file for the setup)
        InitWindow(Globals.ScreenWidth, Globals.ScreenHeight, "Chess");
        SetTargetFPS(60);

        //get where the game is running
        BytePointer workingDirectory = GetWorkingDirectory();
        //set it to our global variable
        Globals.RunDirectory = workingDirectory.getString();
        //clean up the memory (NEEDED)
        workingDirectory.close();
        //set the resources directory (This might need to change if the game is run outside of this project)
        Globals.ResourceDirectory = Globals.RunDirectory + "/src/main/resources/";
        //set the images directory
        Globals.ImageDirectory = Globals.ResourceDirectory + "Images/";

        //Initialize systems
        ChessBoard.Init();

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
