package com.example.Chess.Chess;

import com.example.Chess.Interaction;
import com.example.Chess.Network.NetworkManager;
import com.example.Chess.Network.Packets.StatePacket;
import com.example.Chess.Rendering.Renderer;
import com.example.Chess.Vector2;

import java.util.ArrayList;

public class GameState
{

    //if we are in a multiplayer game we need to lock our movement
    public static boolean isOurTurn;
    //Track whose turn it is (true = black's turn; false = white's turn)
    public static boolean isBlackTurn;
    //true for white, false for black
    public static boolean ourSide;
    public static boolean lost;
    public static boolean inCheck;

    public static void Init()
    {
        lost = false;
        isBlackTurn = false;
        isOurTurn = !NetworkManager.isClient;
        ourSide = true;
        inCheck = false;
        Interaction.disableInteraction = false;
    }

    /**
     * Will check if we are in check or checkmate
     */
    public static void CheckKingStatus()
    {
        if(NetworkManager.isClient)
        {
            int kingID = -1;
            Vector2 kingPos = null;

            // Find our king
            for (int i = 0; i < ChessBoard.chessPieces.length; i++)
            {
                if (ChessBoard.chessPieces[i].id == -1)
                    continue;

                //check for white king
                if (ChessBoard.chessPieces[i].GetPieceType() == 6 && ChessBoard.chessPieces[i].side == ourSide)
                {
                    kingID = i;
                    kingPos = ChessBoard.chessPieces[i].position;
                    break;
                }
            }

            //we could not find a white king
            if (kingID == -1)
                return;

            if(!inCheck)
            {
                inCheck = KingCheck(!ourSide, kingPos);
                if (inCheck)
                {
                    if(KingCheckMate(!ourSide, kingID))
                    {
                        //we have won
                        if(ourSide)
                        {
                            NetworkManager.client.SendPacket(new StatePacket(true));
                            //we have lost
                            Interaction.disableInteraction = true;
                            GameState.lost = false;
                            Renderer.Draw2d = false;
                        }
                    }
                    else
                    {

                    }
                }
            }
        }
        else
        {
            int kingIDWhite = -1;
            int kingIDBlack = -1;
            Vector2 kingPosWhite = null;
            Vector2 kingPosBlack = null;

            // Find our king
            for (int i = 0; i < ChessBoard.chessPieces.length; i++)
            {
                if (ChessBoard.chessPieces[i].id == -1)
                    continue;

                //check for white king
                if (ChessBoard.chessPieces[i].GetPieceType() == 6 && ChessBoard.chessPieces[i].side == !ourSide)
                {
                    kingIDWhite = i;
                    kingPosWhite = ChessBoard.chessPieces[i].position;
                }
                else if(ChessBoard.chessPieces[i].GetPieceType() == 6 && ChessBoard.chessPieces[i].side == ourSide)
                {
                    kingIDBlack = i;
                    kingPosBlack =  ChessBoard.chessPieces[i].position;

                }
            }

            //we could not find a white king
            if (kingIDWhite == -1)
                return;

            if(!inCheck)
            {
                inCheck = KingCheck(ourSide, kingPosWhite);
                if (inCheck)
                {
                    if(KingCheckMate(ourSide, kingIDWhite))
                    {
                        //we have lost
                        Interaction.disableInteraction = true;
                        GameState.lost = true;
                        Renderer.Draw2d = false;
                    }
                }
            }

            //we could not find a black king
            if(kingIDBlack == -1)
                return;

            if(!inCheck)
            {
                inCheck = KingCheck(!ourSide, kingPosBlack);
                if (inCheck)
                {
                    if(KingCheckMate(!ourSide, kingIDBlack))
                    {
                        //we have lost
                        Interaction.disableInteraction = true;
                        GameState.lost = true;
                        Renderer.Draw2d = false;
                    }
                }
            }
        }
    }

    /**
     * Checks if the king is in check
     * @param side the side to check for
     * @return true if in check
     */
    static boolean KingCheck(boolean side, Vector2 kingPos)
    {
        boolean isInCheck = false;

        //check for king in check
        for (int i = 0; i < ChessBoard.chessPieces.length; i++)
        {
            //check if its an enemy piece
            if (ChessBoard.chessPieces[i].id == -1 || ChessBoard.chessPieces[i].side == !side)
                continue;

            if (ChessBoard.chessPieces[i].CheckMove(kingPos))
            {
                isInCheck = true;
                break;
            }
        }

        return isInCheck;
    }

    /**
     * Checks if the king is in checkmate
     * @param side the side to check for
     * @return true if in checkmate
     */
    static boolean KingCheckMate(boolean side, int kingID)
    {
        ArrayList<Vector2> safeMoves = new ArrayList<>();

        for (int x = 0; x < ChessBoard.boardSize; x++)
        {
            for (int y = 0; y < ChessBoard.boardSize; y++)
            {
                Vector2 pos = new Vector2(x, y);

                //if the king cant move to the pos it cannot ever be in check at that position
                if (!ChessBoard.chessPieces[kingID].CheckMove(pos))
                    continue;

                boolean isSafe = true;
                for (int j = 0; j < ChessBoard.chessPieces.length; j++)
                {
                    //check if its an enemy piece
                    if (ChessBoard.chessPieces[j].id == -1 || ChessBoard.chessPieces[j].side == !side)
                        continue;

                    //check if the enemy piece can move there
                    if (ChessBoard.chessPieces[j].CheckMove(pos))
                    {
                        //check to make sure that it is an enemy that is blocking that position
                        isSafe = false;
                        break;
                    }
                }

                if (isSafe)
                    safeMoves.add(pos);
            }
        }

        //if the king cannot move anywhere then it is in checkmate
        return safeMoves.isEmpty();
    }
}
