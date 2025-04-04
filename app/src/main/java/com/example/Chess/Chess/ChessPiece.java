package com.example.Chess.Chess;
import static com.raylib.Raylib.*;

import com.example.Chess.Globals;
import com.example.Chess.Vector2;
import com.raylib.Colors;
import com.raylib.Raylib;
import com.raylib.Raylib.Texture;
import static com.raylib.Colors.*;

/**
 * @author ericl, phillip, daniel
 */
public abstract class ChessPiece
{
    public int id;
    public Vector2 position;
    private Texture image;
    /**
     * False for white
     * True for black
     */
    public boolean side;

    public void Init()
    {
        LoadImageToPiece();
    }

    /**
     * Loads the pieces image
     */
    public void LoadImageToPiece()
    {
        //resize the images to the correct size
        Image tempImage = LoadImage(GetImageLocation());
        int size = Globals.ScreenWidth / ChessBoard.boardSize;
        ImageResize(tempImage, size, size);
        image = LoadTextureFromImage(tempImage);
    }

    /**
     * Draws the piece at its position
     */
    public void DrawPiece()
    {
        Vector2 scaledPos = ChessBoard.ScalePosToBoardSpace(position);
        DrawTexture(image, (int)scaledPos.x, (int)scaledPos.y, Globals.ChessPieceHue);
    }

    public void DrawPossibleMoves()
    {
        double xScale = Globals.ScreenWidth / (double)ChessBoard.boardSize;
        double yScale = Globals.ScreenHeight / (double)ChessBoard.boardSize;

        for (int i = 0; i < ChessBoard.boardSize; i++)
        {
            for (int j = 0; j < ChessBoard.boardSize; j++)
            {
                if(CheckMove(new Vector2(i,j)))
                {
                    Color color = GREEN;
                    color.a((byte)(65 * (Math.sin(Raylib.GetTime() * 5) + 1) + 50));

                    Raylib.DrawRectangle((int) Math.floor(i * xScale), (int) Math.floor(j * yScale), (int) xScale, (int) yScale, color);
                }
            }
        }
    }

    /**
     * Will set the piece to a position considering other systems in the ChessBoard
     * @param position the position to go to
     */
    public void SetToPosition(Vector2 position)
    {
        //set our old position id to -1 as there is nothing there now
        ChessBoard.SetPieceIdAtPos(this.position, -1);
        this.position = position;
        //set our new position id to be the id of this chess piece
        ChessBoard.SetPieceIdAtPos(this.position, id);
    }

    /**
     * Will get the image location for the piece. Is abstract so each specific piece has its own implementation and can be used here
     * @return the image location
     */
    public abstract String GetImageLocation();

    /**
     * Tries to move a piece to a position with its rules implemented in its class
     * @param pos the position to move to
     * @return true if it can make the move, false if it cannot make the move
     */
    public abstract boolean TryMove(Vector2 pos);

    /**
     * Checks if a piece can move to the position
     * @param pos the position to check if we can move
     * @return true if we can make the move, false if we cannot
     */
    public abstract boolean CheckMove(Vector2 pos);

    /**
     * Checks in a direction from a position to a wanted position, if there is a piece of a border of the chess board
     * @param dir the direction to go in (Should be integers and whole numbers)
     * @param pos the position to start from
     * @param wantedPos the position we want to go to
     * @return the number of checks it has done (can be multiplied by direction to get the final position it landed on), Will be 0 if we cannot move at all
     */
    public int CheckInDirection(Vector2 dir, Vector2 pos, Vector2 wantedPos)
    {
        int moveCount = 0;
        //since there is the case that we find a piece along the path we check
        //we need to store this in case there is a piece along the path to the wanted position
        //as if we return the first time we find a piece, it is likely not going to be along the direction
        //that we want to go to
        int moveCountPiece = 0;
        Vector2 posMove = pos;
        boolean foundPos = false;

        while (true)
        {
            posMove = Vector2.Add(posMove, dir);

            //if we reach out of bounds we have either gone out of the board or never reached our wanted position
            if(!ChessBoard.PosInBounds(posMove))
            {
                return 0;
            }

            //we have a piece in our moved position so this is the farthest we can go
            if(ChessBoard.GetPieceIdAtPos(posMove) != -1)
            {
                if(ChessBoard.GetChessPieceAtPos(posMove).side == this.side)
                    return 0;
            }

            //we have reached where we wanted to go with no issues
            if(posMove.equals(wantedPos))
            {
                //find the maximum we can go in the direction to the wanted position
                posMove = pos;
                for (int i = 0; i < moveCount + 1; i++)
                {
                    posMove = Vector2.Add(posMove, dir);
                    if(ChessBoard.GetPieceIdAtPos(posMove) != -1)
                    {
                        return i + 1;
                    }
                }
                moveCount++;
                return moveCount;
            }

            moveCount++;
        }
    }

    /**
     * Will try to take a piece at a position
     * @param position the position we want to take
     * @return true if its successful and false if it's not
     */
    public boolean TryTakePiece(Vector2 position)
    {
        //check if it's a valid position
        if(ChessBoard.GetPieceIdAtPos(position) == -1)
            return false;

        //check which side its on to be the opposite of ours
        if(ChessBoard.GetChessPieceAtPos(position).side == this.side)
            return false;

        ChessBoard.DeletePiece(position);
        return true;
    }

    public abstract ChessPiece Copy();
}
