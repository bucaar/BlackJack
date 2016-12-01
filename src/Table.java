import java.util.ArrayList;

class Table {
    private Player[] table;
    private ArrayList<Hand>[] hands;
    private Hand dealer;
    private Deck shoe;

    /**
     * The constructor for a table.
     * @param seats the number of seats at this table
     * @param decks the number of decks in this shoe.
     */
    public Table(int seats, int decks) {
        table = new Player[seats];
        hands = new ArrayList[seats];
        dealer = new Hand();
        shoe = new Deck(decks);
        shoe.shuffle();
    }
    
    /**
     * Seats a specified player at the specified seat
     * @param player The player to seat
     * @param seat The seat to be sat at
     * @return whether or not the player was sat
     */
    public boolean seatPlayer(Player player, int seat){
        //if the seat isnt taken already,
        if(table[seat] == null){
            //add the player, his hand array, and his first (empty) hand 
            //so he can place a wager to play.
            table[seat] = player;
            hands[seat] = new ArrayList<>();
            hands[seat].add(new Hand());
            return true;
        }
        return false;
    }
    
    /**
     * A method for a player to place a wager on their hand.
     * @param player The player that is making the wager
     * @param seat The seat that the player is sitting at
     * @param wager The amount that the player wishes to wager
     * @return Whether or not the wager was successfully made
     */
    public boolean placeWager(Player player, int seat, int wager){
        //if it is a valid seat
        if(seat < 0 || seat >= table.length){
            return false;
        }
        //the only player to wager is the one who is sitting there
        if(table[seat] != player){
            return false;
        }
        //can only wager if he has one hand with no cards
        if(hands[seat] == null || hands[seat].size() != 1 || hands[seat].get(0).size() != 0){
            return false;
        }
        //if the player does not have enough money to wager
        if(player.getMoney() < wager){
            return false;
        }
        //otherwise, continue
        player.takeMoney(wager);
        hands[seat].get(0).setWager(wager);
        return true;
    }
    
    /**
     * The method to deal cards to a table.
     * The table must be cleared before calling
     */
    public void dealTable(){
        //does the deck need a shuffle?
        if(shoe.needsShuffle()){
            shoe.shuffle();
        }
        //deal cards to each of the table hands with a wager, and then the dealer: twice
        for(int t=0;t<2;t++){
            for(int s=0;s<table.length;s++){
                //if there is a player sitting here and they have a wager
                if(hands[s] != null && hands[s].get(0).getWager() > 0){
                    //deal them a card.
                    Card dealt = shoe.deal();
                    hands[s].get(0).addCard(dealt);
                }
            }
            //deal the dealer a card.
            Card dealt = shoe.deal();
            dealer.addCard(dealt);
        }
    }
    
    /**
     * This method will check the dealers hand for a blackjack.
     * it will offer insurance to any hands that want it.
     * This method should be called before playSeat(int).
     * @return true if dealer has a blackjack, otherwise false.
     */
    public boolean checkDealer(){
        //if the dealer is showing an ace
        if(dealer.isFirstAce()){
            //TODO: offer insurance.
            System.out.println("Insurance?");
            //TODO: if any hands have a blackjack, offer even money.
            System.out.println("Any players with blackjack, even money?");
        }
        //if the dealer is not showing a 10 card, then he doesn't have anything
        else if(!dealer.isFirstTen()){
            return false;
        }
        
        //if we haven't returned yet, we should check his hand for a 21.
        if(dealer.getValue() == 21){
            //TODO: pay out insurance bets
            return true;
        }
        //TODO: lose insurance bets
        return false;
    }
    
