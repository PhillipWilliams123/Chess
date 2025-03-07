package com.example.Chess.Chess;
import static com.raylib.Raylib.*;
import com.raylib.Raylib.Image;

/**
 * @author ericl, phillip, daniel
 */
public abstract class ChessPiece
{
    private Image image;

    public void DrawPiece()
    {
        image = LoadImage(GetImageLocation());

    }

    public abstract String GetImageLocation();
    public abstract boolean ValidMove()

}
