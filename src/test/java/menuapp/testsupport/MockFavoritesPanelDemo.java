package menuapp.testsupport;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import menuapp.view.FavoritesPanel;

/**
 * Throwaway launcher with FavoritesPane as a bare window. 
 * It can be looked at and clicked through before wiring. Delete this class once the 
 * real frame is wired up.
 */
public class MockFavoritesPanelDemo {

  /**
   * Opens a window holding only the favorites screen.
   *
   * @param args unused
   */
  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        JFrame demoFrame = new JFrame("Favorites Panel Demo ONLY");
        demoFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        demoFrame.setContentPane(new FavoritesPanel(new MockController()));
        demoFrame.setSize(600, 400);
        demoFrame.setLocationRelativeTo(null);
        demoFrame.setVisible(true);
      }
    });
  }
}
