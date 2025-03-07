package com.example.Chess.Chess;

import com.example.Chess.Globals;

public class Rook extends ChessPiece {

    @Override
    public String GetImageLocation()
    {
        return Globals.ResourceDirectory + "/Images/King.png";
        
    }
}