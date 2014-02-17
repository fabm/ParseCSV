package parsercsv.converters;

import java.lang.reflect.Field;

public interface CSVDecoder {
    public Object decode(String string, Field field) throws ConverterException;
}
