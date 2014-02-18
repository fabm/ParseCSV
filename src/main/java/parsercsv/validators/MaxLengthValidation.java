package parsercsv.validators;

public class MaxLengthValidation implements Validator<Validator.MaxLength> {

    private int max;
    private String value;

    @Override
    public void annotationSource(MaxLength annotation) {
        max = annotation.value();
    }

    @Override
    public boolean isValid(String str) {
        value = str;
        return str.length() < max;
    }

    @Override
    public boolean isValid(Object object) {
        return true;
    }

    @Override
    public String getErrorMessage(Action action, String fieldName) {
        String str = "The field %s with the string \"%s\" must be have less than %d caracters";
        str = String.format(str, fieldName, value, max);
        return str;
    }



}
