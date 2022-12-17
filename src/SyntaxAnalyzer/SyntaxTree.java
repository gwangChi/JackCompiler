package SyntaxAnalyzer;

import java.util.ArrayList;

public class SyntaxTree {
    private ArrayList<SyntaxTree> children;
    private SyntaxTree parent;
    private String syntaxName, strValue;

    public SyntaxTree(String syntaxName, SyntaxTree parent){
        this.syntaxName = syntaxName;
        this.children = new ArrayList<SyntaxTree>();
        this.strValue = "";
        this.parent = parent;
    }

    public SyntaxTree(String syntaxName, String strValue, SyntaxTree parent){
        this.syntaxName = syntaxName;
        this.strValue = strValue;
        this.children = new ArrayList<SyntaxTree>();
        this.parent = parent;
    }
    public void addChildren(SyntaxTree t){
        this.children.add(t);
    }
    public ArrayList<SyntaxTree> getChildren() {
        return (ArrayList<SyntaxTree>) children.clone();
    }

    public SyntaxTree getParent() {
        return parent;
    }

    public String getStrValue() {
        return strValue;
    }

    public String getSyntaxName() {
        return syntaxName;
    }
}
