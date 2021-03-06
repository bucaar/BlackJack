import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.swing.JApplet;

class Table extends JApplet{
    private Player[] table;
    private ArrayList<Hand>[] hands;
    private Hand dealer;
    private Deck shoe;
    private boolean hideDealer;
    private boolean playing = false;
    
    private int cardWidth = 150;
    private int cardHeight = 275;
    private int dieOffset = 0;
    
    private BufferedImage back;
    private BufferedImage bjStar;

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
        
        try{
            back = ImageIO.read(new File("src\\images\\back.png"));
            bjStar = ImageIO.read(new File("src\\images\\bjStar.png"));
        }
        catch(IOException e){}
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
     * Searches the table for the given player
     * @param client The client to search for
     * @return the associated player, null if none.
     */
    public Player getPlayer(Client client){
        for(int seat=0;seat<table.length;seat++){
            if(table[seat] != null && table[seat].getClient() == client){
                return table[seat];
            }
        }
        return null;
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
        //make sure the wager is some positive number
        if(wager <= 0){
            return false;
        }
        //otherwise, continue
        player.takeMoney(wager);
        hands[seat].get(0).setWager(wager);
        return true;
    }
    
    /**
     * 
     * @return whether or not every sat player has a wager
     */
    public boolean readyToDeal(){
        //if the game is playing, then we are not ready.
        if(playing){
            return false;
        }
        
        boolean ready = true;
        boolean anyone = false;
        for(int seat=0;seat<table.length;seat++){
            //if there is someone here
            if(table[seat] != null){
                //there is someone sitting
                anyone = true;
                //if they don't have a wager, they arent ready.
                if(hands[seat].size() == 1 && hands[seat].get(0).getWager() == 0){
                    ready = false;
                }
            }
        }
        //we are ready if there is anone and everyone is ready
        return anyone && ready;
    }
    
