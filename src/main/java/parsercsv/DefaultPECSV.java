package parsercsv;

import parsercsv.CSVRecord;
import parsercsv.DefaultParser;
import parsercsv.ParserEngine;
import parsercsv.Parser;
import parsercsv.converters.*;

public class DefaultPECSV extends ParserEngine {

    private CSVDecoder decoder;
    private CSVEncoder encoder;
    private DefaultParser parser;

    public DefaultPECSV() {
        this.decoder = new DefaultCSVDecoder();
        this.encoder = new DefaultCSVEncoder();
        this.parser = new DefaultParser();
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

}
