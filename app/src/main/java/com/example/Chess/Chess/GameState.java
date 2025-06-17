package com.example.Chess.Chess;

import com.example.Chess.Interaction;
import com.example.Chess.Network.NetworkManager;
import com.example.Chess.Network.Packets.StatePacket;
import com.example.Chess.Rendering.Renderer;
import com.example.Chess.Rules.OriginalRules;
import static com.example.Chess.UI.QuantumUiButton.IsQuantumUiOpen;

public class GameState {

    public static boolean isOurTurn;
    public static boolean isBlackTurn;
    public static boolean ourSide;
    public static boolean lost;
    public static boolean inCheck;

    public static void Init() {
        IsQuantumUiOpen = false;
        lost = false;
        isBlackTurn = false;
        isOurTurn = !NetworkManager.isClient;
        ourSide = true;
        inCheck = false;
        Interaction.disableInteraction = false;
        Renderer.Draw2d = true;
    }

    public static void CheckKingStatus() {
        if (NetworkManager.isClient) {
            checkKingStatusForSide(ourSide);
        } else {
            checkKingStatusForSide(false); // White
            checkKingStatusForSide(true);  // Black
        }
    }

    private static void checkKingStatusForSide(boolean side) {
        inCheck = OriginalRules.isInCheck(side);
        if (inCheck && OriginalRules.isCheckmate(side)) {
            handleCheckmate(side);
        }
    }

    private static void handleCheckmate(boolean isBlack) {
        if (NetworkManager.isClient && isBlack == ourSide) {
            NetworkManager.client.SendPacket(new StatePacket(true));
        }
        Interaction.disableInteraction = true;
        lost = true;
        Renderer.Draw2d = false;
    }
}
