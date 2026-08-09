package menuapp.view;

import javax.swing.*;

import menuapp.controller.AppController;
import menuapp.model.Role;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    registerCards(controller);
    layOutComponents();
    attachListeners();

    showCard(ROLE_CARD);
  }

  /**
   * Builds the main panel card, staff card, and the customer card with each role holding a tab screen.
   * Customers have three screens: menu, cart, and faves
   * Staff have two screens, inventory and sales
   * @param controller
   */
  private void registerCards(AppController controller) {
    addCardToLayout(ROLE_CARD, new RoleSelectionPanel(controller, new RoleSelectionListener() {
      @Override
      public void roleSelected(Role role) {
        showCard(cardNameFor(role));
      }
    }
    ));
    //CUSTOMER PANELS
    TabbedRolePanel customerScreens = new TabbedRolePanel(controller);
    customerScreens.addScreen("\u2615 Menu", new MenuPanel(controller));
    customerScreens.addScreen("\uD83D\uDED2 Cart", new OrderPanel(controller));
    customerScreens.addScreen("\u2665 Favorites", new FavoritesPanel(controller));
    addCardToLayout(cardNameFor(Role.CUSTOMER), customerScreens);

    //STAFF PANEL
    TabbedRolePanel staffScreens = new TabbedRolePanel(controller);
    staffScreens.addScreen("\u270F Inventory", new InventoryPanel(controller));
    staffScreens.addScreen("\uD83D\uDCC8 Sales", new SalesChartPanel(controller));
    addCardToLayout(cardNameFor(Role.STAFF), staffScreens);
  }

  /**
   * Adds a card to both layout and the look up map so that the card is visible.
   * @param cardName name card is registered under
   * @param screen the screen to register
   */
  private void addCardToLayout(String cardName, AppPanel screen) {
    cardHolder.add(screen, cardName);
    cards.put(cardName, screen);
  }

  /** Card is centered with navigation strip at the bottom */
  private void layOutComponents() {
    JPanel navigationStrip = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    navigationStrip.setBorder(BorderFactory.createEmptyBorder(4, 8, 4,8));
    navigationStrip.add(switchRoleButton);

    setLayout(new BorderLayout());
    add(cardHolder, BorderLayout.CENTER);
    add(navigationStrip, BorderLayout.SOUTH);
  }

  /** Wires to listener */
  private void attachListeners() {
    switchRoleButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        showCard(ROLE_CARD);
      }
    }
    );
  }

  /**
   * Shows a card, then redraws it, and then updates the navigation strip. Unknown name shows and redraws nothing
   * instead of throwing.
   * @param cardName name of the card to show
   */
  private void showCard(String cardName) {
    AppPanel screen = cards.get(cardName);
    if (screen == null) {
      return;
    }
    cardLayout.show(cardHolder, cardName);
    switchRoleButton.setVisible(!ROLE_CARD.equals(cardName)); // this collapses the strip
    screen.refresh();
  }

  /**
   * Returns to the role screen chosen with the names originating from the {@code Role} enum.
   * @param role user would like to return to
   * @return card name
   */
  static String cardNameFor(Role role) {
    if (role == null) {
      return ROLE_CARD;
    }
    return role.name();
  }
}