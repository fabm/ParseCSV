package parsercsv.converters;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DefaultCSVEncoder implements CSVEncoder {

    private static String dateEncode(Date date, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(date);
    }

    @Override
    public String encode(Field field, Object object) throws ConverterException {
        if (object == null) return "";

        Class<?> type = field.getType();

        DateType dateType = getDateType(type);

        if (dateType != null) {
            String error = "Expected DatePattern annotation to encode a %s field %s";

            Date date = null;

            switch (dateType) {
                case CALENDAR:
                    Calendar calendar = (Calendar) object;
                    date = calendar.getTime();
                    break;
                case DATE:
                    date = (Date) object;
                    break;
            }

            DatePattern datePatternAnnotation = field.getAnnotation(DatePattern.class);
            if (datePatternAnnotation == null) {
                error = String.format(error, dateType.getStringType(), field.getName());
                throw new ConverterException(String.format(error, field.getName()));
            } else
                return dateEncode(date, datePatternAnnotation.encode());
        }
        return object.toString();
    }

    private DateType getDateType(Class<?> type) {
        if (type == Date.class) {
            return DateType.DATE;
        } else if (Calendar.class.isAssignableFrom(type))
            return DateType.CALENDAR;
        else return null;
    }

    private enum DateType {
        CALENDAR, DATE;

        String getStringType() {
            switch (this) {
                case CALENDAR:
                    return "calendar";
                case DATE:
                    return "date";
            }
            return null;
        }
    }
}
