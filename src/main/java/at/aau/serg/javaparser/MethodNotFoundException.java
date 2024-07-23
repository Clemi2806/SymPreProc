package at.aau.serg.javaparser;

public class MethodNotFoundException extends Exception{
    public MethodNotFoundException(String message) {
        super(message);
    }
    public MethodNotFoundException() {
        super();
    }
}
