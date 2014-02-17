package testes;


import parsercsv.CSVCollumn;
import parsercsv.CSVEmbedded;
import parsercsv.CSVRecord;
import parsercsv.converters.DatePattern;
import parsercsv.validators.Validator;

import java.util.Date;


@CSVRecord(17)
public class DtoTest {

    @CSVCollumn(1)
    private String test1;

    @Validator.MaxLength(9)
    @Validator.MinLength(3)
    @CSVCollumn(2)
    private String teste2;
    @CSVCollumn(3)
    private int teste3;
    @CSVCollumn(4)
    private TresPrimeiros teste4;
    @Validator.Required
    @CSVCollumn(5)
    private String teste5;
    @CSVEmbedded
    private InnerDto innerDto;
    @DatePattern(
            encode = ConverterUtils.DATE_PATTERN_ENCODE,
            decode = ConverterUtils.DATE_PATTERN_ENCODE
    )
    @CSVCollumn(8)
    private Date teste6;
    @CSVCollumn(9)
    private String teste7;

    public InnerDto getInnerDto() {
        return innerDto;
    }

    public void setInnerDto(InnerDto innerDto) {
        this.innerDto = innerDto;
    }

    public String getTeste5() {
        return teste5;
    }

    public void setTeste5(String teste5) {
        this.teste5 = teste5;
    }

    public Date getTeste6() {
        return teste6;
    }

    public void setTeste6(Date teste6) {
        this.teste6 = teste6;
    }

    public String getTeste7() {
        return teste7;
    }

    public void setTeste7(String teste7) {
        this.teste7 = teste7;
    }

    public TresPrimeiros getTeste4() {
        return teste4;
    }

    public void setTeste4(TresPrimeiros teste4) {
        this.teste4 = teste4;
    }

    public String getTest1() {
        return test1;
    }

    public void setTest1(String test1) {
        this.test1 = test1;
    }

    public String getTeste2() {
        return teste2;
    }

    public void setTeste2(String teste2) {
        this.teste2 = teste2;
    }

    public int getTeste3() {
        return teste3;
    }

    public void setTeste3(int teste3) {
        this.teste3 = teste3;
    }
}
