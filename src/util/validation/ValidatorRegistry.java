package util.validation;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import annotation.validation.DateType;
import annotation.validation.Email;
import annotation.validation.Length;
import annotation.validation.Numeric;
import annotation.validation.Required;
import util.validation.validator.DateValidator;
import util.validation.validator.EmailValidator;
import util.validation.validator.FieldValidator;
import util.validation.validator.LengthValidator;
import util.validation.validator.NumericValidator;
import util.validation.validator.RequiredValidator;

public class ValidatorRegistry {
    private static final Map<Class<?extends Annotation>, FieldValidator> validators = new HashMap<>();
    
    static {
        validators.put(Length.class, new LengthValidator());
        validators.put(DateType.class, new DateValidator());
        validators.put(Email.class, new EmailValidator());
        validators.put(Required.class, new RequiredValidator());
        validators.put(Numeric.class, new NumericValidator());
    }
    
    public static FieldValidator getValidator(Class<?extends Annotation> annotation) {
        return validators.get(annotation);
    }
}