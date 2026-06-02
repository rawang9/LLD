package Day18InterpreterPattern;

/**
 * Terminal Expression.
 *
 * Example:
 * ACTIVE
 * PREMIUM
 */
public class StatusExpression implements Expression {

    private final String expectedStatus;

    public StatusExpression(String expectedStatus) {
        this.expectedStatus = expectedStatus;
    }

    @Override
    public boolean interpret(UserContext context) {
        return expectedStatus.equalsIgnoreCase(
                context.getStatus()
        );
    }
}