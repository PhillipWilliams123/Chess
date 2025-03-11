package com.example.Chess.Chess;
import static com.raylib.Raylib.*;

import com.example.Chess.Globals;
import com.example.Chess.Vector2;
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
        ImageResize(tempImage, 70, 70);
        image = LoadTextureFromImage(tempImage);
    }

    /**
     * Draws the piece at its position
     */
    public void DrawPiece()
    {
        int padding = 5;
        Vector2 scaledPos = ChessBoard.ScalePosToBoardSpace(position);
        DrawTexture(image, (int)scaledPos.x + padding, (int)scaledPos.y + padding, Globals.ChessPieceHue);
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
     * Checks in a direction from a position to a wanted position, if there is a piece of a border of the chess board
     * @param dir the direction to go in (Should be integers and whole numbers)
     * @param pos the position to start from
     * @param wantedPos the position we want to go to
     * @return the number of checks it has done (can be multiplied by direction to get the final position it landed on), Will be 0 if we cannot move at all
     */
    public int CheckInDirection(Vector2 dir, Vector2 pos, Vector2 wantedPos)
    {

        //(Might have bugs im not sure, but it works with the rook in my testing)

        int moveCount = 0;
        Vector2 posMove = pos;
        while (true)
        {
            posMove = Vector2.Add(posMove, dir);

            //if we reach out of bounds we have either gone out of the board or never reached our wanted position
            if(!ChessBoard.PosInBounds(posMove))
            {
                return 0;
            }

            ChessPiece piece = ChessBoard.GetChessPieceAtPos(posMove);
            //we have a piece in our moved position so this is the farthest we can go
            if(piece.id != -1)
            {
                return moveCount;
            }

            //we have reached where we wanted to go with no issues
            if(posMove.Equals(wantedPos))
            {
                moveCount++;
                return moveCount;
            }

            moveCount++;
        }
    }

    public abstract ChessPiece Copy();
}
