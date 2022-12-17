package Compiler;

import SyntaxAnalyzer.SyntaxTree;
import SyntaxAnalyzer.Token;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class Compiler {
    private String directoryName;
    private int labelNum;

    private ArrayList<String> vmCode;
    private ArrayList<Function> FTable; // table of functions/constructor/methods
    private ArrayList<Variable> CTable; // class-level variable table
    private ArrayList<Variable> STable; // Subroutine-level variable table
    private String className;

    public Compiler(String directoryName) {
        this.directoryName = directoryName;
        this.vmCode = new ArrayList<String>();
        this.CTable = new ArrayList<Variable>();
        this.STable = new ArrayList<Variable>();
        this.FTable = new ArrayList<Function>();
        this.className = "";
        this.labelNum = 0;
    }

    public void writeVM() throws IOException{
        FileOutputStream outFile = new FileOutputStream(this.directoryName+"//"+this.className+".vm");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outFile));

        for(String line:this.vmCode){
            bw.write(line);
            bw.newLine();
        }
        //close file after writing
        bw.close();
    }
    public boolean isObject(String var){
        String type = getSTableType(var);
        if (!type.equals("")){
            return !(type.equals("int") || type.equals("char") || type.equals("boolean"));
        }
        type = getCTableType(var);
        return !(type.equals("int") || type.equals("char") || type.equals("boolean"));
    }

    public boolean isVariable(String var){
        int varIndex = getSTableIndex(var);
        if(varIndex != -1){
            return true;
        }
        //get variable index from class symbol table
        varIndex = getCTableIndex(var);
        if(varIndex != -1){
            return true;
        }
        return false;
    }

    public String getFTableKind(String fName){
        String kind = "";
        for(Function f:this.FTable){
            if(f.getName().equals(fName)){
                kind = f.getKind();
            }
        }
        return kind;
    }

    public String getFTableType(String fName){
        String type = "";
        for(Function f:this.FTable){
            if(f.getName().equals(fName)){
                type = f.getType();
            }
        }
        return type;
    }

    public void scan2FTable(SyntaxTree t){
        //t is class level tree, use this function in compileTree to store all function properties
        for(SyntaxTree child:t.getChildren()) {
            if(child.getSyntaxName().equals("subroutineDec")) {
                //add function to FTable
                this.FTable.add(new Function(child.getChildren().get(0).getStrValue(),
                        child.getChildren().get(1).getStrValue(),
                        t.getChildren().get(1).getStrValue() + "." + child.getChildren().get(2).getStrValue()));
            }
        }
    }

    public String getSTableKind(String var){
        String kind = "";
        for(Variable v:this.STable){
            if(v.getName().equals(var)){
                kind = (v.getKind() == Variable.Kind.VAR)?"local":"argument";
            }
        }
        return kind;
    }
    public String getCTableKind(String var){
        String kind = "";
        for(Variable v:this.CTable){
            if(v.getName().equals(var)){
                kind = (v.getKind() == Variable.Kind.FIELD)?"this":"static";
            }
        }
        return kind;
    }

    public String getSTableType(String var){
        for(Variable v:this.STable){
            if(v.getName().equals(var)){
                return v.getType();
            }
        }
        return "";
    }
    public String getCTableType(String var){
        for(Variable v:this.CTable){
            if(v.getName().equals(var)){
                return v.getType();
            }
        }
        return "";
    }
    public int getSTableIndex(String var){
        for(Variable v:this.STable){
            if(v.getName().equals(var)){
                return v.getIndex();
            }
        }
        return -1;
    }

    public int getCTableIndex(String var){
        for(Variable v:this.CTable){
            if(v.getName().equals(var)){
                return v.getIndex();
            }
        }
        return -1;
    }

    public int getSTableNumKind(Variable.Kind kind){
        int num = 0;
        for(Variable v:this.STable){
            if(v.getKind() == kind){
                num++;
            }
        }
        return num;
    }
    public int getCTableNumKind(Variable.Kind kind){
        int num = 0;
        for(Variable v:this.CTable){
            if(v.getKind() == kind){
                num++;
            }
        }
        return num;
    }

    public void clearSTable(){
        for(int i=this.STable.size()-1; i>=0; i--){
            this.STable.remove(i);
        }
    }
    public void clearCTable(){
        for(int i=this.CTable.size()-1; i>=0; i--){
            this.CTable.remove(i);
        }
    }

    public void clearVMCode(){
        for(int i=this.vmCode.size()-1; i>=0; i--){
            this.vmCode.remove(i);
        }
    }

    /**
     * Scans the class functions into the FTable, then compile each class subtree in root tree t's children list.
     * @param t is the root tree for all class files under the directory
     */
    public void compileRootTree(SyntaxTree t) throws IOException{
        for(SyntaxTree child:t.getChildren()){
            scan2FTable(child);
        }

        for(SyntaxTree child:t.getChildren()){
            //child is class-level subtree of rootTree
            compileClass(child);
            //writes the current compiled class.vm code
            writeVM();
            clearVMCode();  //clears the vmCode arraylist
            this.className = "";    //resets the class name
            this.labelNum = 0;  //resets label initial value
        }
    }
    public void compileClass(SyntaxTree t){
        //TODO: Grammar check
        this.className = t.getChildren().get(1).getStrValue();

        for(SyntaxTree child:t.getChildren()){
            if(child.getSyntaxName().equals("classVarDec")){
                compileClassVarDec(child);
            } else if (child.getSyntaxName().equals("subroutineDec")){
                compileSubroutineDec(child);
            }
        }
        clearCTable();
    }
    public void compileClassVarDec(SyntaxTree t){
        //TODO: Grammar check
        Variable.Kind k = t.getChildren().get(0).getStrValue().equals("field")?
                Variable.Kind.FIELD:Variable.Kind.STATIC;
        for(int i=2; i<t.getChildren().size(); i+=2) {
            //add var to class-level symbol table
            this.CTable.add(new Variable(t.getChildren().get(i).getStrValue(),
                    t.getChildren().get(1).getStrValue(), k,
                    getCTableNumKind(k), Variable.Level.CLASS));
        }
    }

    public void compileSubroutineDec(SyntaxTree t){
        if(!Grammar.isSubroutineDec(t)){
            System.out.println("SYNTAX ERROR!!! Wrong subroutine declaration!");
            return;
        }

        /**
         * //add function to FTable
        this.FTable.add(new Function(t.getChildren().get(0).getStrValue(),
                t.getChildren().get(1).getStrValue(),
                this.className+"."+t.getChildren().get(2).getStrValue()));
         */

        compileParameterList(t.getChildren().get(4), t.getChildren().get(0).getStrValue().equals("method"));

        //declare all variables here
        for(SyntaxTree grandChild:t.getChildren().get(6).getChildren()){
            if(grandChild.getSyntaxName().equals("varDec")){
                compileVarDec(grandChild);
            }
        }
        this.vmCode.add("function "+this.className+"."+t.getChildren().get(2).getStrValue()+" "+
                getSTableNumKind(Variable.Kind.VAR));

        if(t.getChildren().get(0).getStrValue().equals("method")){
            this.vmCode.add("push argument 0");
            this.vmCode.add("pop pointer 0");
        } else if (t.getChildren().get(0).getStrValue().equals("constructor")){
            this.vmCode.add("push constant "+getCTableNumKind(Variable.Kind.FIELD));
            this.vmCode.add("call Memory.alloc 1");
            this.vmCode.add("pop pointer 0");
        }
        compileSubroutineBody(t.getChildren().get(6), t.getChildren().get(1).getStrValue().equals("void"));

        //delete subroutine table content at end of the subroutine
        clearSTable();
    }

    public void compileParameterList(SyntaxTree t, boolean isMethod){
        //TODO: Grammar check
        if(isMethod){
            this.STable.add(new Variable("this",
                    this.className, Variable.Kind.ARG,
                    0, Variable.Level.SUBROUTINE));
        }
        for(int i=1; i<t.getChildren().size(); i+=3){
            //add arg to subroutine table
            this.STable.add(new Variable(t.getChildren().get(i).getStrValue(),
                    t.getChildren().get(i-1).getStrValue(), Variable.Kind.ARG,
                    getSTableNumKind(Variable.Kind.ARG), Variable.Level.SUBROUTINE));
        }
    }
    public void compileSubroutineBody(SyntaxTree t, boolean isVoid){
        //TODO: Grammar check
        if(t.getChildren().get(t.getChildren().size()-2).getSyntaxName().equals("statements")) {
            compileStatements(t.getChildren().get(t.getChildren().size() - 2), isVoid);
        } else {
            System.out.println("SYNTAX ERROR!!! Statements not found!");
        }
    }

    public void compileVarDec(SyntaxTree t){
        //TODO: Grammar check
        for(int i=2; i<t.getChildren().size(); i+=2) {
            //add var to subroutine table
            this.STable.add(new Variable(t.getChildren().get(i).getStrValue(),
                    t.getChildren().get(1).getStrValue(), Variable.Kind.VAR,
                    getSTableNumKind(Variable.Kind.VAR), Variable.Level.SUBROUTINE));
        }
    }
    public void compileLetStatement(SyntaxTree t){
        //TODO: Grammar check
        //if it's an Array
        if (isVariable(t.getChildren().get(1).getStrValue()) &&
                isObject(t.getChildren().get(1).getStrValue()) &&
                t.getChildren().size()==8){
            compileExpression(t.getChildren().get(3));
            compileVariable(t.getChildren().get(1).getStrValue(), true);
            this.vmCode.add("add");
            //compiles the RHS expression for array let
            compileExpression(t.getChildren().get(6));
            this.vmCode.add("pop temp 0");
            this.vmCode.add("pop pointer 1");
            this.vmCode.add("push temp 0");
            this.vmCode.add("pop that 0");
        } else {    //if it's int, char, or boolean
            //compiles the RHS expression, which is pushed onto the stack
            compileExpression(t.getChildren().get(3));
            compileVariable(t.getChildren().get(1).getStrValue(), false);
        }
    }
    public void compileIfStatement(SyntaxTree t, boolean isVoid){
        //TODO: Grammar check
        int currLabel = this.labelNum;
        this.labelNum++;

        compileExpression(t.getChildren().get(2));
        this.vmCode.add("not");
        this.vmCode.add("if-goto IF_L"+currLabel);
        compileStatements(t.getChildren().get(5), isVoid);

        if(t.getChildren().size()>7){   //else is present
            this.vmCode.add("goto ELSE_L"+currLabel);
            this.vmCode.add("label IF_L"+currLabel);
            compileStatements(t.getChildren().get(9), isVoid);
            this.vmCode.add("label ELSE_L"+currLabel);
        } else {
            this.vmCode.add("label IF_L"+currLabel);
        }
    }
    public void compileWhileStatement(SyntaxTree t, boolean isVoid){
        //TODO: Grammar check
        int currLabel = this.labelNum;
        this.labelNum++;

        this.vmCode.add("label WHILE_EXP"+currLabel);
        compileExpression(t.getChildren().get(2));
        this.vmCode.add("not");
        this.vmCode.add("if-goto WHILE_END"+currLabel);
        compileStatements(t.getChildren().get(5), isVoid);
        this.vmCode.add("goto WHILE_EXP"+currLabel);
        this.vmCode.add("label WHILE_END"+currLabel);
    }
    public void compileDoStatement(SyntaxTree t){
        //TODO: Grammar check
        //subroutine call from do
        if (t.getChildren().size() == 6) {
            int numArgs = 0;

            if(getFTableKind(this.className+"."+t.getChildren().get(1).getStrValue()).equals("method")){
                this.vmCode.add("push pointer 0");
                numArgs = compileExpressionList(t.getChildren().get(3))+1;
            } else {
                numArgs = compileExpressionList(t.getChildren().get(3));
            }

            this.vmCode.add("call " + this.className+"."+t.getChildren().get(1).getStrValue()
                    + " " + numArgs);
            if (getFTableType(this.className+"."+t.getChildren().get(1).getStrValue())
                    .equals("void") ||
                    Function.isVoidFunction(this.className+"."+ t.getChildren().get(1).getStrValue())) {
                this.vmCode.add("pop temp 0");
            }

        } else {
            int numArgs = 0;
            String identifier = t.getChildren().get(1).getStrValue();

            if(isVariable(identifier) && isObject(identifier)){
                identifier = getSTableType(t.getChildren().get(1).getStrValue());
                if (identifier.equals("")){
                    identifier = getCTableType(t.getChildren().get(1).getStrValue());
                }
                //pushes the variable pointer onto stack
                compileVariable(t.getChildren().get(1).getStrValue(), true);
                numArgs = compileExpressionList(t.getChildren().get(5))+1;
            } else {
                numArgs = compileExpressionList(t.getChildren().get(5));
            }
            //identifier is class name
            String fName = identifier+"."+ t.getChildren().get(3).getStrValue();
            this.vmCode.add("call "+fName+" "+numArgs);

            if(getFTableType(fName).equals("void") ||
                    Function.isVoidFunction(fName)){
                this.vmCode.add("pop temp 0");
            }

        }
    }
    public void compileReturnStatement(SyntaxTree t, boolean isVoid){
        //TODO: Grammar check
        if(t.getChildren().get(1).getSyntaxName().equals("expression")){
            compileExpression(t.getChildren().get(1));
        }
        if(isVoid){
            this.vmCode.add("push constant 0");
        }
        this.vmCode.add("return");
    }
    public void compileStatements(SyntaxTree t, boolean isVoid){
        for(SyntaxTree child:t.getChildren()) {
            switch (child.getSyntaxName()) {
                case "letStatement":
                    compileLetStatement(child);
                    break;
                case "ifStatement":
                    compileIfStatement(child, isVoid);
                    break;
                case "whileStatement":
                    compileWhileStatement(child, isVoid);
                    break;
                case "doStatement":
                    compileDoStatement(child);
                    break;
                case "returnStatement":
                    compileReturnStatement(child, isVoid);
                    break;
                default:
                    System.out.println("SYNTAX ERROR!!! "+child.getSyntaxName()+" is not a valid statement!");
            }
        }
    }
    public void compileVariable(String var, boolean isPush){
        //get variable index from subroutine symbol table
        int varIndex = getSTableIndex(var);
        if(varIndex != -1){
            this.vmCode.add((isPush?"push":"pop") + " "+getSTableKind(var)+" " + varIndex);
            return;
        }
        //get variable index from class symbol table
        varIndex = getCTableIndex(var);
        if(varIndex != -1){
            this.vmCode.add((isPush?"push":"pop") + " "+getCTableKind(var)+" " + varIndex);
        }
    }

    public void compileOp(String op){
        switch (op){
            case "+":
                this.vmCode.add("add");
                break;
            case "-":
                this.vmCode.add("sub");
                break;
            case "*":
                this.vmCode.add("call Math.multiply 2");
                break;
            case "/":
                this.vmCode.add("call Math.divide 2");
                break;
            case "&":
                this.vmCode.add("and");
                break;
            case "|":
                this.vmCode.add("or");
                break;
            case "<":
                this.vmCode.add("lt");
                break;
            case ">":
                this.vmCode.add("gt");
                break;
            case "=":
                this.vmCode.add("eq");
                break;
            default:
                System.out.println("SYNTAX ERROR!!! "+op+" is not a valid operation symbol!");
        }
    }
    
    public void compileUnaryOp(String op){
        switch (op){
            case "-":
                this.vmCode.add("neg");
                break;
            case "~":
                this.vmCode.add("not");
                break;
            default:
                System.out.println("SYNTAX ERROR!!! "+op+" is not a valid unary operation symbol!");
        }
    }

    public void compileTerm(SyntaxTree t){
        /** All term types specified on page 218 in Chapter 11 */
        //if t is a leaf/token
        if(t.getChildren().size() == 0 && !t.getStrValue().equals("")) {
            if (t.getSyntaxName().equals("integerConstant")) {
                this.vmCode.add("push constant " + t.getStrValue());
            } else if (t.getSyntaxName().equals("keyword")) {
                compileKeywordConstant(t.getStrValue());
            } else if (t.getSyntaxName().equals("stringConstant")) {
                compileStringConstant(t.getStrValue());
            } else if (isVariable(t.getStrValue())) {
                compileVariable(t.getStrValue(), true);
            } else {
                System.out.println("SYNTAX ERROR!!! Something is wrong with token "+t.getStrValue()+"!");
            }
        } else if (t.getChildren().size() == 2 &&
                Token.isUnaryOp(t.getChildren().get(0).getStrValue())) {    // UnaryOp Term
            compileTerm(t.getChildren().get(1));
            compileUnaryOp(t.getChildren().get(0).getStrValue());
        } else if (Grammar.isShortSubroutineCall(t)) {
            int numArgs = 0;

            if(getFTableKind(t.getChildren().get(0).getStrValue()).equals("method")){
                this.vmCode.add("push pointer 0");
                numArgs = compileExpressionList(t.getChildren().get(2))+1;
            } else {
                numArgs = compileExpressionList(t.getChildren().get(2));
            }

            this.vmCode.add("call " + this.className+"."+t.getChildren().get(0).getStrValue()
                    + " " + numArgs);
            if (getFTableType(this.className+"."+t.getChildren().get(0).getStrValue())
                    .equals("void") ||
                    Function.isVoidFunction(this.className+"."+ t.getChildren().get(0).getStrValue())) {
                this.vmCode.add("pop temp 0");
            }

        } else if (Grammar.isLongSubroutineCall(t)) {
            int numArgs = 0;
            String identifier = t.getChildren().get(0).getStrValue();

            if(isVariable(identifier) && isObject(identifier)){
                identifier = getSTableType(t.getChildren().get(0).getStrValue());
                if (identifier.equals("")){
                    identifier = getCTableType(t.getChildren().get(0).getStrValue());
                }
                //pushes the variable pointer onto stack
                compileVariable(t.getChildren().get(0).getStrValue(), true);
                numArgs = compileExpressionList(t.getChildren().get(4))+1;
            } else {
                numArgs = compileExpressionList(t.getChildren().get(4));
            }
            //identifier is class name
            String fName = identifier+"."+ t.getChildren().get(2).getStrValue();
            this.vmCode.add("call "+fName+" "+numArgs);

            if(getFTableType(fName).equals("void") ||
                    Function.isVoidFunction(identifier+"."+
                    t.getChildren().get(2).getStrValue())){
                this.vmCode.add("pop temp 0");
            }

        } else if(Grammar.isArray(t)){
            compileExpression(t.getChildren().get(2));  //pushes relative address in array
            compileTerm(t.getChildren().get(0));    //pushes Array object heap address
            this.vmCode.add("add");
            this.vmCode.add("pop pointer 1");   //set that to the added address on the heap
            this.vmCode.add("push that 0"); //pushes the value pointed by that onto the stack
        } else if (Grammar.isBracketExpression(t)) {
            compileExpression(t.getChildren().get(1));
        } else if (t.getSyntaxName().equals("term")) {
            compileTerm(t.getChildren().get(0));
        } else{
            System.out.println("SYNTAX ERROR!!! Term <"+t.getSyntaxName()+"> is not a valid term!");
        }
    }

    public int compileExpressionList(SyntaxTree t){
        /** exp, exp, ..., exp */
        int numArgs = 0;

        for(SyntaxTree child:t.getChildren()){
            if(!child.getSyntaxName().equals("symbol")){
                compileExpression(child);
                numArgs++;
            }
        }
        return numArgs;
    }

    public void compileStringConstant(String str){
        this.vmCode.add("push constant "+str.length());
        this.vmCode.add("call String.new 1");
        for(int i=0; i<str.length(); i++){
            this.vmCode.add("push constant "+((int) str.charAt(i)));
            this.vmCode.add("call String.appendChar 2");
        }
    }

    public void compileKeywordConstant(String str){
        switch(str){
            case "true":
                this.vmCode.add("push constant 1");
                this.vmCode.add("neg");
                break;
            case "false":
            case "null":
                this.vmCode.add("push constant 0");
                break;
            case "this":
                this.vmCode.add("push pointer 0");
                break;
            default:
                System.out.println("SYNTAX ERROR!!! "+str+" is not a valid keyword constant!");
        }
    }
    public void compileExpression(SyntaxTree t){
        if(t.getChildren().size()==0){
            return;
        }
        //expression is of the form: term(op term)+, compile (term op term) first
        compileTerm(t.getChildren().get(0));
        for (int i = 1; i < t.getChildren().size(); i += 2) {
            compileTerm(t.getChildren().get(i + 1));
            // compile operation after compiling term
            compileOp(t.getChildren().get(i).getStrValue());
        }
    }
}
