package com.example.ygo.duel;

import com.example.ygo.model.Card;

import java.util.Random;

public class Duel {
    private int playerScore = 0;
    private int aiScore = 0;
    private final Random random = new Random();
    private final BattleListener listener;

    public Duel(BattleListener listener) {
        this.listener = listener;
    }

    public void reset() {
        playerScore = 0;
        aiScore = 0;
        listener.onScoreChanged(playerScore, aiScore);
    }

    public void playTurn(Card playerCard, Card aiCard, boolean playerInAttack, boolean aiInAttack) {
        String winner = decideWinner(playerCard, aiCard, playerInAttack, aiInAttack);

        if ("player".equals(winner)) playerScore++;
        else if ("ai".equals(winner)) aiScore++;

        listener.onTurn(playerCard, aiCard, winner, playerInAttack, aiInAttack);
        listener.onScoreChanged(playerScore, aiScore);

        if (playerScore >= 2 || aiScore >= 2) {
            String finalWinner = playerScore > aiScore ? "Jugador" : "Máquina";
            listener.onDuelEnded(finalWinner);
        }
    }

    private String decideWinner(Card p, Card a, boolean pAtk, boolean aAtk) {
        if (pAtk && aAtk) {
            if (p.getAtk() > a.getAtk()) return "player";
            if (p.getAtk() < a.getAtk()) return "ai";
            return "draw";
        }
        if (pAtk && !aAtk) {
            if (p.getAtk() > a.getDef()) return "player";
            if (p.getAtk() < a.getDef()) return "ai";
            return "draw";
        }
        if (!pAtk && aAtk) {
            if (a.getAtk() > p.getDef()) return "ai";
            if (a.getAtk() < p.getDef()) return "player";
            return "draw";
        }
        if (!pAtk && !aAtk) {
            if (p.getDef() > a.getDef()) return "player";
            if (p.getDef() < a.getDef()) return "ai";
            return "draw";
        }
        return "draw";
    }

    public boolean aiChooseAttackMode() {
        return random.nextBoolean();
    }
}