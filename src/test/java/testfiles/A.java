package testfiles;

public class A {
    public static void main(String[] args) {
        calculate(0);
    }
    public static int calculate(int a) {
        int x = B.x();

        new C().x();

        int z = B.y;

        if(x>5) {
            assert a > 10;
        }

        return x+1;
    }
}
