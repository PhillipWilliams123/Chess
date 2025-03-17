package com.example.Chess;
import static com.raylib.Colors.*;
import com.raylib.Raylib;

public class Globals
{
    public static final int ScreenWidth = 640;
    public static final int ScreenHeight = 640;

    //Where the games files should be located and stuff
    public static String RunDirectory;
    public static String ResourceDirectory;
    public static String ImageDirectory;

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

    public static Raylib.Color[] BoardColors = {BROWN, BEIGE};
    public static Raylib.Color ChessPieceHue = WHITE;

}