package parsercsv;

import parsercsv.converters.CSVDecoder;
import parsercsv.converters.CSVEncoder;
import parsercsv.converters.ConverterException;
import parsercsv.validators.Validator;
import parsercsv.validators.ValidatorAnotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Marshaller {
    private static String accessibleError = "It's not possible access to field %s";

    protected abstract CSVDecoder getDecoder();

    protected abstract CSVEncoder getEncoder();

    protected abstract Parser getParser();

    protected abstract int getRecordSize();

    protected CSVRecord getReccordAnnotation(Class<?> eClass) throws ConverterException, ConverterException {
        CSVRecord recordAnnotation = eClass.getAnnotation(CSVRecord.class);

        if (recordAnnotation == null) {
            String error = "class %s does not annotated with CSVRecord";
            throw new ConverterException(String.format(error, eClass));
        }
        return recordAnnotation;
    }

    public String decode(Object container) throws ConverterException {
        if (container == null) {
            throw new ConverterException("Impossible to decode a null object");
        }
        StringBuilder sb = new StringBuilder();
        Class<?> eClass = container.getClass();

        CSVRecord recordAnnotation = getReccordAnnotation(eClass);

        Map<Integer, MappedField> fieldMap = createFieldMap(eClass, container);

        Field field = null;
        for (int i = 0; i < getRecordSize() - 1; i++) {
            MappedField mappedField = fieldMap.get(i+1);
            if (mappedField != null) {
                sb.append(decodeField(mappedField.field, mappedField.container));
            }
            sb.append(";");
        }
        MappedField mappedField = fieldMap.get(getRecordSize());
        if (mappedField != null) {
            sb.append(decodeField(mappedField.field, mappedField.container));
        }

        return sb.toString();
    }

    protected Map<Integer, MappedField> createFieldMap(Class<?> eClass, Object container) throws ConverterException {
        HashMap<Integer, MappedField> fieldMap = new HashMap<Integer, MappedField>();
        return createFieldMap(eClass, fieldMap, container);
    }

    protected Map<Integer, MappedField> createFieldMap(Class<?> eClass, Map<Integer, MappedField> fieldMap, Object container)
            throws ConverterException {
        for (Field field : eClass.getDeclaredFields()) {
            field.setAccessible(true);
            CSVCollumn csvCollumnAnnotation = field.getAnnotation(CSVCollumn.class);
            CSVEmbedded csvEmbeddedAnnotation = field.getAnnotation(CSVEmbedded.class);

            if (csvCollumnAnnotation != null && csvEmbeddedAnnotation != null) {
                throw new ConverterException("The field %s can't be an enbedded and a column at the same time");
            }
            if (csvCollumnAnnotation != null) {
                if (csvCollumnAnnotation.value() > getRecordSize()) {
                    throw new ConverterException(String.format(
                            "The reccord can only have %d " +
                                    "columns and the field %s have the position %d mapped",
                            getRecordSize(),
                            field.getName(),
                            csvCollumnAnnotation.value()
                    )
                    );
                }
                if (fieldMap.containsKey(csvCollumnAnnotation.value())) {
                    String error = "The position %d are mapped twice, in field %s and %s";
                    throw new ConverterException(
                            String.format(
                                    error,
                                    csvCollumnAnnotation.value(),
                                    fieldMap.get(csvCollumnAnnotation.value()).field.getName(),
                                    field.getName()
                            )
                    );
                }
                MappedField mappedField = new MappedField();
                mappedField.field = field;
                mappedField.container = container;
                fieldMap.put(csvCollumnAnnotation.value(), mappedField);
            }
            if (csvEmbeddedAnnotation != null) {
                try {
                    createFieldMap(field.getType(), fieldMap, field.get(container));
                } catch (IllegalAccessException e) {
                    throw new ConverterException(e);
                }
            }
        }
        return fieldMap;
    }

    private String decodeField(Field field, Object container) throws ConverterException {
        field.setAccessible(true);
        try {
            Object object = field.get(container);
            for (Validator validator : getValidators(field)) {
                if (!validator.isValid(object)) {
                    throw new ConverterException(
                            validator.getErrorMessage(
                                    Validator.Action.decode,
                                    field.getName()
                            )
                    );
                }
            }
            return getEncoder().encode(field, field.get(container));
        } catch (IllegalAccessException e) {
            throw new ConverterException(String.format(accessibleError, field.getName()), e);
        }
    }

    /**
     * Encode from CSV String to T object
     *
     * @param csv
     * @param eClass
     * @param <T>
     * @return
     * @throws ConverterException
     */
    public <T> T encode(String csv, Class<T> eClass) throws ConverterException {
        CSVRecord recordAnnotation = getReccordAnnotation(eClass);

        if (recordAnnotation.value() < 0) {
            String error = "Quantity of columns must be positive, not %d";
            throw new ConverterException(String.format(error, recordAnnotation.value()));
        }


        List<String> strings = getParser().getList(csv);


        if (strings.size() != getRecordSize()) {
            throw new ConverterException(
                    String.format(
                            "The number of columns in csv must be %d like was declared in" +
                                    " annotation %s not %d",
                            recordAnnotation.value(),
                            CSVRecord.class.getSimpleName(),
                            strings.size()
                    )
            );
        }

        Object object = encodeFields(eClass, strings);

        return (T) object;
    }

    private ArrayList<Validator> getValidators(Field field) {
        ArrayList<Validator> validators = new ArrayList<Validator>();
        for (Annotation annotation : field.getDeclaredAnnotations()) {
            Validator validator = null;
            for (Annotation sndLevelAnnotation :
                    annotation.annotationType().getDeclaredAnnotations()) {
                if (sndLevelAnnotation.annotationType() == ValidatorAnotation.class) {
                    ValidatorAnotation validatorAnotation =
                            (ValidatorAnotation) sndLevelAnnotation;
                    try {
                        validator = validatorAnotation.value().newInstance();
                        validator.annotationSource(annotation);
                        validators.add(validator);
                    } catch (InstantiationException e) {
                        new ConverterException("It's not possible instantite the validator", e);
                    } catch (IllegalAccessException e) {
                        new ConverterException(accessibleError, e);
                    }
                }
            }
        }
        return validators;
    }

    protected Object encodeFields(Class<?> eClass, List<String> strings) throws ConverterException {
        boolean returnNull = true;
        try {
            Object instance = eClass.newInstance();
            for (Field field : eClass.getDeclaredFields()) {
                CSVCollumn csvCollumnAnnotation = field.getAnnotation(CSVCollumn.class);
                CSVEmbedded csvEmbeddedAnnotation = field.getAnnotation(CSVEmbedded.class);
                if (csvCollumnAnnotation != null) {
                    if(csvCollumnAnnotation.value()>getRecordSize()){
                        String error = "The number of column must be between [1;%d], " +
                                "the field \"%s\" have position %d";
                        throw new ConverterException(
                                String.format(
                                        error,
                                        getRecordSize(),
                                        field.getName(),
                                        csvCollumnAnnotation.value()
                                ));
                    }
                    returnNull = false;
                    encodeField(field, strings.get(csvCollumnAnnotation.value() - 1), instance);
                }
                if (csvEmbeddedAnnotation != null) {
                    field.setAccessible(true);
                    field.set(instance, encodeFields(field.getType(), strings));
                }
            }
            if (returnNull) {
                return null;
            }
            return instance;
        } catch (InstantiationException e) {
            throw new ConverterException(e);
        } catch (IllegalAccessException e) {
            throw new ConverterException(e);
        }
    }

    private void encodeField(Field field, String string, Object container) throws ConverterException {
        for (Validator validator : getValidators(field)) {
            if (!validator.isValid(string)) {
                throw new ConverterException(
                        validator.getErrorMessage(
                                Validator.Action.encode,
                                field.getName()
                        )
                );
            }
        }
        Object converted;
        accessibleError = String.format(accessibleError, field.getName());
        field.setAccessible(true);
        converted = getDecoder().decode(string, field);
        try {
            field.set(container, converted);
        } catch (IllegalAccessException e) {
            throw new ConverterException(accessibleError, e);
        }
    }

    public static class MappedField {
        Object container;
        Field field;
    }

}
