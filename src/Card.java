
import java.awt.image.BufferedImage;


class Card {
    private String face;
    private String suit;
    private int value;
    private BufferedImage image;
    /**
     * The constructor for a card
     * @param face A one character string of the face (A,5,T,J,Q,K)
     * @param suit A one character string of the suit (C,H,D,S)
     */
    public Card(String face, String suit, BufferedImage image) {
        this.face = face;
        this.suit = suit;
        calculateValue();
        
        this.image = image;
    }
    
    /**
     * a getter method to return the face of the card
     * @return the face of the card
     */
    public String getFace() {
        return face;
    }

    /**
     * a getter method to return the suit of the card
     * @return the suit of the card
     */
    public String getSuit() {
        return suit;
    }

    /**
     * a getter method to return the value of the card
     * @return the value of the card (A = 1)
     */
    public int getValue() {
        return value;
    }

    /**
     * a helper method to set the value of value
     * based on the face
     */
    private void calculateValue() {
        try {
            value = Integer.parseInt(face);
        } catch (Exception e) {
            switch (face) {
                case "A":
                    value = 1;
                    break;
                case "T":
                case "J":
                case "Q":
                case "K":
                    value = 10;
                    break;
            }
        }
    }

    /**
     * 
     * @return whether or not getValue() is equal to the value 1
     */
    public boolean isAce() {
        return value == 1;
    }
    
    /**
     * 
     * @return whether or not getValue() is equal to the value 10
     */
    public boolean isTen() {
        return value == 10;
    }
    
    public BufferedImage getImage(){
        return image;
    }

    /**
     * 
     * @return a two character string representing this card
     */
    public String toString() {
        return face + suit;
    }
    
    public boolean equals(Object o){
        if(!(o instanceof Card)){
            return false;
        }
        Card c = (Card) o;
        return this.getValue() == c.getValue();        
    }
}
