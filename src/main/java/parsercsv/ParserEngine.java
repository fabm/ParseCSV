package parsercsv;

import parsercsv.converters.CSVDecoder;
import parsercsv.converters.CSVEncoder;
import parsercsv.converters.ConverterCSVException;
import parsercsv.validators.Validator;
import parsercsv.validators.ValidatorAnotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ParserEngine {
    private static String accessibleError = "It's not possible access to field %s";

    protected int recordSize;
    protected abstract CSVDecoder getDecoder();

    protected abstract CSVEncoder getEncoder();

    protected abstract Parser getParser();

    protected void invoceReccordAnnotation(Class<?> eClass) throws ConverterCSVException, ConverterCSVException {
        CSVRecord recordAnnotation = eClass.getAnnotation(CSVRecord.class);

        if (recordAnnotation == null) {
            String error = "class %s does not annotated with CSVRecord";
            throw new ConverterCSVException(String.format(error, eClass));
        }
        recordSize = recordAnnotation.value();
        getParser().setRecordSize(recordSize);
    }

    public String decode(Object container) throws ConverterCSVException {
        if (container == null) {
            throw new ConverterCSVException("Impossible to decode a null object");
        }
        StringBuilder sb = new StringBuilder();
        Class<?> eClass = container.getClass();

        invoceReccordAnnotation(eClass);

        Map<Integer, MappedField> fieldMap = createFieldMap(eClass, container);

        Field field = null;
        for (int i = 0; i < recordSize - 1; i++) {
            MappedField mappedField = fieldMap.get(i+1);
            if (mappedField != null) {
                sb.append(decodeField(mappedField.field, mappedField.container));
            }
            sb.append(";");
        }
        MappedField mappedField = fieldMap.get(recordSize);
        if (mappedField != null) {
            sb.append(decodeField(mappedField.field, mappedField.container));
        }

        return sb.toString();
    }

    protected Map<Integer, MappedField> createFieldMap(Class<?> eClass, Object container) throws ConverterCSVException {
        HashMap<Integer, MappedField> fieldMap = new HashMap<Integer, MappedField>();
        return createFieldMap(eClass, fieldMap, container);
    }

    protected Map<Integer, MappedField> createFieldMap(Class<?> eClass, Map<Integer, MappedField> fieldMap, Object container)
            throws ConverterCSVException {
        for (Field field : eClass.getDeclaredFields()) {
            field.setAccessible(true);
            CSVCollumn csvCollumnAnnotation = field.getAnnotation(CSVCollumn.class);
            CSVEmbedded csvEmbeddedAnnotation = field.getAnnotation(CSVEmbedded.class);

            if (csvCollumnAnnotation != null && csvEmbeddedAnnotation != null) {
                throw new ConverterCSVException("The field %s can't be an enbedded and a column at the same time");
            }
            if (csvCollumnAnnotation != null) {
                if (csvCollumnAnnotation.value() > recordSize) {
                    throw new ConverterCSVException(String.format(
                            "The reccord can only have %d " +
                                    "columns and the field %s have the position %d mapped",
                            recordSize,
                            field.getName(),
                            csvCollumnAnnotation.value()
                    )
                    );
                }
                if (fieldMap.containsKey(csvCollumnAnnotation.value())) {
                    String error = "The position %d are mapped twice, in field %s and %s";
                    throw new ConverterCSVException(
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
                    throw new ConverterCSVException(e);
                }
            }
        }
        return fieldMap;
    }

    private String decodeField(Field field, Object container) throws ConverterCSVException {
        field.setAccessible(true);
        try {
            Object object = field.get(container);
            for (Validator validator : getValidators(field)) {
                if (!validator.isValid(object)) {
                    throw new ConverterCSVException(
                            validator.getErrorMessage(
                                    Validator.Action.decode,
                                    field.getName()
                            )
                    );
                }
            }
            return getEncoder().encode(field, field.get(container));
        } catch (IllegalAccessException e) {
            throw new ConverterCSVException(String.format(accessibleError, field.getName()), e);
        }
    }

    /**
     * Encode from CSV String to T object
     *
     * @param csv
     * @param eClass
     * @param <T>
     * @return
     * @throws parsercsv.converters.ConverterCSVException
     */
    public <T> T encode(String csv, Class<T> eClass) throws ConverterCSVException {
        invoceReccordAnnotation(eClass);

        if (recordSize < 0) {
            String error = "Quantity of columns must be positive, not %d";
            throw new ConverterCSVException(String.format(error, recordSize));
        }


        List<String> strings = getParser().getList(csv);


        if (strings.size() != recordSize) {
            throw new ConverterCSVException(
                    String.format(
                            "The number of columns in csv must be %d like was declared in" +
                                    " annotation %s not %d",
                            recordSize,
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
                        new ConverterCSVException("It's not possible instantite the validator", e);
                    } catch (IllegalAccessException e) {
                        new ConverterCSVException(accessibleError, e);
                    }
                }
            }
        }
        return validators;
    }

    protected Object encodeFields(Class<?> eClass, List<String> strings) throws ConverterCSVException {
        boolean returnNull = true;
        try {
            Object instance = eClass.newInstance();
            for (Field field : eClass.getDeclaredFields()) {
                CSVCollumn csvCollumnAnnotation = field.getAnnotation(CSVCollumn.class);
                CSVEmbedded csvEmbeddedAnnotation = field.getAnnotation(CSVEmbedded.class);
                if (csvCollumnAnnotation != null) {
                    if(csvCollumnAnnotation.value()> recordSize){
                        String error = "The number of column must be between [1;%d], " +
                                "the field \"%s\" have position %d";
                        throw new ConverterCSVException(
                                String.format(
                                        error,
                                        recordSize,
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
            throw new ConverterCSVException(e);
        } catch (IllegalAccessException e) {
            throw new ConverterCSVException(e);
        }
    }

    private void encodeField(Field field, String string, Object container) throws ConverterCSVException {
        for (Validator validator : getValidators(field)) {
            if (!validator.isValid(string)) {
                throw new ConverterCSVException(
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
            throw new ConverterCSVException(accessibleError, e);
        }
    }

    public static class MappedField {
        Object container;
        Field field;
    }

}
