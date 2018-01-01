package Node;

public class CatNode extends Node{

    public CatNode(){
        setType(TYPE.CAT);
        setValue(".");
    }

    @Override
    public String toString() {
        return getValue();
    }
}
