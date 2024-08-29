class StaticVariableReferenceRead {
    public static int snippet() {
        if(B.x > 10) {
            return 20;
        } else {
            return -1;
        }
    }
}