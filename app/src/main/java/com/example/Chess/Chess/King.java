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

        //Handle castling separately
        if (Math.abs(pos.x - position.x) == 2) {
            return performCastling(pos);
        }

        ChessPiece targetPiece = ChessBoard.GetChessPieceAtPos(pos);
        int targetId = targetPiece != null ? targetPiece.id : -1;

        // Save original state
        Vector2 originalPos = new Vector2(position.x, position.y);

        // Simulate the move
        SetToPosition(pos);
        ChessBoard.SetPieceIdAtPos(originalPos, -1);
        ChessBoard.SetPieceIdAtPos(pos, id);

        // Special case: when capturing, temporarily remove the captured piece
        if (targetPiece != null && targetPiece.id != -1) {
            ChessBoard.chessPieces[targetId] = new EmptyPiece();
        }

        boolean inCheckAfterMove = OriginalRules.isInCheck(side);

        // Restore state
        SetToPosition(originalPos);
        ChessBoard.SetPieceIdAtPos(originalPos, id);
        ChessBoard.SetPieceIdAtPos(pos, targetId);
        if (targetPiece != null && targetPiece.id != -1) {
            ChessBoard.chessPieces[targetId] = targetPiece;
        }

        if (inCheckAfterMove) {
            return false;
        }

        // Actually perform the move
        if (targetPiece != null && targetPiece.id != -1 && targetPiece.side != this.side) {
            TryTakePiece(pos);
        }
        SetToPosition(pos);
        return true;
    }

    @Override
    public boolean CheckMove(Vector2 pos) {
        if (!ChessBoard.PosInBounds(pos) || pos.equals(position)) {
            return false;
        }

        double dx = Math.abs(pos.x - position.x);
        double dy = Math.abs(pos.y - position.y);

        //Normal king move (1 square in any direction)
        if (dx <= 1 && dy <= 1) {
            ChessPiece target = ChessBoard.GetChessPieceAtPos(pos);
            return target.id == -1 || target.side != this.side;
        }

        //Castling check (simplified without hasMoved flag)
        if (dx == 2 && dy == 0 && moveCount == 0) {
            return isValidCastling(pos);
        }

        return false;
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
        if (rook == null || !(rook instanceof Rook) || rook.moveCount > 0) {
            return false;
        }

        //Move the king
        SetToPosition(targetPos);
        moveCount++;

        //Move the rook
        rook.SetToPosition(new Vector2(newRookX, y));
        rook.moveCount++;

        return true;
    }

    private boolean isValidCastling(Vector2 targetPos) {
        if (moveCount > 0 || OriginalRules.isInCheck(side)) {
            return false;
        }

        boolean kingside = targetPos.x > position.x;
        int rookX = kingside ? 7 : 0;
        ChessPiece rook = ChessBoard.GetChessPieceAtPos(new Vector2(rookX, position.y));

        if (!(rook instanceof Rook) || rook.moveCount > 0) {
            return false;
        }

        //Check if path is clear
        int direction = kingside ? 1 : -1;
        for (int x = (int) position.x + direction; x != rookX; x += direction) {
            if (ChessBoard.GetPieceIdAtPos(new Vector2(x, position.y)) != -1) {
                return false;
            }
        }

        //Check if squares are not under attack
        for (int x = (int) position.x; x != (int) targetPos.x + direction; x += direction) {
            if (isSquareUnderAttack(new Vector2(x, position.y))) {
                return false;
            }
        }

        return true;
    }

    private boolean isSquareUnderAttack(Vector2 pos) {
        for (ChessPiece piece : ChessBoard.chessPieces) {
            if (piece != null && piece.id != -1 && piece.side != this.side && piece.CheckMove(pos)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ChessPiece Copy() {
        King copy = new King(new Vector2(position.x, position.y), side);
        copy.moveCount = this.moveCount;
        return copy;
    }
}
