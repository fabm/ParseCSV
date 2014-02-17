package testes;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import parsercsv.converters.ConverterException;

public class MarshallerCSVTest {

    @Test
    public void testCSV() throws ConverterException {
        String csv="primeira;segunda;5;SEGUNDO;in;vi1;vi2;20140101;;;;;;;;;ultima";

        MyCSVMarshaller myCSVMarshaller = new MyCSVMarshaller();

        DtoTest dtoTest = myCSVMarshaller.encode(csv, DtoTest.class);

        Assert.assertEquals(dtoTest.getTest1(),"primeira");
        Assert.assertEquals(dtoTest.getTeste2(),"segunda");
        Assert.assertEquals(dtoTest.getTeste3(),5);
        Assert.assertEquals(dtoTest.getTeste4(), TresPrimeiros.SEGUNDO);
        Assert.assertEquals(dtoTest.getTeste6(), new GregorianCalendar(2014, Calendar.JANUARY, 01).getTime());

        Assert.assertEquals(dtoTest.getInnerDto().getTesteInner1(),"vi1");
        Assert.assertEquals(dtoTest.getInnerDto().getTesteInner2(),"vi2");

        Assert.assertEquals(myCSVMarshaller.decode(dtoTest),csv);
    }
}
