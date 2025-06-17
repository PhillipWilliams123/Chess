package com.example.Chess;

import com.example.Chess.Chess.ChessBoard;
import com.example.Chess.Chess.ChessPiece;
import com.example.Chess.Chess.ChessSound;
import com.example.Chess.Chess.GameState;
import com.example.Chess.Chess.King;
import com.example.Chess.Network.NetworkManager;
import com.example.Chess.Network.Packets.PieceMovePacket;
import com.example.Chess.Rules.QuantumRules;
import com.example.Chess.UI.QuantumUiButton;
import com.raylib.Raylib;
import static com.raylib.Colors.*;
import static com.raylib.Raylib.GetMouseX;
import static com.raylib.Raylib.GetMouseY;
import static com.raylib.Raylib.IsMouseButtonPressed;
import static com.raylib.Raylib.MOUSE_BUTTON_LEFT;
import static com.raylib.Raylib.MOUSE_BUTTON_RIGHT;

public class Interaction {

    // Piece selection
    public static int currentSelectedPiece = -1;
    private static Vector2 currentSelectedPosition;
    public static boolean disableInteraction;

    // Turn management
    public static boolean isBlackTurn = false;
    public static boolean isOurTurn = false;

    public static boolean IsBlackTurn() {
        return isBlackTurn;
    }

    public static void SetTurn(boolean isBlackTurn) {
        Interaction.isBlackTurn = isBlackTurn;
    }

    public static void Init() {
        isBlackTurn = false;
        isOurTurn = !NetworkManager.isClient;
    }

    public static void Update() {
        Vector2 mousePos = getMouseBoardPos();

        // Right click - open quantum UI
        if (IsMouseButtonPressed(MOUSE_BUTTON_RIGHT)) {
            if (GetMouseX() < Globals.ChessWidth) {
                ChessPiece piece = ChessBoard.GetChessPieceAtPos(mousePos);
                if (piece != null && piece.id != -1 && piece.side == isBlackTurn
                        && !(piece instanceof King)) {  // Add this condition
                    QuantumUiButton.showForPiece(piece);
                }
            }
        }

        // Left click - normal moves or quantum operations
        if (IsMouseButtonPressed(MOUSE_BUTTON_LEFT)) {
            if (QuantumUiButton.IsQuantumUiOpen) {
                // Let quantum UI handle clicks
                QuantumUiButton.updateButtons();
            } else {
                handleNormalMove(mousePos);
            }
        }

        // Highlight possible moves
        if (currentSelectedPiece != -1) {
            ChessPiece piece = ChessBoard.chessPieces[currentSelectedPiece];
            piece.position = currentSelectedPosition;
            piece.DrawPossibleMoves();
            piece.position = mousePos;
        }
    }

    private static void handleNormalMove(Vector2 mousePos) {
        if (currentSelectedPiece == -1) {
            // Select piece
            ChessPiece piece = ChessBoard.GetChessPieceAtPos(mousePos);
            if (piece != null && piece.id != -1 && piece.side == isBlackTurn) {
                currentSelectedPiece = piece.id;
                currentSelectedPosition = new Vector2(piece.position);
            }
        } else {
            // Attempt move
            ChessPiece selectedPiece = ChessBoard.chessPieces[currentSelectedPiece];
            selectedPiece.position = currentSelectedPosition;

            // Check if clicking on current position (don't allow self-deletion)
            if (mousePos.equals(selectedPiece.position)) {
                currentSelectedPiece = -1;
                return;
            }

            ChessPiece target = ChessBoard.GetChessPieceAtPos(mousePos);

            if (target.id != -1) {
                // Handle captures
                if (QuantumRules.isQuantumPiece(selectedPiece) || QuantumRules.isQuantumPiece(target)) {
                    QuantumRules.resolveQuantumCapture(selectedPiece, target, mousePos);
                    isBlackTurn = !isBlackTurn;

                    // Send network packet
                    if (NetworkManager.isClient) {
                        PieceMovePacket packet = new PieceMovePacket(selectedPiece.position, currentSelectedPiece, GameState.isBlackTurn);
                        NetworkManager.client.SendPacket(packet);
                    }
                } else {
                    // Normal capture
                    if (selectedPiece.TryMove(mousePos)) {
                        isBlackTurn = !isBlackTurn;
                        if (NetworkManager.isClient) {
                            PieceMovePacket packet = new PieceMovePacket(selectedPiece.position, currentSelectedPiece, GameState.isBlackTurn);
                            NetworkManager.client.SendPacket(packet);
                        }
                        ChessSound.PlayMove();
                    }
                }
            } else {
                // Normal move
                if (selectedPiece.TryMove(mousePos)) {
                    isBlackTurn = !isBlackTurn;
                    if (NetworkManager.isClient) {
                        PieceMovePacket packet = new PieceMovePacket(selectedPiece.position, currentSelectedPiece, GameState.isBlackTurn);
                        NetworkManager.client.SendPacket(packet);
                    }
                    ChessSound.PlayMove();
                }
            }
            currentSelectedPiece = -1;
        }
    }

    private static Vector2 getMouseBoardPos() {
        Vector2 mousePos = new Vector2(GetMouseX(), GetMouseY());
        return Vector2.Floor(Vector2.Mul(
                Vector2.Div(mousePos, new Vector2(Globals.ChessWidth, Globals.ScreenHeight)),
                ChessBoard.boardSize
        ));
    }

    public static void HighlightSpot() {
        //Draw a square to show what position is highlighted
        Vector2 mousePos = new Vector2(Raylib.GetMousePosition().x(), Raylib.GetMousePosition().y());

        //Check if it's out of the board
        if (mousePos.x > Globals.ChessWidth - 1) {
            return;
        }

        mousePos = Vector2.Mul(Vector2.Div(mousePos, new Vector2(Globals.ChessWidth, Globals.ScreenHeight)), ChessBoard.boardSize);

        //Highlight color (green for current player's turn, red for opponent's turn)
        Raylib.Color highColor = RED;
        if (GameState.isOurTurn) {
            if (ChessBoard.GetChessPieceAtPos(mousePos).side == !GameState.isBlackTurn) {
                highColor = GREEN;
            }
        }
        highColor.a((byte) (65 * (Math.sin(Raylib.GetTime() * 5) + 1)));

        double xScale = Globals.ChessWidth / (double) ChessBoard.boardSize;
        double yScale = Globals.ScreenHeight / (double) ChessBoard.boardSize;
        mousePos = Vector2.Floor(mousePos);

        Raylib.DrawRectangle((int) (mousePos.x * xScale), (int) (mousePos.y * yScale), (int) xScale, (int) yScale, highColor);
    }
}
