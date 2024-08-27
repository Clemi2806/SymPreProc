package testfiles.objects;

public class A {
    public static void main(String[] args) {

    }

    static B bStatic = new B();

    public static void snippet() {
        B b = new B();
        B b2 = new B();
        b.y = 10;
        b2.y = 20;
        bStatic.y = 20;
        B.z = b2.y;
        int x = bStatic.y;
        if((b.y = b.getX()) > 100) {
            b.setX(10);
        } else {
            b.setX(30);
        }
    }
}
