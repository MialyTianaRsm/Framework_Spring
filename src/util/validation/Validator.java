package util.validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import manager.exception.ModelValidationException;
import util.validation.validator.FieldValidator;

public class Validator {
    public static void checkField(String value, Field field) throws ModelValidationException {
        Annotation[] annotations = field.getAnnotations();

        for(Annotation annotation : annotations) {
            FieldValidator validator = ValidatorRegistry.getValidator(annotation.annotationType());

            if (validator != null) {
                validator.validate(value , annotation, field);
            }
        }
    }
}