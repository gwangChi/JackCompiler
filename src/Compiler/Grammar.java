package Compiler;

import SyntaxAnalyzer.SyntaxTree;

public class Grammar {
    public static boolean isSubroutineDec(SyntaxTree t){
        return t.getChildren().size() == 7 &&
                t.getSyntaxName().equals("subroutineDec");
    }
    public static boolean isLongSubroutineCall(SyntaxTree t){
        return t.getChildren().size() == 6 &&
                t.getChildren().get(1).getStrValue().equals(".") &&
                t.getChildren().get(0).getSyntaxName().equals("identifier") &&
                t.getChildren().get(2).getSyntaxName().equals("identifier") &&
                t.getChildren().get(3).getStrValue().equals("(") &&
                t.getChildren().get(5).getStrValue().equals(")");
    }

    public static boolean isShortSubroutineCall(SyntaxTree t){
        return t.getChildren().size() == 4 &&
                t.getChildren().get(0).getSyntaxName().equals("identifier") &&
                t.getChildren().get(1).getStrValue().equals("(") &&
                t.getChildren().get(3).getStrValue().equals(")");
    }

    public static boolean isArray(SyntaxTree t){
        return t.getChildren().size() == 4 &&
                t.getChildren().get(0).getSyntaxName().equals("identifier") &&
                t.getChildren().get(1).getStrValue().equals("[") &&
                t.getChildren().get(3).getStrValue().equals("]");

    }

    public static boolean isBracketExpression(SyntaxTree t){
        return t.getChildren().size() == 3 &&
                t.getChildren().get(1).getSyntaxName().equals("expression") &&
                t.getChildren().get(0).getStrValue().equals("(") &&
                t.getChildren().get(2).getStrValue().equals(")");
    }
}
