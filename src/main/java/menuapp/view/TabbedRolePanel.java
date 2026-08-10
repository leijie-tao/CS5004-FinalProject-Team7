package menuapp.view;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import menuapp.controller.AppController;

/**
 * Holds several screens behind tabs while behaving like a single screen from the
 * outside.
 */
class TabbedRolePanel extends AppPanel {

    /** The tab strip set to private, so no caller can bypass the refresh on switch. */
    private final JTabbedPane screenTabs;

    /**
     * Builds an empty tabbed screen. Add the individual screens with
     * @param controller the shared controller
     */
    TabbedRolePanel(AppController controller) {
        super(controller);
        this.screenTabs = new JTabbedPane();

        setLayout(new BorderLayout());
        add(screenTabs, BorderLayout.CENTER);

        screenTabs.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent event) {
                refreshSelectedScreen();
            }
        }
        );
    }

    /**
     * Adds one screen under a tab. Adding the first screen selects it.
     * @param tabTitle the text on the tab
     * @param screen the screen to show under it
     */
    void addScreen(String tabTitle, AppPanel screen) {
        screenTabs.addTab(tabTitle, screen);
    }

    /**
     * Redraws the screen the user is actually looking at. Hidden tabs are
     * left alone and refreshed when it is selected so a screen never
     * renders data it is about to re-read anyway.
     */
    @Override
    public void refresh() {
        refreshSelectedScreen();
    }

    /**
     * Refreshes the selected tab when there is one. The instanceof hands back null
     * before any screen is added and null fails an instanceof test.
     */
    private void refreshSelectedScreen() {
        Component selectedScreen = screenTabs.getSelectedComponent();
        if (selectedScreen instanceof AppPanel) {
            ((AppPanel) selectedScreen).refresh();
        }
    }

    /** @return how many screens are behind the tabs */
    int screenCount() {
        return screenTabs.getTabCount();
    }

    /**
     * Selects a screen by position like clicking a tab.
     * This is used by the tests to prove the switch triggers a refresh.
     * @param index the position of the tab to select
     */
    void selectScreen(int index) {
        screenTabs.setSelectedIndex(index);
    }
}