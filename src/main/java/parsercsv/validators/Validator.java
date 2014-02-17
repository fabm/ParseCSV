package parsercsv.validators;

import java.lang.annotation.*;

public interface Validator {
    enum Action{
        encode,decode;
    }
    void annotationSource(Annotation annotation);
    boolean isValid(String string);
    boolean isValid(Object object);
    String getErrorMessage(Action action, String fieldName);

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @ValidatorAnotation(RequiredValidation.class)
    @interface Required{}


    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @ValidatorAnotation(MinLengthValidation.class)
    @interface MinLength{
        int value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @ValidatorAnotation(MaxLengthValidation.class)
    @interface MaxLength{
        int value();
    }

}
