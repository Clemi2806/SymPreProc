package testfiles.multipleCalls;

public class C1 {
    public static void main(String[] args) {
        snippet(0,0);
    }

    public static void snippet(int x, int y) {
        int z = C2.doSomething();

        if(x < 20 + z) {
            int f = C2.doSomething();
            y += f;
        }

        assert x < y;
    }
}
