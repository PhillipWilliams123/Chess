package com.example.Chess.Rendering;

import com.example.Chess.Chess.ChessBoard;
import com.example.Chess.Chess.ChessPiece;
import com.example.Chess.Chess.GameState;
import com.example.Chess.Globals;
import com.raylib.Raylib;
import com.example.Chess.UI.UI;

import static com.raylib.Raylib.*;
import static com.raylib.Colors.*;

/**
 * @author ericl
 */
public class Renderer
{


    public static boolean Draw2d;
    public Raylib.Camera3D camera;

    /**
     * Draws the checkerboard pattern
     */
    public void DrawChessBoard()
    {
        if(!Draw2d)
            return;

        //draw the chess board using two for loops
        //where we draw a color if the index is even and another color if its odd
        //creating a checkerboard pattern

        //the scale for the screen
        double xScale = Globals.ChessWidth / (double)ChessBoard.boardSize;
        double yScale = Globals.ScreenHeight / (double)ChessBoard.boardSize;
        for (int x = 0; x < ChessBoard.boardSize; x++) {
            for (int y = 0; y < ChessBoard.boardSize; y++) {
                if ((x + y) % 2 != 0) {
                    //some other color
                    Raylib.DrawRectangle((int) Math.floor(x * xScale), (int) Math.floor(y * yScale), (int) xScale + 1, (int) yScale + 1, Globals.BoardColors[0]);
                } else {
                    //some color
                    Raylib.DrawRectangle((int) Math.floor(x * xScale), (int) Math.floor(y * yScale), (int) xScale + 1, (int) yScale + 1, Globals.BoardColors[1]);
                }
            }
        }
    }

    /**
     * Will draw the chess pieces
     */
    public void DrawPieces()
    {
        if(!Draw2d)
            return;

        for (int i = 0; i < ChessBoard.chessPieces.length; i++)
        {
            if(ChessBoard.chessPieces[i].id == -1)
                continue;

            ChessBoard.chessPieces[i].DrawPiece();
        }
    }

    static double radius;
    public void Draw3DChessBoard()
    {
        radius += GetFrameTime();

        if(radius > ChessBoard.boardSize)
            radius = ChessBoard.boardSize;

        //if win or lose can set the background color
        if(!GameState.lost)
            Raylib.ClearBackground(GREEN);
        else
            Raylib.ClearBackground(RED);

        camera = new Camera3D()
                ._position((new Vector3().x((float) (Math.cos(GetTime()) * radius)).y((float) (radius + 0.5)).z((float) radius)))
                .target(new Vector3().x(0.01f).y(0.5f).z(0.01f))
                .up(new Vector3().x(0.0f).y(1.0f).z(0.0f))
                .fovy(55)
                .projection(CAMERA_PERSPECTIVE);

        Raylib.UpdateCamera(camera, CAMERA_ORBITAL);

        Raylib.BeginMode3D(camera);

        DrawExtraBackgroundBoards();

        for (int x = 0; x < ChessBoard.boardSize; x++) {
            for (int y = 0; y < ChessBoard.boardSize; y++) {
                if ((x + y) % 2 != 0) {
                    //some other color
                    Raylib.DrawCube(new Vector3().x(x - 3.5f).y(-49.5f).z(y - 3.5f), 1, 100, 1, Globals.BoardColors[0]);
                } else {
                    //some color
                    Raylib.DrawCube(new Vector3().x(x - 3.5f).y(-49.5f).z(y - 3.5f), 1, 100, 1, Globals.BoardColors[1]);
                }
            }
        }

        for (int i = 0; i < ChessBoard.chessPieces.length; i++)
        {
            if(ChessBoard.chessPieces[i].id == -1)
                continue;

            Raylib.DrawBillboard(camera, ChessBoard.chessPieces[i].image, new Vector3().x((float) ChessBoard.chessPieces[i].position.x - 3.5f).y(1).z((float) ChessBoard.chessPieces[i].position.y - 3.5f), 1f, WHITE);
        }

        Raylib.EndMode3D();
    }

    private void DrawExtraBackgroundBoards()
    {
        for (int i = 0; i < 180; i += ChessBoard.boardSize * 3)
        {
            float xRad = -(float) (Math.cos(i * Math.PI / 180.0f) * 100);
            float zRad = -(float) (Math.sin(i * Math.PI / 180.0f) * 100);
            float yRad = (float) (Math.sin(GetTime() * 2 + (i * Math.PI / 180.0f)) * 30);

            for (int x = 0; x < ChessBoard.boardSize; x++) {
                for (int y = 0; y < ChessBoard.boardSize; y++) {
                    if ((x + y) % 2 != 0) {
                        //some other color
                        Raylib.DrawCube(new Vector3().x(x - 3.5f + xRad).y(-150 + yRad).z(y - 3.5f + zRad), 1, 200, 1, Globals.BoardColors[0]);
                    } else {
                        //some color
                        Raylib.DrawCube(new Vector3().x(x - 3.5f + xRad).y(-150 + yRad).z(y - 3.5f + zRad), 1, 200, 1, Globals.BoardColors[1]);
                    }
                }
            }

            for (int g = 0; g < ChessBoard.chessPieces.length; g++)
            {
                if(ChessBoard.chessPieces[g].id == -1)
                    continue;

                Raylib.DrawBillboard(camera, ChessBoard.chessPieces[g].image, new Vector3().x((float) ChessBoard.chessPieces[g].position.x - 3.5f + xRad).y(-49 + yRad).z((float) ChessBoard.chessPieces[g].position.y - 3.5f + zRad), 1f, WHITE);
            }
        }
    }
}
