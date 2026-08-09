package menuapp.view;

import javax.swing.*;

import menuapp.controller.AppController;
import menuapp.model.Role;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Top-level window. Holds a {@code CardLayout} and switches screens by
 * {@link menuapp.model.Role}.
 */
public class MainFrame extends JFrame {
  /** Role selection card name */
  static final String ROLE_CARD = "ROLE_SELECTION";
  /** Decides which card is showing */
  private final CardLayout cardLayout;
  /** Holds the cards */
  private final JPanel cardHolder;
  /** Registered cards stored here. Note, that cards are shown by name but values are not handed back */
  private final Map<String, AppPanel> cards;
  /** Returns to the role screen */
  private final JButton switchRoleButton;


  /**
   * Builds the frame around the shared controller.
   * The dependency chain for MainFrame is the following:
   * 1. Making sure frame propeties like title, size, and other operations are implemented
   * 2. Creating an empty shell first that would hold CardLayout and empty cards. These must exist first as everything
   * would write into these.
   * 3. Register cards for cardHolder and the map.
   * 4. Assembling the physical layout.
   * 5. showCard(ROLE_CARD) refreshes panel it shows meaning fields interacting with it must be non-null.
   * @param controller the shared controller
   */
  public MainFrame(AppController controller) {
    super("Restaurant Menu");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(900, 600);
    setLocationRelativeTo(null);

    this.cardLayout = new CardLayout();
    this.cardHolder = new JPanel(cardLayout);
    this.cards = new HashMap<String, AppPanel>();
    this.switchRoleButton = new JButton("Switch role");

    // NOTE: DO NOT CHANGE ORDER! Dependency chain
    registeredCards(controller);
    layOutComponents();
    attachListerners();

    showCard(ROLE_CARD);
  }

  private void registerCards(AppController controller) {
    registerCards(ROLE_CARD, new RoleSelectionPanel(controller, new RoleSelectionListener() {
      @Override
      public void roleSelected(Role role) {
        showCard(cardNameFor(role));
      }
    }

    );
  }

}