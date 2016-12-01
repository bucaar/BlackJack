/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Aaron
 */
public class PlayerTest {
    private int p1ID = 8493;
    private int p2ID = 1432;
    private String p1N = "Joey99";
    private String p2N = "Alice43";
    private Player player1;
    private Player player2;
    
    public PlayerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        player1 = new Player(p1N, null);
        player2 = new Player(p2N, null);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of toString method, of class Player.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        assertEquals(p1N, player1.toString());
        assertEquals(p2N, player2.toString());
    }

    /**
     * Test of getUsername method, of class Player.
     */
    @Test
    public void testGetUsername() {
        System.out.println("getUsername");
        assertEquals(p1N, player1.getUsername());
        assertEquals(p2N, player2.getUsername());
    }

    /**
     * Test of getMoney method, of class Player.
     */
    @Test
    public void testGetMoney() {
        System.out.println("getMoney");
        assertEquals(100, player1.getMoney());
        assertEquals(100, player2.getMoney());
    }

    /**
     * Test of takeMoney method, of class Player.
     */
    @Test
    public void testGiveAndTakeMoney() {
        System.out.println("takeMoney");
        player1.giveMoney(50);
        assertEquals(150, player1.getMoney());
        player1.takeMoney(50);
        assertEquals(100, player2.getMoney());
    }
    
}
