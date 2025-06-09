package com.example.Chess.Quantum;

import com.example.Chess.Chess.ChessBoard;
import com.example.Chess.Chess.ChessPiece;
import com.example.Chess.Chess.Rook;
import com.example.Chess.Network.NetworkManager;
import com.example.Chess.Network.Packets.PieceMovePacket;
import com.example.Chess.Vector2;
import com.example.Chess.Rendering.Renderer;
import com.example.Chess.Chess.Pawn;
import com.example.Chess.Globals;
import com.raylib.Raylib;

import java.util.ArrayList;

import static com.example.Chess.Chess.ChessBoard.*;
import static com.example.Chess.Interaction.HighlightSpot;
import static com.example.Chess.Interaction.currentSelectedPiece;


public class Moveset {
    static Vector2 mousePos;
    static ArrayList<ChessPiece> quantumCopies = new ArrayList<>();

    public static void RenderSecondPiece() {
        for (int i = 0; i < quantumCopies.toArray().length; i++) {
            if (quantumCopies.get(i).id == -1)
                continue;

            quantumCopies.get(i).DrawPiece();
        }
    }

    public static void DrawSecondPiece() {

        Vector2 mousePos = null;
        ChessPiece clickedPiece = null;
        if (Raylib.IsKeyPressed(Raylib.KEY_B)) {
            if (currentSelectedPiece == -1) {
                HighlightSpot();
                mousePos = new Vector2(Raylib.GetMousePosition().x(), Raylib.GetMousePosition().y());
                mousePos = Vector2.Mul(Vector2.Div(mousePos, new Vector2(Globals.ChessWidth, Globals.ScreenHeight)), boardSize);
                mousePos = Vector2.Floor(mousePos);
            } else {
                clickedPiece = GetChessPieceAtPos(mousePos);
            }
            if (clickedPiece != null && clickedPiece.id != -1) {
                ChessPiece copy = clickedPiece.Copy();
                copy.LoadImageToPiece();
                copy.position = new Vector2(mousePos.x, mousePos.y);
                quantumCopies.add(copy);
            }
        }
    }

}



