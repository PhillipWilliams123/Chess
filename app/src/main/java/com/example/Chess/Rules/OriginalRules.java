package com.example.Chess.Rules;

import com.example.Chess.Chess.*;
import com.example.Chess.Vector2;
import java.util.ArrayList;
import java.util.List;

public class OriginalRules {

    public static boolean isInCheckValidating = false;

    
    //Boolean to verify if the king is in check
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

    public static boolean isCheckmate(boolean isBlack) {
        if (!isInCheck(isBlack)) {
            return false;
        }

        King king = (King) ChessBoard.GetChessPieceAtPos(findKingPosition(isBlack));
        if (king == null) {
            return true;
        }

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) {
                    continue;
                }

                Vector2 newPos = Vector2.Add(king.position, new Vector2(dx, dy));
                if (king.CheckMove(newPos) && !wouldBeInCheck(king, newPos)) {
                    return false;
                }
            }
        }

        List<ChessPiece> checkingPieces = getCheckingPieces(isBlack, king.position);
        if (checkingPieces.size() > 1) {
            return true;
        }

        ChessPiece attacker = checkingPieces.get(0);

        if (canPieceBeCaptured(attacker, isBlack)) {
            return false;
        }

        if (!(attacker instanceof Knight) && !(attacker instanceof Pawn)) {
            List<Vector2> path = getPathBetween(king.position, attacker.position);
            for (Vector2 blockPos : path) {
                if (canSquareBeBlocked(blockPos, isBlack)) {
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean wouldBeInCheck(King king, Vector2 newPos) {
        Vector2 originalPos = king.position;
        ChessPiece capturedPiece = ChessBoard.GetChessPieceAtPos(newPos);
        int capturedId = ChessBoard.GetPieceIdAtPos(newPos);

        if(capturedId == -1)
            return false;

        king.SetToPosition(newPos);
        if (capturedPiece != null) {
            ChessBoard.SetPieceIdAtPos(newPos, king.id);
            ChessBoard.SetPieceIdAtPos(originalPos, -1);
        }

        boolean inCheck = isInCheck(king.side);

        king.SetToPosition(originalPos);
        if (capturedPiece != null) {
            ChessBoard.chessPieces[capturedId] = capturedPiece;
            ChessBoard.SetPieceIdAtPos(newPos, capturedId);
        } else {
            ChessBoard.SetPieceIdAtPos(newPos, -1);
        }

        return inCheck;
    }

    private static boolean canPieceBeCaptured(ChessPiece target, boolean isBlack) {
        for (ChessPiece piece : ChessBoard.chessPieces) {
            if (piece != null && piece.side == isBlack && piece.CheckMove(target.position)) {
                Vector2 originalPos = piece.position;
                ChessPiece capturedPiece = target;
                int capturedId = ChessBoard.GetPieceIdAtPos(target.position);

                if(capturedId == -1)
                    continue;

                piece.SetToPosition(target.position);
                ChessBoard.SetPieceIdAtPos(target.position, piece.id);
                ChessBoard.SetPieceIdAtPos(originalPos, -1);

                boolean stillInCheck = isInCheck(isBlack);

                piece.SetToPosition(originalPos);
                ChessBoard.chessPieces[capturedId] = capturedPiece;
                ChessBoard.SetPieceIdAtPos(target.position, capturedId);
                ChessBoard.SetPieceIdAtPos(originalPos, piece.id);

                if (!stillInCheck) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean canSquareBeBlocked(Vector2 blockPos, boolean isBlack) {
        for (ChessPiece piece : ChessBoard.chessPieces) {
            if (piece != null && piece.side == isBlack && !(piece instanceof King) && piece.CheckMove(blockPos)) {
                Vector2 originalPos = piece.position;
                ChessPiece capturedPiece = ChessBoard.GetChessPieceAtPos(blockPos);
                int capturedId = ChessBoard.GetPieceIdAtPos(blockPos);

                if(capturedId == -1)
                    continue;

                piece.SetToPosition(blockPos);
                if (capturedPiece != null) {
                    ChessBoard.SetPieceIdAtPos(blockPos, piece.id);
                    ChessBoard.SetPieceIdAtPos(originalPos, -1);
                }

                boolean stillInCheck = isInCheck(isBlack);

                piece.SetToPosition(originalPos);
                if (capturedPiece != null) {
                    ChessBoard.chessPieces[capturedId] = capturedPiece;
                    ChessBoard.SetPieceIdAtPos(blockPos, capturedId);
                } else {
                    ChessBoard.SetPieceIdAtPos(blockPos, -1);
                }

                if (!stillInCheck) {
                    return true;
                }
            }
        }
        return false;
    }

    private static List<ChessPiece> getCheckingPieces(boolean isBlack, Vector2 kingPos) {
        List<ChessPiece> checkingPieces = new ArrayList<>();
        for (ChessPiece piece : ChessBoard.chessPieces) {
            if (piece != null && piece.side != isBlack && piece.CheckMove(kingPos)) {
                checkingPieces.add(piece);
            }
        }
        return checkingPieces;
    }

    private static List<Vector2> getPathBetween(Vector2 a, Vector2 b) {
        List<Vector2> path = new ArrayList<>();
        if (a.x == b.x) {
            int step = a.y < b.y ? 1 : -1;
            for (int y = (int) a.y + step; y != (int) b.y; y += step) {
                path.add(new Vector2(a.x, y));
            }
        } else if (a.y == b.y) {
            int step = a.x < b.x ? 1 : -1;
            for (int x = (int) a.x + step; x != (int) b.x; x += step) {
                path.add(new Vector2(x, a.y));
            }
        } else if (Math.abs(a.x - b.x) == Math.abs(a.y - b.y)) {
            int stepX = a.x < b.x ? 1 : -1;
            int stepY = a.y < b.y ? 1 : -1;
            for (int i = 1; i < Math.abs(a.x - b.x); i++) {
                path.add(new Vector2(a.x + i * stepX, a.y + i * stepY));
            }
        }
        return path;
    }

    public static boolean isStalemate(boolean isBlack) {
        if (isInCheck(isBlack)) {
            return false;
        }

        for (ChessPiece piece : ChessBoard.chessPieces) {
            if (piece == null || piece.side != isBlack) {
                continue;
            }

            for (int x = 0; x < ChessBoard.boardSize; x++) {
                for (int y = 0; y < ChessBoard.boardSize; y++) {
                    Vector2 targetPos = new Vector2(x, y);
                    if (piece.CheckMove(targetPos)) {
                        Vector2 originalPos = piece.position;
                        ChessPiece capturedPiece = ChessBoard.GetChessPieceAtPos(targetPos);
                        int capturedId = ChessBoard.GetPieceIdAtPos(targetPos);

                        piece.SetToPosition(targetPos);
                        if (capturedPiece != null) {
                            ChessBoard.SetPieceIdAtPos(targetPos, piece.id);
                            ChessBoard.SetPieceIdAtPos(originalPos, -1);
                        }

                        boolean inCheckAfterMove = isInCheck(isBlack);

                        piece.SetToPosition(originalPos);
                        if (capturedPiece != null) {
                            ChessBoard.chessPieces[capturedId] = capturedPiece;
                            ChessBoard.SetPieceIdAtPos(targetPos, capturedId);
                        } else {
                            ChessBoard.SetPieceIdAtPos(targetPos, -1);
                        }

                        if (!inCheckAfterMove) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private static Vector2 findKingPosition(boolean isBlack) {
        for (ChessPiece piece : ChessBoard.chessPieces) {
            if (piece != null && piece instanceof King && piece.side == isBlack) {
                return piece.position;
            }
        }
        return null;
    }

    public static boolean wouldMovePutKingInCheck(ChessPiece piece, Vector2 targetPos, boolean isBlack) {
        // First check if position is valid
        if (targetPos.x < 0 || targetPos.x >= ChessBoard.boardSize
                || targetPos.y < 0 || targetPos.y >= ChessBoard.boardSize) {
            return true; // Invalid positions are considered "in check"
        }

        Vector2 originalPos = piece.position;
        int capturedId = ChessBoard.GetPieceIdAtPos(targetPos);
        ChessPiece capturedPiece = capturedId != -1 ? ChessBoard.chessPieces[capturedId] : null;

        // Simulate the move
        piece.SetToPosition(targetPos);
        if (capturedId != -1) {
            ChessBoard.SetPieceIdAtPos(targetPos, piece.id);
            ChessBoard.SetPieceIdAtPos(originalPos, -1);
        }

        boolean inCheck = isInCheck(isBlack);

        //Undo the simulation
        piece.SetToPosition(originalPos);
        if (capturedId != -1) {
            ChessBoard.chessPieces[capturedId] = capturedPiece;
            ChessBoard.SetPieceIdAtPos(targetPos, capturedId);
        } else {
            ChessBoard.SetPieceIdAtPos(targetPos, -1);
        }

        return inCheck;
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
}
