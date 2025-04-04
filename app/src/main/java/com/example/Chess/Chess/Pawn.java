package com.example.Chess.Chess;
import com.example.Chess.Vector2;
import com.example.Chess.Globals;
public class Pawn extends ChessPiece
{

    //Needs a constructor for the position
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

        // Pawn can move one square forward, or two from the starting position
        int direction = side ? 1 : -1; // White moves up (-1), Black moves down (+1)
        Vector2 forwardOne = new Vector2(0, direction);
        Vector2 captureLeft = new Vector2(-1, direction);
        Vector2 captureRight = new Vector2(1, direction);

        if ((position.y == 1 || position.y == 6) && CheckInDirection(forwardOne, position, pos) == 2) {
            TryTakePiece(Vector2.Add(position, Vector2.Mul(forwardOne, 2)));
            SetToPosition(Vector2.Add(position, Vector2.Mul(forwardOne, 2)));
            return true;
        }
        if (CheckInDirection(forwardOne, position, pos) == 1)
        {
            TryTakePiece(Vector2.Add(position, forwardOne));
            SetToPosition(Vector2.Add(position, forwardOne));
            return true;
        }
        if (CheckInDirection(captureLeft, position, pos) == 1 || CheckInDirection(captureRight, position, pos) == 1) {
            TryTakePiece(pos);
            SetToPosition(pos);
            return true;
        }

        return false;
    
    }

    @Override
    public boolean CheckMove(Vector2 pos)
    {
        // Pawn can move one square forward, or two from the starting position
        int direction = side ? 1 : -1; // White moves up (-1), Black moves down (+1)
        Vector2 forwardOne = new Vector2(0, direction);
        Vector2 captureLeft = new Vector2(-1, direction);
        Vector2 captureRight = new Vector2(1, direction);

        if ((position.y == 1 || position.y == 6) && CheckInDirection(forwardOne, position, pos) == 2) {
            return Vector2.Add(position, Vector2.Mul(forwardOne, 2)).equals(pos);
        }
        if (CheckInDirection(forwardOne, position, pos) == 1)
        {
            return Vector2.Add(position, forwardOne).equals(pos);
        }
        if (CheckInDirection(captureLeft, position, pos) == 1 || CheckInDirection(captureRight, position, pos) == 1) {
            return true;
        }

        return false;
    }

    @Override
    public ChessPiece Copy() {
          return new Pawn(this.position, this.side);
    }
}
