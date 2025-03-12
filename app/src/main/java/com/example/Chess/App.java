package com.example.Chess;
import com.example.Chess.Chess.ChessBoard;
import com.example.Chess.Chess.Rook;
import com.example.Chess.Rendering.Renderer;
import org.bytedeco.javacpp.BytePointer;

import static com.raylib.Colors.*;
import static com.raylib.Raylib.*;

public class App
{
    public static Renderer mainRenderer;

    public static void main(String[] args)
    {
        //starts our window (should move to its own file for the setup)
        InitWindow(Globals.ScreenWidth, Globals.ScreenHeight, "Chess");
        SetTargetFPS(60);

        //create any classes and resource management
        PreInitialize();
        //run setup of any classes and systems
        Initialize();

        while (!WindowShouldClose())
        {
            //run our game loop
            Update();

            //This tells Raylib that we want to start drawing something to the screen
            BeginDrawing();
            //sets the background to a color which are constants starting with RAY"color"
            ClearBackground(RAYWHITE);

            //run our render code
            Render();

            //Tells raylib that we have stopped drawing stuff
            EndDrawing();
        }
        
        //does some cleanup
        CloseWindow();
    }

    public static void PreInitialize()
    {
        mainRenderer = new Renderer();

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
    }

    public static void Initialize()
    {
        //initialize any systems
        ChessBoard.Init();
    }

    public static void Update()
    {
        //Main update loop code
    }

    public static void Render()
    {
        //any drawing commands should be put in here or rendering code

        mainRenderer.DrawChessBoard();
        mainRenderer.DrawPieces();
    }
}
