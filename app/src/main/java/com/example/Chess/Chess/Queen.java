package com.example.Chess.Chess;
import com.example.Chess.Vector2;

public class Queen extends ChessPiece
{

    //Needs a constructor for the position

    @Override
    public String GetImageLocation() {
        return "";
    }

    @Override
    public boolean TryMove(Vector2 pos) {
        return false;
    }

    @Override
    public ChessPiece Copy() {
        return null;
    }
}
