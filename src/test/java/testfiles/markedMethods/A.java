package testfiles.markedMethods;

public class A {
    public static void snippet(int age) {
        if (age < 18) {
            System.out.println("You are underage");
        } else {
            System.out.println("You are an adult");
        }
    }
}
