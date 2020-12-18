import javax.swing.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class BlackJack {
    private Random rand = new Random();

    private Integer goToValue;
    private Integer[] cardDeck = new Integer[11];
    private Integer numberOfCardsInDeck;
    private Player player;
    private Player computer;
    private String turn;


    public BlackJack() {
        player = new Player();
        computer = new Player();
        for(Integer index=0;index<11;index++) {
            cardDeck[index] = 1;
        }
        numberOfCardsInDeck=11;
        goToValue = 21;
        initPlayerCards();
    }

    public Player getPlayer() {
        return player;
    }

    public Player getComputer() {
        return computer;
    }

    public void initPlayerCards() {
        Integer coinflip = rand.nextInt(2);
        if (coinflip == 0) {
            /* Player starts */
            turn = "Player";
            dealHiddenCardToPlayer(player);
            dealHiddenCardToPlayer(computer);
            dealCardToPlayer(player);
            dealCardToPlayer(computer);
        } else {
            /* Computer starts */
            turn = "Computer";
            dealHiddenCardToPlayer(computer);
            dealHiddenCardToPlayer(player);
            dealCardToPlayer(computer);
            dealCardToPlayer(player);
        }
    }

    public void dealHiddenCardToPlayer(Player player) {
        Integer card = generateRandomCardFromDeck();
        if (card != -1) {
            player.setHiddenCard(card);
        }
    }

    public void dealCardToPlayer(Player player) {
        Integer card = generateRandomCardFromDeck();
        if (card != -1) {
            player.addShownCard(card);
        }
    }

    public Integer generateRandomCardFromDeck() {
        Integer card = -1;
        if(numberOfCardsInDeck>0) {
            do {
                card = rand.nextInt(11);
            } while(cardDeck[card] == 0);
            cardDeck[card] = 0;
            numberOfCardsInDeck--;
            return card+1;
        } else {
            return card;
        }
    }

    /* AI Methods */
    /* Decisions:
        0       - Stay
        1       - Hit
        2...k...n    - Trump card : index = k-2
     */
    public Integer generateAIDecision() {
        Integer decision = 0; /* Init decision is stay */
        Integer numberOfGoodCards = 0; /* number of good drawable cards */
        Integer numberOfBadCards = 0; /* number of bad drawable cards */
        Integer numberOfDrawableCards = 0; /* number of drawable cards */
        Integer aiCardSum = computer.getTotalCardValue(); /* total value of cards in ai hand */
        Float probabilityToDrawGoodCard = 0.0f;

        Float probabilityThatPlayerIsBust = 0.0f;
        Integer playerCardSum = calculatePlayerVisibleCardsSum();
        Integer numberOfGoodCardsForPlayer = 0;
        Integer numberOfBadCardsForPlayer = 0;
        Integer numberOfDrawableCardsForPlayer = 0;
        Integer maxPossibleCardForPlayer = 0;

        /* Set arraylist containing cards that are visible to AI */
        ArrayList<Integer> visibleCards = getCardsVisibleToAI();

        /* Calculate the probability that the player may be bust.
        * Also calculate number of good and bad cards for AI. */
        for (Integer card=1; card <= 11; card++) {
            if (visibleCards.contains(card) == false) {
                /* Check if card is good for AI. */
                if (aiCardSum + card <= goToValue) {
                    numberOfGoodCards++;
                    numberOfDrawableCards++;
                } else {
                    numberOfBadCards++;
                    numberOfDrawableCards++;
                }
                if (card > maxPossibleCardForPlayer) {
                    maxPossibleCardForPlayer = card;
                }
                if (playerCardSum + card <= goToValue) {
                    numberOfGoodCardsForPlayer++;
                    numberOfDrawableCardsForPlayer++;
                } else {
                    numberOfBadCardsForPlayer++;
                    numberOfDrawableCardsForPlayer++;
                }
            }
        }
        probabilityThatPlayerIsBust = numberOfBadCardsForPlayer.floatValue()/numberOfDrawableCardsForPlayer.floatValue();
        probabilityToDrawGoodCard = numberOfGoodCards.floatValue()/numberOfDrawableCards.floatValue();
        if (aiCardSum > playerCardSum + maxPossibleCardForPlayer) {
            return 0;
        } else if (probabilityThatPlayerIsBust >= probabilityToDrawGoodCard) {
            return 0;
        } else {
            Float choice = rand.nextFloat();
            if(choice<probabilityToDrawGoodCard) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public Integer calculatePlayerVisibleCardsSum() {
        Integer cardSum = 0;
        for (Integer index=0; index < player.getShownCards().size(); index++) {
            cardSum += player.getShownCards().get(index);
        }
        return cardSum;
    }

    public ArrayList<Integer> getCardsVisibleToAI() {
        ArrayList<Integer> visibleCards = new ArrayList<>();
        visibleCards.add(computer.getHiddenCard());
        for(Integer index=0; index < computer.getShownCards().size(); index++) {
            visibleCards.add(computer.getShownCards().get(index));
        }
        for(Integer index=0; index < player.getShownCards().size(); index++) {
            visibleCards.add(player.getShownCards().get(index));
        }
        return visibleCards;
    }

    /* Other Methods */
    public Integer checkWinner() {
        Integer playerTotal = player.getTotalCardValue();
        Integer computerTotal = computer.getTotalCardValue();
        if (playerTotal > goToValue && computerTotal > goToValue) {
            return 0; /* draw */
        } else if (computerTotal > goToValue) {
            return 1; /* player wins */
        } else if (playerTotal > goToValue) {
            return 2; /* computer wins */
        } else if (playerTotal == computerTotal) {
            return 0; /* draw */
        } else if (playerTotal == 4 && player.getHiddenCard() == 4) {
            return 1; /* player wins */
        } else if (computerTotal == 4 && computer.getHiddenCard() == 4) {
            return 2; /* computer wins */
        } else if (playerTotal > computerTotal)  {
            return 1; /* player wins */
        } else {
            return 2; /* computer wins */
        }
    }
}

class Player {
    Integer hiddenCard;
    ArrayList<Integer> shownCards = new ArrayList<>();
    ArrayList<TrumpCard> trumpCardsDeck = new ArrayList<>();
    ArrayList<TrumpCard> trumpCardsBoard = new ArrayList<>();
    Integer totalCardSum;

    public Player() {
        totalCardSum = 0;
    }

    /* Hidden Card Methods */
    public Integer getHiddenCard() {
        return hiddenCard;
    }

    public void setHiddenCard(Integer hiddenCard) {
        this.hiddenCard = hiddenCard;
    }

    /* Shown Cards Methods */
    public ArrayList<Integer> getShownCards() {
        return shownCards;
    }

    public void setShownCards(ArrayList<Integer> shownCards) {
        this.shownCards = shownCards;
    }

    public void addShownCard(Integer card) {
        shownCards.add(card);
    }

    /* Card Value Methods */
    public Integer getTotalCardValue() {
        Integer sum = hiddenCard;
        for(Integer index=0; index<shownCards.size(); index++) {
            sum += shownCards.get(index);
        }
        totalCardSum = sum;
        return sum;
    }

    /* Trump Cards Methods */
    public void addTrumpCardToDeck(TrumpCard trumpCard) {
        trumpCardsDeck.add(trumpCard);
    }

    public void removeTrumpCardFromDeck(Integer index) {
        trumpCardsDeck.remove(index);
    }

    public void useTrumpCard(Integer index) {
        TrumpCard trumpCard = trumpCardsDeck.get(index);
        trumpCardsBoard.add(trumpCard);
        removeTrumpCardFromDeck(index);
    }

    public void clearTrumpCardsBoard() {
        trumpCardsBoard.clear();
    }
}


/* Trump Card Types
        One-Up  Your opponent's bet increases by 1 while this card is on the table.
        Two-Up	Your opponent's bet increases by 2 while this card is on the table.
        Two-Up+	Return your opponent's last face-up card to the deck. Also, your opponent's bet increases by 2 while this card is on the table.
        2 Card	Draw the 2 card. If it is no longer in the deck, nothing happens.
        3 Card	Draw the 3 card. If it is no longer in the deck, nothing happens.
        4 Card	Draw the 4 card. If it is no longer in the deck, nothing happens.
        5 Card	Draw the 5 card. If it is no longer in the deck, nothing happens.
        6 Card	Draw the 6 card. If it is no longer in the deck, nothing happens.
        7 Card	Draw the 7 card. If it is no longer in the deck, nothing happens.
        Return	Return the last face-up card you drew to the deck.
        Exchange	Swap the last cards drawn by you and your opponent. (Face-down cards cannot be swapped.)
        Trump Switch	Discard two of your trump cards at random, then draw three more trump cards.
        Trump Switch+	Discard two of your trump cards at random, then draw four more trump cards.
        Shield	Your bet is reduced by 1 while this card is on the table.
        Shield+	Your bet is reduced by 2 while this card is on the table.
        Destroy+	Remove all your opponent's trump cards from the table.
        Destroy++	Remove all of your opponent's trump cards from the table. Your opponent cannot use trump cards while this is on the table.
        Perfect Draw	Draw the best possible card from the deck.
        Perfect Draw+	Draw the best possible card from the deck. Also, your opponent's bet increases by 5 while this card is on the table.
        Ultimate Draw	Draw the best possible card from the deck. Also, draw two trump cards.
        Go for 17	The closest to 17 wins while this card is on the table. Replaces other "Go For" cards that are already on the table.
        Go for 24	The closest to 24 wins while this card is on the table. Replaces other "Go For" cards that are already on the table.
        Go for 27	The closest to 27 wins while this card is on the table. Replaces other "Go For" cards that are already on the table.
 */
class TrumpCard {
    String type;
    ImageIcon imageIcon;

    public TrumpCard(String type, ImageIcon imageIcon) {
        this.type = type;
        this.imageIcon = imageIcon;
    }

    public TrumpCard(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ImageIcon getImageIcon() {
        return imageIcon;
    }

    public void setImageIcon(ImageIcon imageIcon) {
        this.imageIcon = imageIcon;
    }
}

class Main {
    public static void main(String[] args) {
    }
}
