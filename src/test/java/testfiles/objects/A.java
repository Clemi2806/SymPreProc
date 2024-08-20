package testfiles.objects;

public class A {
    public static void main(String[] args) {

    }

    public static void snippet() {
        B b = new B();
        B b2 = new B();
        b.y = 10;
        b2.y = 20;
        B.z = 10;
        if(b.getX() > 100) {
            b.setX(10);
        } else {
            b.setX(30);
        }
    }
}
