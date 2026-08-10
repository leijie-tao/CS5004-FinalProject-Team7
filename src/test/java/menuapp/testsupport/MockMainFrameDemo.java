package menuapp.testsupport;

import javax.swing.SwingUtilities;
import menuapp.view.MainFrame;

/**
 * Throwaway launcher for the real {@code MainFrame} driven by
 * {@link MockController}, so the whole navigation path can be clicked through
 * before the model and the real controller are finished.
 */
public class MockMainFrameDemo {

    /**
     * Opens the real window on the event dispatch thread.
     * @param args unused
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainFrame(new MockController()).setVisible(true);
            }
        }
        );
    }
}