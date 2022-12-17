package Compiler;

public class Variable {
    enum Kind{
        FIELD, STATIC, ARG, VAR
    }

    enum Level{
        CLASS, SUBROUTINE
    }

    private String type, name;
    private int index;
    private Kind kind;
    private Level level;

    public Variable(String name, String type, Kind kind, int index, Level level) {
        this.type = type;
        this.name = name;
        this.index = index;
        this.kind = kind;
        this.level = level;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public Kind getKind() {
        return kind;
    }

    public Level getLevel() {
        return level;
    }
}
