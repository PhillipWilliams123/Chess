package com.example.Chess.Chess;

import com.example.Chess.Vector2;
import com.example.Chess.Globals;
import com.example.Chess.Rules.OriginalRules;

public class Pawn extends ChessPiece {

    public Pawn(Vector2 position, boolean side) {
        this.side = side;
        this.position = position;
    }

    @Override
    public String GetImageLocation() {
        return Globals.ImageDirectory + Globals.PieceImages[0 + (6 * (side ? 1 : 0))];
    }

    @Override
    public boolean TryMove(Vector2 pos) {
        if (!CheckMove(pos)) {
            return false;
        }

        // Handle en passant
        if (isEnPassantMove(pos)) {
            return executeEnPassant(pos);
        }

        int direction = side ? 1 : -1;
        Vector2 forwardOne = new Vector2(0, direction);

        // Handle normal moves
        if (pos.x == position.x) {
            // Single move forward
            if (pos.equals(Vector2.Add(position, forwardOne))) {
                if (!OriginalRules.wouldMovePutKingInCheck(this, pos, this.side)) {
                    SetToPosition(pos);
                    return true;
                }
            }
            // Double move from starting position
            if ((position.y == 1 || position.y == 6)
                    && pos.equals(Vector2.Add(position, Vector2.Mul(forwardOne, 2)))) {
                if (!OriginalRules.wouldMovePutKingInCheck(this, pos, this.side)) {
                    SetToPosition(pos);
                    return true;
                }
            }
        }

        // Handle captures
        Vector2 captureLeft = new Vector2(-1, direction);
        Vector2 captureRight = new Vector2(1, direction);
        if (pos.equals(Vector2.Add(position, captureLeft)) || pos.equals(Vector2.Add(position, captureRight))) {
            ChessPiece targetPiece = ChessBoard.GetChessPieceAtPos(pos);
            if (targetPiece.id != -1 && targetPiece.side != this.side) {
                if (!OriginalRules.wouldMovePutKingInCheck(this, pos, this.side)) {
                    if (TryTakePiece(pos)) {
                        SetToPosition(pos);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public boolean CheckMove(Vector2 pos) {
        // First check basic pawn movement rules
        if (!basicCheckMove(pos)) {
            return false;
        }

        // Then verify this move wouldn't leave king in check
        return !OriginalRules.wouldMovePutKingInCheck(this, pos, this.side);
    }

    private boolean basicCheckMove(Vector2 pos) {
        // First check if position is valid
        if (!ChessBoard.PosInBounds(pos)) {
            return false;
        }

        int direction = side ? 1 : -1;
        Vector2 forwardOne = new Vector2(0, direction);
        Vector2 captureLeft = new Vector2(-1, direction);
        Vector2 captureRight = new Vector2(1, direction);

        // Check en passant first
        if (isEnPassantMove(pos)) {
            return true;
        }

        // Check normal forward moves
        if (pos.x == position.x) {
            // Single move forward
            if (pos.equals(Vector2.Add(position, forwardOne))) {
                return ChessBoard.GetChessPieceAtPos(pos).id == -1;
            }
            // Double move from starting position
            if ((position.y == 1 || position.y == 6)
                    && pos.equals(Vector2.Add(position, Vector2.Mul(forwardOne, 2)))) {
                Vector2 intermediate = Vector2.Add(position, forwardOne);
                return ChessBoard.GetChessPieceAtPos(intermediate).id == -1
                        && ChessBoard.GetChessPieceAtPos(pos).id == -1;
            }
        }

        // Check captures (including en passant)
        if (pos.equals(Vector2.Add(position, captureLeft))
                || pos.equals(Vector2.Add(position, captureRight))) {
            ChessPiece targetPiece = ChessBoard.GetChessPieceAtPos(pos);
            // Normal capture
            if (targetPiece.id != -1) {
                return targetPiece.side != this.side;
            }
        }

        return false;
    }

    public boolean isEnPassantMove(Vector2 targetPos) {
        int direction = side ? 1 : -1;
        Vector2 captureLeft = new Vector2(-1, direction);
        Vector2 captureRight = new Vector2(1, direction);

        // Must be moving diagonally to empty square
        if (!targetPos.equals(Vector2.Add(position, captureLeft))
                && !targetPos.equals(Vector2.Add(position, captureRight))) {
            return false;
        }

        // Target square must be empty
        if (ChessBoard.GetChessPieceAtPos(targetPos).id != -1) {
            return false;
        }

        // Check for adjacent enemy pawn that just moved two squares
        Vector2 adjacentPos = new Vector2(targetPos.x, position.y);
        ChessPiece adjacentPiece = ChessBoard.GetChessPieceAtPos(adjacentPos);

        return adjacentPiece instanceof Pawn
                && adjacentPiece.side != this.side
                && Math.abs(adjacentPiece.position.y - position.y) == 0
                && Math.abs(adjacentPiece.moveCount) == 1;
    }

    private boolean executeEnPassant(Vector2 targetPos) {
        Vector2 capturedPawnPos = new Vector2(targetPos.x, position.y);
        ChessPiece capturedPawn = ChessBoard.GetChessPieceAtPos(capturedPawnPos);

        if (capturedPawn == null || capturedPawn.side == this.side) {
            return false;
        }

        // Remove the captured pawn
        ChessBoard.DeletePiece(capturedPawnPos);
        // Move the attacking pawn
        SetToPosition(targetPos);
        return true;
    }

    @Override
    public ChessPiece Copy() {
        Pawn copy = new Pawn(new Vector2(position.x, position.y), side);
        copy.moveCount = this.moveCount;
        return copy;
    }
}
