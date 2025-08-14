package util.validation.validator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import manager.exception.ModelValidationException;

public interface FieldValidator {
    public void validate(String value, Annotation annotation,Field field) throws ModelValidationException;
}