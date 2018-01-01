package Node;

public class ClassifierNode extends Node{

    public static enum CTYPE{
        START,
        ADD,
        QUESTION
    }

    private CTYPE classType;

    public ClassifierNode(CTYPE classType){
        this.classType=classType;
         refreshValue();
        setType(TYPE.START);
    }

    public CTYPE getClassType() {
        return classType;
    }

    public void setClassType(CTYPE classType) {
        this.classType = classType;
        refreshValue();
    }

    private void refreshValue(){
        switch (classType){
            case ADD:
                setValue("+");
                break;
            case START:
                setValue("*");
                break;
            case QUESTION:
                setValue("?");
                break;
        }
    }

    @Override
    public String toString() {

       return getValue();
    }
}