    /**
     * This method will handle all communication to complete the seat
     * @param seat the seat to play
     * 
     */
    public void playSeat(int seat){
        //if there is no hand here, return false
        if(hands[seat] == null || hands[seat].isEmpty()){
            return;
        }
        
        Player player = table[seat];
        
        
        //loop through every hand of this player
        for(int h=0;h<hands[seat].size();h++){
            
        
            //store the hand for easy referencing
            Hand hand = hands[seat].get(h);
            
            //if the first hand is a blackjack, return true (no input needed)
            if(h == 0 && hand.getValue() == 21){
                //TODO: tell player they recieved a blackjack
                player.writeString("[BJ] Wager: " + hand.getWager() + ", Hand " + h + ": " + hand.toString());
                //pay out the black jack 3:2
                player.giveMoney((int)(hand.getWager() * 2.5));
                clearHands(seat);
                return;
            }
            
            //question loop
            while(hand.getValue() <= 21){
                //deal a card if they only have one card (happens from splitting)
                if(hand.size() == 1){
                    hand.addCard(shoe.deal());
                }
                
                //TODO: notify player it is their turn and which hand
                player.writeString("It is your (" + table[seat].getUsername() + ") turn. (hand " + (h+1) + "/" + hands[seat].size() + ").");
        
                
                //TODO: ask player what they want to do
                player.writeString(tableAsString(true));
                player.writeString("Action? (H, S, D, P)");
                char option = player.readString().toUpperCase().charAt(0);
                
                //player hits
                if(option == 'H'){
                    hand.addCard(shoe.deal());
                }
                //player stays
                else if(option == 'S'){
                    break;
                }
                //player doubles (only on first two cards)
                else if(option == 'D'){
                    if(hand.size() == 2 && player.getMoney() >= hand.getWager()){
                        player.takeMoney(hand.getWager());
                        hand.setWager(hand.getWager() * 2);
                        hand.addCard(shoe.deal());
                        break;
                    }
                    else{
                        //TODO they cannot do this.
                        player.writeString("You cannot double down.");
                    }
                }
                //player splits (only on first two cards) and he has enough money
                else if(option == 'P'){
                    if(hand.canSplit() && player.getMoney() >= hand.getWager()){
                        Card splitCard = hand.removeFirstCard();
                        Hand newHand = new Hand();
                        newHand.addCard(splitCard);
                        newHand.setWager(hand.getWager());
                        player.takeMoney(hand.getWager());
                        hands[seat].add(newHand);
                    }
                    else{
                        //TODO they cannot do this
                        player.writeString("You cannot split");
                    }
                }
            }
            //TODO notify player their hand is completed.
            player.writeString("[DONE] Hand " + (h+1) + ": " + hand.toString());
            //check to see if they busted.
            if(hand.getValue() > 21){
                //they lose this hand and make sure we do not skip their next hand after deleting
                //TODO notify player that their hand busted
                player.writeString("Your hand busted!");
                hands[seat].remove(h);
                h--;
            }
        }
    }
    
    /**
     * This method plays through the dealer's turn.
     * @return true if dealer busts, false otherwise.
     */
    public boolean playDealer(){
        //dealer will hit to a hard 17
        while(dealer.getValue() <= 17){
            //if it is a hard 17, then break.
            if(dealer.getValue() == 17 && !dealer.isSoft()){
                break;
            }
            //the dealer needs to hit.
            System.out.println("Dealer hits!");
            dealer.addCard(shoe.deal());
            System.out.println(this.tableAsString(false));
        }
        System.out.println("Dealer finished!");
        System.out.println(this.tableAsString(false));
        return dealer.getValue() > 21;
    }
    
    /**
     * This method will pay out the table after comparing each hand to the dealer's.
     * This must be called before clearHand() or clearHands(int)
     */
    public void payTable(){
        //for every seat at this table
        for(int seat=0;seat<table.length;seat++){
            //if there is a player here that has hands
            if(table[seat] != null && hands[seat] != null
                    && !hands[seat].isEmpty()){
                //capture the player for payments
                Player player = table[seat];
                //for every hand at this seat
                for(int h=0;h<hands[seat].size();h++){
                    //capture the hand for referencing
                    Hand hand = hands[seat].get(h);
                    //did the player bust?
                    if(hand.getValue() > 21){
                        //do not pay.
                        System.out.println("THIS SHOULDNT HAPPEN: Player bust");
                    }
                    //did the dealer bust?
                    else if(dealer.getValue() > 21){
                        //TODO notify player of win
                        player.giveMoney(hand.getWager() * 2);
                    }
                    //did the player beat the dealer?
                    else if(hand.getValue() > dealer.getValue()){
                        //TODO notify player of win
                        player.giveMoney(hand.getWager() * 2);
                    }
                    //did the player push?
                    else if(hand.getValue() == dealer.getValue()){
                        player.giveMoney(hand.getWager());
                    }
                    //no? lose.
                    else{
                        //TODO notify player of loss
                        //the dealer beat the player
                        //no need to do anything
                    }
                }
            }
        }
    }
    
    /**
     * This method clears the dealers hand
     */
    public void clearHand(){
        dealer = new Hand();
    }
    
    /**
     * This method clears the hand at the specified seat
     * @param seat the seat to be cleared
     */
    public void clearHands(int seat){
        hands[seat] = new ArrayList<>();
        hands[seat].add(new Hand());
    }
    
    /**
     * This method opens a seat on the table
     * @param seat the seat to open
     */
    public void leaveSeat(int seat){
        table[seat] = null;
        hands[seat] = null;
    }
    
    /**
     * 
     * @return The string representation of this table.
     */
    public String tableAsString(boolean obstructDealer){
        StringBuilder out = new StringBuilder();
        
        //show the dealer's hand
        out.append("Dealer:\n\t").append(obstructDealer?dealer.dealerHand():dealer.toString()).append("\n");
        
        //for every seat at this table
        for(int s=0;s<table.length;s++){
            out.append("Seat ").append(s).append(": ");
            //if there is a player sitting here,
            if(table[s] != null){
                out.append(table[s].getUsername()).append(" $").append(table[s].getMoney());
                //if the player has hands,
                for(int h=0;h<hands[s].size();h++){
                    Hand hand = hands[s].get(h);
                    out.append("\n\t").append(h+1).append(": ");
                    out.append(hand.toString());
                }
            }
            
            //new line for next player.
            if(s < table.length-1){
                out.append("\n");
            }
        }
        
        return out.toString();
    }
}
