
import java.util.Scanner;


class Player {
    private String username;
    private int id;
    private int money = 100;
    
    /**
     * The constructor for Player
     * @param username the player's username
     * @param id the player's id
     */
    public Player(String username, int id) {
        this.username = username;
        this.id = id;
    }

    /**
     * 
     * @return the username for the username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * 
     * @return the id for the player
     */
    public int getId(){
        return id;
    }

    /**
     * 
     * @return the amount of money this player owns
     */
    public int getMoney() {
        return money;
    }

    /**
     * Reduces the amount of money the player owns
     * @param money the amount to reduce by
     */
    public void takeMoney(int money) {
        this.money -= money;
    }
    
    /**
     * Increases the amount of money the player owns
     * @param money the amount to increase by
     */
    public void giveMoney(int money) {
        this.money += money;
    }

    /**
     * 
     * @return the username of the player as a String
     */
    public String toString() {
        return username;
    }
    
    public void writeString(String out){
        System.out.println(out);
    }
    
    private Scanner scan = new Scanner(System.in);
    public String readString(){
        return scan.nextLine();
    }
}
