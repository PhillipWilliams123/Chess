package com.example.Chess.Chess;

import com.example.Chess.Vector2;
import com.example.Chess.Globals;

public class Pawn extends ChessPiece {

    public Pawn(Vector2 position, boolean side) {
        this.side = side;
        this.position = position;
    }

    @Override
    public String GetImageLocation() {
        return Globals.ImageDirectory + Globals.PieceImages[0 + (6 * (side ? 1 : 0))];
    }

    @Override
    public boolean TryMove(Vector2 pos) {
        int direction = side ? -1 : 1; // White up (-1), Black down (+1)
        Vector2 oneStep = Vector2.Add(position, new Vector2(0, direction));
        Vector2 twoStep = Vector2.Add(position, new Vector2(0, 2 * direction));
        Vector2 leftCapture = Vector2.Add(position, new Vector2(-1, direction));
        Vector2 rightCapture = Vector2.Add(position, new Vector2(1, direction));
        int startRow = side ? 6 : 1;

        // Move forward 1
        if (pos.equals(oneStep) && ChessBoard.GetPieceIdAtPos(pos) == -1) {
            SetToPosition(pos);
            return true;
        }

        // Move forward 2 if on start row and path is clear
        if (position.y == startRow && pos.equals(twoStep)) {
            if (ChessBoard.GetPieceIdAtPos(oneStep) == -1 && ChessBoard.GetPieceIdAtPos(twoStep) == -1) {
                SetToPosition(pos);
                return true;
            }
        }

        // Capture diagonally left or right
        if ((pos.equals(leftCapture) || pos.equals(rightCapture)) && ChessBoard.GetPieceIdAtPos(pos) != -1) {
            ChessPiece target = ChessBoard.GetChessPieceAtPos(pos);
            if (target.side != this.side) {
                TryTakePiece(pos);
                SetToPosition(pos);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean CheckMove(Vector2 pos) {
        int direction = side ? -1 : 1;
        Vector2 oneStep = Vector2.Add(position, new Vector2(0, direction));
        Vector2 twoStep = Vector2.Add(position, new Vector2(0, 2 * direction));
        Vector2 leftCapture = Vector2.Add(position, new Vector2(-1, direction));
        Vector2 rightCapture = Vector2.Add(position, new Vector2(1, direction));
        int startRow = side ? 6 : 1;

        if (pos.equals(oneStep) && ChessBoard.GetPieceIdAtPos(pos) == -1) {
            return true;
        }

        if (position.y == startRow && pos.equals(twoStep)) {
            return ChessBoard.GetPieceIdAtPos(oneStep) == -1 && ChessBoard.GetPieceIdAtPos(twoStep) == -1;
        }

        if ((pos.equals(leftCapture) || pos.equals(rightCapture)) && ChessBoard.GetPieceIdAtPos(pos) != -1) {
            ChessPiece target = ChessBoard.GetChessPieceAtPos(pos);
            if (target.side != this.side) {
                return true;
            }
        }

        return false;
    }

    @Override
    public ChessPiece Copy() {
        return new Pawn(new Vector2(position.x, position.y), side);
    }
}
