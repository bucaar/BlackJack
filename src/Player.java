class Player {
    private String username;
    private int money;
    
    private Client client;
    
    /**
     * The constructor for Player
     * @param username the player's username
     * @param id the player's id
     */
    public Player(String username, Client client) {
        this.username = username;
        this.client = client;
        
        this.money = 100;
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
     * 
     * @return whether or not the socket connection is active
     */
    public boolean isActive(){
        if(client == null){
            return false;
        }
        return client.active();
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
    
    /**
     * 
     * @return the associated client for this player.
     */
    public Client getClient(){
        return client;
    }
    
    /**
     * Writes the string with an attached EOL for easy reading.
     * @param out The string to write to the client
     */
    public void writeString(String out){
        if(isActive()){
            client.write(out + BlackJackDemo.EOL);
        }
    }
    
    /**
     * Reads from the client until the EOL character is found.
     * @return The string read from the client, null if none found.
     */
    public String readString(){
        if(isActive()){
            String in =  client.readStringUntil(BlackJackDemo.EOL);
            if(in != null){
                return in.trim();
            }
        }
        return null;
    }
    
    /**
     * 
     * @param o The object to compare to
     * @return true if the this has the same client as o, or if o is the client of this.
     */
    public boolean equals(Object o){
        if(o instanceof Player){
            Player p = (Player) o;
            return this.getClient() == p.getClient();
        }
        
        return false;
        
        
    }
}
