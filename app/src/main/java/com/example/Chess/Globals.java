package com.example.Chess;
import static com.raylib.Colors.*;
import com.raylib.Raylib;

public class Globals
{

    public static final String Version = "v0.15";
    public static final int ChessWidth = 640;
    public static final int ScreenHeight = 640;
    public static final int UIWidth = 360;
  
    //Where the games files should be located and stuff
    public static String RunDirectory;
    public static String ResourceDirectory;
    public static String ImageDirectory;
    public static String SoundDirectory;
    /**
     * List of the piece file names
     * starting with white, and black can be got by adding 6 to the index
     */
    public static String[] PieceImages =
            {
                    "White_Pawn.png",
                    "White_Knight.png",
                    "White_Bishop.png",
                    "White_Rook.png",
                    "White_Queen.png",
                    "White_King.png",
                    "Black_Pawn.png",
                    "Black_Knight.png",
                    "Black_Bishop.png",
                    "Black_Rook.png",
                    "Black_Queen.png",
                    "Black_King.png",
            };
    public static String[]PieceSounds =
            {
                    "capture.mp3",
                    "move.mp3",
                    "notify.mp3",
            };

    public static Raylib.Color[] BoardColors = {BROWN, BEIGE};
    public static Raylib.Color ChessPieceHue = WHITE;

}
