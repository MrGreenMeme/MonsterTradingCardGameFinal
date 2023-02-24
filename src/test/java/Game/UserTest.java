package Game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    @Test
    public void showStatsTest() {
        User user = new User("username", "name", "bio", "image", 20, 0, 0, 100);
        String expectedResult = "{\"Wins:\":0,\"Games:\":0}";
        assertEquals(expectedResult, user.showStats());
    }

    @Test
    public void testShowUserInfo() {
        User user = new User("username", "name", "bio", "image", 20, 0, 0, 100);
        String expectedResult = "{\"Image:\":\"image\",\"Name:\":\"name\",\"Coins:\":\"20\",\"Bio:\":\"bio\"}";
        assertEquals(expectedResult, user.showUserInfo());
    }

    @Test
    public void testAcquirePackage_success() {
        User user = new User("username", "name", "bio", "image", 20, 0, 0, 100);
        assertTrue(user.acquirePackage());
    }

    @Test
    public void testAcquirePackage_fail() {
        User user = new User("username", "name", "bio", "image", 1, 0, 0, 100);
        assertFalse(user.acquirePackage());
    }


}