package testes;


import parsercsv.CSVCollumn;

public class InnerDto {

    @CSVCollumn(6)
    private String testeInner1;

    @CSVCollumn(7)
    private String testeInner2;

    @CSVCollumn(17)
    private String ultimo;

    public String getUltimo() {
        return ultimo;
    }

    public void setUltimo(String ultimo) {
        this.ultimo = ultimo;
    }

    public String getTesteInner1() {
        return testeInner1;
    }

    public void setTesteInner1(String testeInner1) {
        this.testeInner1 = testeInner1;
    }

    public String getTesteInner2() {
        return testeInner2;
    }

    public void setTesteInner2(String testeInner2) {
        this.testeInner2 = testeInner2;
    }
}
