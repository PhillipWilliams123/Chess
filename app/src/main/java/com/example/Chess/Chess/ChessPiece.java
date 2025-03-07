package com.example.Chess.Chess;
import static com.raylib.Raylib.*;
import com.example.Chess.Vector2;
import com.raylib.Raylib;
import com.raylib.Raylib.Texture;
import static com.raylib.Colors.*;

/**
 * @author ericl, phillip, daniel
 */
public abstract class ChessPiece
{
    public int id;
    public Vector2 position;
    private Texture image;

    public void LoadImageToPiece()
    {
        image = LoadTexture(GetImageLocation());
    }

    public void DrawPiece()
    {
        DrawTexture(image, 0, 0, WHITE);
    }

    /**
     * Will get the image location for the piece. Is abstract so each specific piece has its own implementation and can be used here
     * @return the image location
     */
    public abstract String GetImageLocation();

    /**
     * Checks if the piece can make a valid move to a position
     * @param pos the position to move to
     * @return true if it can make the move, false if it cannot make the move
     */
    public abstract boolean ValidMove(Vector2 pos);

}
