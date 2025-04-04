package com.example.Chess.Rendering;

import com.example.Chess.Chess.ChessBoard;
import com.example.Chess.Globals;
import com.raylib.Raylib;

import static com.raylib.Colors.*;

/**
 * @author ericl
 */
public class Renderer {

    /**
     * Draws the checkerboard pattern
     */
    public void DrawChessBoard()
    {
        //draw the chess board using two for loops
        //where we draw a color if the index is even and another color if its odd
        //creating a checkerboard pattern

        //the scale for the screen
        double xScale = Globals.ScreenWidth / (double)ChessBoard.boardSize;
        double yScale = Globals.ScreenHeight / (double)ChessBoard.boardSize;
        for (int x = 0; x < ChessBoard.boardSize; x++) {
            for (int y = 0; y < ChessBoard.boardSize; y++) {
                if ((x + y) % 2 != 0) {
                    //some other color
                    Raylib.DrawRectangle((int) Math.floor(x * xScale), (int) Math.floor(y * yScale), (int) xScale + 1, (int) yScale + 1, Globals.BoardColors[0]);
                } else {
                    //some color
                    Raylib.DrawRectangle((int) Math.floor(x * xScale), (int) Math.floor(y * yScale), (int) xScale + 1, (int) yScale + 1, Globals.BoardColors[1]);
                }
            }
        }
    }

    /**
     * Will draw the chess pieces
     */
    public void DrawPieces()
    {
        for (int i = 0; i < ChessBoard.chessPieces.length; i++)
        {
            if(ChessBoard.chessPieces[i].id == -1)
                continue;

            ChessBoard.chessPieces[i].DrawPiece();
        }
    }
}
