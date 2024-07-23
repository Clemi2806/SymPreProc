package testfiles.multipleCalls;

public class C2 {
    public static int z = (int)(Math.random() * 10);

    public static int doSomething() {
        int result = 0;

        int x = z;
        int y = z + x;

        for (int i = 1; i <= Math.abs(x - y); i++) {
            if (i % 2 == 0) {
                result += i * x;
            } else {
                result -= i * y;
            }
        }
        return result + (x * y) - (x / (y + 1));
    }
}
