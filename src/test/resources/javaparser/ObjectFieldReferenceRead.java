class ObjectFieldReferenceRead {
    static B b = new B();
    public static int snippet() {
        if(b.y > 10) {
            return 20;
        } else {
            return -1;
        }
    }
}