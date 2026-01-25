package com.github.javaparser;

import com.github.javaparser.ast.type.VarType;
import com.github.javaparser.ast.validator.SingleNodeTypeValidator;
import com.github.javaparser.ast.validator.Validator;
import com.github.javaparser.ast.validator.language_level_validations.Java11Validator;
import com.github.javaparser.ast.validator.language_level_validations.Java25Validator;
import com.github.javaparser.ast.validator.language_level_validations.chunks.VarValidator;

import java.util.List;

public class FixValidators {

    public static void fixJavaValidator(JavaParser parser) {
        ParserConfiguration config = parser.getParserConfiguration();
        config.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_25);
        List<Validator> validators = ((Java11Validator) parser.getParserConfiguration().getLanguageLevel().validator).getValidators();
        validators.replaceAll(v -> {
            if (v instanceof SingleNodeTypeValidator<?> sntv) {
                // use reflection to check if the validator is VarValidator
                // via     private final TypedValidator<N> validator; in SingleNodeTypeValidator
                try {
                    var typedValidatorField = SingleNodeTypeValidator.class.getDeclaredField("validator");
                    typedValidatorField.setAccessible(true);
                    var typedValidator = typedValidatorField.get(sntv);
                    if (typedValidator instanceof VarValidator) {
                        // replace with new VarValidator(true)
                        return new SingleNodeTypeValidator<>(VarType.class, new com.github.javaparser.VarValidator(true));
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }

            }
            return v;
        });
    }
}