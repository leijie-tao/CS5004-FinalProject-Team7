package menuapp.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import menuapp.model.Role;
import menuapp.controller.AppController;

/** First screen where the user picks customer or staff. */
public class RoleSelectionPanel extends AppPanel {
  /** Heading over button that user reads */
  static final String TITLE_TEXT = "Welcome! Select your role:";
  /** Text that follows role button once chosen so that user knows what their role is */
  private static final String BUTTON_PREFIX = "Continue as ";
  /** Repeat role picked by the user known via RoleSelectionListener */
  private final RoleSelectionListener selectionListener;

  /**
   * Builds the role screen for user to see and interact with.
   * @param controller this is the shared controller that is held within AppPanel
   * @param listener tells which role the user picked via detected click
   */
  public RoleSelectionPanel(AppController controller, RoleSelectionListener listener) {
    super(controller);
    if (listener == null) {
      throw new IllegalArgumentException("Failed to pick up a role selection via click");
    }
    this.selectionListener = listener;
    layOutComponents();
  }

  /** Heading created over role buttons. Must be placed after RoleSelectionPanel */
  private void layOutComponents() {
    setLayout(new BorderLayout(0, 16));
    setBorder(BorderFactory.createEmptyBorder(24,24,24,24)); // box

    JLabel titleLabel = new JLabel(TITLE_TEXT, SwingConstants.CENTER);
    titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
    add(titleLabel, BorderLayout.NORTH);

    add(buildRoleButtons(), BorderLayout.CENTER);
  }

  /**
   * Builds a button for each role as it goes through values in Roles in order to build a collection of buttons.
   * @return row holding every button role
   */
  private JPanel buildRoleButtons() {
    JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0 ));
    for (Role role : Role.values()) {
      buttonRow.add(buildRoleButton(role));
    }
    return buttonRow;
  }

  /**
   * Builds one button per role, with the role arriving as a parameter rather than reading it as a variable. Passing
   * role as a value allows listener to capture a value that can't be changed.
   *
   * @param role
   * @return
   */
  private JButton buildRoleButton(final Role role){
    JButton roleButton = new JButton(buttonTextFor(role));
    roleButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        selectionListener.roleSelected(role);
      }
    }
    );
    return roleButton;
  }

  /**
   * Builds a label for each button role so that user knows which role they currently are. Formatting is done through
   * ItemTableFormat {@code formatEnumName(String)} so that the same role information informs both role/category panel.
   * @param role assigned to the label
   * @return button text or empty string if no role
   */
  static String buttonTextFor(Role role) {
    if (role == null) {
      return "";
    }
    return BUTTON_PREFIX + ItemTableFormat.formatEnumName(role.name());
  }

  /**
   * Does nothing on purpose. Lack of model here meants there's nothing to re-read.
   * Kept method because {@code MainFrame} redraws every card shown, including this one and it can't throw.
   */
  @Override
  public void refresh() {
    // this is empty on purpose since there's no model here.
  }
}