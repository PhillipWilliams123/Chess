package com.example.Chess.UI;

import com.example.Chess.Chess.ChessBoard;
import com.example.Chess.Chess.ChessPiece;
import com.example.Chess.Rules.QuantumRules;
import com.example.Chess.Vector2;
import com.raylib.Raylib;

import static com.example.Chess.Chess.ChessBoard.GetChessPieceAtPos;
import static com.example.Chess.Chess.ChessBoard.PosInBounds;
import com.example.Chess.Globals;
import com.example.Chess.Interaction;
import static com.raylib.Colors.*;
import static com.raylib.Raylib.*;
import java.util.List;
import java.util.ArrayList;

public class QuantumUiButton {

    public static boolean IsQuantumUiOpen = false;
    public static UiButton[] Quantumbuttons;
    private static Vector2 mergeHighlightPosition;

    private static List<Vector2> mergePositions = new ArrayList<>();

    // Quantum action states
    private static int quantumActionState = 0; // 0=idle, 1=split select piece, 2=split pos1, 3=split pos2
    private static ChessPiece selectedPiece;
    private static Vector2 splitPosition1;
    public static boolean showingSplitMoves = false;

    private static List<Vector2> possibleMergePositions = new ArrayList<>();
    private static boolean showingMergeMoves = false;
    private static ChessPiece mergeTargetPiece;

    public static void Initialize() {
        Quantumbuttons = new UiButton[3];
        updateButtonPositions();
    }

    private static Vector2 originalPiecePosition;

    public static void showForPiece(ChessPiece piece) {
        if (piece == null || piece.id == -1) {
            return;
        }

        // Ensure buttons are initialized
        if (Quantumbuttons == null) {
            Initialize();
        }

        originalPiecePosition = new Vector2(piece.position);
        IsQuantumUiOpen = true;
        selectedPiece = piece;
        quantumActionState = 0;
        showingSplitMoves = false;
        showingMergeMoves = false;

        // Update button positions and states
        updateButtonPositions();

        // Set merge button visibility based on quantum state
        if (Quantumbuttons[1] != null) {
            Quantumbuttons[1].draw = QuantumRules.isQuantumPiece(piece);
        }
    }

    public static void hide() {
        IsQuantumUiOpen = false;
        quantumActionState = 0;
        selectedPiece = null;
        splitPosition1 = null;
        showingSplitMoves = false;
        showingMergeMoves = false;
        possibleMergePositions.clear();
        mergeTargetPiece = null;
    }

    public static void updateButtons() {
        if (!IsQuantumUiOpen) {
            return;
        }

        Vector2 mousePos = getMouseBoardPos();

        // Only process left clicks
        if (!IsMouseButtonPressed(MOUSE_BUTTON_RIGHT)) {
            return;
        }

        // First check button clicks
        if (Quantumbuttons[0] != null && Quantumbuttons[0].IsButtonClicked()) {
            handleSplitAction();
            return;
        }
        if (Quantumbuttons[1] != null && Quantumbuttons[1].IsButtonClicked()) {
            handleMergeAction();
            return;
        }
        if (Quantumbuttons[2] != null && Quantumbuttons[2].IsButtonClicked()) {
            hide();
            return;
        }

        //Then check board clicks only if no buttons were clicked
        if (GetMouseX() < Globals.ChessWidth) { // Only on board area
            handleBoardClick(mousePos);
        }
    }

    private static int splitStep = 0; // 0=idle, 1=first position, 2=second position
    private static Vector2 firstSplitPos;

    private static void handleSplitAction() {
        if (quantumActionState == 0) {
            // Verify we're splitting a non-quantum piece
            if (QuantumRules.isQuantumPiece(selectedPiece)) {
                System.err.println("Cannot split an already quantum piece!");
                return;
            }
            quantumActionState = 1;
            showingSplitMoves = true;
            splitStep = 1;
            Quantumbuttons[0].text = "Cancel Split";
            Quantumbuttons[1].draw = false;
        } else {
            resetSplitState();
        }
    }

