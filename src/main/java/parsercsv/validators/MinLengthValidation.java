package parsercsv.validators;

public class MinLengthValidation implements Validator<Validator.MinLength>{
    private int min;
    private String value;

    @Override
    public void annotationSource(MinLength annotation) {
        min = annotation.value();
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
