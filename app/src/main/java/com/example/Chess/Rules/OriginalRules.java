package com.example.Chess.Rules;

import com.example.Chess.Chess.*;
import com.example.Chess.Vector2;
import java.util.ArrayList;
import java.util.List;

public class OriginalRules {

    public static boolean isInCheckValidating = false;
    public static boolean[] whiteCastlingRights = {true, true};
    public static boolean[] blackCastlingRights = {true, true};

    public static boolean isInCheck(boolean isBlack) {
        Vector2 kingPos = findKingPosition(isBlack);
        if (kingPos == null) {
            return false;
        }

        isInCheckValidating = true;
        try {
            for (ChessPiece piece : ChessBoard.chessPieces) {
                if (piece != null && piece.id != -1 && piece.side != isBlack
                        && !QuantumRules.isQuantumPiece(piece) && piece.CheckMove(kingPos)) {
                    return true;
                }
            }
            return false;
        } finally {
            isInCheckValidating = false;
        }
    }

    public static boolean isCheckmate(boolean isBlack) {
        if (!isInCheck(isBlack)) {
            return false;
        }

        ChessPiece king = findKing(isBlack);
        if (king == null) {
            return false;
        }

        // Check if king can move out of check
        if (canKingEscapeCheck(king)) {
            return false;
        }

        List<ChessPiece> attackers = getCheckingPieces(isBlack);
        if (attackers.size() > 1) {
            return true; // Double check - only king can move
        }
        ChessPiece attacker = attackers.get(0);

        // Check if attacker can be captured
        if (canPieceBeCaptured(attacker, isBlack)) {
            return false;
        }

        // For sliding pieces, check if attack can be blocked
        if (attacker instanceof Queen || attacker instanceof Rook || attacker instanceof Bishop) {
            if (canAttackBeBlocked(king, attacker, isBlack)) {
                return false;
            }
        }

        return true;
    }

    private static boolean canKingEscapeCheck(ChessPiece king) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) {
                    continue;
                }

                Vector2 movePos = new Vector2(king.position.x + dx, king.position.y + dy);
                if (!ChessBoard.PosInBounds(movePos)) {
                    continue;
                }

                ChessPiece target = ChessBoard.GetChessPieceAtPos(movePos);
                if (target.id != -1 && target.side == king.side) {
                    continue;
                }

                if (king.CheckMove(movePos) && !wouldQuantumMovePutKingInCheck(king, movePos, king.side)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean canPieceBeCaptured(ChessPiece target, boolean isBlack) {
        for (ChessPiece piece : ChessBoard.chessPieces) {
            if (piece == null || piece.side != isBlack || piece.id == -1) {
                continue;
            }

            // Skip if piece is the king (handled separately)
            if (piece instanceof King) {
                continue;
            }

            if (piece.CheckMove(target.position)) {
                // Handle quantum captures differently
                if (QuantumRules.isQuantumPiece(piece) || QuantumRules.isQuantumPiece(target)) {
                    // Quantum captures are handled by the collapse system
                    return true; // Assume it's possible to capture
                }

                // Normal capture simulation
                Vector2 originalPos = piece.position;
                int targetId = target.id;
                ChessPiece capturedPiece = ChessBoard.chessPieces[targetId];

                piece.SetToPosition(target.position);
                ChessBoard.SetPieceIdAtPos(originalPos, -1);
                ChessBoard.SetPieceIdAtPos(target.position, piece.id);
                ChessBoard.chessPieces[targetId] = new EmptyPiece();

                boolean stillInCheck = isInCheck(isBlack);

                // Restore state
                piece.SetToPosition(originalPos);
                ChessBoard.SetPieceIdAtPos(originalPos, piece.id);
                ChessBoard.SetPieceIdAtPos(target.position, targetId);
                ChessBoard.chessPieces[targetId] = capturedPiece;

                if (!stillInCheck) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean canAttackBeBlocked(ChessPiece king, ChessPiece attacker, boolean isBlack) {
        List<Vector2> path = getPathBetween(king.position, attacker.position);
        for (Vector2 blockPos : path) {
            for (ChessPiece piece : ChessBoard.chessPieces) {
                if (piece == null || piece.side != isBlack || piece.id == -1) {
                    continue;
                }

                if (piece.CheckMove(blockPos)) {
                    // Simulate block
                    Vector2 originalPos = piece.position;
                    int blockPosId = ChessBoard.GetPieceIdAtPos(blockPos);
                    ChessPiece blockPiece = blockPosId != -1 ? ChessBoard.chessPieces[blockPosId] : null;

                    piece.SetToPosition(blockPos);
                    ChessBoard.SetPieceIdAtPos(originalPos, -1);
                    ChessBoard.SetPieceIdAtPos(blockPos, piece.id);

                    boolean stillInCheck = isInCheck(isBlack);

                    // Restore state
                    piece.SetToPosition(originalPos);
                    ChessBoard.SetPieceIdAtPos(originalPos, piece.id);
                    if (blockPiece != null) {
                        ChessBoard.SetPieceIdAtPos(blockPos, blockPosId);
                    } else {
                        ChessBoard.SetPieceIdAtPos(blockPos, -1);
                    }

                    if (!stillInCheck) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean wouldMovePutKingInCheck(ChessPiece piece, Vector2 targetPos, boolean isBlack) {
        // Quantum pieces can't put king in check
        if (QuantumRules.isQuantumPiece(piece)) {
            return false;
        }

        // Save original state
        Vector2 originalPos = piece.position;
        int targetId = ChessBoard.GetPieceIdAtPos(targetPos);
        ChessPiece capturedPiece = targetId != -1 ? ChessBoard.chessPieces[targetId] : null;

        // Simulate the move
        piece.SetToPosition(targetPos);
        ChessBoard.SetPieceIdAtPos(originalPos, -1);
        ChessBoard.SetPieceIdAtPos(targetPos, piece.id);
        if (targetId != -1) {
            ChessBoard.chessPieces[targetId] = new EmptyPiece();
        }

        boolean inCheck = isInCheck(isBlack);

        // Restore original state
        piece.SetToPosition(originalPos);
        ChessBoard.SetPieceIdAtPos(originalPos, piece.id);
        ChessBoard.SetPieceIdAtPos(targetPos, targetId);
        if (targetId != -1) {
            ChessBoard.chessPieces[targetId] = capturedPiece;
        }

        return inCheck;
    }

    public static boolean wouldQuantumMovePutKingInCheck(ChessPiece piece, Vector2 targetPos, boolean isBlack) {
        // Quantum pieces can never put king in check
        if (QuantumRules.isQuantumPiece(piece)) {
            return false;
        }
        return wouldMovePutKingInCheck(piece, targetPos, isBlack);
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

    public static void updateGameState(ChessPiece movedPiece, Vector2 originalPos) {
        // Update castling rights
        if (movedPiece instanceof King) {
            if (movedPiece.side) {
                whiteCastlingRights[0] = false;
                whiteCastlingRights[1] = false;
            } else {
                blackCastlingRights[0] = false;
                blackCastlingRights[1] = false;
            }
        } else if (movedPiece instanceof Rook) {
            if (movedPiece.side) {
                if (originalPos.x == 0) {
                    whiteCastlingRights[1] = false;
                }
                if (originalPos.x == 7) {
                    whiteCastlingRights[0] = false;
                }
            } else {
                if (originalPos.x == 0) {
                    blackCastlingRights[1] = false;
                }
                if (originalPos.x == 7) {
                    blackCastlingRights[0] = false;
                }
            }
        }
    }
}
