package parsercsv.converters;

public class ConverterCSVException extends Exception{
    public ConverterCSVException(Throwable cause) {
        super(cause);
    }

    public ConverterCSVException(String message) {
        super(message);
    }

    public ConverterCSVException(String message, Throwable cause) {
        super(message, cause);
    }
}
