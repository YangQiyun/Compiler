package Node;

public class CharacterNode extends Node{



    public CharacterNode(String value){
        setValue(value);
        setType(TYPE.CHARACTER);
    }


    @Override
    public String toString() {
        return getValue();
    }
}
