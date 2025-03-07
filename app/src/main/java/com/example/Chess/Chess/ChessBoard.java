package com.example.Chess.Chess;

import com.example.Chess.Vector2;

public class ChessBoard
{
    public static final int boardSize = 8;
    public static ChessPiece[] chessPieces;

    /**
     * Initializes the chess board
     */
    public static void Init()
    {
        chessPieces = new ChessPiece[boardSize * boardSize];

        //initialize each piece to an empty piece
        for (int i = 0; i < boardSize * boardSize; i++)
        {
           chessPieces[i] = new EmptyPiece();
        }

        chessPieces[0] = new Rook();
        System.out.println(chessPieces[0].GetImageLocation());
        chessPieces[0].LoadImageToPiece();
    }

    public static void TestDraw()
    {
        chessPieces[0].DrawPiece();
    }

    /**
     * Will take in a position and return an index into the chess board array
     * @param pos the position from 0-8 and 0-8 on x and y
     * @return the index into the chessPieces array
     */
    public static int PosToIndex(Vector2 pos)
    {
        if(pos.x < 0 || pos.x >= 8 || pos.y < 0 || pos.y >= 8)
            return -1;
        return (boardSize * (int)Math.floor(pos.x)) + (int)Math.floor(pos.y);
    }

    /**
     * Checks if a position is within bounds of the chess board
     * @param pos the position can accept any value
     * @return true if we are within bounds, and false if we are not in bounds
     */
    public static boolean PosInBounds(Vector2 pos)
    {
        if(pos.x < 0 || pos.x >= 8 || pos.y < 0 || pos.y >= 8)
            return false;
        return true;
    }

    /**
     * Will get the chess piece at the position specified in a range of 0-7 and 0-7
     * @param pos the position in a range of 0-7 x and 0-7 y
     * @return will return the chess piece or an empty piece if it is out of bounds or does not exist
     */
    public static ChessPiece GetChessPieceAtPos(Vector2 pos)
    {
        int index = PosToIndex(pos);
        //our index has returned -1 so we return null
        if(index < 0)
            return new EmptyPiece();

        return chessPieces[index];
    }

    public static Vector2 ScalePosToBoardSpace()
    {

    }
}
