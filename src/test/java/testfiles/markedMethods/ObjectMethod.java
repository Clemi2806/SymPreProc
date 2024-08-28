package testfiles.markedMethods;

public class ObjectMethod {
    static ObjectMethod objectMethod = new ObjectMethod();
    private int x;

    public ObjectMethod() {
        x = 20;
    }

    public void setX(int x) {
        this.x = x;
    }

    static void snippet(int a) {
        objectMethod.setX(10);
    }

}
