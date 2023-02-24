package Game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {
    @Test
    void instaKill_DragonGoblin() {
        Card testcard1 = new MonsterCard("testid","testname", 3, MonsterType.Dragon, ElementType.Fire);
        Card testcard2 = new MonsterCard("testid","testname", 3, MonsterType.Goblin, ElementType.Normal);
        assertTrue(testcard1.instaKill(testcard2));
    }
    @Test
    void instaKill_KrakenSpell() {
        Card testcard1 = new SpellCard("testid","testname", 3, ElementType.Fire);
        Card testcard2 = new MonsterCard("testid","testname", 3, MonsterType.Kraken, ElementType.Water);
        assertTrue(testcard2.instaKill(testcard1));
    }

    @Test
    void instaKill_WizardOrk() {
        Card testcard1 = new MonsterCard("testid","testname", 3, MonsterType.Wizard, ElementType.Water);
        Card testcard2 = new MonsterCard("testid","testname", 3, MonsterType.Ork, ElementType.Normal);
        assertTrue(testcard1.instaKill(testcard2));
    }

    @Test
    void instaKill_False() {
        Card testcard1 = new SpellCard("testid","testname", 3, ElementType.Water);
        Card testcard2 = new SpellCard("testid","testname", 3, ElementType.Water);
        assertFalse(testcard1.instaKill(testcard2));
    }

    @Test
    void calculateDamage_Normal() {
        Card testcard1 = new MonsterCard("testid","testname", 3, MonsterType.Dragon, ElementType.Fire);
        Card testcard2 = new MonsterCard("testid","testname", 3, MonsterType.Dragon, ElementType.Fire);
        assertEquals(3, testcard1.calculateDamage(testcard2));
    }

    @Test
    void calculateDamage_Effective() {
        Card testcard1 = new SpellCard("testid","testname", 3, ElementType.Fire);
        Card testcard2 = new MonsterCard("testid","testname", 3, MonsterType.Knight, ElementType.Water);
        assertEquals(6, testcard2.calculateDamage(testcard1));
    }

    @Test
    void calculateDamage_Ineffective() {
        Card testcard1 = new SpellCard("testid","testname", 3, ElementType.Fire);
        Card testcard2 = new MonsterCard("testid","testname", 3, MonsterType.Troll, ElementType.Water);
        assertEquals(1.5, testcard1.calculateDamage(testcard2));
    }
}