package parsercsv;

import parsercsv.converters.ConverterCSVException;

import java.util.List;

public interface Parser {
    List<String> getList(String csv) throws ConverterCSVException;
    void setRecordSize(int recordSize);
}
