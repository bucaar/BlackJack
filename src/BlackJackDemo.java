
import java.awt.Dimension;
import java.util.ArrayList;
import javax.swing.JFrame;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Aaron
 */
public class BlackJackDemo extends JFrame{
    public static final char EOL = '\n';
    
    private Server server;
    private Table table;
    private ArrayList<Player> lobby;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new BlackJackDemo().start();
    }
    
    public BlackJackDemo(){
        this.server = new Server(4949);
        this.table = new Table(3, 6);
        this.lobby = new ArrayList<>();
        
        setTitle("Blackjack");
        setPreferredSize(new Dimension(1500, 800));
        add(table);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
    
    public void start(){
        while(server.active()){
            //process input
            Client client = server.available();
            if(client != null){
                String in = readString(client);
                if(in != null && !in.isEmpty()){
                    processInput(client, in);
                    System.out.println("\n\n-----");
                    System.out.println(lobby);
                    System.out.println("");
                    System.out.println(table.tableAsString(true));
                }
            }
            
            //ensure all players are still connected.
            for(int i=0;i<lobby.size();i++){
                if(!lobby.get(i).isActive()){
                    //remove and don't skip the next person
                    lobby.remove(i);
                    i--;
                }
            }
            table.removeDisconnected();
            
            //see if table is ready to deal
            if(table.readyToDeal()){
                //deal the table
                System.out.println("\nDEALING...");
                table.dealTable();
                //check the dealer's cards for blackjack
                System.out.println("\n");
                System.out.println(table.tableAsString(true));
                if(table.checkDealer()){
                    //dealer has blackjack
                }
                else{
                    //dealer doesn't have blackjack
                    table.playSeats();
                    if(table.playDealer()){
                        //dealer busts
                    }
                    else{
                        //dealer doesn't bust
                    }
                }
                table.payTable();
                table.clearHands();
                table.clearHand();
                table.broadcastToTable("T");
                System.out.println("\n");
                System.out.println(table.tableAsString(true));
            }
        }
    }
    
    public void processInput(Client client, String message){
        //figure out what type of request
        char action = message.charAt(0);
        //only keep the rest, if there is any
        if(message.length()>2){
            message = message.substring(2);
        }
        else{
            message = "";
        }
        //which action?
        switch(action){
            case 'J': {//Join lobby
                //create the player object with the username
                Player player = new Player(message, client);
                //is the username valid?
                if(message.length() < 3){
                    player.writeString("N Your username is not long enough.");
                }
                //see if this player already exists in the lobby or the table.
                else if(lobby.contains(player) || table.getPlayer(client) != null){
                    player.writeString("N You have already joined.");
                }
                //okay to join the lobby!
                else{
                    //add the player
                    lobby.add(player);
                    player.writeString("Y You have been added to the lobby!");
                    //display the player the seat options.
                    player.writeString("S " + table.serverSeatOptions());
                    table.repaint();
                }
                break;
            }
            case 'S': {//Sit at the table
                //see which player is trying to sit.
                Player player = getPlayerFromLobby(client);
                //if the player exists, 
                if(player != null){
                    //get the player
                    try{
                        //see which seat he wanted
                        int seat = Integer.parseInt(message);
                        //try to sit
                        if(table.seatPlayer(player, seat)){
                            //remvoe from the lobby
                            lobby.remove(player);
                            player.writeString("Y You are now sitting at the table.");
                            //notify him to place his bet.
                            player.writeString("T " + player.getMoney());
                            table.repaint();
                        }
                        //we couldn't sit them.
                        else{
                            player.writeString("N You could not sit at seat " + seat + ".");
                            //reprompt to try again
                            player.writeString("S " + table.serverSeatOptions());
                        }
                    }
                    catch(NumberFormatException e){
                        player.writeString("N (" + message + ") is not a valid seat.");
                        player.writeString("S " + table.serverSeatOptions());
                    }
                }
                //if they dont,
                else{
                    //they cannot sit down because they were not in the lobby.
                    writeString(client, "N You cannot sit because you haven't joined the game.");
                }
                //Update all lobby player's available seat options
                broadcastToLobby("S " + table.serverSeatOptions());
                break;
            }
            case 'L': {//Leave the table to the lobby
                //see which player is trying to leave.
                Player player = table.getPlayer(client);
                //if the player exists, 
                if(player != null){
                    //clear that seat.
                    table.leaveSeat(client);
                    //add the player to the lobby
                    lobby.add(player);
                    //notify player he is no longer sitting
                    player.writeString("Y You are no longer sitting at the table.");
                    
                    //Update all lobby player's available seat options
                    broadcastToLobby("S " + table.serverSeatOptions());
                    table.repaint();
                }
                //if they dont,
                else{
                    //they cannot leave.
                    writeString(client, "N You cannot leave because you aren't sitting.");
                }
                break;
            }
            case 'B': {//Bet a wager
                //see which player is trying to bet.
                Player player = table.getPlayer(client);
                //if the player exists, 
                if(player != null){
                    try{
                        //see how much they would like to wager
                        int wager = Integer.parseInt(message);
                        int seat = table.getSeat(client);
                        //if they are seated at the table
                        if(seat >= 0){
                            //place the wager.
                            if(table.placeWager(player, seat, wager)){
                                player.writeString("Y Wager of " + wager + " placed.");
                                table.repaint();
                            }
                            //couldn't wager.
                            else{
                                player.writeString("N Wager of " + wager + " could not be placed.");
                                player.writeString("T " + player.getMoney());
                            }
                        }
                        //this shouldn't happen.
                        else{
                            
                        }
                    }
                    catch(NumberFormatException e){
                        player.writeString("N (" + message + ") is not a valid wager.");
                        player.writeString("T " + player.getMoney());
                    }
                }
                //if they dont,
                else{
                    //they cannot leave.
                    writeString(client, "N You cannot bet because you aren't sitting.");
                }
                break;
            }
            default: {//Anything else?
                writeString(client, "N Action '" + action + "' not allowed now.");
                break;
            }
        }
    }
    
    public void broadcastToLobby(String message){
        for(Player p : lobby){
            p.writeString(message);
        }
    }
    
    public Player getPlayerFromLobby(Client c){
        for(Player p : lobby){
            if(p.getClient() == c){
                return p;
            }
        }
        return null;
    }
    
    public void writeString(Client c, String message){
        c.write(message + EOL);
    }
    
    public String readString(Client c){
        String read = c.readStringUntil(EOL);
        return read==null?null:read.trim();
    }
}
