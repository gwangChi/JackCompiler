package SyntaxAnalyzer;

import java.util.ArrayList;
import java.util.stream.Stream;

public class Token {
    public static final String[] KEYWORDS = {"class", "constructor", "function",
                                            "method", "field", "static", "var",
                                            "int", "char", "boolean", "void",
                                            "true", "false", "null", "this", "let",
                                            "do", "if", "else", "while", "return"};
    public static final  String[] SYMBOLS = {"{", "}", "(", ")", "[", "]", ".", ",",
                                            ";", "+", "-", "*", "/", "&", "|", "<",
                                            ">", "=", "~"};

    public static String[] OP_LIST = {
            "+", "-", "*", "/", "&", "|", "<", ">", "="
    };
    public static String[] UNARY_OP_LIST = {
            "-", "~"
    };
    public enum TYPE{
        INT_CONST, STR_CONST, KEY_CONST, VAR_NAME, ARRAY, BRACKET_EXP, UNARY_TERM
    };

    public static boolean isOp(String str){
        for(String s:OP_LIST){
            if(s.equals(str)){
                return true;
            }
        }
        return false;
    }
    public static boolean isUnaryOp(String str){
        for(String s:UNARY_OP_LIST){
            if(s.equals(str)){
                return true;
            }
        }
        return false;
    }
    public static boolean isKeyword(String token){
        for(String keyword:KEYWORDS){
            if(keyword.equals(token)){
                return true;
            }
        }
        return false;
    }

    public static String getKeyword(String token){
        return token;
    }

    public static boolean isSymbol(String token){
        for(String symbol:SYMBOLS){
            if(symbol.equals(token)){
                return true;
            }
        }
        return false;
    }

    public static String getSymbol(String token){
        return token;
    }
    public static boolean isIntegerConstant(String token){
        return (token.matches("[0-9]+") && Integer.parseInt(token) >= 0
                && Integer.parseInt(token) <= 32767);
    }

    public static int getIntegerConstant(String token){
        return Integer.parseInt(token);
    }

    public static boolean isStringConstant(String token){
        return token.matches("^\"[^\"\n]*\"?[^\"\n]*\"$" );
    }

    public static String getStringConstant(String token){
        return token.replaceAll("\"", "");
    }

    public static boolean isIdentifier(String token){
        return (token.matches("^[a-zA-Z_][a-zA-Z0-9_]*$"));
    }

    public static String getIdentifier(String token){
        return token;
    }

    public static boolean isToken(String token){
        return isKeyword(token) || isSymbol(token) || isIntegerConstant(token)
                || isStringConstant(token) || isIdentifier(token);
    }
    private String tokenValue, tokenType, strValue;

    public String getTokenValue(){
        return this.tokenValue;
    }

    public String getStrValue(){
        return this.strValue;
    }

    public String getTokenType(){
        return this.tokenType;
    }

    public Token(String token){
        this.tokenize(token);
    }
    public boolean tokenize(String token) {
        if(isKeyword(token)){
            this.tokenValue = "<keyword> "+getKeyword(token)+" </keyword>";
            this.tokenType = "keyword";
            this.strValue = getKeyword(token);
            return true;
        } else if (isSymbol(token)) {
            this.tokenValue = "<symbol> "+getSymbol(token)+" </symbol>";
            this.tokenType = "symbol";
            this.strValue = getSymbol(token);
            return true;
        } else if(isIntegerConstant(token)){
            this.tokenValue = "<integerConstant> "+getIntegerConstant(token)+" </integerConstant>";
            this.tokenType = "integerConstant";
            this.strValue = String.valueOf(getIntegerConstant(token));
            return true;
        } else if (isStringConstant(token)) {
            this.tokenValue = "<stringConstant> "+getStringConstant(token)+" </stringConstant>";
            this.tokenType = "stringConstant";
            this.strValue = getStringConstant(token);
            return true;
        } else if (isIdentifier(token)) {
            this.tokenValue = "<identifier> "+getIdentifier(token)+" </identifier>";
            this.tokenType = "identifier";
            this.strValue = getIdentifier(token);
            return true;
        }

        return false;
    }
}
