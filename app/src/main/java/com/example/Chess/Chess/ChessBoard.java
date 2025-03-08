package com.example.Chess.Chess;

import com.example.Chess.Globals;
import com.example.Chess.Vector2;

import java.util.LinkedList;
import java.util.Queue;

public class ChessBoard
{
    public static final int boardSize = 8;
    public static ChessPiece[] chessPieces;
    /**
     * The id of the chessPiece into the chessPiece array as its position on the board will probably not match the position in the array
     */
    public static int[] chessPieceIds;
    /**
     * Ids we can use when a piece is "destroyed" or when we want to add another piece
     */
    public static Queue<Integer> freeChessPieceIds;

    /**
     * Initializes the chess board
     */
    public static void Init()
    {
        chessPieces = new ChessPiece[boardSize * boardSize];
        chessPieceIds = new int[boardSize * boardSize];
        freeChessPieceIds = new LinkedList<>();

        //initialize each piece to an empty piece
        for (int i = 0; i < boardSize * boardSize; i++)
        {
           chessPieces[i] = new EmptyPiece();
           freeChessPieceIds.add(i);
        }
    }

    /**
     * Will take in a position and return an index into a array based on positions (chessPieceIds)
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
        if(index == -1)
            return new EmptyPiece();

        return chessPieces[index];
    }

    /**
     * Sets the id of a chess piece at the position
     * @param pos the position to set it at
     * @param id the id to set it at
     */
    public static void SetPieceIdAtPos(Vector2 pos, int id)
    {
        int index = PosToIndex(pos);
        if(index == -1)
            return;
        chessPieceIds[index] = id;
    }

    /**
     * Gets the id of a chess piece at the position, use to access a piece in the chessPiece array
     * @param pos the position to set it at
     * @return -1 if the spot is empty or out of bounds, or the pieces id
     */
    public static int GetPieceIdAtPos(Vector2 pos)
    {
        int index = PosToIndex(pos);
        if(index == -1)
            return -1;
        return chessPieceIds[index];
    }

    /**
     * Scales a position into an 8, 8 space, from window space
     * @return The scaled vector
     */
    public static Vector2 ScalePosToBoardSpace(Vector2 pos)
    {
        return Vector2.Mul(pos, Vector2.Div(new Vector2(Globals.ScreenWidth, Globals.ScreenHeight), boardSize));
    }

    /**
     * Adds a piece onto the board, will do nothing if it's not a valid spot to add a piece
     * @param piece the chessPiece to add with accept anything that implements the chessPiece class
     */
    public static void AddPiece(ChessPiece piece)
    {
        //check if it's a valid spot on the board
        if(!PosInBounds(piece.position))
            return;

        //check if there's already a piece there
        ChessPiece checkPiece = GetChessPieceAtPos(piece.position);
        if(checkPiece.id != -1)
            return;

        //we are good to add a piece
        int index = freeChessPieceIds.poll();
        chessPieces[index] = piece.Copy();
        chessPieces[index].id = index;
        chessPieces[index].Init();
        SetPieceIdAtPos(piece.position, index);
    }

    /**
     * Will set a piece on the position to empty effectively "deleting it"
     * @param position the position of the piece to be "deleted"
     */
    public static void DeletePiece(Vector2 position)
    {
        //check if it's a valid spot on the board
        if(!PosInBounds(position))
            return;

        int index = PosToIndex(position);
        int id = GetChessPieceAtPos(position).id;
        freeChessPieceIds.add(id);
        chessPieces[index] = new EmptyPiece();
    }
}
