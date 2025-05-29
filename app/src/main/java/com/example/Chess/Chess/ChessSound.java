package com.example.Chess.Chess;

import com.example.Chess.Globals;
import com.raylib.Raylib;

import static com.raylib.Raylib.LoadSound;
import static com.raylib.Raylib.PlaySound;

public class ChessSound {

    public static Raylib.Sound CaptureSound;
    public static Raylib.Sound MoveSound;
    public static Raylib.Sound NotifySound;
    public static void Initialize() {

       CaptureSound =   LoadSound(Globals.SoundDirectory + Globals.PieceSounds[0]);

      MoveSound =   LoadSound(Globals.SoundDirectory + Globals.PieceSounds[1]);

        NotifySound =     LoadSound(Globals.SoundDirectory + Globals.PieceSounds[2]);


    }
    public static void PlayCapture(){
         PlaySound (CaptureSound);
    }
    public static void PlayMove(){
        PlaySound (MoveSound);
    }

    public static void PlayNotify(){
        PlaySound (NotifySound);
    }
}
