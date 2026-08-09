package menuapp.view;
import menuapp.model.Role;

/**
 * Job is to announce the user's chosen role by reporting the click. It doesn't know which card
 * was chosen, and only calls one method.
 */
interface RoleSelectionListener {

    /**
     * Reports the user's chosen role.
     * @param role of either staff or customer that the user picked. There's no null.
     */
    void roleSelected( Role role);
}