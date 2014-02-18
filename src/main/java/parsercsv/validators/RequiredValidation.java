package parsercsv.validators;

import java.lang.annotation.Annotation;

public class RequiredValidation implements Validator<Validator.Required>{

    @Override
    public void annotationSource(Required annotation) {
        //its no necessary
    }

    @Override
    public boolean isValid(String string) {
        return !string.isEmpty();
    }

    @Override
    public boolean isValid(Object object) {
        return object!=null;
    }

    @Override
    public String getErrorMessage(Action action, String fieldName) {
        String error = "The field %s is required";
        error = String.format(error,fieldName);
        return error;
    }

}
