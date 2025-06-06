package com.example.Chess.Chess;

import com.example.Chess.Vector2;
import com.example.Chess.Globals;
import com.example.Chess.Interaction;
import com.example.Chess.Rules.OriginalRules;

public class Pawn extends ChessPiece {

    private boolean canBeEnPassantTarget = false;

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

        if (canEnPassant(pos)) {
            return executeEnPassant(pos);
        }

        int direction = side ? 1 : -1;
        Vector2 forwardOne = new Vector2(0, direction);

        if (pos.x == position.x) {
            // Forward moves
            if ((position.y == 1 || position.y == 6)
                    && CheckInDirection(forwardOne, position, pos) == 2
                    && ChessBoard.GetPieceIdAtPos(Vector2.Add(position, forwardOne)) == -1
                    && ChessBoard.GetPieceIdAtPos(pos) == -1) {
                SetToPosition(pos);
                canBeEnPassantTarget = true;
                return true;
            }

            if (CheckInDirection(forwardOne, position, pos) == 1
                    && ChessBoard.GetPieceIdAtPos(pos) == -1) {
                SetToPosition(pos);
                canBeEnPassantTarget = false;
                return true;
            }
        } else {
            // Diagonal captures
            Vector2 diff = Vector2.Sub(pos, position);
            if (Math.abs(diff.x) == 1 && diff.y == direction) {
                int targetId = ChessBoard.GetPieceIdAtPos(pos);
                if (targetId != -1) {
                    ChessPiece targetPiece = ChessBoard.chessPieces[targetId];
                    if (targetPiece.side != this.side) {
                        TryTakePiece(pos);
                        SetToPosition(pos);
                        canBeEnPassantTarget = false;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean CheckMove(Vector2 pos) {
        // Position bounds check
        if (pos.x < 0 || pos.x >= ChessBoard.boardSize
                || pos.y < 0 || pos.y >= ChessBoard.boardSize) {
            return false;
        }

        // Basic move validation
        if (!basicCheckMove(pos)) {
            return false;
        }

        // Simulate the move to check if it would leave king in check
        Vector2 originalPos = position;
        int targetId = ChessBoard.GetPieceIdAtPos(pos);
        ChessPiece capturedPiece = targetId != -1 ? ChessBoard.chessPieces[targetId] : null;
        
        // Make temporary move
        SetToPosition(pos);
        if (targetId != -1) {
            ChessBoard.SetPieceIdAtPos(pos, this.id);
            ChessBoard.SetPieceIdAtPos(originalPos, -1);
        }

        boolean stillInCheck = OriginalRules.isInCheck(this.side);

        // Undo temporary move
        SetToPosition(originalPos);
        if (targetId != -1) {
            ChessBoard.chessPieces[targetId] = capturedPiece;
            ChessBoard.SetPieceIdAtPos(pos, targetId);
        }

        // Only allow moves that get king out of check if currently in check
        if (OriginalRules.isInCheck(this.side)) {
            return !stillInCheck;
        }
        return true;
    }

    private boolean basicCheckMove(Vector2 pos) {
        int direction = side ? 1 : -1;
        Vector2 forwardOne = new Vector2(0, direction);

        // En passant check
        if (canEnPassant(pos)) {
            return true;
        }

        // Forward moves
        if (pos.x == position.x) {
            if ((position.y == 1 || position.y == 6)
                    && CheckInDirection(forwardOne, position, pos) == 2
                    && ChessBoard.GetPieceIdAtPos(Vector2.Add(position, forwardOne)) == -1
                    && ChessBoard.GetPieceIdAtPos(pos) == -1) {
                return true;
            }

            if (CheckInDirection(forwardOne, position, pos) == 1
                    && ChessBoard.GetPieceIdAtPos(pos) == -1) {
                return true;
            }
        } 
        // Diagonal captures
        else {
            Vector2 diff = Vector2.Sub(pos, position);
            if (Math.abs(diff.x) == 1 && diff.y == direction) {
                int targetId = ChessBoard.GetPieceIdAtPos(pos);
                if (targetId != -1) {
                    return ChessBoard.chessPieces[targetId].side != this.side;
                }
            }
        }
        return false;
    }

    private boolean canEnPassant(Vector2 pos) {
        if (Interaction.isBlackTurn == this.side) {
            return false;
        }

        int direction = side ? 1 : -1;
        Vector2 diff = Vector2.Sub(pos, position);

        if (Math.abs(diff.x) != 1 || diff.y != direction) {
            return false;
        }

        Vector2 adjacentPos = new Vector2(pos.x, position.y);
        int adjacentPieceId = ChessBoard.GetPieceIdAtPos(adjacentPos);
        if (adjacentPieceId == -1) {
            return false;
        }

        ChessPiece adjacentPiece = ChessBoard.chessPieces[adjacentPieceId];
        return (adjacentPiece instanceof Pawn)
                && (adjacentPiece.side != this.side)
                && ((Pawn) adjacentPiece).canBeEnPassantTarget;
    }

    private boolean executeEnPassant(Vector2 pos) {
        Vector2 capturedPawnPos = new Vector2(pos.x, position.y);
        TryTakePiece(capturedPawnPos);
        SetToPosition(pos);
        canBeEnPassantTarget = false;
        return true;
    }

    @Override
    public ChessPiece Copy() {
        Pawn copy = new Pawn(this.position, this.side);
        copy.canBeEnPassantTarget = this.canBeEnPassantTarget;
        return copy;
    }
}