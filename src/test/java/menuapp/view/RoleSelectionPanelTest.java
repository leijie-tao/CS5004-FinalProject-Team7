package menuapp.view;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import menuapp.model.Role;

import java.util.HashSet;
import java.util.Set;


/**
 * Tests the role screen and its accompanying buttons.
 */
public class RoleSelectionPanelTest {

    /**
     * The panel builds buttons by reading through the Role's values, and then adds a role.
     * This can't come back as blank.
     */
    @Test
    public void buttonTextCoversAllRoles() {
        for (Role role : Role.values()) {
            String label = RoleSelectionPanel.buttonTextFor(role);
            assertNotNull(label);
            assertFalse(label.trim().isEmpty());
        }
    }

    /** Roles can't share one label, meaning every role must produce a different button label. */
    @Test
    public void buttonTextUniqueEachRole() {
        Set<String> labels = new HashSet<String>(); // set so no duplicate
        for (Role role : Role.values()) {
            labels.add(RoleSelectionPanel.buttonTextFor(role)); // add returned button
        }
        assertEquals(Role.values().length, labels.size());
    }

    /** If a role is missing returns as an actual blank and not null. */
    @Test
    public void buttonTextForHandlingNull() {
        assertEquals("", RoleSelectionPanel.buttonTextFor(null));
    }

    /** Testing to make sure there's an actual heading that users can read */
    @Test
    public void titleTextVisible() {
        assertFalse(RoleSelectionPanel.TITLE_TEXT.trim().isEmpty());
    }
}