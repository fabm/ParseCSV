package parsercsv.validators;

import java.lang.annotation.Annotation;

public class MinLengthValidation implements Validator{
    private int min;
    private String value;

    @Override
    public void annotationSource(Annotation annotation) {
        MinLength aMinLength = (MinLength) annotation;
        min = aMinLength.value();
    }

    @Override
    public boolean isValid(String str) {
        value = str;
        return str.length()>min;
    }

    @Override
    public boolean isValid(Object object) {
        return true;
    }

    @Override
    public String getErrorMessage(Action action, String fieldName) {
        String str = "The field %s with the string \"%s\" must be have more than %d caracters";
        str = String.format(str,fieldName,value,min);
        return str;
    }

}