    private static void handleMergeAction() {
        if (quantumActionState == 0 && QuantumRules.isQuantumPiece(selectedPiece)) {
            if (Quantumbuttons[1] == null) {
                return;
            }

            quantumActionState = 3;
            showingMergeMoves = true;
            Quantumbuttons[1].text = "Cancel Merge";

            if (Quantumbuttons[0] != null) {
                Quantumbuttons[0].draw = false;
            }

            // Calculate moves from original position
            Vector2 tempPos = selectedPiece.position;
            selectedPiece.position = originalPiecePosition;
            mergePositions = getPossibleMoves(selectedPiece);
            selectedPiece.position = tempPos;
        } else {
            resetMergeState();
        }
    }

    public static void handleBoardClick(Vector2 boardPos) {
        if (showingSplitMoves && selectedPiece != null) {
            ChessPiece clickedPiece = GetChessPieceAtPos(boardPos);

            if (showingSplitMoves && selectedPiece != null) {
                if (splitStep == 1) {
                    // First split position
                    if (isValidSplitPosition(boardPos)) {
                        firstSplitPos = boardPos;
                        splitStep = 2;
                    }
                } else if (splitStep == 2) {
                    // Second split position
                    if (isValidSplitPosition(boardPos) && !boardPos.equals(firstSplitPos)) {
                        // THIS IS WHERE WE CALL completeSplit
                        completeSplit(firstSplitPos, boardPos);

                        // End turn and cleanup
                        Interaction.isBlackTurn = !Interaction.isBlackTurn;
                        resetSplitState();
                        hide();
                    }
                }
            }
        } else if (showingMergeMoves && quantumActionState == 3) {
            // Validate move from original position
            Vector2 tempPos = selectedPiece.position;
            selectedPiece.position = originalPiecePosition;
            boolean isValidMove = selectedPiece.CheckMove(boardPos);
            selectedPiece.position = tempPos;

            if (isValidMove) {
                // Perform the merge (this will create a new normal piece)
                if (QuantumRules.attemptMerge(selectedPiece, boardPos)) {
                    // Success - hide UI and switch turns
                    Interaction.isBlackTurn = !Interaction.isBlackTurn;
                    hide();
                }
            }
        }
    }

    private static boolean isValidSplitPosition(Vector2 pos) {
        if (!ChessBoard.PosInBounds(pos)) {
            return false;
        }

        // Prevent selecting existing quantum pieces
        ChessPiece pieceAtPos = ChessBoard.GetChessPieceAtPos(pos);
        if (pieceAtPos.id != -1 && QuantumRules.isQuantumPiece(pieceAtPos)) {
            return false;
        }

        // Check move against original position
        Vector2 tempPos = selectedPiece.position;
        selectedPiece.position = originalPiecePosition;
        boolean isValid = selectedPiece.CheckMove(pos);
        selectedPiece.position = tempPos;

        return isValid;
    }

    private static void completeSplit(Vector2 pos1, Vector2 pos2) {
        // Delete original FIRST
        ChessBoard.DeletePiece(selectedPiece.position);

        // Create and add both pieces
        ChessPiece piece1 = createQuantumPiece(pos1);
        ChessPiece piece2 = createQuantumPiece(pos2);

        // Entangle them
        QuantumRules.createEntanglement(piece1, piece2);
    }

    private static ChessPiece createQuantumPiece(Vector2 pos) {
        ChessPiece piece = selectedPiece.Copy();
        piece.position = pos;
        piece.id = ChessBoard.getNextAvailableId();
        ChessBoard.AddPiece(piece);
        return piece;
    }

    private static void resetSplitState() {
        splitStep = 0;
        firstSplitPos = null;
        quantumActionState = 0;
        showingSplitMoves = false;
        Quantumbuttons[0].text = "Split Piece";
        Quantumbuttons[1].draw = true; // Show merge button again
    }

    private static void resetMergeState() {
        quantumActionState = 0;
        showingMergeMoves = false;
        //possibleMergePositions.clear();
        Quantumbuttons[1].text = "Merge Pieces";
        Quantumbuttons[0].draw = true; // Show split button again
        //mergeTargetPiece = null;

        mergeHighlightPosition = null;
    }

