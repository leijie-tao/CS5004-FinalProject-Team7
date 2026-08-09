package menuapp.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;
import menuapp.model.Role;
import org.junit.jupiter.api.Test;

/**
 * Tests helpers found in the MainFrame.
 */
public class MainFrameTest {

    /** Not a test--purpose is to collect card keu for every role */
    private Set<String> roleCardNames() {
        Set<String> names = new HashSet<String>();
        for (Role role : Role.values()) {
            names.add(MainFrame.cardNameFor(role));
        }
        return names;
    }

    /** Each role produces a card name and if the card name is blank then showCard will register it to not be available again. */
    @Test
    public void individualRoleProducedCardKey() {
        for (Role role : Role.values()) {
            String cardName = MainFrame.cardNameFor(role);

            assertNotNull(cardName);
            assertFalse(cardName.trim().isEmpty());
        }
    }

    /**
     * Each role is unique and none share one card name. If there is a role that shares a card name with another
     * the cardlayout will only show one panel to user while the card map hands back another for redraw meaning
     * the visible screen is not up to date and accurate.
     */
    @Test
    public void cardNameUniquePerRole() {
        assertEquals(Role.values().length, roleCardNames().size());
    }

    @Test
    public void roleCardNotBlank() {
        assertNotNull(MainFrame.ROLE_CARD);
        assertFalse(MainFrame.ROLE_CARD.trim().isEmpty());
    }

    @Test
    public void cardNameReturnNullForMissingRole() {
        assertTrue(MainFrame.ROLE_CARD.equals(MainFrame.cardNameFor(null)));
    }
}
