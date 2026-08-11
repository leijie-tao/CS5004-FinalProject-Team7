package menuapp.testsupport;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import menuapp.view.MenuPanel;
import menuapp.view.OrderPanel;

import javax.swing.JTabbedPane;

/**
 * Throwaway launcher pairing the menu and cart screens behind one controller,
 * so items can be added and then seen in the cart. Delete once the manual GUI
 * pass for this panel is signed off.
 */
public class MockOrderPanelDemo {

    /**
     * Opens a two tab window driven by a single fake controller.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                MockController sharedController = new MockController();
                JTabbedPane tabs = new JTabbedPane();
                tabs.addTab("Menu", new MenuPanel(sharedController));
                tabs.addTab("Cart", new OrderPanel(sharedController));

                JFrame demoFrame = new JFrame("Order Panel Demo ONLY");
                demoFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                demoFrame.setContentPane(tabs);
                demoFrame.setSize(820, 500);
                demoFrame.setLocationRelativeTo(null);
                demoFrame.setVisible(true);
            }
        });
    }
}