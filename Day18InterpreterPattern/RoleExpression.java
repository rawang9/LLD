package Day18InterpreterPattern;

/**
 * Terminal Expression.
 *
 * Example:
 * ADMIN
 * MANAGER
 */
public class RoleExpression implements Expression {

    private final String expectedRole;

    public RoleExpression(String expectedRole) {
        this.expectedRole = expectedRole;
    }

    @Override
    public boolean interpret(UserContext context) {
        return expectedRole.equalsIgnoreCase(
                context.getRole()
        );
    }
}