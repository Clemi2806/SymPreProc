package testfiles.report;

public class Main {
    static UserDatabase userDatabase = new UserDatabase();

    public static void main(String[] args) {
        validateUser(0);
    }

    public static boolean validateUser(long userId) {
        int age = userDatabase.getAge(userId);

        if(age < 18) {
            System.out.println("User " + userId +  " is too young");
            return false;
        } else {
            System.out.println("User " + userId +  " is valid");
            return true;
        }
    }
}
