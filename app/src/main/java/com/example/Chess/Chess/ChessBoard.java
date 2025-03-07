package com.example.Chess.Chess;

import com.example.Chess.Vector2;

public class ChessBoard
{
    public static final int boardSize = 8;
    public ChessPiece[] chessPieces;

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
     *
     * @param pos
     * @return
     */
    public static ChessPiece GetChessPieceAtPos(Vector2 pos)
    {
        int index = PosToIndex(pos);
        if(index < 0)
            return null;


    }
}
