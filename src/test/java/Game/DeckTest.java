package Game;

import Game.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {
        @Test
        public void testAddNewCard() {
            Deck deck = new Deck(null);
            Card testCard = new MonsterCard("testid", "testname", 2, MonsterType.FireElf, ElementType.Fire);
            deck.addCard(testCard);
            assertEquals(1, deck.getSize());
        }

        @Test
        public void testIsEmpty() {
            Deck deck = new Deck(null);
            assertTrue(deck.isEmpty());
            Card testcard = new MonsterCard("testid","testname", 2, MonsterType.FireElf, ElementType.Fire);
            deck.addCard(testcard);
            assertFalse(deck.isEmpty());
        }

    @Test
    public void testDeleteNewCard() {
        Deck deck = new Deck(null);
        assertTrue(deck.isEmpty());
        Card testcard = new MonsterCard("testid","testname", 2, MonsterType.FireElf, ElementType.Fire);
        deck.addCard(testcard);
        deck.removeCard(testcard);
        assertTrue(deck.isEmpty());
    }

    @Test
    public void testGetSize() {
        Deck deck = new Deck(null);
        assertEquals(0, deck.getSize());
        Card testCard1 = new MonsterCard("testid", "testname", 2, MonsterType.FireElf, ElementType.Fire);
        deck.addCard(testCard1);
        Card testCard2 = new MonsterCard("testid", "testname", 2, MonsterType.Troll, ElementType.Normal);
        deck.addCard(testCard2);
        Card testCard3 = new MonsterCard("testid", "testname", 2, MonsterType.Kraken, ElementType.Water);
        deck.addCard(testCard3);
        assertEquals(3, deck.getSize());
    }

    @Test
    public void testGetRandomCard() {
        Deck deck = new Deck(null);
        Card testcard1 = new MonsterCard("testid","testname", 2, MonsterType.Knight, ElementType.Normal);
        Card testcard2 = new MonsterCard("testid","testname", 2, MonsterType.Ork, ElementType.Normal);
        Card testcard3 = new MonsterCard("testid","testname", 2, MonsterType.Goblin, ElementType.Normal);
        deck.addCard(testcard1);
        deck.addCard(testcard2);
        deck.addCard(testcard3);
        assertTrue(deck.getRandomCard() instanceof MonsterCard);
    }


}