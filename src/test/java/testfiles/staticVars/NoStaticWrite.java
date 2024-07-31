package testfiles.staticVars;

public class NoStaticWrite {
    public static void snippet(int a) {
        if(a > 20) {
            a = B.x;
        } else {
            a = 100;
        }
    }
}
