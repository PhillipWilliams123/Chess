package com.example.Chess.Chess;

import com.example.Chess.Vector2;
import com.example.Chess.Globals;
import com.example.Chess.Rules.OriginalRules;
import static com.example.Chess.Rules.OriginalRules.isInCheckValidating;

public class Bishop extends ChessPiece {

    public Bishop(Vector2 position, boolean side) {
        this.side = side;
        this.position = position;
    }

    @Override
    public String GetImageLocation() {
        return Globals.ImageDirectory + Globals.PieceImages[2 + (6 * (side ? 1 : 0))];
    }

    @Override
    public boolean TryMove(Vector2 pos) {
        if (!CheckMove(pos)) {
            return false;
        }

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
        // Must move diagonally
        double dx = Math.abs(pos.x - position.x);
        double dy = Math.abs(pos.y - position.y);
        if (dx != dy) {
            return false;
        }

        // Check path is clear
        int stepX = (int)Math.signum(pos.x - position.x);
        int stepY = (int)Math.signum(pos.y - position.y);
        
        Vector2 current = Vector2.Add(position, new Vector2(stepX, stepY));
        while (!current.equals(pos)) {
            if (ChessBoard.GetPieceIdAtPos(current) != -1) {
                return false;
            }
            current = Vector2.Add(current, new Vector2(stepX, stepY));
        }

        // Check target position
        int targetId = ChessBoard.GetPieceIdAtPos(pos);
        return targetId == -1 || ChessBoard.chessPieces[targetId].side != this.side;
    }

    @Override
    public ChessPiece Copy() {
        return new Bishop(this.position, this.side);
    }
}