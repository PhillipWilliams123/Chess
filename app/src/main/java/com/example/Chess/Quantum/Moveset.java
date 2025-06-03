package com.example.Chess.Quantum;

import com.example.Chess.Chess.ChessBoard;
import com.example.Chess.Chess.ChessPiece;
import com.example.Chess.Vector2;
import com.raylib.Raylib;
import com.example.Chess.Chess.Pawn;
import static com.example.Chess.Chess.ChessBoard.*;

public class Moveset {
public static void DrawSecondPiece (){
    int mouseX = (int) Raylib.GetMousePosition().x();
    int mouseY = (int) Raylib.GetMousePosition().y();
    if(GetPieceIdAtPos(new Vector2(mouseX, mouseY)) == chessPieceIds[0]){

    }
    if(GetPieceIdAtPos(new Vector2(mouseX, mouseY)) == chessPieceIds[1]){

    }
    if(GetPieceIdAtPos(new Vector2(mouseX, mouseY)) == chessPieceIds[2]){

    }
    if(GetPieceIdAtPos(new Vector2(mouseX, mouseY)) == chessPieceIds[3]){

    }
    if(GetPieceIdAtPos(new Vector2(mouseX, mouseY)) == chessPieceIds[4]){

    }
    if(GetPieceIdAtPos(new Vector2(mouseX, mouseY)) == chessPieceIds[5]){

    }
}
}
