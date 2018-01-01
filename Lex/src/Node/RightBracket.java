package Node;

public class RightBracket extends Node{

    public RightBracket(){
        setValue(")");
        setType(TYPE.BRACKETS);
    }


    @Override
    public String toString() {
        return getValue();
    }
}
