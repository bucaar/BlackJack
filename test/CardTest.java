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
public class CardTest {
    private Card jackOfHearts;
    private Card aceOfSpades;
    public CardTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        jackOfHearts = new Card("J", "H", null);
        aceOfSpades = new Card("A", "S", null);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getFace method, of class Card.
     */
    @Test
    public void testGetFace() {
        System.out.println("getFace");
        assertEquals("J", jackOfHearts.getFace());
        assertEquals("A", aceOfSpades.getFace());
    }

    /**
     * Test of getSuit method, of class Card.
     */
    @Test
    public void testGetSuit() {
        System.out.println("getSuit");
        assertEquals("H", jackOfHearts.getSuit());
        assertEquals("S", aceOfSpades.getSuit());
    }

    /**
     * Test of getValue method, of class Card.
     */
    @Test
    public void testGetValue() {
        System.out.println("getValue");
        assertEquals(10, jackOfHearts.getValue());
        assertEquals(1, aceOfSpades.getValue());
    }

    /**
     * Test of isAce method, of class Card.
     */
    @Test
    public void testIsAce() {
        System.out.println("isAce");
        assertEquals(false, jackOfHearts.isAce());
        assertEquals(true, aceOfSpades.isAce());
    }

    /**
     * Test of toString method, of class Card.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        assertEquals("JH", jackOfHearts.toString());
        assertEquals("AS", aceOfSpades.toString());
    }
    
}
