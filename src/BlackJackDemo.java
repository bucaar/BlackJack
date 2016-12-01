/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Aaron
 */
public class BlackJackDemo {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Table t = new Table(1, 6);
        Player p = new Player("Joe", 123);
        t.seatPlayer(p, 0);
        
        while(true){
            System.out.println("\n\n");
            p.writeString("You have " + p.getMoney() + " dollars.");
            if(t.placeWager(p, 0, 20)){
                p.writeString("You made a wager of 20 dollars.");
            }
            else{
                p.writeString("You must not have any money!");
                break;
            }
            t.dealTable();
            if(t.checkDealer()){
                p.writeString("Dealer has a blackjack.");
            }
            else{
                t.playSeat(0);
                if(t.playDealer()){
                    p.writeString("The dealer busted!");
                }
            }
            t.payTable();
            t.clearHand();
            t.clearHands(0);
        }
    }
}
