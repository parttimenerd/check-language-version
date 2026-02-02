// Edge case: Reflection API usage (Java 1.1+, but file uses features up to Java 16)
// Expected Version: 16
// Required Features: ALPHA3_ARRAY_SYNTAX, ANNOTATIONS, COLLECTIONS_FRAMEWORK, GENERICS, INNER_CLASSES, LAMBDAS, PATTERN_MATCHING_INSTANCEOF, REFLECTION, CLASS_PROPERTY
import java.lang.reflect.*;
import java.lang.annotation.Annotation;
import java.util.*;

class ReflectionEdgeCases_Java1 {

    // Java 1.1: Basic reflection - getting Class object
    public void testGetClass() {
        String str = "hello";
        Class<?> clazz = str.getClass();

        Class<?> stringClass = String.class;

        try {
            Class<?> forName = Class.forName("java.util.ArrayList");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Java 1.1: Inspecting class metadata
    public void testClassMetadata() {
        Class<?> clazz = ArrayList.class;

        String name = clazz.getName();
        String simpleName = clazz.getSimpleName();
        Package pkg = clazz.getPackage();
        Class<?> superclass = clazz.getSuperclass();
        Class<?>[] interfaces = clazz.getInterfaces();
        int modifiers = clazz.getModifiers();

        boolean isInterface = clazz.isInterface();
        boolean isArray = clazz.isArray();
        boolean isPrimitive = clazz.isPrimitive();
        boolean isEnum = clazz.isEnum();
    }

    // Java 1.1: Getting constructors
    public void testConstructors() throws Exception {
        Class<?> clazz = ArrayList.class;

        Constructor<?>[] constructors = clazz.getConstructors();
        Constructor<?>[] allConstructors = clazz.getDeclaredConstructors();
        Constructor<?> noArg = clazz.getConstructor();
        Constructor<?> withCapacity = clazz.getConstructor(int.class);

        // Create instance
        Object instance = noArg.newInstance();
    }

    // Java 1.1: Getting methods
    public void testMethods() throws Exception {
        Class<?> clazz = String.class;

        Method[] methods = clazz.getMethods();
        Method[] declaredMethods = clazz.getDeclaredMethods();
        Method length = clazz.getMethod("length");
        Method substring = clazz.getMethod("substring", int.class, int.class);

        // Invoke method
        String str = "hello";
        Object result = length.invoke(str);
        Object sub = substring.invoke(str, 1, 3);
    }

    // Java 1.1: Getting fields
    public void testFields() throws Exception {
        Class<?> clazz = Integer.class;

        Field[] fields = clazz.getFields();
        Field[] declaredFields = clazz.getDeclaredFields();
        Field maxValue = clazz.getField("MAX_VALUE");

        Object value = maxValue.get(null);  // static field
    }

    // Java 1.1: Accessing private members
    public void testAccessPrivate() throws Exception {
        Class<?> clazz = MyClass.class;

        Field privateField = clazz.getDeclaredField("privateValue");
        privateField.setAccessible(true);

        MyClass obj = new MyClass();
        privateField.set(obj, 42);
        int value = (int) privateField.get(obj);

        Method privateMethod = clazz.getDeclaredMethod("privateMethod");
        privateMethod.setAccessible(true);
        privateMethod.invoke(obj);
    }

    // Java 1.3: Dynamic proxy
    public void testDynamicProxy() {
        List<?> proxyList = (List<?>) Proxy.newProxyInstance(
            List.class.getClassLoader(),
            new Class<?>[] { List.class },
            (proxy, method, args) -> {
                System.out.println("Method called: " + method.getName());
                return null;
            }
        );
    }

    // Java 5: Generics reflection
    public void testGenericsReflection() throws Exception {
        Class<?> clazz = ArrayList.class;

        TypeVariable<?>[] typeParams = clazz.getTypeParameters();

        Field field = GenericClass.class.getDeclaredField("list");
        Type genericType = field.getGenericType();

        if (genericType instanceof ParameterizedType pt) {
            Type[] typeArgs = pt.getActualTypeArguments();
        }
    }

    // Java 5: Annotation reflection
    public void testAnnotationReflection() throws Exception {
        Class<?> clazz = MyAnnotatedClass.class;

        Annotation[] annotations = clazz.getAnnotations();
        Deprecated deprecated = clazz.getAnnotation(Deprecated.class);

        Method method = clazz.getMethod("annotatedMethod");
        Annotation[] methodAnnotations = method.getAnnotations();
    }

    // Java 9: Module reflection
    public void testModuleReflection() {
        Module module = String.class.getModule();
        String moduleName = module.getName();
        boolean isNamed = module.isNamed();
    }

    // Helper classes
    static class MyClass {
        private int privateValue;
        private void privateMethod() {}
    }

    static class GenericClass {
        List<String> list;
    }

    @Deprecated
    static class MyAnnotatedClass {
        @SuppressWarnings("unused")
        public void annotatedMethod() {}
    }
}