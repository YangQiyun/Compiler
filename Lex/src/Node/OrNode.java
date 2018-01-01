package Node;

public class OrNode extends Node{

    public OrNode(){
        setType(TYPE.OR);
        setValue("|");
    }

    @Override
    public String toString() {
        return getValue();
    }
}
