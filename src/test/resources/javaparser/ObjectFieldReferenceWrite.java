class ObjectFieldReferenceWrite {
    static B b = new B();
    public static void snippet(int a) {
        if(a > 10) {
            b.y = 20;
        } else {
            b.y = -1;
        }
    }
}