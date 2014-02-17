import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static String parstr;
    private static String help;
    private static ArrayList<Expression> axioms;
    private static ArrayList<Expression> allExpressions;
    private static Map<String, Expression> map = new HashMap<String, Expression>();

    public static void main(String[] args) throws IOException {
        axioms = new ArrayList<Expression>();
        allExpressions = new ArrayList<Expression>();
        makeAxioms();
        Scanner sc = new Scanner(new FileReader("input.txt"));
        String now;
        boolean flag;
        while (sc.hasNext()) {
            help = "";
            now = sc.nextLine();
            parstr = now;
            parstr = parstr.replaceAll("->", ">");
            Expression exp = parse(0, parstr.length() - 1);
            allExpressions.add(exp);
            flag = axiomSatisfy(exp);
            if (!flag) {
                flag = checkMP(exp);
            }
            if (!flag) {
                System.out.println("Ошибка в " + allExpressions.size());
                return;
            }
            //System.out.println(parstr + "   " + help);
        }
        System.out.println("Доказательство корректно!");
    }

    private static boolean checkMP(Expression exp) {
        Expression temp, left;
        for (int i = 0; i < allExpressions.size(); i++) {
            temp = allExpressions.get(i);
            if (temp.op != '>') continue;

            if (equalsTree(temp.right, exp)) {
                left = temp.left;

                for (int j = 0; j < allExpressions.size(); j++) {
                    if (equalsTree(left, allExpressions.get(j))) {
                        help = "MP " + Integer.toString(i + 1) + " " + Integer.toString(j + 1);
                        return true;
                    }
                }

            }
        }
        return false;
    }

    private static boolean axiomSatisfy(Expression exp) {
        for (int i = 0; i < axioms.size(); i++) {
            map.clear();
            if (equals(exp, axioms.get(i))) {
                help = "Аксиома " + Integer.toString(i + 1);
                return true;
            }
        }
        return false;
    }

    private static boolean equals(Expression my, Expression axiom) {
        if (my == null && axiom == null) return true;
        if (my == null || axiom == null) return false;
        if (my.op == axiom.op && my.op != 's') return (equals(my.left, axiom.left) && equals(my.right, axiom.right));
        if (axiom.op == 's') {
            if (!map.containsKey(axiom.literal)){
                map.put(axiom.literal, my);
                return true;
            }
            if (equalsTree(my, map.get(axiom.literal))) return true;
            else return false;
        }
        return false;
    }

    private static boolean equalsTree(Expression one, Expression two) {
        if (one == null && two == null) return true;
        if (one == null || two == null) return false;
        return (one.op == two.op && one.literal.equals(two.literal) && equalsTree(one.right, two.right) && equalsTree(one.left, two.left));
    }

    private static void makeAxioms() {
        parstr = "a>(b>a)";
        axioms.add(parse(0, parstr.length() - 1));
        parstr = "(c>b)>(c>b>d)>(c>d)";
        axioms.add(parse(0, parstr.length() - 1));
        parstr = "c>b>(c&b)";
        axioms.add(parse(0, parstr.length() - 1));
        parstr = "c&a>c";
        axioms.add(parse(0, parstr.length() - 1));
        parstr = "c&a>a";
        axioms.add(parse(0, parstr.length() - 1));
        parstr = "c>c|b";
        axioms.add(parse(0, parstr.length() - 1));
        parstr = "b>c|b";
        axioms.add(parse(0, parstr.length() - 1));
        parstr = "(c>d)>(b>d)>(c|b>d)";
        axioms.add(parse(0, parstr.length() - 1));
        parstr = "((c)>(b))>((c)>!(b))>!(c)";
        axioms.add(parse(0, parstr.length() - 1));
    }

    private static Expression parse(int l, int r) {
        int balance = 0;
        if (l > r) return null;
        for (int i = l; i <= r; i++) {
            if (parstr.charAt(i) == '(') {
                balance++;
                continue;
            }
            if (parstr.charAt(i) == ')') {
                balance--;
                continue;
            }
            if (balance == 0 && parstr.charAt(i) == '>') {
                return new Expression(parse(l, i - 1), parse(i + 1, r), '>');
            }
        }
        balance = 0;
        for (int i = l; i <= r; i++) {
            if (parstr.charAt(i) == '(') {
                balance++;
                continue;
            }
            if (parstr.charAt(i) == ')') {
                balance--;
                continue;
            }
            if (balance == 0 && parstr.charAt(i) == '|') {
                return new Expression(parse(l, i - 1), parse(i + 1, r), '|');
            }
        }
        balance = 0;
        for (int i = l; i <= r; i++) {
            if (parstr.charAt(i) == '(') {
                balance++;
                continue;
            }
            if (parstr.charAt(i) == ')') {
                balance--;
                continue;
            }
            if (balance == 0 && parstr.charAt(i) == '&') {
                return new Expression(parse(l, i - 1), parse(i + 1, r), '&');
            }
        }
        balance = 0;
        for (int i = l; i <= r; i++) {
            if (parstr.charAt(i) == '(') {
                balance++;
                continue;
            }
            if (parstr.charAt(i) == ')') {
                balance--;
                continue;
            }
            if (balance == 0 && parstr.charAt(i) == '!') {
                return new Expression(null, parse(i + 1, r), '!');
            }
        }
        if (parstr.charAt(l) != '(') {
            return new Expression(parstr.substring(l,r + 1));
        }
        return parse(l + 1, r - 1);
    }

    static class Expression {
        Expression left, right;
        String literal;
        char op;
        public Expression(){}
        public Expression(String literal) {
            this.literal = literal;
            this.op = 's'; //symbol
            this.left = null;
            this.right = null;
        }
        public Expression(Expression left, Expression right, char operation) {
            this.left = left;
            this.right = right;
            this.op = operation;
            this.literal = "null";
        }
    }
}
