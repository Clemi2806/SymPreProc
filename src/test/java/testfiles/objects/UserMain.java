package testfiles.objects;

public class UserMain {
    public static void main(String[] args) {
        User user = new User("", 0);
        snippet(user);
    }

    public static void snippet(User user) {
        if(user.getAge() >= 18) {
            System.out.println("Congratulations " + user.getName());
        } else {
            System.out.println("Sorry " + user.getName());
        }
    }
}
