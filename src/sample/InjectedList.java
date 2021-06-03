package sample;

public class InjectedList {
    String tkVal;
    String tkType;

    public String getTkVal() {
        return tkVal.toString();
    }

    public void setTkVal(String tkVal) {
        this.tkVal = tkVal;
    }

    public String getTkType() {
        return tkType;
    }

    public void setTkType(String tkType) {
        this.tkType = tkType;
    }

    public InjectedList(String tkVal, String tkType) {
        super();
        this.tkVal = tkVal;
        this.tkType = tkType;
    }
}
