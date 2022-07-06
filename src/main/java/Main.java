import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter math expression");
        String input;

        do {
            String mathExpression = sc.nextLine();
            while(!(mathExpression.contains("+") || mathExpression.contains("*")
                    || mathExpression.contains("-") || mathExpression.contains("/"))) {
                System.out.println("Input doesn't contain math expression. Try again");
                mathExpression = sc.nextLine();
            }

            ASTree tree = new ASTree();

            AstNode astNode = null;
            try {
                astNode = tree.buildTree(mathExpression);
            } catch (ParserException e) {
                throw new RuntimeException(e);
            }
            Map<String, Integer> params = tree.params;
            Set<String> vars = params.keySet();

            for (String var : vars) {
                System.out.print(var + " = ");
                int value = Integer.parseInt(sc.nextLine());
                params.replace(var, value);
            }

            do {
                System.out.println("Type:\n" +
                    "Print - to print out AST\n" +
                    "Calculate - to calculate expression\n" +
                    "New - to enter new expression\n" +
                    "Exit - to finish program");

                input = sc.nextLine();

                switch (input.toLowerCase()) {
                    case "print":
                        System.out.println(astNode.prettyPrint());
                        break;
                    case "calculate":
                        try {
                            System.out.println(tree.calculateParserTree(astNode, params));
                        } catch (ParserException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case "new":
                        System.out.println("Enter math expression");
                    default:
                        break;
                }
            } while(!input.equals("exit") && !input.equals("new"));


        } while(!input.equals("exit"));
    }
}
