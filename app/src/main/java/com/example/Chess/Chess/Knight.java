package com.example.Chess.Chess;

import com.example.Chess.Vector2;
import com.example.Chess.Globals;

public class Knight extends ChessPiece {

    public Knight(Vector2 position, boolean side) {
        this.side = side;
        this.position = position;
    }

    @Override
    public String GetImageLocation() {
        return Globals.ImageDirectory + Globals.PieceImages[1 + (6 * (side ? 1 : 0))];
    }

    @Override
    public boolean TryMove(Vector2 pos) {
        //Checks if it is L-Shaped and not on friendly piece
        if (CheckMove(pos) == false) {
            return false;
        }

        // Check if the position is not empty
        if (ChessBoard.GetPieceIdAtPos(pos) != -1) {
            //Checks if the If the piece belongs to the opponent
            //If so, the knight removes the opponent's piece from the board
            if (TryTakePiece(pos) == false) {
                //Executes if capture attempt fails
                return false;
            }
        }

        
        //Either moving to an empty square or successfully capturing an opponent's piece
        SetToPosition(pos);
        return true;
    }

    @Override
    public boolean CheckMove(Vector2 pos) {
        // Calculate the absolute differences in x and y coordinates between the position it tries to get to and the knight's current position
        double dx = Math.abs(pos.x - position.x);
        double dy = Math.abs(pos.y - position.y);

        // Knight moves in an L-shape --> 2 horizontal, 1 vertical or 1 horizontal, 2 vertical
        boolean isValidMove = (dx == 2 && dy == 1) || (dx == 1 && dy == 2);

        //If move is no valid (not L-shaped), return false
        if (isValidMove == false) {
            return false;
        }

        //Get the target's position
        int targetId = ChessBoard.GetPieceIdAtPos(pos);
        //Checks if target position is empty or contains opponent's piece
        if (targetId == -1 || ChessBoard.chessPieces[targetId].side != this.side) {
            return true;
        }

        return false;
    }

    @Override
    public ChessPiece Copy() {
        return new Knight(new Vector2(position.x, position.y), side);
    }

    @Override
    public int GetPieceType()
    {
        return 2;
    }
}
