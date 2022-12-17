package SyntaxAnalyzer;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class Parser {
    private FileOutputStream outFile;
    private BufferedWriter bw;
    private ArrayList<String> parsedXML;

    public void parseTree(SyntaxTree tree, int line){
        if(line == parsedXML.size() || tree == null){
            return;
        }
        if(parsedXML.get(line).matches("^<[a-zA-Z]+> .* </[a-zA-Z]+>$")){
            String syntaxName = parsedXML.get(line).replaceAll(">.+$","")
                    .replaceAll("^<","");
            String strValue = parsedXML.get(line).replaceFirst("^<[a-zA-Z]+>", "")
                    .replaceFirst("</[a-zA-Z]+>$", "")
                    .replaceFirst("^\\s", "")
                    .replaceFirst("\\s$", "");

            tree.addChildren(new SyntaxTree(syntaxName, strValue, tree));

            parseTree(tree, line+1);
        } else if(parsedXML.get(line).matches("^<[a-zA-Z]+>$")){
            String syntaxName = parsedXML.get(line).substring(1, parsedXML.get(line).length()-1);
            tree.addChildren(new SyntaxTree(syntaxName, tree));
            parseTree(tree.getChildren().get(tree.getChildren().size()-1), line+1);
        } else if(parsedXML.get(line).matches("^</[a-zA-Z]+>$")){
            parseTree(tree.getParent(), line+1);
        } else{
            System.out.println("XML ERROR!!! "+parsedXML.get(line)+" is not accepted XML format.");
        }
    }

    public static void printTree(SyntaxTree tree, int indentation){
        String indent = "";
        for(int i=0; i<indentation; i++){
            indent += "  ";
        }
        System.out.println(indent+"<"+tree.getSyntaxName()+">");
        for(SyntaxTree t:tree.getChildren()){
            printTree(t, indentation+1);
        }
    }

    public void printParsedXML(){
        for(String line:this.parsedXML){
            System.out.println(line);
        }
    }
    public ArrayList<String> getParsedXML() {
        return (ArrayList<String>) parsedXML.clone();
    }

    public void writeIndentation(int indentLevel) throws IOException{
        for(int i=0; i<indentLevel; i++){
            bw.write("  ");
        }
    }

    public void closeFile() throws IOException{
        this.bw.close();
    }

    public Parser(String fileName) throws IOException {
        this.outFile = new FileOutputStream(fileName);
        this.bw = new BufferedWriter(new OutputStreamWriter(this.outFile));
        this.parsedXML = new ArrayList<String>();
    }

    public void parse(ArrayList<Token> tokens) throws IOException{
        compileClass(tokens, 0);
    }
    public void processStr(ArrayList<Token> tokens, int indentLevel, String str) throws IOException{
        if(tokens.get(0).getStrValue().equals(str)){
            writeIndentation(indentLevel);
            this.bw.write(tokens.get(0).getTokenValue());
            this.bw.newLine();
            /***/
            this.parsedXML.add(tokens.get(0).getTokenValue());
            /***/
        }else {
            System.out.println("SYNTAX ERROR: "+tokens.get(0).getStrValue()+" not matching "+str+"!!!");
        }
        tokens.remove(0);
    }

    public void processIdentifier(ArrayList<Token> tokens, int indentLevel) throws IOException{
        if(tokens.get(0).getTokenType().equals("identifier")){
            writeIndentation(indentLevel);
            this.bw.write(tokens.get(0).getTokenValue());
            this.bw.newLine();
            /***/
            this.parsedXML.add(tokens.get(0).getTokenValue());
            /***/
        }else{
            System.out.println("SYNTAX ERROR: "+tokens.get(0).getStrValue()+" is not a varName!!!");
        }
        tokens.remove(0);
    }

    public void processConstant(ArrayList<Token> tokens, int indentLevel) throws IOException{
        if(tokens.get(0).getTokenType().equals("stringConstant")|| tokens.get(0).getTokenType().equals("keyword")||
                tokens.get(0).getTokenType().equals("integerConstant") || tokens.get(0).getStrValue().equals("-")||
                tokens.get(0).getStrValue().equals("~")){
            writeIndentation(indentLevel);
            this.bw.write(tokens.get(0).getTokenValue());
            this.bw.newLine();
            /***/
            this.parsedXML.add(tokens.get(0).getTokenValue());
            /***/
        } else {
            System.out.println("CODE ERROR: Wrong constant/keywords!!!");
        }
        tokens.remove(0);
    }

    public void processOp(ArrayList<Token> tokens, int indentLevel) throws IOException{
        if (tokens.get(0).getStrValue().equals("+")||tokens.get(0).getStrValue().equals("-")||
                tokens.get(0).getStrValue().equals("*")||tokens.get(0).getStrValue().equals("/")||
                tokens.get(0).getStrValue().equals("&")||tokens.get(0).getStrValue().equals("|")||
                tokens.get(0).getStrValue().equals("<")||tokens.get(0).getStrValue().equals(">")||
                tokens.get(0).getStrValue().equals("=")||tokens.get(0).getStrValue().equals("~")){
            writeIndentation(indentLevel);
            this.bw.write(tokens.get(0).getTokenValue());
            this.bw.newLine();
            /***/
            this.parsedXML.add(tokens.get(0).getTokenValue());
            /***/
        }else {
            System.out.println("CODE ERROR: "+tokens.get(0).getStrValue()+" is wrong operation!!!");
        }
        tokens.remove(0);
    }

    public void processType(ArrayList<Token> tokens, int indentLevel) throws IOException{
        if(tokens.get(0).getStrValue().equals("int")||tokens.get(0).getStrValue().equals("char")||
                tokens.get(0).getStrValue().equals("boolean")||tokens.get(0).getTokenType().equals("identifier")){
            writeIndentation(indentLevel);
            this.bw.write(tokens.get(0).getTokenValue());
            this.bw.newLine();
            /***/
            this.parsedXML.add(tokens.get(0).getTokenValue());
            /***/
        } else {
            System.out.println("CODE ERROR: Wrong type!!!");
        }
        tokens.remove(0);
    }

    public void processSubroutine(ArrayList<Token> tokens, int indentLevel) throws IOException{
        if(tokens.get(0).getStrValue().equals("constructor")||tokens.get(0).getStrValue().equals("function")||
                tokens.get(0).getStrValue().equals("method")){
            writeIndentation(indentLevel);
            this.bw.write(tokens.get(0).getTokenValue());
            this.bw.newLine();
            /***/
            this.parsedXML.add(tokens.get(0).getTokenValue());
            /***/
        } else {
            System.out.println("CODE ERROR: Wrong declaration of subroutine!!!");
        }
        tokens.remove(0);
    }

    public void process(ArrayList<Token> tokens, int indentLevel) throws IOException{
        writeIndentation(indentLevel);
        this.bw.write(tokens.get(0).getTokenValue());
        this.bw.newLine();
        /***/
        this.parsedXML.add(tokens.get(0).getTokenValue());
        /***/
        tokens.remove(0);
    }

    public void compileClass(ArrayList<Token> tokens, int indentLevel) throws IOException{
        writeIndentation(indentLevel);
        this.bw.write("<class>");
        this.bw.newLine();
        /***/
        this.parsedXML.add("<class>");
        /***/

        if(tokens.get(0).getStrValue().equals("class")){
            process(tokens, indentLevel+1);
        } else {
            System.out.println("CODE ERROR: Missing class declaration!!!");
        }

        processIdentifier(tokens, indentLevel+1);
        processStr(tokens, indentLevel+1, "{");

        while(tokens.get(0).getStrValue().equals("static")||tokens.get(0).getStrValue().equals("field")){
            compileClassVarDec(tokens, indentLevel+1);
        }

        while(tokens.get(0).getStrValue().equals("constructor") || tokens.get(0).getStrValue().equals("function")||
                tokens.get(0).getStrValue().equals("method")){
            compileSubroutineDec(tokens, indentLevel+1);
        }

        processStr(tokens, indentLevel+1, "}");

        writeIndentation(indentLevel);
        this.bw.write("</class>");
        this.bw.newLine();
        /***/
        this.parsedXML.add("</class>");
        /***/
    }

    public void compileClassVarDec(ArrayList<Token> tokens, int indentLevel) throws IOException{
        writeIndentation(indentLevel);
        this.bw.write("<classVarDec>");
        this.bw.newLine();
        /***/
        this.parsedXML.add("<classVarDec>");
        /***/

        if(tokens.get(0).getStrValue().equals("static")||tokens.get(0).getStrValue().equals("field")){
            process(tokens, indentLevel+1);
        } else {
            System.out.println("CODE ERROR: Wrong class variable declaration!!!");
        }

        processType(tokens, indentLevel+1);
        processIdentifier(tokens, indentLevel+1);

        while(tokens.get(0).getStrValue().equals(",")){
            processStr(tokens, indentLevel+1, ",");
            processIdentifier(tokens, indentLevel+1);
        }

        processStr(tokens, indentLevel+1, ";");

        writeIndentation(indentLevel);
        this.bw.write("</classVarDec>");
        this.bw.newLine();
        /***/
        this.parsedXML.add("</classVarDec>");
        /***/
    }

    public void compileSubroutineDec(ArrayList<Token> tokens, int indentLevel) throws IOException{
        writeIndentation(indentLevel);
        this.bw.write("<subroutineDec>");
        this.bw.newLine();
        /***/
        this.parsedXML.add("<subroutineDec>");
        /***/

        processSubroutine(tokens, indentLevel+1);

        if(tokens.get(0).getStrValue().equals("void")){
            writeIndentation(indentLevel+1);
            this.bw.write(tokens.get(0).getTokenValue());
            this.bw.newLine();
            /***/
            this.parsedXML.add(tokens.get(0).getTokenValue());
            /***/
            tokens.remove(0);
        }
        else{
            processType(tokens, indentLevel + 1);
        }
        // process subroutineName
        processIdentifier(tokens, indentLevel+1);
        processStr(tokens, indentLevel+1, "(");
        compileParameterList(tokens, indentLevel+1);
        processStr(tokens, indentLevel+1, ")");
        compileSubroutineBody(tokens, indentLevel+1);

        writeIndentation(indentLevel);
        this.bw.write("</subroutineDec>");
        this.bw.newLine();
        /***/
        this.parsedXML.add("</subroutineDec>");
        /***/
    }

    public void compileParameterList(ArrayList<Token> tokens, int indentLevel) throws IOException{
        writeIndentation(indentLevel);
        this.bw.write("<parameterList>");
        this.bw.newLine();
        /***/
        this.parsedXML.add("<parameterList>");
        /***/

        if(tokens.get(0).getStrValue().equals("int")||tokens.get(0).getStrValue().equals("char")||
                tokens.get(0).getStrValue().equals("boolean")||tokens.get(0).getTokenType().equals("identifier")){
            processType(tokens, indentLevel+1);
            processIdentifier(tokens, indentLevel+1);

            while(tokens.get(0).getStrValue().equals(",")){
                processStr(tokens, indentLevel+1, ",");
                processType(tokens, indentLevel+1);
                processIdentifier(tokens, indentLevel+1);
            }
        }

        writeIndentation(indentLevel);
        this.bw.write("</parameterList>");
        this.bw.newLine();
        /***/
        this.parsedXML.add("</parameterList>");
        /***/
    }

    public void compileSubroutineBody(ArrayList<Token> tokens, int indentLevel) throws IOException{
        writeIndentation(indentLevel);
        this.bw.write("<subroutineBody>");
        this.bw.newLine();
        /***/
        this.parsedXML.add("<subroutineBody>");
        /***/

        processStr(tokens, indentLevel+1, "{");
        while(tokens.get(0).getStrValue().equals("var")){
            compileVarDec(tokens, indentLevel+1);
        }
        compileStatements(tokens, indentLevel+1);
        processStr(tokens, indentLevel+1, "}");
        writeIndentation(indentLevel);
        this.bw.write("</subroutineBody>");
        this.bw.newLine();
        /***/
        this.parsedXML.add("</subroutineBody>");
        /***/
    }

    public void compileVarDec(ArrayList<Token> tokens, int indentLevel) throws IOException{
        writeIndentation(indentLevel);
        this.bw.write("<varDec>");
        this.bw.newLine();
        /***/
        this.parsedXML.add("<varDec>");
        /***/

        processStr(tokens, indentLevel+1, "var");
        processType(tokens, indentLevel+1);
        processIdentifier(tokens, indentLevel+1);

        while(tokens.get(0).getStrValue().equals(",")){
            processStr(tokens, indentLevel+1, ",");
            processIdentifier(tokens, indentLevel+1);
        }

        processStr(tokens, indentLevel+1, ";");

        writeIndentation(indentLevel);
        this.bw.write("</varDec>");
        this.bw.newLine();
        /***/
        this.parsedXML.add("</varDec>");
        /***/
    }
    public void compileTerm(ArrayList<Token> tokens, int indentLevel) throws IOException{
        if (tokens.get(0).getTokenType().equals("identifier")){
            writeIndentation(indentLevel);
            this.bw.write("<term>");
            this.bw.newLine();
            /***/
            this.parsedXML.add("<term>");
            /***/
            if(tokens.get(1).getStrValue().equals(".")){
                compileSubroutineCall(tokens, indentLevel+1);
            } else if (tokens.get(1).getStrValue().equals("[")) {
                processIdentifier(tokens, indentLevel+1);
                processStr(tokens, indentLevel+1, "[");
                compileExpression(tokens, indentLevel+1);
                processStr(tokens, indentLevel+1, "]");
            } else {
                processIdentifier(tokens, indentLevel+1);
            }
            writeIndentation(indentLevel);
            this.bw.write("</term>");
            this.bw.newLine();
            /***/
            this.parsedXML.add("</term>");
            /***/
        } else if (tokens.get(0).getStrValue().equals("(")) {
            writeIndentation(indentLevel);
            this.bw.write("<term>");
            this.bw.newLine();
            /***/
            this.parsedXML.add("<term>");
            /***/
            processStr(tokens, indentLevel+1, "(");
            compileExpression(tokens, indentLevel+1);
            processStr(tokens, indentLevel+1, ")");
            writeIndentation(indentLevel);
            this.bw.write("</term>");
            this.bw.newLine();
            /***/
            this.parsedXML.add("</term>");
            /***/
        } else if(tokens.get(0).getStrValue().equals("+")||tokens.get(0).getStrValue().equals("-")||
                tokens.get(0).getStrValue().equals("*")||tokens.get(0).getStrValue().equals("/")||
                tokens.get(0).getStrValue().equals("&")||tokens.get(0).getStrValue().equals("|")||
                tokens.get(0).getStrValue().equals("<")||tokens.get(0).getStrValue().equals(">")||
                tokens.get(0).getStrValue().equals("=")||tokens.get(0).getStrValue().equals("~")){
            writeIndentation(indentLevel);
            this.bw.write("<term>");
            this.bw.newLine();
            /***/
            this.parsedXML.add("<term>");
            /***/
            processOp(tokens, indentLevel+1);
            compileTerm(tokens, indentLevel+1);
            writeIndentation(indentLevel);
            this.bw.write("</term>");
            this.bw.newLine();
            /***/
            this.parsedXML.add("</term>");
            /***/
        } else {
            writeIndentation(indentLevel);
            this.bw.write("<term>");
            this.bw.newLine();
            /***/
            this.parsedXML.add("<term>");
            /***/
            processConstant(tokens, indentLevel+1);
            writeIndentation(indentLevel);
            this.bw.write("</term>");
            this.bw.newLine();
            /***/
            this.parsedXML.add("</term>");
            /***/
        }
    }
    public void compileExpression(ArrayList<Token> tokens, int indentLevel) throws IOException{
        writeIndentation(indentLevel);
        this.bw.write("<expression>");
        this.bw.newLine();
        /***/
        this.parsedXML.add("<expression>");
        /***/

        compileTerm(tokens, indentLevel+1);

        while(tokens.get(0).getStrValue().equals("+")||tokens.get(0).getStrValue().equals("-")||
                tokens.get(0).getStrValue().equals("*")||tokens.get(0).getStrValue().equals("/")||
                tokens.get(0).getStrValue().equals("&")||tokens.get(0).getStrValue().equals("|")||
                tokens.get(0).getStrValue().equals("<")||tokens.get(0).getStrValue().equals(">")||
                tokens.get(0).getStrValue().equals("=")){
            processOp(tokens, indentLevel+1);
            compileTerm(tokens, indentLevel+1);
        }
        writeIndentation(indentLevel);
        this.bw.write("</expression>");
        this.bw.newLine();
        /***/
        this.parsedXML.add("</expression>");
        /***/
    }

    public void compileExpressionList(ArrayList<Token> tokens, int indentLevel) throws  IOException{
        writeIndentation(indentLevel);
        this.bw.write("<expressionList>");
        this.bw.newLine();
        /***/
        this.parsedXML.add("<expressionList>");
        /***/

        if (tokens.get(0).getTokenType().equals("integerConstant")||
                tokens.get(0).getTokenType().equals("stringConstant")||
                tokens.get(0).getTokenType().equals("keyword")||tokens.get(0).getTokenType().equals("identifier")||
                tokens.get(0).getStrValue().equals("-")||tokens.get(0).getStrValue().equals("~")||
                tokens.get(0).getStrValue().equals("(")){
            compileExpression(tokens, indentLevel+1);
            while(tokens.get(0).getStrValue().equals(",")){
                processStr(tokens, indentLevel+1, ",");
                compileExpression(tokens, indentLevel+1);
            }
        }

        writeIndentation(indentLevel);
        this.bw.write("</expressionList>");
        this.bw.newLine();
        /***/
        this.parsedXML.add("</expressionList>");
        /***/
    }

    public void compileStatements(ArrayList<Token> tokens, int indentLevel) throws IOException{
        writeIndentation(indentLevel);
        this.bw.write("<statements>");
        this.bw.newLine();
        /***/
        this.parsedXML.add("<statements>");
        /***/

        while (tokens.get(0).getStrValue().equals("let")||tokens.get(0).getStrValue().equals("if")||
                tokens.get(0).getStrValue().equals("while")||tokens.get(0).getStrValue().equals("do")||
                tokens.get(0).getStrValue().equals("return")){

            if (tokens.get(0).getStrValue().equals("let")){
                compileLet(tokens, indentLevel+1);
            } else if (tokens.get(0).getStrValue().equals("if")) {
                compileIf(tokens, indentLevel+1);
            } else if (tokens.get(0).getStrValue().equals("while")) {
                compileWhile(tokens, indentLevel+1);
            } else if (tokens.get(0).getStrValue().equals("do")) {
                compileDo(tokens, indentLevel+1);
            } else if (tokens.get(0).getStrValue().equals("return")) {
                compileReturn(tokens, indentLevel+1);
            } else{
                System.out.println("CODE ERROR: Wrong statement declaration!!!");
            }
        }
        writeIndentation(indentLevel);
        this.bw.write("</statements>");
        this.bw.newLine();
        /***/
        this.parsedXML.add("</statements>");
        /***/
    }
    public void compileLet(ArrayList<Token> tokens, int indentLevel) throws IOException{
        writeIndentation(indentLevel);
        this.bw.write("<letStatement>");
        this.bw.newLine();
        /***/
        this.parsedXML.add("<letStatement>");
        /***/
        processStr(tokens, indentLevel+1, "let");
        processIdentifier(tokens, indentLevel+1);
        if(tokens.get(0).getStrValue().equals("[")){
            processStr(tokens, indentLevel+1, "[");
            compileExpression(tokens, indentLevel+1);
            processStr(tokens, indentLevel+1,"]");
        }
        processStr(tokens, indentLevel+1, "=");
        compileExpression(tokens, indentLevel+1);
        processStr(tokens, indentLevel+1, ";");

        writeIndentation(indentLevel);
        this.bw.write("</letStatement>");
        this.bw.newLine();
        /***/
        this.parsedXML.add("</letStatement>");
        /***/
    }

    public void compileIf(ArrayList<Token> tokens, int indentLevel) throws IOException{
        writeIndentation(indentLevel);
        this.bw.write("<ifStatement>");
        this.bw.newLine();
        /***/
        this.parsedXML.add("<ifStatement>");
        /***/

        processStr(tokens, indentLevel+1, "if");
        processStr(tokens, indentLevel+1, "(");
        compileExpression(tokens, indentLevel+1);
        processStr(tokens, indentLevel+1, ")");
        processStr(tokens, indentLevel+1, "{");
        compileStatements(tokens, indentLevel+1);
        processStr(tokens, indentLevel+1, "}");

        if(tokens.get(0).getStrValue().equals("else")){
            processStr(tokens, indentLevel+1, "else");
            processStr(tokens, indentLevel+1, "{");
            compileStatements(tokens, indentLevel+1);
            processStr(tokens, indentLevel+1, "}");
        }
        writeIndentation(indentLevel);
        this.bw.write("</ifStatement>");
        this.bw.newLine();
        /***/
        this.parsedXML.add("</ifStatement>");
        /***/
    }

    public void compileWhile(ArrayList<Token> tokens, int indentLevel) throws IOException{
        writeIndentation(indentLevel);
        this.bw.write("<whileStatement>");
        this.bw.newLine();
        /***/
        this.parsedXML.add("<whileStatement>");
        /***/

        processStr(tokens, indentLevel+1, "while");
        processStr(tokens,indentLevel+1, "(");
        compileExpression(tokens, indentLevel+1);
        processStr(tokens, indentLevel+1, ")");
        processStr(tokens, indentLevel+1, "{");
        compileStatements(tokens, indentLevel+1);
        processStr(tokens,indentLevel+1,"}");

        writeIndentation(indentLevel);
        this.bw.write("</whileStatement>");
        this.bw.newLine();
        /***/
        this.parsedXML.add("</whileStatement>");
        /***/
    }

    public void compileDo(ArrayList<Token> tokens, int indentLevel) throws IOException{
        writeIndentation(indentLevel);
        this.bw.write("<doStatement>");
        this.bw.newLine();
        /***/
        this.parsedXML.add("<doStatement>");
        /***/

        processStr(tokens, indentLevel+1, "do");
        compileSubroutineCall(tokens, indentLevel+1);
        processStr(tokens, indentLevel+1, ";");

        writeIndentation(indentLevel);
        this.bw.write("</doStatement>");
        this.bw.newLine();
        /***/
        this.parsedXML.add("</doStatement>");
        /***/
    }

    public void compileReturn(ArrayList<Token> tokens, int indentLevel) throws IOException{
        writeIndentation(indentLevel);
        this.bw.write("<returnStatement>");
        this.bw.newLine();
        /***/
        this.parsedXML.add("<returnStatement>");
        /***/
        processStr(tokens, indentLevel+1, "return");
        if(!tokens.get(0).getStrValue().equals(";")){
            compileExpression(tokens, indentLevel+1);
        }
        processStr(tokens, indentLevel+1, ";");
        writeIndentation(indentLevel);
        this.bw.write("</returnStatement>");
        this.bw.newLine();
        /***/
        this.parsedXML.add("</returnStatement>");
        /***/
    }
    public void compileSubroutineCall(ArrayList<Token> tokens, int indentLevel) throws IOException{
        processIdentifier(tokens, indentLevel);
        if(tokens.get(0).getStrValue().equals(".")){
            processStr(tokens, indentLevel, ".");
            processIdentifier(tokens, indentLevel);
        }
        processStr(tokens, indentLevel, "(");
        compileExpressionList(tokens, indentLevel);
        processStr(tokens, indentLevel, ")");
    }
}
