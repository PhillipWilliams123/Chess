package com.example.Chess.Chess;

import com.example.Chess.Vector2;
import com.example.Chess.Globals;

public class King extends ChessPiece {

    public King(Vector2 position, boolean side) {
        this.side = side;
        this.position = position;
    }

    @Override
    public String GetImageLocation() {
        return Globals.ImageDirectory + Globals.PieceImages[5 + (6 * (side ? 1 : 0))];
    }

    @Override
    public boolean TryMove(Vector2 pos) {
        if (!CheckMove(pos)) {
            return false;
        }
        
        // Check if the move would capture a piece
        if (ChessBoard.GetPieceIdAtPos(pos) != -1) {
            if (!TryTakePiece(pos)) {
                return false;
            }
        }
        
        SetToPosition(pos);
        return true;
    }

    @Override
    public boolean CheckMove(Vector2 pos) {
        // King can move exactly one square in any direction
        double dx = Math.abs(pos.x - position.x);
        double dy = Math.abs(pos.y - position.y);
        
        // Normal king move (1 square in any direction)
        if ((dx <= 1 && dy <= 1) && (dx + dy > 0)) {
            // Check if target position is empty or contains opponent's piece
            int targetId = ChessBoard.GetPieceIdAtPos(pos);
            //If the square is empty or that there is an opponent occupying that space
            return targetId == -1 || ChessBoard.chessPieces[targetId].side != this.side;
        }
        
        //TODO: Add castling logic here
        
        return false;
    }

    @Override
    public ChessPiece Copy() {
        return new King(this.position, this.side);
    }
}
