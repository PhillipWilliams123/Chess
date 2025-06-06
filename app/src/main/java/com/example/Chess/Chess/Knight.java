package com.example.Chess.Chess;

import com.example.Chess.Vector2;
import com.example.Chess.Globals;
import com.example.Chess.Rules.OriginalRules;
import static com.example.Chess.Rules.OriginalRules.isInCheckValidating;

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
        // First check basic movement rules
        if (!CheckMove(pos)) {
            return false;
        }

        // Check if the position is not empty
        if (ChessBoard.GetPieceIdAtPos(pos) != -1) {
            // Check if the piece belongs to the opponent
            if (!TryTakePiece(pos)) {
                return false;
            }
        }

        // Either moving to an empty square or successfully capturing an opponent's piece
        SetToPosition(pos);
        return true;
    }

    @Override
    public boolean CheckMove(Vector2 pos) {
        // First check if position is valid
        if (pos.x < 0 || pos.x >= ChessBoard.boardSize
                || pos.y < 0 || pos.y >= ChessBoard.boardSize) {
            return false;
        }

        if (isInCheckValidating) {
            return basicCheckMove(pos);
        }

        if (!basicCheckMove(pos)) {
            return false;
        }

        return !OriginalRules.wouldMovePutKingInCheck(this, pos, this.side);
    }

    private boolean basicCheckMove(Vector2 pos) {
        // Calculate the absolute differences in coordinates
        double dx = Math.abs(pos.x - position.x);
        double dy = Math.abs(pos.y - position.y);

        // Knight moves in an L-shape
        boolean isValidMove = (dx == 2 && dy == 1) || (dx == 1 && dy == 2);
        if (!isValidMove) {
            return false;
        }

        // Check if target position is empty or contains opponent's piece
        int targetId = ChessBoard.GetPieceIdAtPos(pos);
        return targetId == -1 || ChessBoard.chessPieces[targetId].side != this.side;
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
