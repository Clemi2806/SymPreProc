package testfiles.markedMethods;

public class MM {
    public static void main(String[] args) {
        snippet(0);
    }
    public static void snippet(int age) {
        if (age < 18) {
            System.out.println("You are underage");
        } else {
            System.out.println("You are an adult");
        }
    }
}
