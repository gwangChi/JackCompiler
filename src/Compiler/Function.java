package Compiler;

public class Function {

    public static boolean isVoidFunction(String fName){
        switch(fName){
            case "String.setCharAt":
            case "String.eraseLastChar":
            case "String.setInt":
            case "Array.dispose":
            case "Output.moveCursor":
            case "Output.printChar":
            case "Output.printString":
            case "Output.printInt":
            case "Output.println":
            case "Output.backSpace":
            case "Screen.clearScreen":
            case "Screen.setColor":
            case "Screen.drawPixel":
            case "Screen.drawLine":
            case "Screen.drawRectangle":
            case "Screen.drawCircle":
            case "Memory.poke":
            case "Memory.deAlloc":
            case "Sys.halt":
            case "Sys.error":
            case "Sys.wait":
                return true;
            default:
                return false;
        }
    }
    private String kind, type, name;

    public Function(String kind, String type, String name) {
        this.kind = kind;
        this.type = type;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
