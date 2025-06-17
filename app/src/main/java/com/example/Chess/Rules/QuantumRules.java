package com.example.Chess.Rules;

import com.example.Chess.Chess.ChessBoard;
import com.example.Chess.Chess.ChessPiece;
import com.example.Chess.Chess.GameState;
import com.example.Chess.Chess.King;
import com.example.Chess.Vector2;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class QuantumRules {

    private static final Random random = new Random();
    private static final Map<ChessPiece, ChessPiece> entangledPieces = new HashMap<>();
    private static final List<PendingCollapse> pendingCollapses = new ArrayList<>();

    public static final Map<Integer, Integer> entanglementMap = new HashMap<>();

    // In QuantumRules.java:
    public static void createEntanglement(ChessPiece piece1, ChessPiece piece2) {
        // Clear any previous entanglement
        entanglementMap.remove(piece1.id);
        entanglementMap.remove(piece2.id);

        // Create new entanglement
        entanglementMap.put(piece1.id, piece2.id);
        entanglementMap.put(piece2.id, piece1.id);
    }

    public static boolean isQuantumPiece(ChessPiece piece) {
        if (piece == null) {
            return false;
        }

        boolean isQuantum = entanglementMap.containsKey(piece.id);
        return isQuantum;
    }

    public static ChessPiece getEntangledPiece(ChessPiece piece) {
        if (!isQuantumPiece(piece)) {
            return null;
        }
        int otherId = entanglementMap.get(piece.id);
        return ChessBoard.chessPieces[otherId];
    }

    private static class PendingCollapse {

        ChessPiece attacker;
        ChessPiece quantumPiece;
        Vector2 capturePos;

        public PendingCollapse(ChessPiece attacker, ChessPiece quantumPiece, Vector2 capturePos) {
            this.attacker = attacker;
            this.quantumPiece = quantumPiece;
            this.capturePos = capturePos;
        }
    }

    // Quantum piece management
    public static boolean canSplit(ChessPiece piece) {
        if (piece == null) {
            return false;
        }
        // King cannot split, all other pieces can if they're not already quantum
        return !(piece instanceof King)
                && !isQuantumPiece(piece)
                && !isShadowPiece(piece);
    }

    private static boolean isShadowPiece(ChessPiece piece) {
        return entangledPieces.containsValue(piece);
    }

    // Split functionality
    public static boolean splitPiece(ChessPiece originalPiece, Vector2 pos1, Vector2 pos2) {
        // Create first quantum piece
        ChessPiece split1 = originalPiece.Copy();
        split1.position = pos1;
        split1.id = ChessBoard.getNextAvailableId();

        // Create second quantum piece 
        ChessPiece split2 = originalPiece.Copy();
        split2.position = pos2;
        split2.id = ChessBoard.getNextAvailableId();

        ChessBoard.DeletePiece(originalPiece.position);

        // 2. Add both pieces to board FIRST
        ChessBoard.AddPiece(split1);
        ChessBoard.AddPiece(split2);

        // 3. THEN create entanglement
        QuantumRules.createEntanglement(split1, split2);

        // 4. Finally init visuals
        split1.Init();
        split2.Init();

        return true;
    }

    // Merge functionality (simplified approach)
    public static boolean attemptMerge(ChessPiece piece, Vector2 mergePos) {
        ChessPiece otherPiece = getEntangledPiece(piece);
        if (otherPiece == null) {
            return false;
        }

        // Create new normal piece (white)
        ChessPiece mergedPiece = piece.Copy();
        mergedPiece.position = mergePos;
        mergedPiece.id = ChessBoard.getNextAvailableId();

        // Remove both quantum pieces
        ChessBoard.DeletePiece(piece.position);
        ChessBoard.DeletePiece(otherPiece.position);

        // Clear entanglement
        clearEntanglement(piece, otherPiece);

        // Add the new normal piece
        ChessBoard.AddPiece(mergedPiece);

        return true;
    }

    public static void clearEntanglement(ChessPiece piece1, ChessPiece piece2) {
        if (piece1 != null) {
            entanglementMap.remove(piece1.id);
        }
        if (piece2 != null) {
            entanglementMap.remove(piece2.id);
        }
    }

    public static List<Vector2> getPossibleMoves(ChessPiece piece) {
        List<Vector2> moves = new ArrayList<>();
        for (int x = 0; x < ChessBoard.boardSize; x++) {
            for (int y = 0; y < ChessBoard.boardSize; y++) {
                Vector2 pos = new Vector2(x, y);
                if (piece.CheckMove(pos)) {
                    moves.add(pos);
                }
            }
        }
        return moves;
    }

    public static boolean areEntangled(ChessPiece piece1, ChessPiece piece2) {
        return entangledPieces.containsKey(piece1) && entangledPieces.get(piece1) == piece2;
    }

    // Collapse handling for captures
    public static void attemptCapture(ChessPiece attacker, ChessPiece target, Vector2 capturePos) {
        if (isQuantumPiece(attacker) || isQuantumPiece(target)) {
            pendingCollapses.add(new PendingCollapse(attacker, target, capturePos));
        } else {
            // Normal capture
            if (target.TryTakePiece(capturePos)) {
                attacker.SetToPosition(capturePos);
            }
        }
    }

    /*public static void resolveQuantumCapture(ChessPiece attacker, ChessPiece target, Vector2 capturePos) {
        boolean attackerIsQuantum = isQuantumPiece(attacker);
        boolean targetIsQuantum = isQuantumPiece(target);

        if (attackerIsQuantum && targetIsQuantum) {
            // Quantum vs Quantum capture
            if (random.nextBoolean()) {
                // Both target pieces are captured
                ChessBoard.DeletePiece(target.position);
                ChessPiece otherTarget = getEntangledPiece(target);
                if (otherTarget != null) {
                    ChessBoard.DeletePiece(otherTarget.position);
                    clearEntanglement(target, otherTarget);
                }
                attacker.SetToPosition(capturePos);
            } else {
                // Both attacker pieces are captured
                ChessBoard.DeletePiece(attacker.position);
                ChessPiece otherAttacker = getEntangledPiece(attacker);
                if (otherAttacker != null) {
                    ChessBoard.DeletePiece(otherAttacker.position);
                    clearEntanglement(attacker, otherAttacker);
                }
            }
        } else if (attackerIsQuantum) {
            // Quantum vs Normal capture
            if (random.nextBoolean()) {
                // Normal piece is captured
                ChessBoard.DeletePiece(capturePos);
            } else {
                // Both quantum pieces are captured
                ChessBoard.DeletePiece(attacker.position);
                ChessPiece otherAttacker = getEntangledPiece(attacker);
                if (otherAttacker != null) {
                    ChessBoard.DeletePiece(otherAttacker.position);
                    clearEntanglement(attacker, otherAttacker);
                }
            }
        } else if (targetIsQuantum) {
            // Normal vs Quantum capture
            if (random.nextBoolean()) {
                // Both quantum pieces are captured
                ChessBoard.DeletePiece(target.position);
                ChessPiece otherTarget = getEntangledPiece(target);
                if (otherTarget != null) {
                    ChessBoard.DeletePiece(otherTarget.position);
                    clearEntanglement(target, otherTarget);
                }
                attacker.SetToPosition(capturePos);
            } else {
                // Quantum pieces survive - just move the normal piece back
                // No need to do anything special here
            }
        }
    }*/
    public static void resolveQuantumCapture(ChessPiece attacker, ChessPiece target, Vector2 capturePos) {
        // Prevent invalid captures
        if (attacker.side == target.side || attacker.position.equals(capturePos)) {
            return;
        }

        boolean attackerIsQuantum = isQuantumPiece(attacker);
        boolean targetIsQuantum = isQuantumPiece(target);

        // Generate random outcome (0 = attacker's side wins, 1 = defender's side wins)
        int outcome = (int) (Math.random()*2);

        if (attackerIsQuantum && targetIsQuantum) {
            // Quantum vs Quantum capture
            if (outcome == 0) {
                // Both target pieces are captured
                ChessPiece otherTarget = getEntangledPiece(target);
                ChessBoard.DeletePiece(target.position);
                ChessBoard.DeletePiece(otherTarget.position);
                attacker.SetToPosition(capturePos);
            } else {
                // Both attacker pieces are captured
                ChessPiece otherAttacker = getEntangledPiece(attacker);
                ChessBoard.DeletePiece(attacker.position);
                ChessBoard.DeletePiece(otherAttacker.position);
            }
        } else if (!attackerIsQuantum && targetIsQuantum) {
            // Normal vs Quantum capture (special case)
            ChessPiece otherTarget = getEntangledPiece(target);

            if (outcome == 0) {
                // Both quantum pieces are deleted and replaced with normal version at otherTarget's position
                ChessPiece normalPiece = target.Copy();
                normalPiece.position = otherTarget.position;
                normalPiece.id = ChessBoard.getNextAvailableId();

                ChessBoard.DeletePiece(target.position);
                ChessBoard.DeletePiece(otherTarget.position);
                ChessBoard.AddPiece(normalPiece);
                attacker.SetToPosition(capturePos);
            } else {
                // Normal piece is deleted
                ChessBoard.DeletePiece(attacker.position);
            }
            clearEntanglement(target, otherTarget);
        } else if (attackerIsQuantum) {
            // Quantum vs Normal capture
            if (outcome == 0) {
                // Normal piece is captured
                ChessBoard.DeletePiece(capturePos);
                attacker.SetToPosition(capturePos);
            } else {
                // Both quantum pieces are captured
                ChessPiece otherAttacker = getEntangledPiece(attacker);
                ChessBoard.DeletePiece(attacker.position);
                ChessBoard.DeletePiece(otherAttacker.position);
                clearEntanglement(attacker, otherAttacker);
            }
        }
    }
}
