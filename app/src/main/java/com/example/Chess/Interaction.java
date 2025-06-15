package com.example.Chess;

import com.example.Chess.Chess.ChessBoard;
import com.example.Chess.Chess.ChessPiece;
import com.example.Chess.Chess.ChessSound;
import com.example.Chess.Chess.GameState;
import com.example.Chess.Network.NetworkManager;
import com.example.Chess.Network.Packets.PieceMovePacket;

//import static com.example.Chess.UI.QuantumUiButton.CheckRightClick;
import static com.example.Chess.UI.QuantumUiButton.IsQuantumUiOpen;

import com.example.Chess.Rules.OriginalRules;
import com.example.Chess.UI.QuantumUiButton;
import com.raylib.Raylib;

import static com.raylib.Colors.*;

public class Interaction
{

    //By default, the mouse is not selecting a piece
    public static int currentSelectedPiece = -1;

    //Stores position when piece was selected
    private static Vector2 currentSelectedPosition;
    public static boolean disableInteraction;

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
        //if(Raylib.IsMouseButtonPressed(Raylib.MOUSE_BUTTON_RIGHT) && QuantumUiButton.IsMouseOverPiece()==true){
        //    CheckRightClick();
        //    IsQuantumUiOpen = true;
        //}

        if(!disableInteraction)
        {
            if (Raylib.IsMouseButtonPressed(Raylib.MOUSE_BUTTON_LEFT)) {

                //if we are connected to a server block movement if its not our turn
                if(NetworkManager.isClient)
                {
                    if(GameState.isOurTurn)
                    {
                        HandlePieceMove(mousePos);
                    }
                }
                else
                {
                    HandlePieceMove(mousePos);
                }

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
            if (piece.id != -1 && piece.side == !GameState.isBlackTurn) {
                currentSelectedPosition = mousePos;
                currentSelectedPiece = ChessBoard.GetPieceIdAtPos(mousePos);
            }
        }
        else
        {
            //Second click - try to move the piece
            ChessPiece selectedPiece = ChessBoard.chessPieces[currentSelectedPiece];

            //Reset position temporarily for move validation
            selectedPiece.position = currentSelectedPosition;
            Vector2 oldPos = selectedPiece.position;

            if(ChessBoard.GetChessPieceAtPos(mousePos).GetPieceType() == 6)
            {
                currentSelectedPiece = -1;
                return;
            }

            if (selectedPiece.TryMove(mousePos))
            {
                /*if(GameState.inCheck)
                {
                    GameState.inCheck = false;
                    GameState.inCheck = OriginalRules.isInCheck(!GameState.ourSide);
                    if(GameState.inCheck)
                    {
                        //revert move as we are still in check
                        selectedPiece.SetToPosition(oldPos);
                        currentSelectedPiece = -1;
                        return;
                    }
                }*/

                //Valid move - switch turns
                GameState.isBlackTurn = !GameState.isBlackTurn;
                if (NetworkManager.isClient)
                {
                    PieceMovePacket packet = new PieceMovePacket(selectedPiece.position, currentSelectedPiece, GameState.isBlackTurn);
                    NetworkManager.client.SendPacket(packet);
                    GameState.isOurTurn = false;
                }
            }
            ChessSound.PlayMove();
            //De-select piece regardless of move success
            currentSelectedPiece = -1;
        }
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
        if(GameState.isOurTurn)
        {
            if(ChessBoard.GetChessPieceAtPos(mousePos).side == !GameState.isBlackTurn)
                highColor = GREEN;
        }
        highColor.a((byte) (65 * (Math.sin(Raylib.GetTime() * 5) + 1)));

        double xScale = Globals.ChessWidth / (double) ChessBoard.boardSize;
        double yScale = Globals.ScreenHeight / (double) ChessBoard.boardSize;
        mousePos = Vector2.Floor(mousePos);

        Raylib.DrawRectangle((int) (mousePos.x * xScale), (int) (mousePos.y * yScale), (int) xScale, (int) yScale, highColor);
    }
}
