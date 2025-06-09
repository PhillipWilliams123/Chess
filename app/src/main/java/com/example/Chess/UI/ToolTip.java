package com.example.Chess.UI;

import com.example.Chess.Globals;
import com.example.Chess.Vector2;
import com.raylib.Raylib;

import static com.raylib.Colors.BLACK;
import static com.raylib.Colors.LIGHTGRAY;
import static com.raylib.Raylib.*;

public class ToolTip
{
    public Vector2 position;
    public Vector2 region;
    private String text;
    private Vector2 size;

    public boolean show;
    private boolean overRegion;

    public ToolTip(Vector2 position, Vector2 region, String text)
    {
        this.position = position;
        this.region = region;
        this.text = text;

        SetText(text);
        show = false;
    }

    public void SetText(String text)
    {
        this.text = text;
        int count = 1;
        for (int i = 0; i < text.length(); i++)
        {
            if (text.charAt(i) == '\n')
                count++;
        }
        size = new Vector2(MeasureText(text, 20), 20 * count);
    }

    /**
     * Updates tooltip checking if the mouse is hovering over a region
     */
    public void Update()
    {
        int mouseX = (int) Raylib.GetMousePosition().x();
        int mouseY = (int) Raylib.GetMousePosition().y();
        if (mouseX >= position.x && mouseX <= position.x + region.x && mouseY >= position.y && mouseY <= position.y + region.y)
        {
            overRegion = true;
        }
        else
        {
            overRegion = false;
        }
    }

    /**
     * Draws the tooltip
     */
    public void Draw()
    {
        if(!show || !overRegion)
            return;

        int mouseX = (int) Raylib.GetMousePosition().x();
        int mouseY = (int) Raylib.GetMousePosition().y();

        if(mouseX + size.x > Globals.ChessWidth + Globals.UIWidth)
        {
            mouseX -= size.x;
        }

        DrawRectangle((int) mouseX, (int) (mouseY - size.y), (int) size.x, (int) size.y, LIGHTGRAY);

        // Draw button border
        DrawRectangleLines((int) mouseX, (int) (mouseY - size.y), (int) size.x, (int) size.y, BLACK);

        // Draw button text centered
        int textWidth = MeasureText(text, 20);
        int textX = (int)mouseX + ((int)size.x - textWidth) / 2;
        int textY = (int)(mouseY - size.y);
        DrawText(text, textX, textY, 20, BLACK);
    }
}
