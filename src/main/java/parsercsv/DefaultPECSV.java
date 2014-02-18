package parsercsv;

import parsercsv.converters.*;

/**
 * Default parser engine CSV
 */
public class DefaultPECSV extends ParserEngine {

    private CSVDecoder decoder;
    private CSVEncoder encoder;
    private Parser parser;

    public DefaultPECSV() {
        this.decoder = new DefaultCSVDecoder();
        this.encoder = new DefaultCSVEncoder();
        this.parser = new SimpleParser();
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
