package Day18InterpreterPattern;

class Main {

    public static void main(String[] args) {

        /*
         * User:
         * Role = ADMIN
         * Status = ACTIVE
         */
        UserContext user =
                new UserContext(
                        "ADMIN",
                        "ACTIVE"
                );

        /*
         * Rule:
         *
         * ADMIN AND ACTIVE
         */
        Expression adminAndActive =
                new AndExpression(
                        new RoleExpression("ADMIN"),
                        new StatusExpression("ACTIVE")
                );

        System.out.println(
                "ADMIN AND ACTIVE => "
                        + adminAndActive.interpret(user)
        );

        /*
         * Rule:
         *
         * ADMIN OR MANAGER
         */
        Expression adminOrManager =
                new OrExpression(
                        new RoleExpression("ADMIN"),
                        new RoleExpression("MANAGER")
                );

        System.out.println(
                "ADMIN OR MANAGER => "
                        + adminOrManager.interpret(user)
        );

        /*
         * Rule:
         *
         * (ADMIN OR MANAGER)
         * AND ACTIVE
         */
        Expression complexRule =
                new AndExpression(
                        new OrExpression(
                                new RoleExpression("ADMIN"),
                                new RoleExpression("MANAGER")
                        ),
                        new StatusExpression("ACTIVE")
                );

        System.out.println(
                "(ADMIN OR MANAGER) AND ACTIVE => "
                        + complexRule.interpret(user)
        );
    }
}