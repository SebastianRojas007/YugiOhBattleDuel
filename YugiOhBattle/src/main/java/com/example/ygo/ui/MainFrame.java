package com.example.ygo.ui;

import com.example.ygo.duel.BattleListener;
import com.example.ygo.duel.Duel;
import com.example.ygo.model.Card;
import com.example.ygo.net.YgoApiClient;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainFrame extends JFrame implements BattleListener {

    private final JButton btnLoadCards = new JButton("Cargar cartas");
    private final JButton btnStartDuel = new JButton("¡A jugar!");
    private final JTextArea logArea = new JTextArea(12, 50);
    private final JPanel playerCardsPanel = new JPanel();
    private final JPanel aiCardsPanel = new JPanel();
    private final JLabel scoreLabel = new JLabel("Jugador: 0  -  Máquina: 0");
    private final JToggleButton modeToggle = new JToggleButton("Modo: ATAQUE");
    private final List<CardPanel> playerCardPanels = new ArrayList<>();
    private final List<CardPanel> aiCardPanels = new ArrayList<>();
    private final YgoApiClient apiClient = new YgoApiClient();
    private Duel duel;

    public MainFrame() {
        setTitle("Yu-Gi-Oh! Battle");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        initUI();
        duel = new Duel(this);
    }

    private void initUI() {
        setLayout(new BorderLayout());
        JPanel top = new JPanel();
        top.add(btnLoadCards);
        top.add(btnStartDuel);
        top.add(modeToggle);
        top.add(scoreLabel);
        add(top, BorderLayout.NORTH);
        btnStartDuel.setEnabled(false);
        playerCardsPanel.setBorder(new TitledBorder("Tus cartas"));
        playerCardsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        aiCardsPanel.setBorder(new TitledBorder("Cartas Maquina"));
        aiCardsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JPanel center = new JPanel(new GridLayout(2, 1));
        center.add(new JScrollPane(playerCardsPanel));
        center.add(new JScrollPane(aiCardsPanel));
        add(center, BorderLayout.CENTER);
        logArea.setEditable(false);
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(new TitledBorder("Log de batalla"));
        add(logScroll, BorderLayout.SOUTH);
        btnLoadCards.addActionListener(this::onLoadCardsClicked);
        btnStartDuel.addActionListener(e -> {
            duel.reset();
            appendLog("¡Es hora del duelo!");
            btnStartDuel.setEnabled(false);
            btnLoadCards.setEnabled(false);
            enablePlayerCardButtons(true);
        });
        modeToggle.addActionListener(e -> {
            boolean atk = modeToggle.isSelected();
            modeToggle.setText(atk ? "Modo: DEFENSA" : "Modo: ATAQUE");
        });
        for (int i = 0; i < 3; i++) {
            CardPanel p = new CardPanel();
            playerCardPanels.add(p);
            playerCardsPanel.add(p.getPanel());
            CardPanel a = new CardPanel();
            aiCardPanels.add(a);
            aiCardsPanel.add(a.getPanel());
        }
    }

    private void onLoadCardsClicked(ActionEvent evt) {
        btnLoadCards.setEnabled(false);
        appendLog("Cargando cartas...");
        SwingWorker<Void, Void> loader = new SwingWorker<>() {
            List<Card> playerCards = new ArrayList<>();
            List<Card> aiCards = new ArrayList<>();
            @Override
            protected Void doInBackground() throws Exception {
                for (int i = 0; i < 3; i++) {
                    Card pc = apiClient.fetchRandomMonster();
                    playerCards.add(pc);
                    Card ac = apiClient.fetchRandomMonster();
                    aiCards.add(ac);
                }
                return null;
            }
            @Override
            protected void done() {
                try {
                    get();
                    for (int i = 0; i < 3; i++) {
                        playerCardPanels.get(i).setCard(playerCards.get(i), true);
                        aiCardPanels.get(i).setCard(aiCards.get(i), false);
                    }
                    appendLog("Cartas cargadas correctamente.");
                    btnStartDuel.setEnabled(true);
                    btnLoadCards.setEnabled(true);
                    enablePlayerCardButtons(false);
                } catch (InterruptedException | ExecutionException ex) {
                    appendLog("Error al cargar cartas: " + ex.getCause());
                    btnLoadCards.setEnabled(true);
                }
            }
        };
        loader.execute();
    }

    private void enablePlayerCardButtons(boolean enable) {
        for (CardPanel cp : playerCardPanels) {
            cp.setPlayButtonEnabled(enable);
        }
    }

    @Override
    public void onTurn(Card playerCard, Card aiCard, String winner, boolean playerInAttack, boolean aiInAttack) {
        String modeP = playerInAttack ? "ATK" : "DEF";
        String modeA = aiInAttack ? "ATK" : "DEF";
        String text = String.format("Turno: Jugador jugó %s (%s) vs Maquina jugó %s (%s) -> %s",
                playerCard.getName(), modeP, aiCard.getName(), modeA,
                "draw".equals(winner) ? "EMPATE" : ("player".equals(winner) ? "JUGADOR GANA" : "Maquina GANA"));
        appendLog(text);
    }

    @Override
    public void onScoreChanged(int playerScore, int aiScore) {
        scoreLabel.setText(String.format("Jugador: %d  -  Máquina: %d", playerScore, aiScore));
    }

    @Override
    public void onDuelEnded(String winner) {
        appendLog(winner + " ¡Gana el duelo!");
        btnLoadCards.setEnabled(true);
        btnStartDuel.setEnabled(false);
        enablePlayerCardButtons(false);
    }

    @Override
    public void onLog(String text) {
        appendLog(text);
    }

    private void appendLog(String line) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(line + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private class CardPanel {
        private final JPanel panel = new JPanel();
        private final JLabel imgLabel = new JLabel();
        private final JLabel nameLabel = new JLabel("...");
        private final JLabel statsLabel = new JLabel("");
        private final JButton playButton = new JButton("Jugar");
        private Card card;

        public CardPanel() {
            panel.setLayout(new BorderLayout());
            panel.setPreferredSize(new Dimension(250, 140));
            panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
            imgLabel.setPreferredSize(new Dimension(120, 120));
            JPanel info = new JPanel(new GridLayout(3, 1));
            info.add(nameLabel);
            info.add(statsLabel);
            info.add(playButton);
            panel.add(imgLabel, BorderLayout.WEST);
            panel.add(info, BorderLayout.CENTER);
            playButton.setEnabled(false);
            playButton.addActionListener(e -> onPlayClicked());
        }

        public JPanel getPanel() { return panel; }

        public void setCard(Card c, boolean isPlayer) {
            this.card = c;
            nameLabel.setText(c.getName());
            statsLabel.setText("<html>ATK: " + c.getAtk() + "<br>DEF: " + c.getDef() + "</html>");
            playButton.setEnabled(false);
            imgLabel.setIcon(null);
            if (c.getImageUrl() != null && !c.getImageUrl().isBlank()) {
                SwingWorker<ImageIcon, Void> imgWorker = new SwingWorker<>() {
                    @Override
                    protected ImageIcon doInBackground() throws Exception {
                        try {
                            URL url = new URL(c.getImageUrl());
                            Image img = ImageIO.read(url);
                            if (img == null) return null;
                            Image scaled = img.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                            return new ImageIcon(scaled);
                        } catch (Exception ex) {
                            return null;
                        }
                    }
                    @Override
                    protected void done() {
                        try {
                            ImageIcon icon = get();
                            if (icon != null) imgLabel.setIcon(icon);
                            else imgLabel.setText("[no image]");
                        } catch (Exception ex) {
                            imgLabel.setText("[no image]");
                        }
                    }
                };
                imgWorker.execute();
            } else {
                imgLabel.setText("[no image]");
            }
            if (isPlayer) {
                playButton.setEnabled(false);
            } else {
                playButton.setEnabled(false);
            }
        }

        public void setPlayButtonEnabled(boolean enabled) {
            playButton.setEnabled(enabled && card != null);
        }

        private void onPlayClicked() {
            if (card == null) return;
            boolean playerSelectedAttack = !modeToggle.isSelected();
            boolean aiAttack = duel.aiChooseAttackMode();
            Card aiCard = chooseRandomAiCard();
            if (aiCard == null) {
                appendLog("Error: la IA no tiene cartas disponibles.");
                return;
            }
            duel.playTurn(card, aiCard, playerSelectedAttack, aiAttack);
            setPlayButtonEnabled(false);
        }
    }

    private Card chooseRandomAiCard() {
        for (CardPanel cp : aiCardPanels) {
            if (cp.card != null) return cp.card;
        }
        return null;
    }
}