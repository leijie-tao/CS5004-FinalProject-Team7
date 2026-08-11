package menuapp.testsupport;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import menuapp.view.MenuPanel;

/**
 * Throwaway launcher for MenuPanel so it can be looked at and clicked through before mainframe is wired.
 * Delete this class once the real frame exists.
 */
public class MockMenuPanelDemo {

    /**
     * Opens a window holding only the menu screen.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame demoFrame = new JFrame("Menu Panel Demo ONLY");
                demoFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                demoFrame.setContentPane(new MenuPanel(new MockController()));
                demoFrame.setSize(760, 460);
                demoFrame.setLocationRelativeTo(null);
                demoFrame.setVisible(true);
            }
        });
    }
}