    public static void RenderButtons() {
        if (!IsQuantumUiOpen) {
            return;
        }

        // Draw buttons
        for (UiButton button : Quantumbuttons) {
            if (button != null && button.draw) {
                button.DrawButton();
            }
        }

        // Draw split position highlights if in split mode
        if (showingSplitMoves) {
            drawSplitHighlights();
        }

        if (showingMergeMoves) {
            drawMergeHighlights();
        }
    }

    private static void drawSplitHighlights() {
        double xScale = Globals.ChessWidth / (double) ChessBoard.boardSize;
        double yScale = Globals.ScreenHeight / (double) ChessBoard.boardSize;

        // Temporarily set piece position to original for move calculation
        Vector2 tempPos = selectedPiece.position;
        selectedPiece.position = originalPiecePosition;

        // Highlight all valid normal moves in blue
        for (int x = 0; x < ChessBoard.boardSize; x++) {
            for (int y = 0; y < ChessBoard.boardSize; y++) {
                Vector2 pos = new Vector2(x, y);
                if (ChessBoard.GetPieceIdAtPos(pos) == -1 && selectedPiece.CheckMove(pos)) {
                    Color color = BLUE;
                    color.a((byte) 100);
                    DrawRectangle(
                            (int) (x * xScale),
                            (int) (y * yScale),
                            (int) xScale,
                            (int) yScale,
                            color
                    );
                }
            }
        }

        // Restore piece position
        selectedPiece.position = tempPos;
    }

    private static void drawMergeHighlights() {
        double xScale = Globals.ChessWidth / (double) ChessBoard.boardSize;
        double yScale = Globals.ScreenHeight / (double) ChessBoard.boardSize;

        // Temporarily set to original position for move calculation
        Vector2 tempPos = selectedPiece.position;
        selectedPiece.position = originalPiecePosition;

        // Highlight all valid merge positions
        for (int x = 0; x < ChessBoard.boardSize; x++) {
            for (int y = 0; y < ChessBoard.boardSize; y++) {
                Vector2 pos = new Vector2(x, y);
                // Only highlight empty squares that are valid moves
                if (ChessBoard.GetPieceIdAtPos(pos) == -1 && selectedPiece.CheckMove(pos)) {
                    Color color = RED;
                    color.a((byte) 100);
                    DrawRectangle(
                            (int) (x * xScale),
                            (int) (y * yScale),
                            (int) xScale,
                            (int) yScale,
                            color
                    );
                }
            }
        }

        // Restore position
        selectedPiece.position = tempPos;
    }

    private static Vector2 getMouseBoardPos() {
        // Convert screen coordinates to board coordinates
        double xScale = Globals.ChessWidth / (double) ChessBoard.boardSize;
        double yScale = Globals.ScreenHeight / (double) ChessBoard.boardSize;

        int mouseX = GetMouseX();
        int mouseY = GetMouseY();

        // Calculate board position
        int boardX = (int) (mouseX / xScale);
        int boardY = (int) (mouseY / yScale);

        return new Vector2(boardX, boardY);
    }

    private static List<Vector2> getPossibleMoves(ChessPiece piece) {
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

    private static void updateButtonPositions() {
        if (selectedPiece == null) {
            return;
        }
        int x = 700;
        int y = 400;

        // Initialize all buttons
        Quantumbuttons[0] = new UiButton(new Vector2(x, y), new Vector2(150, 40), "Split Piece", true);
        Quantumbuttons[1] = new UiButton(new Vector2(x, y + 50), new Vector2(150, 40), "Merge Pieces", true);
        Quantumbuttons[2] = new UiButton(new Vector2(x, y + 100), new Vector2(150, 40), "Cancel", true);

        // Set initial visibility
        if (selectedPiece != null) {
            Quantumbuttons[1].draw = QuantumRules.isQuantumPiece(selectedPiece);
        }
    }
}
