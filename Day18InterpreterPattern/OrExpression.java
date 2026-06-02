package Day18InterpreterPattern;

/**
 * Non-Terminal Expression.
 *
 * Represents:
 *
 * left OR right
 */
public class OrExpression implements Expression {

    private final Expression left;
    private final Expression right;

    public OrExpression(
            Expression left,
            Expression right
    ) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean interpret(UserContext context) {

        return left.interpret(context)
                ||
                right.interpret(context);
    }
}