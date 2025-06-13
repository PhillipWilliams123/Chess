package com.example.Chess.Rules;

import com.example.Chess.Chess.*;
import com.example.Chess.Vector2;
import java.util.ArrayList;
import java.util.List;

public class OriginalRules {

    public static boolean isInCheckValidating = false;
    public static boolean[] whiteCastlingRights = {true, true}; // [kingside, queenside]
    public static boolean[] blackCastlingRights = {true, true};

    public static boolean isInCheck(boolean isBlack) {
        Vector2 kingPos = findKingPosition(isBlack);
        if (kingPos == null) {
            return false;
        }

        isInCheckValidating = true;
        try {
            for (ChessPiece piece : ChessBoard.chessPieces) {
                if (piece != null && piece.side != isBlack && piece.CheckMove(kingPos)) {
                    return true;
                }
            }
            return false;
        } finally {
            isInCheckValidating = false;
        }
    }

    private static boolean canKingEscapeCheck(ChessPiece king) {
        // Check all 8 possible king moves
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) {
                    continue;
                }

                Vector2 movePos = new Vector2(king.position.x + dx, king.position.y + dy);

                if (king.CheckMove(movePos) && !wouldMovePutKingInCheck(king, movePos, king.side)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean canAttackerBeCaptured(ChessPiece attacker, boolean isBlack) {
        for (ChessPiece piece : ChessBoard.chessPieces) {
            if (piece != null && piece.id != -1 && piece.side == isBlack) {
                // Save original state
                Vector2 originalPos = piece.position;
                int attackerId = attacker.id;

                // Simulate capture
                piece.SetToPosition(attacker.position);
                ChessBoard.SetPieceIdAtPos(originalPos, -1);
                ChessBoard.SetPieceIdAtPos(attacker.position, piece.id);

                boolean stillInCheck = isInCheck(isBlack);

                // Restore state
                piece.SetToPosition(originalPos);
                ChessBoard.SetPieceIdAtPos(originalPos, piece.id);
                ChessBoard.SetPieceIdAtPos(attacker.position, attackerId);

                if (!stillInCheck) {
                    return true;
                }
            }
        }
        return false;
    }

// Update the isCheckmate method to use this:
    public static boolean isCheckmate(boolean isBlack) {
        if (!isInCheck(isBlack)) {
            return false;
        }

        ChessPiece king = findKing(isBlack);
        if (king == null) {
            return false;
        }

        // 1. Check if king can move out of check
        if (canKingEscapeCheck(king)) {
            return false;
        }

        List<ChessPiece> attackers = getCheckingPieces(isBlack);

        // If multiple attackers, only king can move (already checked above)
        if (attackers.size() > 1) {
            return true;
        }

        ChessPiece attacker = attackers.get(0);

        // 2. Check if attacker can be captured
        if (canAttackerBeCaptured(attacker, isBlack)) {
            return false;
        }

        // 3. For sliding pieces, check if attack can be blocked
        if (attacker instanceof Queen || attacker instanceof Rook || attacker instanceof Bishop) {
            if (canAttackBeBlocked(king, attacker, isBlack)) {
                return false;
            }
        }

        return true;
    }

    private static boolean canAttackBeBlocked(ChessPiece king, ChessPiece attacker, boolean isBlack) {
        List<Vector2> path = getPathBetween(king.position, attacker.position);
        for (Vector2 blockPos : path) {
            for (ChessPiece piece : ChessBoard.chessPieces) {
                if (piece != null && piece.side == isBlack && piece != king) {
                    if (piece.CheckMove(blockPos)) {
                        // Simulate block
                        Vector2 originalPos = piece.position;
                        piece.SetToPosition(blockPos);
                        ChessBoard.SetPieceIdAtPos(originalPos, -1);
                        ChessBoard.SetPieceIdAtPos(blockPos, piece.id);

                        boolean stillInCheck = isInCheck(isBlack);

                        // Undo simulation
                        piece.SetToPosition(originalPos);
                        ChessBoard.SetPieceIdAtPos(originalPos, piece.id);
                        ChessBoard.SetPieceIdAtPos(blockPos, -1);

                        if (!stillInCheck) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean isStalemate(boolean isBlack) {
        if (isInCheck(isBlack)) {
            return false;
        }

        // Check if any legal moves exist
        for (ChessPiece piece : ChessBoard.chessPieces) {
            if (piece != null && piece.side == isBlack) {
                List<Vector2> legalMoves = filterLegalMoves(piece, isBlack);
                if (!legalMoves.isEmpty()) {
                    return false;
                }
            }
        }

        return true;
    }

    public static List<Vector2> filterLegalMoves(ChessPiece piece, boolean isBlack) {
        List<Vector2> legalMoves = new ArrayList<>();

        for (int x = 0; x < ChessBoard.boardSize; x++) {
            for (int y = 0; y < ChessBoard.boardSize; y++) {
                Vector2 targetPos = new Vector2(x, y);
                if (piece.CheckMove(targetPos)) {
                    if (!wouldMovePutKingInCheck(piece, targetPos, isBlack)) {
                        legalMoves.add(targetPos);
                    }
                }
            }
        }

        return legalMoves;
    }

    public static boolean wouldMovePutKingInCheck(ChessPiece piece, Vector2 targetPos, boolean isBlack) {
        // Save original state
        Vector2 originalPos = piece.position;
        int targetId = ChessBoard.GetPieceIdAtPos(targetPos);
        ChessPiece capturedPiece = targetId != -1 ? ChessBoard.chessPieces[targetId] : null;

        // Simulate the move
        piece.SetToPosition(targetPos);
        if (targetId != -1) {
            ChessBoard.SetPieceIdAtPos(targetPos, piece.id);
            ChessBoard.SetPieceIdAtPos(originalPos, -1);
        }

        boolean inCheck = isInCheck(isBlack);

        // Restore original state
        piece.SetToPosition(originalPos);
        if (targetId != -1) {
            ChessBoard.chessPieces[targetId] = capturedPiece;
            ChessBoard.SetPieceIdAtPos(targetPos, targetId);
        } else {
            ChessBoard.SetPieceIdAtPos(targetPos, -1);
        }

        return inCheck;
    }

    private static List<ChessPiece> getCheckingPieces(boolean side) {
        List<ChessPiece> attackers = new ArrayList<>();
        ChessPiece king = findKing(side);
        if (king == null) {
            return attackers;
        }

        for (ChessPiece piece : ChessBoard.chessPieces) {
            if (piece != null && piece.side != side && piece.CheckMove(king.position)) {
                attackers.add(piece);
            }
        }
        return attackers;
    }

    private static List<Vector2> getPathBetween(Vector2 a, Vector2 b) {
        List<Vector2> path = new ArrayList<>();
        //Vertical path
        if (a.x == b.x) {
            int step = a.y < b.y ? 1 : -1;
            for (int y = (int) a.y + step; y != (int) b.y; y += step) {
                path.add(new Vector2(a.x, y));
            }
        } //Horizontal path
        else if (a.y == b.y) {
            int step = a.x < b.x ? 1 : -1;
            for (int x = (int) a.x + step; x != (int) b.x; x += step) {
                path.add(new Vector2(x, a.y));
            }
        } //Diagonal path
        else if (Math.abs(a.x - b.x) == Math.abs(a.y - b.y)) {
            int stepX = a.x < b.x ? 1 : -1;
            int stepY = a.y < b.y ? 1 : -1;
            for (int i = 1; i < Math.abs(a.x - b.x); i++) {
                path.add(new Vector2(a.x + i * stepX, a.y + i * stepY));
            }
        }
        return path;
    }

    //
    private static ChessPiece findKing(boolean side) {
        for (ChessPiece piece : ChessBoard.chessPieces) {
            if (piece != null && piece.id >= 0 && piece.id < ChessBoard.chessPieces.length
                    && piece instanceof King && piece.side == side) {
                return piece;
            }
        }
        return null;
    }

    private static Vector2 findKingPosition(boolean isBlack) {
        ChessPiece king = findKing(isBlack);
        return king != null ? king.position : null;
    }
}
