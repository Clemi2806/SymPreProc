package testfiles.staticVars;

public class A2 {
    public static void main(String[] args) {
        snippet(0);
    }

    public static int snippet(int a) {
        if(B.x == 0) {
            B.x = 0;
            return a;
        } else {
            B.x = 1;
            return -a;
        }
    }
}
