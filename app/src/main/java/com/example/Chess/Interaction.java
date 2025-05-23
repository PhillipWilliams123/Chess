package com.example.Chess;

import com.example.Chess.Chess.ChessBoard;
import com.example.Chess.Network.NetworkManager;
import com.example.Chess.Network.Packets.PieceMovePacket;
import com.raylib.Raylib;

import static com.raylib.Colors.*;

public class Interaction
{
    private static int currentSelectedPiece = -1;
    private static Vector2 currentSelectedPosition;
    public static boolean disableInteraction;

    public static void Update()
    {
        if(disableInteraction)
            return;

        //convert mouse position to the board position
        Vector2 mousePos = new Vector2(Raylib.GetMousePosition().x(), Raylib.GetMousePosition().y());

        mousePos = Vector2.Mul(Vector2.Div(mousePos, new Vector2(Globals.ChessWidth, Globals.ScreenHeight)), ChessBoard.boardSize);
        mousePos = Vector2.Floor(mousePos);

        HighlightSpot();

        if(Raylib.IsMouseButtonPressed(Raylib.MOUSE_BUTTON_LEFT))
        {
            if(currentSelectedPiece == -1)
            {
                currentSelectedPosition = mousePos;
                currentSelectedPiece = ChessBoard.GetPieceIdAtPos(mousePos);
            }
            else
            {
                ChessBoard.chessPieces[currentSelectedPiece].position = currentSelectedPosition;
                if(ChessBoard.chessPieces[currentSelectedPiece].TryMove(mousePos))
                {
                    if(NetworkManager.initialized && NetworkManager.isClient)
                    {
                        PieceMovePacket packet = new PieceMovePacket(ChessBoard.chessPieces[currentSelectedPiece].position, currentSelectedPiece);
                        NetworkManager.client.SendPacket(packet);
                    }
                }
                currentSelectedPiece = -1;
            }
        }

        //if we have a selected piece, put it at our mouse and show the possible moves
        if(currentSelectedPiece != -1)
        {
            //draw the highlighted spots
            ChessBoard.chessPieces[currentSelectedPiece].position = currentSelectedPosition;
            ChessBoard.chessPieces[currentSelectedPiece].DrawPossibleMoves();
            //if its in the chessboard draw at the mouse position
            if(Raylib.GetMousePosition().x() < Globals.ChessWidth - 1)
            {
                ChessBoard.chessPieces[currentSelectedPiece].position = mousePos;
            }
        }
    }

    private static void HighlightSpot()
    {
        //draw a square of a color to show what position is highlighted
        Vector2 mousePos = new Vector2(Raylib.GetMousePosition().x(), Raylib.GetMousePosition().y());

        //check if its out of the board
        if(mousePos.x > Globals.ChessWidth - 1)
            return;

        mousePos = Vector2.Mul(Vector2.Div(mousePos, new Vector2(Globals.ChessWidth, Globals.ScreenHeight)), ChessBoard.boardSize);

        Raylib.Color highColor = GREEN;
        highColor.a((byte)(65 * (Math.sin(Raylib.GetTime() * 5) + 1)));

        double xScale = Globals.ChessWidth / (double)ChessBoard.boardSize;
        double yScale = Globals.ScreenHeight / (double)ChessBoard.boardSize;
        mousePos = Vector2.Floor(mousePos);
        Raylib.DrawRectangle((int) (mousePos.x * xScale), (int) (mousePos.y * yScale), (int) xScale, (int) yScale, highColor);
    }
}
