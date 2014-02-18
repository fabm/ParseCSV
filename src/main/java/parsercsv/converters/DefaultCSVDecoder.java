package parsercsv.converters;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DefaultCSVDecoder implements CSVDecoder {

    @Override
    public Object decode(String string, Field field) throws ConverterCSVException {
        Class type = field.getType();

        if(string.isEmpty())
            return null;
        else
        if(type == Date.class){
            DatePattern datePatternAnnotation = field.getAnnotation(DatePattern.class);
            if(datePatternAnnotation == null){
                String error = "DatePattern annotation must be present decode field %s";
                throw new ConverterCSVException(String.format(error,field.getName()));
            }
            String inPattern = datePatternAnnotation.decode();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(inPattern);
            try {
                return simpleDateFormat.parse(string);
            } catch (ParseException e) {
                String error = "It's not possible to convert \"%s\" to date with pattern \"%s\"";
                throw new ConverterCSVException(String.format(error,string,inPattern));
            }
        }
        else
        if(type == String.class){
            return string;
        }
        if(Integer.TYPE.isAssignableFrom(type)){
            try {
                return Integer.parseInt(string);
            }catch (NumberFormatException nfe){
                throw new ConverterCSVException(nfe);
            }
        }
        if(Enum.class.isAssignableFrom(type)){
            return Enum.valueOf(type, string);
        }

        throw new ConverterCSVException(String.format("Type %s unexpected",type.getName()));
    }
}
