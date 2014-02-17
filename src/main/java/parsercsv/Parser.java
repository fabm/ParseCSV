package parsercsv;

import parsercsv.converters.ConverterException;

import java.util.List;

public interface Parser {
    List<String> getList(String csv) throws ConverterException;
}
