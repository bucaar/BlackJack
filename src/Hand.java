
import java.util.ArrayList;
import java.util.Iterator;

class Hand implements Iterable<Card>{
    private ArrayList<Card> cards;
    private boolean containsAce;
    private boolean soft;
    private int wager;
    
    private boolean alive = true;

    /**
     * The constructor for a hand
     */
    public Hand() {
        cards = new ArrayList();
    }

    /**
     * 
     * @return removes and returns the first dealt card to this hand (or null if empty)
     */
    public Card removeFirstCard() {
        return cards.isEmpty() ? null : cards.remove(0);
    }

    /**
     * 
     * @param c The card to be added to this hand
     */
    public void addCard(Card c) {
        cards.add(c);
        if (c.getFace().equals("A")) {
            containsAce = true;
        }
    }
    
    /**
     * 
     * @return Whether or not the value of this hand is increased by an ace
     */
    public boolean isSoft(){
        getValue();
        return soft;
    }
    
    /**
     * 
     * @return whether or not the hand is a pair of cards
     */
    public boolean canSplit(){
        return cards.size() == 2 && cards.get(0).getValue() == cards.get(1).getValue();
    }

    /**
     * 
     * @return whether or not the hand contains an ace
     */
    public boolean doesContainAce() {
        return containsAce;
    }

    /**
     * 
     * @return whether or not the first card is an ace (useful for dealers)
     */
    public boolean isFirstAce() {
        return cards.size() > 0 ? cards.get(0).isAce() : false;
    }
    
    /**
     * 
     * @return whether or not the first card is a ten-value (useful for dealers)
     */
    public boolean isFirstTen() {
        return cards.size() > 0 ? cards.get(0).isTen() : false;
    }

    /**
     * this method adds up all of the individual values of the cards in this hand
     * values will be shown as soft values over hard values
     * (A, 7) will return 18, not 8.
     * @return the total value of this hand
     */
    public int getValue() {
        int value = 0;
        for (Card c : cards) {
            value += c.getValue();
        }

        if (doesContainAce() && value + 10 <= 21) {
            value += 10;
            soft = true;
        }

        return value;
    }
    
    /**
     * 
     * @return The value of the dealers first card A = 11
     */
    public int getDealerValue(){
        if(cards.isEmpty()) 
            return 0;
        int value = cards.get(0).getValue();
        if(value == 1){
            value += 10;
        }
        
        return value;
    }

    /**
     * Sets the wager of this hand
     * @param wager the wager to set
     */
    public void setWager(int wager) {
        this.wager = wager;
    }

    /**
     * 
     * @return the wager of this hand
     */
    public int getWager() {
        return wager;
    }

    /**
     * 
     * @return The total number of cards in this hand
     */
    public int size(){
        return cards.size();
    }
    
    /**
     * 
     * @return A string showing the top card of the dealer's hand.
     */
    public String dealerHand(){
        if(cards.isEmpty()){
            return "";
        }
        return "[" + cards.get(0) + "]";
    }
    
    /**
     * This method sets isAlive() to false
     */
    public void die(){
        alive = false;
    }
    
    /**
     * 
     * @return Whether or not this hand is removed
     */
    public boolean isAlive(){
        return alive;
    }
    
    /**
     * 
     * @return A string containing the wager, cards in the hand, value of the hand.
     */
    public String toString(){
        return "$" + wager + " " + cards.toString() + " = " + getValue();
    }

    @Override
    public Iterator<Card> iterator() {
        return new HandIterator();
    }
    
    private class HandIterator implements Iterator<Card>{
        private int position = 0;
        
        @Override
        public boolean hasNext() {
            return position < cards.size();
        }

        @Override
        public Card next() {
            return cards.get(position++);
        }
    }
}
