package com.example.Chess;

import com.example.Chess.Chess.ChessBoard;
import com.example.Chess.Chess.ChessPiece;
import com.example.Chess.Chess.ChessSound;
import com.example.Chess.Network.NetworkManager;
import com.example.Chess.Network.Packets.PieceMovePacket;

import static com.example.Chess.UI.QuantumUiButton.CheckRightClick;
import static com.example.Chess.UI.QuantumUiButton.IsQuantumUiOpen;

import com.example.Chess.UI.QuantumUiButton;
import com.raylib.Raylib;

import static com.raylib.Colors.*;

public class Interaction {

    //By default, the mouse is not selecting a piece
    static int currentSelectedPiece = -1;

    //Stores position when piece was selected
    private static Vector2 currentSelectedPosition;
    public static boolean disableInteraction;

    //Track whose turn it is (true = black's turn; false = white's turn)
    public static boolean isBlackTurn = false;
    //if we are in a multiplayer game we need to lock our movement
    public static boolean isOurTurn = false;

    //Checks if it is black's turn
    public static boolean IsBlackTurn() {
        return isBlackTurn;
    }

    public static void SetTurn(boolean isBlackTurn) {
        Interaction.isBlackTurn = isBlackTurn;
    }

    public static void Init() {
        isBlackTurn = false;
        isOurTurn = false;
        if (!NetworkManager.isClient) {
            isOurTurn = true;
        }
    }

    public static void Update() {

        //Convert mouse position to the board position
        Vector2 mousePos = new Vector2(Raylib.GetMousePosition().x(), Raylib.GetMousePosition().y());

        //Scalar multiplcation
        mousePos = Vector2.Mul(Vector2.Div(mousePos, new Vector2(Globals.ChessWidth, Globals.ScreenHeight)), ChessBoard.boardSize);
        mousePos = Vector2.Floor(mousePos);

        if (ChessBoard.GetPieceIdAtPos(mousePos) != -1) {
            //Highlight the current mouse position
            HighlightSpot();
        }

        //Highlight the current mouse position
        HighlightSpot();
        if (Raylib.IsMouseButtonPressed(Raylib.MOUSE_BUTTON_RIGHT) && QuantumUiButton.IsMouseOverPiece() == true) {
            CheckRightClick();
            IsQuantumUiOpen = true;
        }
        if (Raylib.IsMouseButtonPressed(Raylib.MOUSE_BUTTON_LEFT)) {

            //if we are connected to a server block movement if its not our turn
            if (NetworkManager.isClient) {
                if (isOurTurn) {
                    HandlePieceMove(mousePos);
                }
            } else {
                HandlePieceMove(mousePos);
            }
        }

        //If we have a selected piece, show its possible moves and follow mouse
        if (currentSelectedPiece != -1) {
            ChessPiece piece = ChessBoard.chessPieces[currentSelectedPiece];

            //Draw possible moves
            piece.position = currentSelectedPosition;
            piece.DrawPossibleMoves();

            //Make piece follow mouse (if within chessboard)
            if (Raylib.GetMousePosition().x() < Globals.ChessWidth - 1) {
                piece.position = mousePos;
            }
        }
    }

    private static void HandlePieceMove(Vector2 mousePos) {
        if (currentSelectedPiece == -1) {
            //First click - select piece
            ChessPiece piece = ChessBoard.GetChessPieceAtPos(mousePos);

            //Only allow selecting pieces of the current player's color
            if (piece.id != -1 && piece.side == isBlackTurn) {
                currentSelectedPosition = mousePos;
                currentSelectedPiece = ChessBoard.GetPieceIdAtPos(mousePos);
            }
        } else {
            //Second click - try to move the piece
            ChessPiece selectedPiece = ChessBoard.chessPieces[currentSelectedPiece];

            //Reset position temporarily for move validation
            selectedPiece.position = currentSelectedPosition;

            if (selectedPiece.TryMove(mousePos)) {
                //Valid move - switch turns
                isBlackTurn = !isBlackTurn;

                if (NetworkManager.isClient) {
                    PieceMovePacket packet = new PieceMovePacket(selectedPiece.position, currentSelectedPiece, isBlackTurn);
                    NetworkManager.client.SendPacket(packet);
                    isOurTurn = false;
                }
            }
            ChessSound.PlayMove();
            //De-select piece regardless of move success
            currentSelectedPiece = -1;
        }
    }

    private static void HighlightSpot() {
        Vector2 mousePos = new Vector2(Raylib.GetMousePosition().x(), Raylib.GetMousePosition().y());

        // Check if it's out of the board
        if (mousePos.x > Globals.ChessWidth - 1) {
            return;
        }

        mousePos = Vector2.Mul(Vector2.Div(mousePos, new Vector2(Globals.ChessWidth, Globals.ScreenHeight)), ChessBoard.boardSize);
        mousePos = Vector2.Floor(mousePos);

        // Only highlight if there's a piece and it's the current player's turn
        ChessPiece piece = ChessBoard.GetChessPieceAtPos(mousePos);
        if (piece.id == -1 || piece.side != isBlackTurn) {
            return;
        }

        // Highlight color (green for current player's piece)
        Raylib.Color highColor = GREEN;
        highColor.a((byte) (65 * (Math.sin(Raylib.GetTime() * 5) + 1)));

        double xScale = Globals.ChessWidth / (double) ChessBoard.boardSize;
        double yScale = Globals.ScreenHeight / (double) ChessBoard.boardSize;

        Raylib.DrawRectangle((int) (mousePos.x * xScale), (int) (mousePos.y * yScale), 
            (int) xScale, (int) yScale, highColor);
    }
}
