package controller;

import dao.CargoDao;
import dao.EstadoDao;
import dao.PessoaDao;
import dao.PessoaSalarioDao;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;
import jakarta.faces.model.SelectItem;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import model.*;
import net.sf.jasperreports.engine.*;
import util.DaoException;
import util.ValidatorUtil;
import util.messagesSystem.ListMessagesSystemControl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@Getter
@Setter
@SessionScoped
public class PessoaSalarioMbean extends AbstractControllerBean implements Serializable {



    @Getter
    @Setter
    public static class RestricoesBusca {
        boolean nome;
        boolean cargo;
        boolean estado;
    }

    @Getter
    @Setter
    public static class ParametrosBusca {
        String nome;
        Integer cargo;
        Integer estado;
    }

    Usuario usuarioLogadoSistema;
    List<Pessoa> pessoas;
    int salariosNaoCalculados;
    ParametrosBusca parametrosBusca;
    RestricoesBusca restricoesBusca;

    public void init() {
        usuarioLogadoSistema = getUsuarioSessao();
        pessoas = new ArrayList<>();
        parametrosBusca = new ParametrosBusca();
        restricoesBusca = new RestricoesBusca();
        salariosNaoCalculados = 0;
        setbotaoNavegacaoClicado(BOTAO_NAVEGACAO_LISTA_FUNCIONARIOS);
        populaLista();
    }

    public String entrarListagemFuncionarios() {
        init();
        ListMessagesSystemControl.limpaMessagesSystem();
        try {
            return redirectPage(PAGINA_PESSOAS_SALARIOS);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Métodos responsáveis por popular os selectBox
    public List<SelectItem> getEstadosCombo() {
        EstadoDao estDao = getDao(EstadoDao.class);
        List<SelectItem> estadosCombo = new ArrayList<>();
        try {
            List<Estado> estados = estDao.findAll();
            if (ValidatorUtil.isNotEmpty(estados)) {
                for (Estado estado : estados) {
                    estadosCombo.add(new SelectItem(estado.getId(), estado.getUf()));
                }
            }
        } finally {
            estDao.close();
        }
        return estadosCombo;
    }

    public List<SelectItem> getCargosCombo() {
        CargoDao crgDao = getDao(CargoDao.class);
        List<SelectItem> cargosCombo = new ArrayList<>();
        try {
            List<Cargo> cargos = crgDao.findAll();
            if (ValidatorUtil.isNotEmpty(cargos)) {
                for (Cargo cargo : cargos) {
                    cargosCombo.add(new SelectItem(cargo.getId(), cargo.getNome()));
                }
            }
        } finally {
            crgDao.close();
        }
        return cargosCombo;
    }

    public String buscar() {
        if (!isTransacaoEmAndamento()) {
            validarDados();
            // logica de exibir mensagens
            try {
                usuarioLogadoSistema = getUsuarioSessao();
                if (ValidatorUtil.isNotEmpty(usuarioLogadoSistema))
                    populaLista();
            } catch (Exception erro) {
                throw new DaoException("Erro ao buscar lista de pessoas com salários calculados, contate o administrador do sistema para maiores informações", erro);
            }
            if (ValidatorUtil.isEmpty(pessoas)) {
                // logica mesngem busca sem resultados
            }
            return null;
        }
        return null;
    }

    private void populaLista() {
        PessoaSalarioDao pSalarioDao = getDao(PessoaSalarioDao.class);
        PessoaDao pDao = getDao(PessoaDao.class);
        try {
            if (isAdministrador(getUsuarioLogadoSistema())) {
                pessoas = pDao.findByNameAndCargoAndEstado(parametrosBusca, restricoesBusca);
                List<PessoaSalario> pessoaSalarios = pSalarioDao.findByPessoas(pessoas);
                if (ValidatorUtil.isNotEmpty(pessoaSalarios)) {
                    for (Pessoa p : pessoas) {
                        for (PessoaSalario ps : pessoaSalarios) {
                            if (ps.getPessoa().getId() == p.getId()) {
                                p.setSalarioCalculado(true);
                                break;
                            }
                        }
                    }
                }
                salariosNaoCalculados = pSalarioDao.countPessoasSemSalarioCalculado(pessoas);
            } else {
                Pessoa pessoa = new Pessoa();
                PessoaSalario pessoaSalario = new PessoaSalario();

                pessoa = pDao.findById(usuarioLogadoSistema.getId());
                pessoaSalario = pSalarioDao.findByPessoa(pessoa.getId());
                if (ValidatorUtil.isNotEmpty(pessoaSalario)) {
                    pessoa.setSalarioCalculado(true);
                    pessoas.add(pessoa);
                } else {
                    pessoa.setSalarioCalculado(false);
                    pessoas.add(pessoa);
                }
            }
        } finally {
            pSalarioDao.close();
            pDao.close();
        }
    }

    private void validarDados() {
        if (restricoesBusca.isNome() && ValidatorUtil.isEmpty(parametrosBusca.getNome())) {
            // logica mensagem campo obrigatório
        }

        if (restricoesBusca.isCargo() && ValidatorUtil.isEmpty(parametrosBusca.getCargo())) {
            // logica mensagem campo obrigatório
        }

        if (restricoesBusca.isEstado() && ValidatorUtil.isEmpty(parametrosBusca.getEstado())) {
            // logica mensagem campo obrigatório
        }
    }


    public String contabilizaSalarios() {
        if (!isTransacaoEmAndamento()) {
            if (salariosNaoCalculados == 0) {
                //mensagem todos os salários já calculados
                return null;
            }

            PessoaSalarioDao psSalarioDao = getDao(PessoaSalarioDao.class);
            List<PessoaSalario> pessoaSalarios = new ArrayList<>();
            try {
                for (Pessoa pessoaFuncionario : pessoas) {
                    PessoaSalario pessoaSalario = new PessoaSalario();
                    if (!pessoaFuncionario.isSalarioCalculado()) {
                        pessoaSalario.setPessoa(pessoaFuncionario);
                        pessoaSalario.setNome(pessoaFuncionario.getNome());
                        pessoaSalario.setSalario(pessoaFuncionario.getCargo().getSalario());
                        pessoaSalarios.add(pessoaSalario);
                    }
                }
                if (ValidatorUtil.isNotEmpty(pessoaSalarios)) {
                    psSalarioDao.contabilizarSalarios(pessoaSalarios);
                }
            } catch (Exception error) {
                throw new DaoException("Erro ao contabilizar salários", error);
            } finally {
                psSalarioDao.close();
            }
            init();
            populaLista();
            return null;
        }
        return null;
    }

    public void exportToPDF() throws JRException, IOException {
        InputStream stream = AbstractControllerBean.class.getResourceAsStream("/reports/pessoas_salarios.jrxml");
        JasperReport reportPath = JasperCompileManager.compileReport(stream);

        JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, null, new PessoaDataSource(pessoas));

        // Exportar para PDF em memória
        byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

        // Enviar o relatório em PDF para o navegador
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
        response.setContentType("application/pdf");
        response.setContentLength(pdfBytes.length);
        response.setHeader("Content-Disposition", "inline; filename=\"relatorio_funcionarios_salarios.pdf\"");

        OutputStream outputStream = response.getOutputStream();
        outputStream.write(pdfBytes);
        outputStream.flush();
        outputStream.close();

        facesContext.responseComplete();
    }
}
