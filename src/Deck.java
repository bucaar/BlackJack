
class Deck {

    private static String faces = "A23456789TJQK";
    private static String suits = "DHCS";

    private Card[] cards;
    private int position = 0;

    /**
     * A constructor for a Deck
     *
     * @param decks the number of decks in this larger deck
     */
    public Deck(int decks) {
        cards = new Card[52 * decks];
        int i = 0;
        for (int d = 0; d < decks; d++) {
            for (int f = 0; f < faces.length(); f++) {
                for (int s = 0; s < suits.length(); s++) {
                    cards[i++] = new Card(faces.substring(f, f + 1), suits.substring(s, s + 1));
                }
            }
        }
    }

    /**
     * method that shuffles the deck
     */
    public void shuffle() {
        for (int i = 0; i < cards.length; i++) {
            int swap = (int) (cards.length * Math.random());
            Card temp = cards[swap];
            cards[swap] = cards[i];
            cards[i] = temp;
        }
        position = 0;
    }

    /**
     * a method that deals the next card off the top of the deck
     *
     * @return the card that has been dealt
     */
    public Card deal() {
        if (position < cards.length) {
            return cards[position++];
        } else {
            return null;
        }
    }

    /**
     *
     * @return whether or not 80% of the cards have been dealt
     */
    public boolean needsShuffle() {
        return percentUsed() > .8;
    }

    /**
     *
     * @return the percent dealt of this deck
     */
    public double percentUsed() {
        return (double) position / cards.length;
    }
}
