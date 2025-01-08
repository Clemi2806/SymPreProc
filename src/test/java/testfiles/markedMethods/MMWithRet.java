package testfiles.markedMethods;

public class MMWithRet {
    int snippet(int a, int b) {
        if(a > b) {
            return Math.floorDiv(a, b);
        } else {
            return Math.floorDiv(b, a);
        }
    }
}
