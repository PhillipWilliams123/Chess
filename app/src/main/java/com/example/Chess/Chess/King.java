package com.example.Chess.Chess;

import com.example.Chess.Vector2;
import com.example.Chess.Globals;
import com.example.Chess.Rules.OriginalRules;

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

        // Handle normal moves
        if (Math.abs(pos.x - position.x) <= 1 && Math.abs(pos.y - position.y) <= 1) {
            return attemptMove(pos);
        }

        // Handle castling
        if (Math.abs(pos.x - position.x) == 2 && pos.y == position.y) {
            return performCastling(pos);
        }

        return false;
    }

    @Override
    public boolean CheckMove(Vector2 pos) {
        // First check if position is valid
        if (!ChessBoard.PosInBounds(pos)) {
            return false;
        }

        double dx = Math.abs(pos.x - position.x);
        double dy = Math.abs(pos.y - position.y);

        // Normal king move (1 square in any direction)
        if ((dx <= 1 && dy <= 1) && (dx + dy > 0)) {
            int targetId = ChessBoard.GetPieceIdAtPos(pos);
            // Can't capture own piece
            if (targetId != -1 && ChessBoard.chessPieces[targetId].side == this.side) {
                return false;
            }
            
            // Check if move would leave king in check
            return !wouldMoveLeaveInCheck(pos);
        }

        // Castling check
        if (dx == 2 && dy == 0 && pos.y == position.y) {
            return isValidCastling(pos);
        }
        
        return false;
    }

    private boolean attemptMove(Vector2 pos) {
        if (wouldMoveLeaveInCheck(pos)) {
            return false;
        }

        int targetId = ChessBoard.GetPieceIdAtPos(pos);
        if (targetId != -1) {
            TryTakePiece(pos);
        }
        SetToPosition(pos);
        moveCount++;
        return true;
    }

    private boolean wouldMoveLeaveInCheck(Vector2 pos) {
        // Save original state
        Vector2 originalPos = new Vector2(position.x, position.y);
        int originalId = ChessBoard.GetPieceIdAtPos(originalPos);
        int targetId = ChessBoard.GetPieceIdAtPos(pos);
        ChessPiece capturedPiece = targetId != -1 ? ChessBoard.chessPieces[targetId] : null;

        // Simulate the move
        ChessBoard.SetPieceIdAtPos(originalPos, -1);
        position = new Vector2(pos.x, pos.y);
        ChessBoard.SetPieceIdAtPos(pos, this.id);

        boolean inCheck = OriginalRules.isInCheck(side);

        // Restore original state
        position = originalPos;
        ChessBoard.SetPieceIdAtPos(originalPos, originalId);
        if (capturedPiece != null) {
            ChessBoard.chessPieces[targetId] = capturedPiece;
            ChessBoard.SetPieceIdAtPos(pos, targetId);
        } else {
            ChessBoard.SetPieceIdAtPos(pos, -1);
        }

        return inCheck;
    }

    private boolean performCastling(Vector2 targetPos) {
        if (!isValidCastling(targetPos)) {
            return false;
        }

        boolean kingside = targetPos.x > position.x;
        int rookX = kingside ? 7 : 0;
        int newRookX = kingside ? 5 : 3;
        int y = (int) position.y;

        ChessPiece rook = ChessBoard.GetChessPieceAtPos(new Vector2(rookX, y));
        if (rook == null || !(rook instanceof Rook)) {
            return false;
        }

        // Move the king
        SetToPosition(targetPos);
        moveCount++;

        // Move the rook
        rook.SetToPosition(new Vector2(newRookX, y));
        rook.moveCount++;

        return true;
    }

    private boolean isValidCastling(Vector2 targetPos) {
        // Check if king has moved
        if (moveCount > 0 || OriginalRules.isInCheck(side)) {
            return false;
        }

        // Determine castling side
        boolean kingside = targetPos.x > position.x;
        int rookX = kingside ? 7 : 0;
        ChessPiece rook = ChessBoard.GetChessPieceAtPos(new Vector2(rookX, position.y));

        // Check if rook exists and hasn't moved
        if (!(rook instanceof Rook) || rook.moveCount > 0) {
            return false;
        }

        // Check if path is clear
        int direction = kingside ? 1 : -1;
        for (int x = (int) position.x + direction; x != rookX; x += direction) {
            if (ChessBoard.GetPieceIdAtPos(new Vector2(x, position.y)) != -1) {
                return false;
            }
        }

        // Check if squares are not under attack
        for (int x = (int) position.x; x != (int) targetPos.x + direction; x += direction) {
            if (isSquareUnderAttack(new Vector2(x, position.y))) {
                return false;
            }
        }

        return true;
    }

    private boolean isSquareUnderAttack(Vector2 pos) {
        for (ChessPiece piece : ChessBoard.chessPieces) {
            if (piece != null && piece.side != this.side && piece.CheckMove(pos)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ChessPiece Copy() {
        King copy = new King(this.position, this.side);
        copy.moveCount = this.moveCount;
        return copy;
    }
}