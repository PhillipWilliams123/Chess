package com.example.Chess;

/**
 * Acts as a direction, position, set of two numbers, basically anything
 */
public class Vector2
{

    public double x;
    public double y;

    public Vector2(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public Vector2(Vector2 a)
    {
        this.x = a.x;
        this.y = a.y;
    }

    public double Length()
    {
        return Math.sqrt(x * x + y * y);
    }

    public double LengthSqr()
    {
        return x * x + y * y;
    }

    public boolean Equals(Vector2 a)
    {
        return x == a.x && y == a.y;
    }

    public static Vector2 Normalize(Vector2 a)
    {
        return Vector2.Div(a, a.Length());
    }

    public static double Dot(Vector2 a, Vector2 b)
    {
        return (a.x * b.x) + (a.y * b.y);
    }

    public static Vector2 Add(Vector2 a, Vector2 b)
    {
        return new Vector2(a.x + b.x, a.y + b.y);
    }

    public static Vector2 Add(Vector2 a, double b)
    {
        return new Vector2(a.x + b, a.y + b);
    }

    public static Vector2 Sub(Vector2 a, Vector2 b)
    {
        return new Vector2(a.x - b.x, a.y - b.y);
    }

    public static Vector2 Sub(Vector2 a, double b)
    {
        return new Vector2(a.x - b, a.y - b);
    }

    public static Vector2 Mul(Vector2 a, Vector2 b)
    {
        return new Vector2(a.x * b.x, a.y * b.y);
    }

    public static Vector2 Mul(Vector2 a, double b)
    {
        return new Vector2(a.x * b, a.y * b);
    }

    public static Vector2 Div(Vector2 a, Vector2 b)
    {
        return new Vector2(a.x / b.x, a.y / b.y);
    }

    public static Vector2 Div(Vector2 a, double b)
    {
        return new Vector2(a.x / b, a.y / b);
    }

    @Override
    public String toString()
    {
        return "<" + x + "," + y + ">";
    }
}