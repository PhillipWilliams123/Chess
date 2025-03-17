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
        // Add our position to the pos
        pos = Vector2.Add(position, pos);

        // Pawn can move one square forward, or two from the starting position
        int direction = side ? -1 : 1; // White moves up (-1), Black moves down (+1)
        Vector2 forwardOne = new Vector2(0, direction);
        Vector2 forwardTwo = new Vector2(0, 2 * direction);
        Vector2 captureLeft = new Vector2(-1, direction);
        Vector2 captureRight = new Vector2(1, direction);

        if (CheckInDirection(forwardOne, position, pos) == 1) {
            position = Vector2.Add(position, forwardOne);
            return true;
        }
        if ((position.y == (side ? 6 : 1)) && CheckInDirection(forwardTwo, position, pos) == 2) {
            position = Vector2.Add(position, forwardTwo);
            return true;
        }
           if (CheckInDirection(captureLeft, position, pos) == 1 || CheckInDirection(captureRight, position, pos) == 1) {
            position = pos;
            return true;
        }

        return false;
    
    }

    @Override
    public boolean CheckMove(Vector2 pos)
    {
        // Add our position to the pos
        pos = Vector2.Add(position, pos);

        // Pawn can move one square forward, or two from the starting position
        int direction = side ? -1 : 1; // White moves up (-1), Black moves down (+1)
        Vector2 forwardOne = new Vector2(0, direction);
        Vector2 forwardTwo = new Vector2(0, 2 * direction);
        Vector2 captureLeft = new Vector2(-1, direction);
        Vector2 captureRight = new Vector2(1, direction);

        if (CheckInDirection(forwardOne, position, pos) == 1) {
            return true;
        }
        if ((position.y == (side ? 6 : 1)) && CheckInDirection(forwardTwo, position, pos) == 2) {
            return true;
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
