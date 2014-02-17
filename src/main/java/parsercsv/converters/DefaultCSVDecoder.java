package parsercsv.converters;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DefaultCSVDecoder implements CSVDecoder {

    @Override
    public Object decode(String string, Field field) throws ConverterException {
        Class<?> type = field.getType();

        if(string.isEmpty())
            return null;
        else
        if(type == Date.class){
            DatePattern datePatternAnnotation = field.getAnnotation(DatePattern.class);
            if(datePatternAnnotation == null){
                String error = "DatePattern annotation must be present decode field %s";
                throw new ConverterException(String.format(error,field.getName()));
            }
            String inPattern = datePatternAnnotation.decode();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(inPattern);
            try {
                return simpleDateFormat.parse(string);
            } catch (ParseException e) {
                String error = "It's not possible to convert \"%s\" to date with pattern \"%s\"";
                throw new ConverterException(String.format(error,string,inPattern));
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
                throw new ConverterException(nfe);
            }
        }
        if(Enum.class.isAssignableFrom(type)){
            Class<Enum> classEnum = (Class<Enum>) type;
            return Enum.valueOf(classEnum,string);
        }

        throw new ConverterException(String.format("Type %s unexpected",type.getName()));
    }
}
