package controller;

import dao.CargoDao;
import dao.EstadoDao;
import dao.PessoaDao;
import dao.PessoaSalarioDao;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;
import jakarta.faces.model.SelectItem;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;
import model.*;
import net.sf.jasperreports.engine.*;
import util.DaoException;
import util.ValidatorUtil;
import util.messagesSystem.ListMessagesSystemControl;
import util.messagesSystem.MensagemSistema;
import util.messagesSystem.TipoMensagem;

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

    private Usuario usuarioLogadoSistema;
    private List<Pessoa> pessoas;
    private int salariosNaoCalculados;
    private ParametrosBusca parametrosBusca;
    private RestricoesBusca restricoesBusca;

    public void init() {
        usuarioLogadoSistema = getUsuarioSessao();
        pessoas = new ArrayList<>();
        parametrosBusca = new ParametrosBusca();
        restricoesBusca = new RestricoesBusca();
        salariosNaoCalculados = 0;
        setMessagesSystem(new ListMessagesSystemControl());
        populaLista();
    }

    public String entrarListagemFuncionarios() {
        init();
        setbotaoNavegacaoClicado(BOTAO_NAVEGACAO_LISTA_FUNCIONARIOS);
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
            if (hasMessages())
                return null;
            // logica de exibir mensagens
            try {
                usuarioLogadoSistema = getUsuarioSessao();
                if (ValidatorUtil.isNotEmpty(usuarioLogadoSistema))
                    populaLista();
            } catch (Exception erro) {
                String mensagemErro = "Erro ao buscar lista de pessoas com salários calculados, contate o administrador do sistema para maiores informações";
                getMessagesSystem().addMensagem(new MensagemSistema(mensagemErro, TipoMensagem.ERROR));
                throw new DaoException(mensagemErro, erro);
            }
            if (ValidatorUtil.isEmpty(pessoas)) {
                getMessagesSystem().addMensagem(new MensagemSistema(MensagemSistema.BUSCA_SEM_RESULTADOS, TipoMensagem.ERROR));
                return null;
            }
            return null;
        }
        getMessagesSystem().addMensagem(new MensagemSistema(MensagemSistema.TRANSACAO_EM_ANDAMENTO, TipoMensagem.ERROR));
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
        List<MensagemSistema> mensagemSistemas = new ArrayList<>();
        if (restricoesBusca.isNome() && ValidatorUtil.isEmpty(parametrosBusca.getNome())) {
            mensagemSistemas.add(new MensagemSistema("Nome: " + MensagemSistema.CAMPO_OBRIGATORIO, TipoMensagem.ERROR));
        }

        if (restricoesBusca.isCargo() && ValidatorUtil.isEmpty(parametrosBusca.getCargo())) {
            mensagemSistemas.add(new MensagemSistema("Cargo: " + MensagemSistema.CAMPO_OBRIGATORIO, TipoMensagem.ERROR));
        }

        if (restricoesBusca.isEstado() && ValidatorUtil.isEmpty(parametrosBusca.getEstado())) {
            mensagemSistemas.add(new MensagemSistema("Estado: " + MensagemSistema.CAMPO_OBRIGATORIO, TipoMensagem.ERROR));
        }

        if (ValidatorUtil.isNotEmpty(mensagemSistemas)) {
            getMessagesSystem().addMensagem(mensagemSistemas);
        }
    }


    public String contabilizaSalarios() {
        if (!isTransacaoEmAndamento()) {
            if (salariosNaoCalculados == 0) {
                getMessagesSystem().addMensagem(new MensagemSistema("Todos os salários da lista já estão calculados.", TipoMensagem.ERROR));
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
                String mensagemErro = "Erro ao contabilizar salários, contate o administrador do sistema para maiores informações";
                getMessagesSystem().addMensagem(new MensagemSistema(mensagemErro, TipoMensagem.ERROR));
                throw new DaoException(mensagemErro, error);
            } finally {
                psSalarioDao.close();
            }
            init();
            getMessagesSystem().addMensagem(new MensagemSistema(MensagemSistema.CALCULO_REALIZADO, TipoMensagem.SUCCESS));
            populaLista();
            return null;
        }
        getMessagesSystem().addMensagem(new MensagemSistema(MensagemSistema.TRANSACAO_EM_ANDAMENTO, TipoMensagem.ERROR));
        return null;
    }

    public String exportToPDF() {
        setMessagesSystem(new ListMessagesSystemControl());
        InputStream stream = AbstractControllerBean.class.getResourceAsStream("/reports/pessoas_salarios.jrxml");
        List<Pessoa> pessoasSalarioCaculado = new ArrayList<>();
        for (Pessoa pessoaFuncionario : pessoas) {
            PessoaSalario pessoaSalario = new PessoaSalario();
            if (pessoaFuncionario.isSalarioCalculado()) {
                pessoasSalarioCaculado.add(pessoaFuncionario);
            }
        }

        if (ValidatorUtil.isEmpty(pessoasSalarioCaculado)) {
            getMessagesSystem().addMensagem(new MensagemSistema("Não há funcionários com salários calculados na listagem atual.", TipoMensagem.ERROR));
            return null;
        }

        try {
            JasperReport reportPath = JasperCompileManager.compileReport(stream);

            JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, null, new PessoaDataSource(pessoasSalarioCaculado));

            // Exportar para PDF em memória
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            // Enviar o relatório em PDF para o navegador
            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();
            HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
            response.setContentType("application/pdf");
            response.setContentLength(pdfBytes.length);
            response.setHeader("Content-Disposition", "inline; filename=\"relatorio_funcionarios_salarios.pdf\"");

            UIViewRoot viewRoot = facesContext.getViewRoot();
            String componentId = "mensagensSystem";
            UIComponent component = viewRoot.findComponent(componentId);
            if (component != null) {
                component.setRendered(true);
            }

            OutputStream outputStream = response.getOutputStream();
            outputStream.write(pdfBytes);
            outputStream.flush();
            outputStream.close();
            facesContext.responseComplete();
        } catch (JRException | IOException errorArquivo) {
            String mensagemErro = "Erro ao gerar arquivo PDF: Não foi possível encontrar o arquivo no caminho espécificado / Arquivo Comrrompido / Não foi possível gerar o PDF";
            getMessagesSystem().addMensagem(new MensagemSistema(mensagemErro, TipoMensagem.ERROR));
            throw new DaoException(mensagemErro, errorArquivo);
        }
        return null;
    }
}
