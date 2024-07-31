package testfiles.staticVars;

import java.util.*;

public class A {
    public static void main(String[] args) {
        int a = (int) (Math.random() * 100);
        int bx = (int) (Math.random() * 100);
        snippet(a, bx);
    }

    public static Object[] snippet(int a, int bx) {
        if(bx < 0) {
            return new Object[]{a, bx};
        } else {
            return new Object[]{-a, bx};
        }
    }
}
