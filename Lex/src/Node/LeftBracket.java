package Node;

public class LeftBracket extends Node{

    public LeftBracket(){
        setType(TYPE.BRACKETS);
        setValue("(");
    }

    @Override
    public String toString() {
        return getValue();
    }
}
