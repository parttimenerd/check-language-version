// Java 24 feature: Class-File API (JEP 484)
// Expected Version: 24
// Required Features: CLASS_FILE_API, FOR_EACH, LAMBDAS
import java.lang.classfile.ClassFile;
import java.lang.classfile.ClassModel;
import java.lang.classfile.MethodModel;
import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;

public class Java24_ClassFileAPI {
    public void testClassFileAPI() throws Exception {
        // Read and parse a class file
        byte[] bytes = getClass().getResourceAsStream("/java/lang/Object.class").readAllBytes();
        ClassModel classModel = ClassFile.of().parse(bytes);

        // Inspect class information
        System.out.println("Class: " + classModel.thisClass().asInternalName());
        System.out.println("Super: " + classModel.superclass().map(s -> s.asInternalName()).orElse("none"));

        // Iterate over methods
        for (MethodModel method : classModel.methods()) {
            System.out.println("Method: " + method.methodName().stringValue());
        }
    }

    public void generateClass() {
        // Generate a new class using the Class-File API
        byte[] newClass = ClassFile.of().build(
            ClassDesc.of("com.example", "Generated"),
            classBuilder -> {
                classBuilder.withVersion(61, 0);  // Java 17 class version
                classBuilder.withMethod(
                    "hello",
                    MethodTypeDesc.ofDescriptor("()V"),
                    ClassFile.ACC_PUBLIC | ClassFile.ACC_STATIC,
                    methodBuilder -> methodBuilder.withCode(codeBuilder -> {
                        codeBuilder.return_();
                    })
                );
            }
        );

        System.out.println("Generated class size: " + newClass.length + " bytes");
    }
}