package me.bechberger.check;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.validator.language_level_validations.Java24Validator;
import com.github.javaparser.ast.validator.language_level_validations.Java25Validator;
import org.junit.jupiter.api.Test;

import static com.github.javaparser.FixValidators.fixJavaValidator;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JavaParserTest {
    @Test
    public void testLambdaVarParsing() {
        String code = """
                import java.util.function.Predicate;
                public class Test {
                    Predicate<String> f = (var a) -> a.isEmpty();
                }
                """;

        JavaParser parser = new JavaParser(new ParserConfiguration().setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_25));
        fixJavaValidator(parser);
        var result = parser.parse(code);
        if (!result.isSuccessful()) {
            result.getProblems().forEach(System.out::println);
        }
        assertTrue(result.isSuccessful(), "Parsing should be successful");
    }

    @Test
    public void testLambdaVarParsing2() {
        String code = """
                import java.util.function.Predicate;
                public class Test {
                    public void x() {
                        List<String> list = List.of("a", "b", "c");
                        list.stream().filter((var s) -> s.isEmpty()).forEach(System.out::println);
                    }
                }
                """;

        JavaParser parser = new JavaParser(new ParserConfiguration().setLanguageLevel(ParserConfiguration.LanguageLevel.BLEEDING_EDGE));
        var result = parser.parse(code);
        if (!result.isSuccessful()) {
            result.getProblems().forEach(System.out::println);
        }
        assertTrue(result.isSuccessful(), "Parsing should be successful");
    }
}