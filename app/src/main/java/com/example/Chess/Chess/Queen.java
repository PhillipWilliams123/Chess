package com.example.Chess.Chess;
import com.example.Chess.Vector2;
import com.example.Chess.Globals;
public class Queen extends ChessPiece
{

    //Needs a constructor for the position
public Queen(Vector2 position, boolean side) {
        this.side = side;
        this.position = position;
    }
    @Override
    public String GetImageLocation() {
        return Globals.ImageDirectory + Globals.PieceImages[4 + (6 * (side ? 1 : 0))];
    }

    @Override
    public boolean TryMove(Vector2 pos) {

        // Queen can move like a rook or a bishop, so we check all 8 directions
        Vector2[] directions = {
            new Vector2(-1, 0), // Left
            new Vector2(1, 0),  // Right
            new Vector2(0, -1), // Up
            new Vector2(0, 1),  // Down
            new Vector2(-1, -1), // Top-left
            new Vector2(1, -1),  // Top-right
            new Vector2(-1, 1),  // Bottom-left
            new Vector2(1, 1)   // Bottom-right
      
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
    public boolean CheckMove(Vector2 pos)
    {
        // Queen can move like a rook or a bishop, so we check all 8 directions
        Vector2[] directions = {
                new Vector2(-1, 0), // Left
                new Vector2(1, 0),  // Right
                new Vector2(0, -1), // Up
                new Vector2(0, 1),  // Down
                new Vector2(-1, -1), // Top-left
                new Vector2(1, -1),  // Top-right
                new Vector2(-1, 1),  // Bottom-left
                new Vector2(1, 1)   // Bottom-right

        };
        for (Vector2 direction : directions) {
            int moveAmount = CheckInDirection(direction, position, pos);
            if(moveAmount != 0)
            {
                return Vector2.Add(position, Vector2.Mul(direction, moveAmount)).equals(pos);
            }
        }

        return false;
    }

    @Override
    public ChessPiece Copy() {
      return new Queen(this.position, this.side);
    }
    @Override
    public int GetPieceType()
    {
        return 5;
    }
}
