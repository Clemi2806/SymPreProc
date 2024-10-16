package testfiles.globalObjects;


import java.util.Random;

public class A {
    private final B b;

    public static void main(String[] args) {
        B b = new B();
        A a = new A(b);

        a.snippet((new Random()).nextInt());
    }

    public A(B b) {
        this.b = b;
    }

    public int snippet(int a) {
        int x = b.getB();

        if(x > 50) {
            return 0;
        }

        assert a + x <= 50;

        return a + x;
    }
}
