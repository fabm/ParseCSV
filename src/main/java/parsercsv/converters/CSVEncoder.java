package parsercsv.converters;

import java.lang.reflect.Field;

public interface CSVEncoder {
    String encode(Field field, Object object) throws ConverterException;
}
