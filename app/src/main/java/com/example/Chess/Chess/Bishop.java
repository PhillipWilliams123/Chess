package com.example.Chess.Chess;

import com.example.Chess.Vector2;
import com.example.Chess.Globals;

public class Bishop extends ChessPiece {

    //Needs a constructor for the position
    public Bishop(Vector2 position, boolean side) {
        this.side = side;
        this.position = position;
    }

    @Override
    public String GetImageLocation() {
        return Globals.ImageDirectory + Globals.PieceImages[2 + (6 * (side ? 1 : 0))];
    }

    @Override
    public boolean TryMove(Vector2 pos) {
        // Bishop can move like a rook or a bishop, so we check all 8 directions
        Vector2[] directions = {
            new Vector2(-1, -1), // Top-left
            new Vector2(1, -1), // Top-right
            new Vector2(-1, 1), // Bottom-left
            new Vector2(1, 1) // Bottom-right
        };

        for (Vector2 direction : directions) {
            int moveAmount = CheckInDirection(direction, position, pos);
            if (moveAmount != 0) {
                TryTakePiece(Vector2.Add(position, Vector2.Mul(direction, moveAmount)));
                SetToPosition(Vector2.Add(position, Vector2.Mul(direction, moveAmount)));
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean CheckMove(Vector2 pos) {
        // Queen can move like a rook or a bishop, so we check all 8 directions
        Vector2[] directions = {
            new Vector2(-1, -1), // Top-left
            new Vector2(1, -1), // Top-right
            new Vector2(-1, 1), // Bottom-left
            new Vector2(1, 1) // Bottom-right
        };
        
        for (Vector2 direction : directions) {
            int moveAmount = CheckInDirection(direction, position, pos);
            if (moveAmount != 0) {
                return Vector2.Add(position, Vector2.Mul(direction, moveAmount)).equals(pos);
            }
        }
        return false;
    }

    @Override
    public ChessPiece Copy() {
        return new Bishop(this.position, this.side);
    }

    @Override
    public int GetPieceType()
    {
        return 3;
    }
}
