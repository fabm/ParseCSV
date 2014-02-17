package testes;

import parsercsv.CSVRecord;
import parsercsv.DefaultParser;
import parsercsv.Marshaller;
import parsercsv.Parser;
import parsercsv.converters.*;

public class MyCSVMarshaller extends Marshaller {

    private CSVDecoder decoder;
    private CSVEncoder encoder;
    private DefaultParser parser;
    private int size;

    public MyCSVMarshaller() {
        this.decoder = new DefaultCSVDecoder();
        this.encoder = new DefaultCSVEncoder();
        this.parser = new DefaultParser();
    }

    @Override
    protected CSVRecord getReccordAnnotation(Class<?> eClass) throws ConverterException {
        CSVRecord recordAnnotation = super.getReccordAnnotation(eClass);
        parser.setNumberColumns(recordAnnotation.value());
        size = recordAnnotation.value();
        return recordAnnotation;
    }

    @Override
    protected CSVDecoder getDecoder() {
        return decoder;
    }

    @Override
    protected CSVEncoder getEncoder(){
        return encoder;
    }

    @Override
    protected Parser getParser() {
        return parser;
    }

    @Override
    protected int getRecordSize() {
        return size;
    }


}
