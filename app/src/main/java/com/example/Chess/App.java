package com.example.Chess;

import com.example.Chess.Chess.ChessBoard;
import com.example.Chess.Chess.Rook;
import com.example.Chess.Network.NetworkManager;
import com.example.Chess.Chess.Pawn;
import com.example.Chess.Chess.Queen;
import com.example.Chess.Chess.Rook;
import com.example.Chess.Network.NetworkManager;
import com.example.Chess.Network.Packets.PingPacket;
import com.example.Chess.Rendering.Renderer;
//import com.example.Chess.UI.UI;
import com.raylib.Raylib;
import org.bytedeco.javacpp.BytePointer;

import static com.raylib.Colors.*;
import static com.raylib.Raylib.*;

public class App {

    public static Renderer mainRenderer;

    public static void main(String[] args) {
        //starts our window (should move to its own file for the setup)
        InitWindow(Globals.ChessWidth + Globals.UIWidth, Globals.ScreenHeight, "Chess");
        SetTargetFPS(100000);

        //create any classes and resource management
        PreInitialize();
        //run setup of any classes and systems
        Initialize();

        while (!WindowShouldClose()) {
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
        NetworkManager.CleanUp();
    }

    public static void PreInitialize() {
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

    public static void Initialize() {
        //initialize any systems
        ChessBoard.Init();
        ChessBoard.InitStandardGame();
    }

    public static void Update() {
        //Main update loop code

        //for server and client testing
        if (Raylib.IsKeyPressed(KEY_S)) {
            //starts the server
            if (!NetworkManager.initialized) {
                NetworkManager.InitServer();
            }
        }
        if (Raylib.IsKeyPressed(KEY_C)) {
            if (!NetworkManager.initialized) {
                NetworkManager.InitClient();
            }
        }

        if (NetworkManager.initialized) {
            NetworkManager.Update();
        }

        /*
        UI.Update(); // Add this line
        if (NetworkManager.initialized) {
            NetworkManager.Update();
        }
         */
    }

    public static void Render() {
        //any drawing commands should be put in here or rendering code

        mainRenderer.DrawChessBoard();
        mainRenderer.DrawPieces();
        //UI.Render();
        Interaction.Update();
    }
}
