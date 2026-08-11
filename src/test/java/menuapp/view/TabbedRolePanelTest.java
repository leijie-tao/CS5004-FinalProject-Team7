package menuapp.view;

import static org.junit.jupiter.api.Assertions.assertEquals;

import menuapp.controller.AppController;
import org.junit.jupiter.api.Test;

/**
 * Tests navigation design that a screen is redrawn when it becomes visible, and only then.
 */
public class TabbedRolePanelTest {

    /**
     * A screen that counts how many times it was redrawn.
     */
    private static final class RecordingScreen extends AppPanel {

        /**
         * How many times {@code refresh} has been called.
         */
        private int refreshCount;

        /**
         * Builds a screen attached to no controller.
         *
         * @param controller always null in these tests
         */
        private RecordingScreen(AppController controller) {
            super(controller);
        }

        @Override
        public void refresh() {
            refreshCount++;
        }
    }

    /**
     * Builds a tabbed panel holding the two screens handed in.
     *
     * @param first  the screen under the first tab
     * @param second the screen under the second tab
     * @return the panel under test
     */
    private TabbedRolePanel panelHolding(RecordingScreen first, RecordingScreen second) {
        TabbedRolePanel tabs = new TabbedRolePanel(null);
        tabs.addScreen("First", first);
        tabs.addScreen("Second", second);
        return tabs;
    }

    /**
     * Both screens end up behind the tabs.
     */
    @Test
    public void everyScreenIsAdded() {
        RecordingScreen first = new RecordingScreen(null);
        RecordingScreen second = new RecordingScreen(null);
        assertEquals(2, panelHolding(first, second).screenCount());
    }

    /**
     * Adding the first screen selects it. Adding a screen behind it changes nothing on screen and so
     * redraws nothing.
     */
    @Test
    public void addingScreensOnlyRedrawsTheFirstOne() {
        RecordingScreen first = new RecordingScreen(null);
        RecordingScreen second = new RecordingScreen(null);
        panelHolding(first, second);
        assertEquals(1, first.refreshCount);
        assertEquals(0, second.refreshCount);
    }

    /**
     * Refreshing the container redraws the visible screen and nothing else.
     */
    @Test
    public void refreshReachesOnlyTheVisibleScreen() {
        RecordingScreen first = new RecordingScreen(null);
        RecordingScreen second = new RecordingScreen(null);
        TabbedRolePanel tabs = panelHolding(first, second);

        int firstBefore = first.refreshCount;
        tabs.refresh();

        assertEquals(firstBefore + 1, first.refreshCount);
        assertEquals(0, second.refreshCount);
    }

    /**
     * Switching tabs redraws the screen the user just moved to.
     */
    @Test
    public void switchingTabsRedrawsTheNewScreen() {
        RecordingScreen first = new RecordingScreen(null);
        RecordingScreen second = new RecordingScreen(null);
        TabbedRolePanel tabs = panelHolding(first, second);

        int firstBefore = first.refreshCount;
        tabs.selectScreen(1);

        assertEquals(1, second.refreshCount);
        assertEquals(firstBefore, first.refreshCount);
    }

    /**
     * Coming back to a screen redraws it again rather than trusting the old paint.
     */
    @Test
    public void returningToAScreenRedrawsItAgain() {
        RecordingScreen first = new RecordingScreen(null);
        RecordingScreen second = new RecordingScreen(null);
        TabbedRolePanel tabs = panelHolding(first, second);

        int firstBefore = first.refreshCount;
        tabs.selectScreen(1);
        tabs.selectScreen(0);

        assertEquals(firstBefore + 1, first.refreshCount);
        assertEquals(1, second.refreshCount);
    }

    /**
     * An empty container refreshes without blowing up on a missing selection.
     */
    @Test
    public void refreshOnAnEmptyContainerIsSafe() {
        new TabbedRolePanel(null).refresh();
        assertEquals(0, new TabbedRolePanel(null).screenCount());
    }
}