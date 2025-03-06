package com.example.Chess.Rendering;

import com.raylib.Raylib;

import static com.raylib.Colors.*;

/**
 * @author ericl
 */
public class Renderer {
    
    public void DrawChessBoard() {
        //draw the chess board using two for loops
        //where we draw a color if the index is even and another color if its odd
        //creating a checkerboard pattern

        //the scale for the screen
        float xScale = 640.0f / 8.0f;
        float yScale = 480.0f / 8.0f;
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if ((x + y) % 2 != 0) {
                    //some other color
                    Raylib.DrawRectangle((int) (x * xScale), (int) (y * yScale), (int) xScale, (int) yScale, BLACK);
                } else {
                    //some color
                    Raylib.DrawRectangle((int) (x * xScale), (int) (y * yScale), (int) xScale, (int) yScale, GRAY);
                }
            }
        }
    }
}
