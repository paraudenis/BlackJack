import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BlackJackGUI {
    private JPanel panel1;
    private int stayCount;
    private JLabel turnLabel;
    private JTextArea playerCardsText;
    private JTextArea computerCardsText;
    private JButton hitButton;
    private JButton stayButton;
    BlackJack blackJack = new BlackJack();


    public BlackJackGUI() {
        hitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stayCount = 0;
                blackJack.dealCardToPlayer(blackJack.getPlayer());

                // ai move
                int choice = blackJack.generateAIDecision();
                if (choice == 1) {
                    blackJack.dealCardToPlayer(blackJack.getComputer());
                } else {
                    stayCount++;
                }

                setCardsText();
            }
        });
        stayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stayCount++;
                if(stayCount == 2) {
                    setEndingCardsText();
                    int winner = blackJack.checkWinner();
                    if (winner == 0) {
                        JOptionPane.showMessageDialog(null,"Draw.");
                    } else if (winner == 1) {
                        JOptionPane.showMessageDialog(null,"Player1 won.");
                    } else {
                        JOptionPane.showMessageDialog(null,"Player2 won.");
                    }
                    blackJack = new BlackJack();
                    stayCount = 0;

                    setCardsText();

                } else {
                    // ai move
                    int choice = blackJack.generateAIDecision();
                    if (choice == 1) {
                        stayCount = 0;
                        blackJack.dealCardToPlayer(blackJack.getComputer());
                        setCardsText();
                    } else {
                        stayCount++;
                        setEndingCardsText();
                        int winner = blackJack.checkWinner();
                        if (winner == 0) {
                            JOptionPane.showMessageDialog(null,"Draw.");
                        } else if (winner == 1) {
                            JOptionPane.showMessageDialog(null,"Player1 won.");
                        } else {
                            JOptionPane.showMessageDialog(null,"Player2 won.");
                        }
                        blackJack = new BlackJack();
                        stayCount = 0;

                        setCardsText();
                    }
                }
            }
        });

        stayCount = 0;

        setCardsText();

    }

    public void setEndingCardsText() {
        String playerCards = blackJack.getPlayer().getHiddenCard().toString() + " ";
        String computerCards = blackJack.getComputer().getHiddenCard().toString() + " ";

        for(Integer index = 0; index < blackJack.getPlayer().getShownCards().size(); index++) {
            playerCards += blackJack.getPlayer().getShownCards().get(index).toString() + " ";
        }

        for(Integer index = 0; index < blackJack.getComputer().getShownCards().size(); index++) {
            computerCards += blackJack.getComputer().getShownCards().get(index).toString() + " ";
        }

        playerCardsText.setText(playerCards);
        computerCardsText.setText(computerCards);
    }

    public void setCardsText() {
        String playerCards = blackJack.getPlayer().getHiddenCard().toString() + " ";
        String computerCards = "X ";

        for(Integer index = 0; index < blackJack.getPlayer().getShownCards().size(); index++) {
            playerCards += blackJack.getPlayer().getShownCards().get(index).toString() + " ";
        }

        for(Integer index = 0; index < blackJack.getComputer().getShownCards().size(); index++) {
            computerCards += blackJack.getComputer().getShownCards().get(index).toString() + " ";
        }

        playerCardsText.setText(playerCards);
        computerCardsText.setText(computerCards);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("BlackJackGUI");
        frame.setContentPane(new BlackJackGUI().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
