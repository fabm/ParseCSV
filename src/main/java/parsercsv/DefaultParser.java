package parsercsv;


import parsercsv.converters.ConverterException;

import java.util.ArrayList;
import java.util.List;

public class DefaultParser implements Parser {
    private int numberColumns;

    public int getNumberColumns() {
        return numberColumns;
    }

    public void setNumberColumns(int numberColumns) {
        this.numberColumns = numberColumns;
    }

    @Override
    public List<String> getList(String csv) throws ConverterException {
        ArrayList<String> strings = new ArrayList<String>();

        boolean innerString = false;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < csv.length(); i++) {
            char c = csv.charAt(i);

            if (c == '"') {
                if (innerString) {
                    strings.add(sb.toString());
                    sb = new StringBuilder();
                    innerString = false;
                    if (csv.charAt(i + 1) != ';') {
                        throw new RuntimeException("Expected ; after\"");
                    } else
                        i += 2;
                } else {
                    innerString = true;
                }
            } else if (c == ';' && !innerString) {
                if (strings.size() > getNumberColumns()) {
                    String error = "Number of columns in csv ar superior than the annotated in %s parameter";
                    throw new ConverterException(String.format(error, CSVRecord.class.getSimpleName()));
                }
                strings.add(sb.toString());
                sb = new StringBuilder();
            } else {
                sb.append(c);
            }
        }
        strings.add(sb.toString());
        return strings;
    }
}
