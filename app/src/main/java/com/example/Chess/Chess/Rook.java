package com.example.Chess.Chess;

import com.example.Chess.Globals;
import com.example.Chess.Vector2;

public class Rook extends ChessPiece {

    public Rook(Vector2 position, boolean side)
    {
        this.side = side;
        this.position = position;
    }

    @Override
    public String GetImageLocation()
    {
        //should be a rook
        return Globals.ImageDirectory + Globals.PieceImages[3 + (6 * (side ? 1 : 0))];
    }

    @Override
    public boolean TryMove(Vector2 pos)
    {
        //Since rook can only move in straight lines we only need to check in the direction to the pos move

        //we just need to check 4 directions
        Vector2 direction = new Vector2(0,0);
        int moveAmount = 0;
        for (int i = 0; i < 4; i++)
        {
            if(i == 0)
            {
                //check to the left
                direction = new Vector2(-1, 0);
                //get the amount we need to move by or if we can move
                moveAmount = CheckInDirection(direction, position, pos);
                if(moveAmount != 0)
                    break;
            }
            else if (i == 1)
            {
                //check to the top
                direction = new Vector2(0, -1);
                //get the amount we need to move by or if we can move
                moveAmount = CheckInDirection(direction, position, pos);
                if(moveAmount != 0)
                    break;
            }
            else if (i == 2)
            {
                //check to the right
                direction = new Vector2(1, 0);
                //get the amount we need to move by or if we can move
                moveAmount = CheckInDirection(direction, position, pos);
                if(moveAmount != 0)
                    break;
            }
            else {
                //check to the bottom
                direction = new Vector2(0, 1);
                //get the amount we need to move by or if we can move
                moveAmount = CheckInDirection(direction, position, pos);
                if(moveAmount != 0)
                    break;
            }
        }

        //if we can move to our selected position then we move and return true
        if(moveAmount != 0)
        {
            //set our old position id to -1 as there is nothing there now
            ChessBoard.SetPieceIdAtPos(position, -1);
            position = Vector2.Add(position, Vector2.Mul(direction, moveAmount));
            //set our new position id to be the id of this chess piece
            ChessBoard.SetPieceIdAtPos(position, id);
            return true;
        }

        return false;
    }

    @Override
    public boolean CheckMove(Vector2 pos)
    {
        //Since rook can only move in straight lines we only need to check in the direction to the pos move

        //we just need to check 4 directions
        Vector2 direction = new Vector2(0,0);
        int moveAmount = 0;
        for (int i = 0; i < 4; i++)
        {
            if(i == 0)
            {
                //check to the left
                direction = new Vector2(-1, 0);
                //get the amount we need to move by or if we can move
                moveAmount = CheckInDirection(direction, position, pos);
                if(moveAmount != 0)
                    break;
            }
            else if (i == 1)
            {
                //check to the top
                direction = new Vector2(0, -1);
                //get the amount we need to move by or if we can move
                moveAmount = CheckInDirection(direction, position, pos);
                if(moveAmount != 0)
                    break;
            }
            else if (i == 2)
            {
                //check to the right
                direction = new Vector2(1, 0);
                //get the amount we need to move by or if we can move
                moveAmount = CheckInDirection(direction, position, pos);
                if(moveAmount != 0)
                    break;
            }
            else {
                //check to the bottom
                direction = new Vector2(0, 1);
                //get the amount we need to move by or if we can move
                moveAmount = CheckInDirection(direction, position, pos);
                if(moveAmount != 0)
                    break;
            }
        }

        //if we can move to our selected position then we move and return true
        return moveAmount != 0;
    }

    @Override
    public ChessPiece Copy()
    {
        return new Rook(this.position, this.side);
    }
}