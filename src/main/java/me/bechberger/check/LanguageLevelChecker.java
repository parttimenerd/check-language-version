package me.bechberger.check;

import com.github.javaparser.*;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.VarType;
import com.github.javaparser.ast.validator.ProblemReporter;
import com.github.javaparser.ast.validator.Validator;
import com.github.javaparser.ast.validator.Validators;
import com.github.javaparser.ast.validator.VisitorValidator;
import com.github.javaparser.ast.validator.language_level_validations.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static com.github.javaparser.FixValidators.fixJavaValidator;

/**
 * Checks the required Java language level for a given Java source file.
 */
public class LanguageLevelChecker {
    record Val(int javaVersion, Validators validators) {
        public Val {
            if (javaVersion < 10) {
                validators.add(new NoVarAllowedValidator());
            }
            if (javaVersion < 25) {
                validators.add(new NoCompactClassAllowed());
            }
        }
    }

    static List<Val> val = new ArrayList<>();
    static {
        val.add(new Val(8, new Java8Validator()));
        val.add(new Val(9, new Java9Validator()));
        val.add(new Val(10, new Java10Validator()));
        val.add(new Val(11, new Java11Validator()));
        // val.add(new Val(12, new Java12Validator())); // nothing added
        // val.add(new Val(13, new Java13Validator())); // nothing added
        val.add(new Val(14, new Java14Validator()));
        val.add(new Val(15, new Java15Validator()));
        val.add(new Val(16, new Java16Validator()));
        val.add(new Val(17, new Java17Validator()));
        // val.add(new Val(18, new Java18Validator())); // nothing added
        // val.add(new Val(19, new Java19Validator())); // nothing added
        // val.add(new Val(20, new Java20Validator())); // nothing added
        val.add(new Val(21, new Java21Validator()));
        val.add(new Val(22, new Java22Validator()));
        // val.add(new Val(23, new Java23Validator())); // nothing added
        // val.add(new Val(24, new Java24Validator())); // nothing added
        val.add(new Val(25, new Java25Validator()));
    }

    static JavaParser parser = new JavaParser(new ParserConfiguration().setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_25));
    {
        fixJavaValidator(parser);
    }
    /**
     * Returns the lowest Java version that is required to compile the given file.
     * @param file The Java source file
     * @return The required Java version or -1 if parsing failed
     */
    public static int parse(File file) throws FileNotFoundException {
        ParseResult<CompilationUnit> node;
        try {
            node = parser.parse(file);
        } catch (StackOverflowError e) {
            return -1;
        }
        if (node.isSuccessful() && node.getResult().isPresent()) {
            for (Val v : val) {
                try {
                    v.validators.accept(node.getResult().get(), new ProblemReporter(p -> {
                        throw new Ex(); // fail the validation on first problem
                    }));
                } catch (Ex e) {
                    continue;
                } catch (StackOverflowError e) {
                    return -1;
                }
                return v.javaVersion;
            }
            return -1; // should not happen
        } else {
            return -1;
        }
    }

    static class NoVarAllowedValidator extends VisitorValidator {
        @Override
        public void visit(VarType type, ProblemReporter p) {
            p.report(type.getTokenRange().get(), "var not allowed");
        }
    }

    static class NoCompactClassAllowed extends VisitorValidator {
        public void visit(ClassOrInterfaceDeclaration decl, ProblemReporter p) {
            if (decl.isCompact()) {
                p.report(decl.getTokenRange().get(), "Compact class not allowed");
            }
        }
    }

    static class Ex extends RuntimeException {}
}