package menuapp.testsupport;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import menuapp.view.AppPanel;
import menuapp.view.FavoritesPanel;
import menuapp.view.MenuPanel;

/**
 * Throwaway launcher that puts both customer screens in one window behind a
 * single controller, to prove they stay in step with each other.
 * Also helps to see any bugs without CardLayout implemented first, role switching, and shared state.
 * Second, to implements first instance of ChangeListner before implementation of MainFrame.
 * Delete this once {@code MainFrame} exists. The tab switch listener below is
 * a stand-in for the redraw that {@code MainFrame} will owe every card it shows.
 */
public class MockSharedControllerDemo {

    /**
     * Opens a two tab window (Menu Panel and Favorites) driven by one controller.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MockController sharedController = new MockController();

                final JTabbedPane tabs = new JTabbedPane();
                tabs.addTab("Menu", new MenuPanel(sharedController));
                tabs.addTab("Favorites", new FavoritesPanel(sharedController));

                // A panel cannot know it just became visible, so the container tells it.
                tabs.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent event) {
                        AppPanel visiblePanel = (AppPanel) tabs.getSelectedComponent();
                        visiblePanel.refresh();
                    }
                });

                JFrame demoFrame = new JFrame("Shared Controller Demo ONLY");
                demoFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                demoFrame.setContentPane(tabs);
                demoFrame.setSize(820, 500);
                demoFrame.setLocationRelativeTo(null);
                demoFrame.setVisible(true);
            }
        });
    }
}