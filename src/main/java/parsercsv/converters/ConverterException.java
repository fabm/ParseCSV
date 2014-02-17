package parsercsv.converters;

public class ConverterException extends Exception{
    public ConverterException(Throwable cause) {
        super(cause);
    }

    public ConverterException(String message) {
        super(message);
    }

    public ConverterException(String message, Throwable cause) {
        super(message, cause);
    }
}
