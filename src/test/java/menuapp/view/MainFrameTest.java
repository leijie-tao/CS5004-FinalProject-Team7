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

}