package com.example.ygo.duel;

import com.example.ygo.model.Card;

public interface BattleListener {
    void onTurn(Card playerCard, Card aiCard, String winner, boolean playerInAttack, boolean aiInAttack);
    void onScoreChanged(int playerScore, int aiScore);
    void onDuelEnded(String winner);
    void onLog(String text);
}