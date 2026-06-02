package Day18InterpreterPattern;


/**
 * Context object.
 *
 * Contains runtime information used by expressions
 * during evaluation.
 */
public class UserContext {

    private final String role;
    private final String status;

    public UserContext(String role, String status) {
        this.role = role;
        this.status = status;
    }

    public String getRole() {
        return role;
    }

    public String getStatus() {
        return status;
    }
}