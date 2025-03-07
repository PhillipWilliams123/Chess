package com.example.Chess;

public class Vector2{

    public double x;
    public double y;

    public Vector2(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public double Length()
    {
        return Math.sqrt(x * x + y * y);
    }

    public double LengthSqr()
    {
        return x * x + y * y;
    }

    public static Vector2 Normalize(Vector2 a)
    {
        return Vector2.Div(a, a.Length());
    }

    public static double Dot(Vector2 a, Vector2 b)
    {
        return (a.x * a.y) + (a.y * b.y);
    }

    public static Vector2 Add(Vector2 a, Vector2 b)
    {
        return new Vector2(a.x + b.y, a.y + b.y);
    }

    public static Vector2 Add(Vector2 a, double b)
    {
        return new Vector2(a.x + b, a.y + b);
    }

    public static Vector2 Sub(Vector2 a, Vector2 b)
    {
        return new Vector2(a.x - b.y, a.y - b.y);
    }

    public static Vector2 Sub(Vector2 a, double b)
    {
        return new Vector2(a.x - b, a.y - b);
    }

    public static Vector2 Mul(Vector2 a, Vector2 b)
    {
        return new Vector2(a.x * b.y, a.y * b.y);
    }

    public static Vector2 Mul(Vector2 a, double b)
    {
        return new Vector2(a.x * b, a.y * b);
    }

    public static Vector2 Div(Vector2 a, Vector2 b)
    {
        return new Vector2(a.x / b.y, a.y / b.y);
    }

    public static Vector2 Div(Vector2 a, double b)
    {
        return new Vector2(a.x / b, a.y / b);
    }
}