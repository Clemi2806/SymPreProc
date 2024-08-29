class StaticVariableReferenceRead {
    public static void snippet(int a) {
        if(a > 10) {
            B.x = 20;
        } else {
            B.x = -1;
        }
    }
}