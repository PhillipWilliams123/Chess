package com.example.Chess.Chess;

import com.example.Chess.Vector2;
import com.example.Chess.Globals;
import com.example.Chess.Rules.OriginalRules;
import static com.example.Chess.Rules.OriginalRules.isInCheckValidating;

public class Rook extends ChessPiece {

    public Rook(Vector2 position, boolean side) {
        this.side = side;
        this.position = position;
    }

    @Override
    public String GetImageLocation() {
        return Globals.ImageDirectory + Globals.PieceImages[3 + (6 * (side ? 1 : 0))];
    }

    @Override
    public boolean TryMove(Vector2 pos) {
        if (!CheckMove(pos)) {
            return false;
        }

        if (ChessBoard.GetPieceIdAtPos(pos) != -1) {
            if (!TryTakePiece(pos)) {
                return false;
            }
        }
        SetToPosition(pos);
        return true;
    }

    @Override
    public boolean CheckMove(Vector2 pos) {
        if (pos.x < 0 || pos.x >= ChessBoard.boardSize
                || pos.y < 0 || pos.y >= ChessBoard.boardSize) {
            return false;
        }

        if (isInCheckValidating) {
            return basicCheckMove(pos);
        }

        if (!basicCheckMove(pos)) {
            return false;
        }

        return !OriginalRules.wouldMovePutKingInCheck(this, pos, this.side);
    }

    private boolean basicCheckMove(Vector2 pos) {
        Vector2[] directions = {
            new Vector2(-1, 0), // Left
            new Vector2(1, 0), // Right
            new Vector2(0, -1), // Up
            new Vector2(0, 1) // Down
        };

        for (Vector2 direction : directions) {
            int moveAmount = CheckInDirection(direction, position, pos);
            if (moveAmount != 0) {
                int targetId = ChessBoard.GetPieceIdAtPos(pos);
                return targetId == -1 || ChessBoard.chessPieces[targetId].side != this.side;
            }
        }
        return false;
    }

    @Override
    public ChessPiece Copy() {
        return new Rook(this.position, this.side);
    }

    @Override
    public int GetPieceType()
    {
        return 4;
    }
}
