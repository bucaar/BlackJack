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
public class DeckTest {
    private Deck deck1;
    private Deck deck1_dup;
    
    public DeckTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        deck1 = new Deck(1);
        deck1_dup = new Deck(1);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of shuffle method, of class Deck.
     */
    @Test
    public void testShuffleAndDeal() {
        System.out.println("shuffle");
        deck1.shuffle();
        Card c1, c2;
        boolean difference = false;
        while(true){
            c1 = deck1.deal();
            c2 = deck1_dup.deal();
            if(c1 != null && c2 != null & !c1.equals(c2)){
                difference = true;
                break;
            }
            if(c1 == null || c2 == null){
                break;
            }
        }
        if(!difference){
            fail("The deck was not shuffled properly.");
        }
    }
    
}