    /**
     * The method to deal cards to a table.
     * The table must be cleared before calling
     */
    public void dealTable(){
        //log that the game is going on. this prevents two games from being dealt at once.
        playing = true;
        //default to hide the dealer
        hideDealer = true;
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
                    repaint();
                    pause(500);
                }
            }
            //deal the dealer a card.
            Card dealt = shoe.deal();
            dealer.addCard(dealt);
            repaint();
            pause(1000);
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
            boolean allResponses;
            do{
                allResponses = true;
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
                char decision = in.charAt(0);
                if(in.length() > 2){
                    in = in.substring(2);
                }
                else{
                    in = "";
                }
                //if they've accepted even money
                if(decision == 'E'){
                    //they must have a blackjack for this.
                    if(hands[seat].get(0).getValue() == 21){
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
                    int wager = hands[seat].get(0).getWager();
                    int amount = wager / 2;
                    try{
                        //see how much they gave
                        amount = Integer.parseInt(in);
                        if(amount > wager / 2){
                            amount = wager / 2;
                            player.writeString("N Cannot buy that much insurance. Limit is 1/2 wager.");
                        }
                    }
                    catch(NumberFormatException e){
                        player.writeString("N You provided an invalid wager format (" + in + ").");
                    }
                    if(amount > 0){
                        insurance[seat] = amount;
                        player.takeMoney(amount);
                        player.writeString("Y Insurance bought for " + amount);
                    }
                    else{
                        player.writeString("Y Insurance declined.");
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
            //no need to hide
            hideDealer = false;
            repaint();
            pause(1000);
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
                    player.giveMoney(insurance[seat] * 3);
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
     * This method will handle all communication to complete all of the seats
     */
    public void playSeats(){
        for(int seat=0;seat<table.length;seat++){
            playSeat(seat);
        }
    }
    /**
     * This method will handle all communication to complete the seat
     * @param seat the seat to play
     * 
     */
    public void playSeat(int seat){
        //if there is no hand here or the player isnt active, return
        if(hands[seat] == null || hands[seat].isEmpty()){
            return;
        }
        
        //if the player is not active, then remove him from the game.
        if(!table[seat].isActive()){
            leaveSeat(seat);
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
                hands[seat].get(h).die();
                int now = hands[seat].size();
                while(hands[seat].size() == now){
                    repaint();
                    pause(100);
                }
                dieOffset = 0;
                clearHands(seat);
                return;
            }
            
            //question loop
            while(hand.getValue() <= 21){
                //deal a card if they only have one card (happens from splitting cards)
                if(hand.size() == 1){
                    //if the card that they have is an ace, they only get one more card.
                    //UNLESS the dealt card is another ace.
                    Card dealt = shoe.deal();
                    hand.addCard(dealt);
                    repaint();
                    if(hand.isFirstAce() && !dealt.isAce()){
                        break;
                    }
                }
                System.out.println("\n");
                System.out.println(tableAsString(true));
                //Notify player it is their turn
                player.writeString("G");
                //wait for input.
                String in;
                do{
                    in = player.readString();
                    //TODO: add delay to not waste CPU.
                }while((in == null || in.isEmpty()) && player.isActive());
                //see if the player is still active, if not, remove him.
                if(!player.isActive()){
                    leaveSeat(seat);
                    return;
                }
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
                    repaint();
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
                    if(hand.size() == 2 && player.getMoney() >= amount && amount <= hand.getWager()){
                        player.takeMoney(amount);
                        hand.setWager(hand.getWager() + amount);
                        hand.addCard(shoe.deal());
                        repaint();
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
                    if(hand.canSplit() && hands[seat].size() < 4 && player.getMoney() >= amount && amount <= hand.getWager()){
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
                //make sure they see what happened.
                pause(1000);
                //they lose this hand and make sure we do not skip their next hand after deleting
                //hands[seat].remove(h);
                //h--;
                hands[seat].get(h).die();
                int now = hands[seat].size();
                while(hands[seat].size() == now){
                    repaint();
                    pause(100);
                }
                h--;
                dieOffset = 0;
                player.writeString("O");
            }
            
            System.out.println("\n");
            System.out.println(tableAsString(true));
        }
    }
    
    /**
     * This method plays through the dealer's turn.
     * @return true if dealer busts, false otherwise.
     */
    public boolean playDealer(){
        //dealer will hit to a hard 17
        hideDealer = false;
        repaint();
        pause(1000);
        while(dealer.getValue() < 17 || 
                (dealer.getValue() == 17 && dealer.isSoft())){
            //the dealer needs to hit.
            dealer.addCard(shoe.deal());
            repaint();
            pause(500);
        }
        System.out.println("\n");
        System.out.println(tableAsString(false));
        if(dealer.getValue() > 21){
            broadcastToTable("K");
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
            //if there is a player here that has hands with a wager
            if(table[seat] != null && hands[seat] != null
                    && !hands[seat].isEmpty() && hands[seat].get(0).getWager() > 0){
                //capture the player for payments
                Player player = table[seat];
                //for every hand at this seat
                for(int h=0;h<hands[seat].size();h++){
                    //capture the hand for referencing
                    Hand hand = hands[seat].get(h);
                    //did the player bust?
                    if(hand.getValue() > 21){
                        //do not pay.
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
     * This should be called after clearHands()
     */
    public void clearHand(){
        playing = false;
        dealer = new Hand();
        repaint();
        pause(1000);
    }
    
    /**
     * This method clears all of the player's hands.
     */
    public void clearHands(){
        for(int seat=0;seat<table.length;seat++){
            if(table[seat] != null){
                clearHands(seat);
            }
        }
        repaint();
        pause(1000);
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
     * This method removes all players that are not active from the table
     */
    public void removeDisconnected(){
        for(int seat = 0;seat<table.length;seat++){
            if(table[seat] != null && !table[seat].isActive()){
                leaveSeat(seat);
                return;
            }
        }
    }
    
    /**
     * This method opens a seat on the table
     * @param client the client to leave
     */
    public void leaveSeat(Client client){
        for(int seat = 0;seat<table.length;seat++){
            if(table[seat] != null && table[seat].getClient() == client){
                leaveSeat(seat);
                return;
            }
        }
    }
    
    /**
     * This method opens a seat on the table
     * @param seat the seat to empty
     */
    public void leaveSeat(int seat){
        clearHands(seat);
        table[seat] = null;
        hands[seat] = null;
    }
    
    /**
     * Searches the table for the given client
     * @param client the client to search for
     * @return the seat number of the client, -1 otherwise
     */
    public int getSeat(Client client){
        for(int seat = 0;seat<table.length;seat++){
            if(table[seat] != null && table[seat].getClient() == client){
                return seat;
            }
        }
        return -1;
    }
    
    public void broadcastToTable(String message){
        for(int i=0;i<table.length;i++){
            if(table[i] != null){
                if(message.equals("T")){
                    table[i].writeString(message + " " + table[i].getMoney());
                }
                else{
                    table[i].writeString(message);
                }
            }
        }
    }
    
    public String serverSeatOptions(){
        String out = "";
        //go through every seat
        for(int seat=0;seat<table.length;seat++){
            //if this isn't the first seat, use a semicolon to separate.
            if(seat > 0){
                out += ";";
            }
            //output the seat number and a space
            out += seat + " ";
            //output O for open, username for taken.
            if(table[seat] == null){
                out += "O";
            }
            else{
                out += table[seat].getUsername();
            }
        }
        return out;
    }
    
    public void paint(Graphics g){
        //calculate card size based on window and how many seats there are.
        cardWidth = (getWidth()-20-(table.length*4-1)*10) / (table.length*4);
        cardHeight = (int)(cardWidth * 1.5);
        
        //background
        g.setFont(new Font("Ariel", Font.PLAIN, cardWidth/5));
        g.setColor(new Color(30,160,70));
        g.fillRect(0, 0, getWidth(), getHeight());
        
        //dealer's hand
        g.setColor(Color.BLACK);
        Iterator<Card> dealersCards = dealer.iterator();
        int card = 0;
        int pad = (getWidth() - (cardWidth+10)*dealer.size() + 10) / 2;
        while(dealersCards.hasNext()){
            Card c = dealersCards.next();
            g.drawImage(c.getImage(), pad + (cardWidth+10) * card, 100, cardWidth, cardHeight, null);
            card++;
            if(hideDealer && dealer.size() == 2){
                g.drawImage(back, pad + (cardWidth+10) * card, 100, cardWidth, cardHeight, null);
                break;
            }
        }
        //dealer's value
        String dealerValue = "[" + (hideDealer?dealer.getDealerValue():dealer.getValue()) + "]";
        g.drawString(dealerValue, getWidth()/2 - g.getFontMetrics().stringWidth(dealerValue)/2, g.getFont().getSize());
        

        //player's info
        for(int seat=0;seat<table.length;seat++){
            //skip the players that do not have hands.
            if(table[seat] == null){
                continue;
            }
            
            //player's username
            g.setColor(Color.BLACK);
            g.drawString(table[seat].getUsername() + " ($" + table[seat].getMoney() + ")", 
                    getWidth()/(table.length)*seat, 
                    getHeight() - 10 - (g.getFont().getSize()+3)*2);
            
            for(int hand = 0; hand < hands[seat].size(); hand++){
                Hand h = hands[seat].get(hand);
                //crude animation for busting or blackjacks
                if(!h.isAlive()){
                    dieOffset += cardHeight*3/4;
                    if(dieOffset > getHeight()){
                        hands[seat].remove(h);
                        hand--;
                        continue;
                    }
                }
                
                Iterator<Card> playersCards = h.iterator();
                card = 0;
                //draw the wager and total for this hand
                g.setColor(Color.BLACK);
                g.drawString("$" + h.getWager() + " [" + h.getValue() + "]", 
                        getWidth()/(table.length)*seat + hand*(cardWidth+10), 
                        getHeight() - 10 - (g.getFont().getSize()+3)*1);
                //draw the cards in this hand
                while(playersCards.hasNext()){
                    Card c = playersCards.next();
                    g.drawImage(c.getImage(), 
                            getWidth()/(table.length)*seat + hand*(cardWidth+10) + card*(cardWidth/4) + 10, 
                            getHeight() - 10 - (g.getFont().getSize()+3)*3 - cardHeight - (cardHeight/4)*card + (!h.isAlive()?dieOffset:0), 
                            cardWidth, 
                            cardHeight, 
                            null);
                    card++;
                }
                //awesome blackjack star representing
                if(hand == 0 && h.size() == 2 && h.getValue() == 21){
                    System.out.println("BLACKJACK");
                    g.drawImage(bjStar, 
                            getWidth()/(table.length)*seat + hand*(cardWidth+10), 
                            getHeight() - 10 - (g.getFont().getSize()+3)*3 - 2*cardHeight + (!h.isAlive()?dieOffset:0), 
                            cardWidth, 
                            cardWidth, 
                            null);
                }
            }
        }
    }
    
    public void pause(int millis){
        try{
            Thread.sleep(millis);
        }
        catch(InterruptedException e){
            
        }
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
