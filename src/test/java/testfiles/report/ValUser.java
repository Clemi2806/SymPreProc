package testfiles.report;

import at.aau.serg.javaparser.ReturnValues;

public class ValUser {

    public static void main(String[] args) {
        validateUser(0,0);
    }

    public static ReturnValues validateUser(long userId, int M_UserDatabase_getAge_ret0) {
        long M_UserDatabase_getAge0_arg0 = 0;
        String M_PrintStream_println1_arg0 = null;
        String M_PrintStream_println0_arg0 = null;
        M_UserDatabase_getAge0_arg0 = userId;
        int age = M_UserDatabase_getAge_ret0;
        if (age < 18) {
            M_PrintStream_println0_arg0 = "User " + userId + " is too young";
            return new ReturnValues(false, M_PrintStream_println0_arg0, M_PrintStream_println1_arg0, M_UserDatabase_getAge0_arg0);
        } else {
            M_PrintStream_println1_arg0 = "User " + userId + " is valid";
            return new ReturnValues(true, M_PrintStream_println0_arg0, M_PrintStream_println1_arg0, M_UserDatabase_getAge0_arg0);
        }
    }
}
