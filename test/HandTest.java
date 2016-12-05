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
public class HandTest {
    
    public HandTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of removeTopCard method, of class Hand.
     */
    @Test
    public void testAddAndRemoveFirstCard() {
        System.out.println("removeTopCard");
        Hand hand = new Hand();
        Card c = new Card("K", "H", null);
        hand.addCard(c);
        Card expResult = c;
        Card result = hand.removeFirstCard();
        assertEquals(expResult, result);
    }

    /**
     * Test of doesContainAce method, of class Hand.
     */
    @Test
    public void testDoesContainAce() {
        System.out.println("doesContainAce");
        Hand hand = new Hand();
        Card c = new Card("K", "H", null);
        Card a = new Card("A", "D", null);
        assertEquals(false, hand.doesContainAce());
        hand.addCard(c);
        assertEquals(false, hand.doesContainAce());
        hand.addCard(a);
        assertEquals(true, hand.doesContainAce());
    }

    /**
     * Test of isFirstAce method, of class Hand.
     */
    @Test
    public void testIsFirstAce() {
        System.out.println("isFirstAce");
        Hand hand = new Hand();
        Card c = new Card("K", "H", null);
        Card a = new Card("A", "D", null);
        hand.addCard(c);
        assertEquals(false, hand.isFirstAce());
        hand.addCard(a);
        assertEquals(false, hand.isFirstAce());
        hand = new Hand();
        hand.addCard(a);
        assertEquals(true, hand.isFirstAce());
        hand.addCard(c);
        assertEquals(true, hand.isFirstAce());
    }

    /**
     * Test of getValue method, of class Hand.
     */
    @Test
    public void testGetValue() {
        System.out.println("getValue");
        Hand hand = new Hand();
        Card c = new Card("K", "H", null);
        Card a = new Card("A", "D", null);
        Card o = new Card("2", "S", null);
        assertEquals(0, hand.getValue());
        hand.addCard(c);
        assertEquals(10, hand.getValue());
        hand.addCard(a);
        assertEquals(21, hand.getValue());
        hand.addCard(o);
        assertEquals(13, hand.getValue());
    }
    
}
