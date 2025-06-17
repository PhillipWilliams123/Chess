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

        ChessPiece targetPiece = ChessBoard.GetChessPieceAtPos(pos);

        //Special handling when capturing the checking piece
        if (targetPiece != null && targetPiece.id != -1) {
            //Save original state
            Vector2 originalPos = new Vector2(position.x, position.y);
            int targetId = targetPiece.id;

            //Simulate the capture
            SetToPosition(pos);
            ChessBoard.SetPieceIdAtPos(originalPos, -1);
            ChessBoard.SetPieceIdAtPos(pos, id);
            ChessBoard.chessPieces[targetId] = new EmptyPiece();

            //Check if this resolves the check
            boolean stillInCheck = OriginalRules.isInCheck(side);

            // Restore state
            SetToPosition(originalPos);
            ChessBoard.SetPieceIdAtPos(originalPos, id);
            ChessBoard.SetPieceIdAtPos(pos, targetId);
            ChessBoard.chessPieces[targetId] = targetPiece;

            if (stillInCheck) {
                return false; //Capture doesn't resolve check
            }

            //Actually perform the capture
            TryTakePiece(pos);
        }

        SetToPosition(pos);
        return true;
    }

    @Override
    public boolean CheckMove(Vector2 pos) {
        // Basic position validation
        if (!ChessBoard.PosInBounds(pos)) {
            return false;
        }

        // Must move diagonally
        double dx = Math.abs(pos.x - position.x);
        double dy = Math.abs(pos.y - position.y);
        if (dx != dy || dx == 0) {
            return false;
        }

        // Check path is clear
        int stepX = (int) Math.signum(pos.x - position.x);
        int stepY = (int) Math.signum(pos.y - position.y);

        Vector2 current = Vector2.Add(position, new Vector2(stepX, stepY));
        while (!current.equals(pos)) {
            if (ChessBoard.GetPieceIdAtPos(current) != -1) {
                return false;
            }
            current = Vector2.Add(current, new Vector2(stepX, stepY));
        }

        // Check target position
        ChessPiece target = ChessBoard.GetChessPieceAtPos(pos);
        if (target.id == -1) {
            return !OriginalRules.wouldMovePutKingInCheck(this, pos, this.side);
        }

        // Can capture opponent's piece
        if (target.side != this.side) {
            // Special case: when checking if this captures a checking piece
            if (isInCheckValidating) {
                return true;
            }

            // Verify this capture doesn't leave king in check
            return !OriginalRules.wouldMovePutKingInCheck(this, pos, this.side);
        }

        return false;
    }

    @Override
    public ChessPiece Copy() {
        return new Bishop(this.position, this.side);
    }
}
