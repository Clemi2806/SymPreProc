package testfiles.globalObjects;

public class B {
    private final int b;

    public B() {
        this.b = (int) (Math.random() * 100);
    }

    public int getB() {
        return b;
    }
}
