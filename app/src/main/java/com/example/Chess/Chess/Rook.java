package com.example.Chess.Chess;

import com.example.Chess.Globals;
import com.example.Chess.Vector2;

public class Rook extends ChessPiece {

    @Override
    public String GetImageLocation()
    {
        return Globals.ImageDirectory + "King.png";
        
    }

    @Override
    public boolean ValidMove(Vector2 pos)
    {
        //Since rook can only move in straight lines we only need to check in the direction to the pos move

        //we just need to check 4 directions
        for (int i = 0; i < 4; i++)
        {
            Vector2 direction;
            if(i == 0)
            {
                //check to the left
                direction = new Vector2(0, -1);

            }
            else if (i == 1)
            {

            }
            else if (i == 2)
            {

            }
            else if (i == 3)
            {

            }
        }

        return false;
    }

    int CheckInDirection(Vector2 dir, Vector2 pos, Vector2 wantedPos)
    {
        boolean check = false;
        int moveCount = 0;
        Vector2 posMove = Vector2.Add(pos, dir);
        while (check == false)
        {
            //if we reach the end of the boards bounds we have finished and can return true
            //or if we have reached our decided position we wanted to move to
            if(ChessBoard.PosInBounds(posMove) == false || posMove.Equals(wantedPos))
            {
                check = true;
            }

            ChessPiece piece = ChessBoard.GetChessPieceAtPos(posMove);
            //we have a piece in our moved position so this is the farthest we can go
            if(piece.id != -1)
            {
                check = true;
            }

            moveCount++;
        }

        return moveCount;
    }
}