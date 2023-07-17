package controller;

import dao.*;
import jakarta.enterprise.context.SessionScoped;

import jakarta.faces.event.ValueChangeEvent;
import jakarta.faces.model.SelectItem;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import model.*;
import util.DaoException;
import util.ValidatorUtil;
import util.messagesSystem.MensagemSistema;
import util.messagesSystem.TipoMensagem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Named
@Getter
@Setter
@SessionScoped
public class CadastroUsuarioMBean extends AbstractControllerBean {

    private Pessoa objMovimentado;
    private Cargo cargoAtual;
    private PessoaSalario psSalarioObjMovimentado;
    private int idEstado;
    private int idCidade;
    private Collection<SelectItem> municipiosCombo = new ArrayList<>();

    public void init() {
        objMovimentado = new Pessoa();
        objMovimentado.setUsuario(new Usuario());
        objMovimentado.setCargo(new Cargo());
        objMovimentado.setEndereco(new Endereco());
        cargoAtual = new Cargo();
        psSalarioObjMovimentado = new PessoaSalario();
        idEstado = 0;
        idCidade = 0;
        municipiosCombo = new ArrayList<>();
    }

    public void entrarCadastroDeUsuario() {
        init();
        try {
            setbotaoNavegacaoClicado(BOTAO_NAVEGACAO_CADASTRO_USUARIO);
            carregarMunicipiosCombo(0);
            redirectPage(PAGINA_CADASTRO_PESSOA);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String preAlterarPessoa(Pessoa pessoaSelecionada) {

        UsuarioDao userDao = getDao(UsuarioDao.class);
        PessoaSalarioDao psDao = getDao(PessoaSalarioDao.class);
        try {
            objMovimentado = pessoaSelecionada;
            cargoAtual = objMovimentado.getCargo();
            objMovimentado.setUsuario(userDao.findByPessoa(objMovimentado.getId()));
            objMovimentado.setPessoaSalario(psDao.findByPessoa(objMovimentado.getId()));
            carregarMunicipiosCombo(objMovimentado.getEndereco().getCidade().getEstado().getId());

            redirectPage(PAGINA_CADASTRO_PESSOA);
        } catch (IOException erro) {
            String mensagemErro = "Ocorreu um problema ao tentar alterar selecionar o funcionário para alteração";
            getMessagesSystem().addMensagem(new MensagemSistema(mensagemErro, TipoMensagem.ERROR));
            throw new DaoException(mensagemErro, erro);
        } finally {
            userDao.close();
            psDao.close();
        }
        
        return null;
    }


    // Métodos dos SelectBox

    public List<SelectItem> getCargosCombo() {
        CargoDao crgDao = getDao(CargoDao.class);
        List<SelectItem> cargosCombo = new ArrayList<>();
        try {
            List<Cargo> cargos = crgDao.findAll();
            if (ValidatorUtil.isNotEmpty(cargos)) {
                for (Cargo cargo : cargos) {
                    cargosCombo.add(new SelectItem(cargo.getId(), cargo.nomeCargoESalario()));
                }
            }
        } finally {
            crgDao.close();
        }
        return cargosCombo;
    }

    public void defineTipoUsuario(ValueChangeEvent value) {
        Integer idCargo = (Integer) value.getNewValue();
        if (ValidatorUtil.isNotEmpty(idCargo)) {
            if (idCargo.equals(Cargo.GERENTE)) {
                objMovimentado.getUsuario().setTipoUsuario(TipoUsuario.ADMINISTRADOR);
            } else {
                objMovimentado.getUsuario().setTipoUsuario(TipoUsuario.CONSULTOR);
            }
        } else {
            objMovimentado.getUsuario().setTipoUsuario(TipoUsuario.CONSULTOR);
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

    public void carregarMunicipios(ValueChangeEvent value) {
        Integer idUf = (Integer) value.getNewValue();
        if (ValidatorUtil.isEmpty(idUf))
            idUf = 0;
        carregarMunicipiosCombo(idUf);
    }

    public void carregarMunicipiosCombo(int idEstado) {
        CidadeDao cdDao = getDao(CidadeDao.class);
        List<Cidade> cidades = new ArrayList<>();
        try {
            cidades = cdDao.findAllByEstado(idEstado);
            if (ValidatorUtil.isNotEmpty(cidades)) {
                for (Cidade cidade : cidades) {
                    municipiosCombo.add(new SelectItem(cidade.getId(), cidade.getNome()));
                }
            } else {
                municipiosCombo = new ArrayList<>();
            }
        } finally {
            cdDao.close();
        }
    }

    public String cancelar() {
        setbotaoNavegacaoClicado(BOTAO_NAVEGACAO_LISTA_FUNCIONARIOS);
        try {
            redirectPage(PAGINA_PESSOAS_SALARIOS);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

}
