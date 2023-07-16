package controller;

import model.Pessoa;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import java.util.Iterator;
import java.util.List;

public class PessoaDataSource implements JRDataSource {
    private final List<Pessoa> pessoas;
    private final Iterator<Pessoa> iterator;
    private Pessoa currentPessoa;

    public PessoaDataSource(List<Pessoa> pessoas) {
        this.pessoas = pessoas;
        this.iterator = pessoas.iterator();
    }

    public boolean next() throws JRException {
        if (iterator.hasNext()) {
            currentPessoa = iterator.next();
            return true;
        }
        return false;
    }

    public Object getFieldValue(JRField jrField) throws JRException {
        String fieldName = jrField.getName();

        if ("nome".equals(fieldName)) {
            return currentPessoa.getNome();
        } else if ("cidade".equals(fieldName)) {
            return currentPessoa.getEndereco().getCidade().getNome();
        } else if ("estado".equals(fieldName)) {
            return currentPessoa.getEndereco().getCidade().getEstado().getUf();
        } else if ("cargo".equals(fieldName)) {
            return currentPessoa.getCargo().getNome();
        } else if ("salario".equals(fieldName)) {
            return currentPessoa.getCargo().getSalario();
        }

        return null;
    }
}
