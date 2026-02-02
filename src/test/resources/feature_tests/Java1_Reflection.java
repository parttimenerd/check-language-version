// Java 1.1 feature: Reflection (class literals)
// Expected Version: 1
// Required Features: CLASS_PROPERTY
class Java1_Reflection {
    public void method() {
        Class clazz = String.class;
        System.out.println(clazz.getName());
    }
}