package com.example.Chess;
import com.example.Chess.Chess.*;
import com.example.Chess.Network.NetworkManager;
import com.example.Chess.Chess.Rook;
import com.example.Chess.Network.NetworkManager;
import com.example.Chess.Network.Packets.PingPacket;
import com.example.Chess.Rendering.Renderer;
import com.example.Chess.UI.MutiUi;
import com.example.Chess.UI.QuantumUiButton;
import com.example.Chess.UI.UI;
import com.example.Chess.UI.UiMenu;
import com.raylib.Raylib;
import org.bytedeco.javacpp.BytePointer;

import static com.example.Chess.UI.QuantumUiButton.IsQuantumUiOpen;
import static com.example.Chess.UI.UI.IsMenuOpen;
import static com.example.Chess.UI.UI.IsMutiMenuOpen;
import static com.raylib.Colors.*;
import static com.raylib.Raylib.*;
import static com.raylib.Raylib.WindowShouldClose;

import com.raylib.Raylib.Sound;

public class App
{
    public static Renderer mainRenderer;

    public static void main(String[] args)
    {
        //starts our window (should move to its own file for the setup)
        InitWindow(Globals.ChessWidth+Globals.UIWidth, Globals.ScreenHeight, "Chess");
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
        CloseAudioDevice();
        CloseWindow();
        NetworkManager.CleanUp();
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
        Globals.SoundDirectory = Globals.ResourceDirectory + "Sounds/";
    }

    public static void Initialize()
    {
        InitAudioDevice();

        //initialize any systems
        GameState.Init();
        ChessBoard.Init();
        ChessSound.Initialize();
        UiMenu.Initialize();
        UI.Initialize();
        MutiUi.Initialize();
        NetworkManager.Init();

        NetworkManager.InitLocaterClient();
        QuantumUiButton.Initialize();
        mainRenderer.Draw2d = true;
    }

    public static void Update()
    {
        //Main update loop code

        NetworkManager.Update();
        UI.updateButtons();
        //if(IsQuantumUiOpen){
        //    QuantumUiButton.updateButtons();
        //}

        if(Interaction.currentSelectedPiece == -1)
            GameState.CheckKingStatus();
    }

    public static void Render()
    {
        //any drawing commands should be put in here or rendering code

        if(!mainRenderer.Draw2d)
            mainRenderer.Draw3DChessBoard();
        mainRenderer.DrawChessBoard();
        mainRenderer.DrawPieces();
        Interaction.Update();
        UI.RenderButtons();
        if(IsQuantumUiOpen){
            QuantumUiButton.RenderButtons();
        }



    }
}
