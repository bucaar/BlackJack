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
        int[] insurance = new int[table.length];
        //if the dealer is showing an ace
        if(dealer.isFirstAce()){
            String[] decisions = new String[table.length];
            
            //Offer insurance/even money.
            for(int seat=0;seat<table.length;seat++){
                Player p = table[seat];
                //skip empty seats, empty hands, or hands with no wagers.
                //note: there will only be one hand at this point.
                if(p == null || hands[seat].isEmpty() || hands[seat].get(0).getWager() == 0){
                    continue;
                }
                //if player has a blackjack, offer even money
                if(hands[seat].get(0).getValue() == 21){
                    p.writeString("E");
                }
                //otherwise, offer insurance
                else{
                    p.writeString("I");
                }
            }
            
            //Wait for all responses
            //TODO: Time limit?
            boolean allResponses = true;
            do{
                for(int seat=0;seat<table.length;seat++){
                    Player player = table[seat];
                    //skip empty seats, empty hands, or hands with no wagers.
                    //note: there will only be one hand at this point.
                    if(player == null || hands[seat].isEmpty() || hands[seat].get(0).getWager() == 0){
                        continue;
                    }
                    if(decisions[seat] == null){
                        decisions[seat] = player.readString();
                    }
                    if(decisions[seat] == null){
                        allResponses = false;
                    }
                }
                //TODO: Add delay to not waste CPU
            }while(!allResponses);
            
            //process players decisions
            for(int seat=0;seat<table.length;seat++){
                Player player = table[seat];
                //skip empty seats, empty hands, or hands with no wagers.
                //note: there will only be one hand at this point.
                if(player == null || hands[seat].isEmpty() || hands[seat].get(0).getWager() == 0){
                    continue;
                }
                //read from the player
                String in = decisions[seat];
                char decision = player.readString().charAt(0);
                if(in.length() > 2){
                    in = in.substring(2);
                }
                //if they've accepted even money
                if(decision == 'E'){
                    //they must have a blackjack for this.
                    if(hands[seat].get(0).getWager() == 21){
                        //pay even money, discard hand.
                        player.giveMoney((int)(hands[seat].get(0).getWager() * 2));
                        clearHands(seat);
                        player.writeString("Y Paid even money.");
                    }
                    //and if they dont,
                    else{
                        player.writeString("N You do not have a blackjack.");
                    }
                }
                //if theyve acccepted insurance
                else if(decision == 'I'){
                    try{
                        //see how much they gave
                        int amount = Integer.parseInt(in);
                        int wager = hands[seat].get(0).getWager();
                        if(amount > wager / 2){
                            amount = wager / 2;
                            player.writeString("N Cannot buy that much insurance. Limit is 1/2 wager.");
                        }
                        insurance[seat] = amount;
                        player.takeMoney(amount);
                        player.writeString("Y Insurance bought for " + amount);
                    }
                    catch(NumberFormatException e){
                        player.writeString("N You provided an invalid wager format (" + in + ").");
                    }
                }
                //or they declined
                else if(decision == 'N'){
                    //shouldn't have to do anything at all.
                    player.writeString("Y Declined.");
                }
            }
        }
        
        //if the dealer is not showing a 10 card, then he doesn't have anything
        else if(!dealer.isFirstTen()){
            return false;
        }
        
        //if we haven't returned yet, we should check his hand for a 21.
        if(dealer.getValue() == 21){
            //tell players dealer has blackjack
            broadcastToTable("D");
            for(int seat=0;seat<table.length;seat++){
                Player player = table[seat];
                //skip empty seats, empty hands, or hands with no wagers.
                //note: there will only be one hand at this point.
                if(player == null || hands[seat].isEmpty() || hands[seat].get(0).getWager() == 0){
                    continue;
                }
                //if the player bought insurance
                if(insurance[seat] > 0){
                    //pay him
                    player.giveMoney(insurance[seat] * 2);
                    player.writeString("Y Insurance paid!");
                }
            }
            return true;
        }
        //lose insurance bets.
        for(int seat=0;seat<table.length;seat++){
            Player player = table[seat];
            //skip empty seats, empty hands, or hands with no wagers.
            //note: there will only be one hand at this point.
            if(player == null || hands[seat].isEmpty() || hands[seat].get(0).getWager() == 0){
                continue;
            }
            //if the player bought insurance
            if(insurance[seat] > 0){
                //notify he lost his insurance.
                player.writeString("Y Insurance lost");
            }
        }
        return false;
    }
    
    /**
     * This method will handle all communication to complete the seat
     * @param seat the seat to play
     * 
     */
    public void playSeat(int seat){
        //if there is no hand here or the player isnt active, return
        if(hands[seat] == null || hands[seat].isEmpty() || !table[seat].isActive()){
            return;
        }
        
        //store the player for easy referencing.
        Player player = table[seat];
        
        //loop through every hand of this player
        for(int h=0;h<hands[seat].size();h++){
            //store the hand for easy referencing
            Hand hand = hands[seat].get(h);
            
            //if the first hand is a blackjack, return (no input needed)
            if(h == 0 && hand.getValue() == 21){
                //notify player they recieved a blackjack
                player.writeString("B");
                //pay out the black jack 3:2
                player.giveMoney((int)(hand.getWager() * 2.5));
                clearHands(seat);
                return;
            }
            
            //question loop
            while(hand.getValue() <= 21){
                //deal a card if they only have one card (happens from splitting cards)
                if(hand.size() == 1){
                    hand.addCard(shoe.deal());
                }
                
                //Notify player it is their turn
                player.writeString("G");
                //wait for input.
                String in;
                do{
                    in = player.readString();
                    //TODO: add delay to not waste CPU.
                }while(in == null);
                char option = in.charAt(0);
                if(in.length() > 2){
                    in = in.substring(2);
                }
                else{
                    in = "";
                }
                
                //player hits
                if(option == 'H'){
                    hand.addCard(shoe.deal());
                    player.writeString("Y You received a card");
                }
                //player stays
                else if(option == 'T'){
                    player.writeString("Y You stay");
                    break;
                }
                //player doubles (only on first two cards)
                else if(option == 'D'){
                    //assume they want to double full.
                    int amount = hand.getWager();
                    try{
                        //if they provided something, then try to read it.
                        if(in.length() > 0){
                            amount = Integer.parseInt(in);
                        }
                    }
                    catch(NumberFormatException e){
                        player.writeString("N You provided an invalid wager format (" + in + ").");
                    }
                    
                    //if they have two cards and they can afford it,
                    if(hand.size() == 2 && player.getMoney() >= amount){
                        player.takeMoney(amount);
                        hand.setWager(hand.getWager() + amount);
                        hand.addCard(shoe.deal());
                        player.writeString("Y you double down for (" + amount + ").");
                        break;
                    }
                    //otherwise
                    else{
                        player.writeString("N You cannot double down for (" + amount + ") right now.");
                    }
                }
                //player splits (only on first two cards) and he has enough money
                else if(option == 'P'){
                    //assume they want to split full.
                    int amount = hand.getWager();
                    try{
                        //if they provided something, then try to read it.
                        if(in.length() > 0){
                            amount = Integer.parseInt(in);
                        }
                    }
                    catch(NumberFormatException e){
                        player.writeString("N You provided an invalid wager format (" + in + ").");
                    }
                    
                    //if they can split and they can afford it
                    if(hand.canSplit() && player.getMoney() >= amount){
                        Card splitCard = hand.removeFirstCard();
                        Hand newHand = new Hand();
                        newHand.addCard(splitCard);
                        newHand.setWager(hand.getWager());
                        player.takeMoney(hand.getWager());
                        hands[seat].add(newHand);
                        player.writeString("Y you split for (" + amount + ")");
                    }
                    //otherwise
                    else{
                        player.writeString("N You cannot split for (" + amount + ") right now.");
                    }
                }
            }
            //check to see if they busted.
            if(hand.getValue() > 21){
                //they lose this hand and make sure we do not skip their next hand after deleting
                hands[seat].remove(h);
                h--;
                player.writeString("O");
            }
        }
    }
    
    /**
     * This method plays through the dealer's turn.
     * @return true if dealer busts, false otherwise.
     */
    public boolean playDealer(){
        //dealer will hit to a hard 17
        while(dealer.getValue() < 17 || 
                (dealer.getValue() == 17 && dealer.isSoft())){
            //the dealer needs to hit.
            dealer.addCard(shoe.deal());
        }
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
                        //make fun of Aaron.
                        player.writeString("N The programmer is a horrible person for making this message show up.");
                    }
                    //did the dealer bust?
                    else if(dealer.getValue() > 21){
                        //Notify player of win
                        player.giveMoney(hand.getWager() * 2);
                        player.writeString("W");
                    }
                    //did the player beat the dealer?
                    else if(hand.getValue() > dealer.getValue()){
                        //Notify player of win
                        player.giveMoney(hand.getWager() * 2);
                        player.writeString("W");
                    }
                    //did the player push?
                    else if(hand.getValue() == dealer.getValue()){
                        //Notify player of push
                        player.giveMoney(hand.getWager());
                        player.writeString("P");
                    }
                    //no? lose.
                    else{
                        //Notify player of loss
                        //the dealer beat the player
                        player.writeString("L");
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
    
    public void broadcastToTable(String message){
        for(int i=0;i<table.length;i++){
            if(table[i] != null){
                table[i].writeString(message);
            }
        }
    }
    
    public void broadcastToLobby(String message){
        //TODO implement lobby
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
            
            //new line for next player (if there is one).
            if(s < table.length-1){
                out.append("\n");
            }
        }
        
        return out.toString();
    }
}
