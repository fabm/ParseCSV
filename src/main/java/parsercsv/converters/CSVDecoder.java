package parsercsv.converters;

import java.lang.reflect.Field;

public interface CSVDecoder<T> {
    public T decode(String string, Field field) throws ConverterCSVException;
}
