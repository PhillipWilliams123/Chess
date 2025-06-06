package com.example.Chess.Chess;

import com.example.Chess.Vector2;

public class EmptyPiece extends ChessPiece
{

    public EmptyPiece()
    {
        //set the id to negative one to show that this is an empty piece
        id = -1;
        position = new Vector2(-1, -1);
    }

    @Override
    public String GetImageLocation() {
        return "";
    }

    @Override
    public boolean TryMove(Vector2 pos) {
        return false;
    }

    @Override
    public boolean CheckMove(Vector2 pos) {
        return false;
    }

    @Override
    public ChessPiece Copy()
    {
        return new EmptyPiece();
    }

    @Override
    public int GetPieceType() {
        return 0;
    }

}
