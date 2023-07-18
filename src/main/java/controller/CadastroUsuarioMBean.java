package controller;

import dao.*;
import jakarta.enterprise.context.SessionScoped;

import jakarta.faces.model.SelectItem;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import model.*;
import util.DaoException;
import util.PasswordControl;
import util.ValidatorUtil;
import util.messagesSystem.ListMessagesSystemControl;
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
    private String novaSenha;
    private String login;
    private Collection<SelectItem> municipiosCombo = new ArrayList<>();

    public void init() {
        objMovimentado = new Pessoa();
        objMovimentado.setUsuario(new Usuario());
        objMovimentado.getUsuario().setTipoUsuario(TipoUsuario.CONSULTOR);
        objMovimentado.setCargo(new Cargo());
        objMovimentado.setEndereco(new Endereco());
        objMovimentado.getEndereco().setCidade(new Cidade());
        objMovimentado.getEndereco().getCidade().setEstado(new Estado());
        objMovimentado.setPessoaSalario(new PessoaSalario());
        cargoAtual = new Cargo();
        psSalarioObjMovimentado = new PessoaSalario();
        idEstado = 0;
        idCidade = 0;
        novaSenha = "";
        login = "";
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
            idEstado = objMovimentado.getEndereco().getCidade().getEstado().getId();
            idCidade = objMovimentado.getEndereco().getCidade().getId();
            setbotaoNavegacaoClicado(BOTAO_NAVEGACAO_CADASTRO_USUARIO);
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

    public String persistirPessoa() {
        setMessagesSystem(new ListMessagesSystemControl());
        validarDados();
        if (hasMessages())
            return null;

        PessoaDao pDao = getDao(PessoaDao.class);
        UsuarioDao userDao = getDao(UsuarioDao.class);
        PessoaSalarioDao psDao = getDao(PessoaSalarioDao.class);
        Usuario usuarioExistente = new Usuario();


        usuarioExistente = userDao.findByUsername(objMovimentado.getUsuario().getLogin());
        if (ValidatorUtil.isNotEmpty(usuarioExistente)) {
            getMessagesSystem().addMensagem(new MensagemSistema("Já existe um usuário cadastrado no sistema com esse login.", TipoMensagem.ERROR));
            objMovimentado.getUsuario().setLogin("");
            return null;
        }

        objMovimentado.getEndereco().getCidade().setId(idCidade);
        try {
            if (objMovimentado.getId() > 0) {
                if (ValidatorUtil.isNotEmpty(novaSenha)) {
                    objMovimentado.getUsuario().setSenha(PasswordControl.hashPassword(novaSenha));
                }

                pDao.alterarPessoaUsuario(objMovimentado);
                getMessagesSystem().addMensagem(new MensagemSistema("Operação realizada com sucesso!", TipoMensagem.SUCCESS));
                if (cargoAtual.getId() != objMovimentado.getCargo().getId() && ValidatorUtil.isNotEmpty(objMovimentado.getPessoaSalario())) {
                    psDao.remover(objMovimentado.getPessoaSalario());
                    getMessagesSystem().addMensagem(new MensagemSistema("O cargo do funcionário foi alterado nessa transação com sucesso! Seu salário precisará ser recalculado.", TipoMensagem.SUCCESS));
                }
                setbotaoNavegacaoClicado(BOTAO_NAVEGACAO_LISTA_FUNCIONARIOS);
                redirectPage(PAGINA_PESSOAS_SALARIOS);
            } else {
                objMovimentado.getUsuario().setLogin(login);
                objMovimentado.getUsuario().setSenha(novaSenha);
                pDao.criarPessoaUsuario(objMovimentado);
                getMessagesSystem().addMensagem(new MensagemSistema("Operação realizada com sucesso!", TipoMensagem.SUCCESS));
                setbotaoNavegacaoClicado(BOTAO_NAVEGACAO_LISTA_FUNCIONARIOS);
                redirectPage(PAGINA_PESSOAS_SALARIOS);
            }

        }catch (Exception error) {
            String mensagemErro = "Erro ao cadastrar/alterar funcionário, contate o administrador do sistema para maiores informações";
            getMessagesSystem().addMensagem(new MensagemSistema(mensagemErro, TipoMensagem.ERROR));
            throw new DaoException(mensagemErro, error);
        } finally {
            pDao.close();
            userDao.close();
            psDao.close();
        }
        return null;
    }

    private void validarDados() {
        List<MensagemSistema> mensagemSistemas = new ArrayList<>();

        if (ValidatorUtil.isNotEmpty(login)) {
            if (login.length() < 3) {
                getMessagesSystem().addMensagem(new MensagemSistema("Login precisa ter mais do que cinco (3) caracteres.", TipoMensagem.ERROR));
                novaSenha = "";
                return;
            }
        } else if (objMovimentado.getId() == 0) {
            if (ValidatorUtil.isEmpty(login)) {
                mensagemSistemas.add(new MensagemSistema("Usuario: " + MensagemSistema.CAMPO_OBRIGATORIO, TipoMensagem.ERROR));
            }
        }

        if (ValidatorUtil.isNotEmpty(novaSenha)) {
            if (novaSenha.length() < 5) {
                getMessagesSystem().addMensagem(new MensagemSistema("Senha precisa ter mais do que cinco (5) caracteres.", TipoMensagem.ERROR));
                novaSenha = "";
                return;
            }
        } else if (objMovimentado.getId() == 0) {
            if (ValidatorUtil.isEmpty(novaSenha)) {
                mensagemSistemas.add(new MensagemSistema("Senha: " + MensagemSistema.CAMPO_OBRIGATORIO, TipoMensagem.ERROR));
            }
        }

        if (ValidatorUtil.isEmpty(objMovimentado.getNome())) {
            mensagemSistemas.add(new MensagemSistema("Nome: " + MensagemSistema.CAMPO_OBRIGATORIO, TipoMensagem.ERROR));
        }

        if (ValidatorUtil.isEmpty(objMovimentado.getEmail())) {
            mensagemSistemas.add(new MensagemSistema("Email: " + MensagemSistema.CAMPO_OBRIGATORIO, TipoMensagem.ERROR));
        }

        if (ValidatorUtil.isEmpty(objMovimentado.getTelefone())) {
            mensagemSistemas.add(new MensagemSistema("Estado: " + MensagemSistema.CAMPO_OBRIGATORIO, TipoMensagem.ERROR));
        } else if (objMovimentado.getTelefone().length() < 13) {
            mensagemSistemas.add(new MensagemSistema("Telefone: Campo incompleto / Invalido.", TipoMensagem.ERROR));
        }

        if (ValidatorUtil.isEmpty(objMovimentado.getCargo())) {
            mensagemSistemas.add(new MensagemSistema("Cargo: " + MensagemSistema.CAMPO_OBRIGATORIO, TipoMensagem.ERROR));
        }

        if (ValidatorUtil.isEmpty(objMovimentado.getEndereco().getCep())) {
            mensagemSistemas.add(new MensagemSistema("CEP: " + MensagemSistema.CAMPO_OBRIGATORIO, TipoMensagem.ERROR));
        } else if (objMovimentado.getEndereco().getCep().length() < 9) {
            mensagemSistemas.add(new MensagemSistema("CEP: Campo incompleto / Invalido.", TipoMensagem.ERROR));
        }

        if (ValidatorUtil.isEmpty(objMovimentado.getEndereco().getDescricao())) {
            mensagemSistemas.add(new MensagemSistema("Endereço Descrição: " + MensagemSistema.CAMPO_OBRIGATORIO, TipoMensagem.ERROR));
        }

        if (ValidatorUtil.isEmpty(idCidade)) {
            mensagemSistemas.add(new MensagemSistema("Município: " + MensagemSistema.CAMPO_OBRIGATORIO, TipoMensagem.ERROR));
        }

        if (ValidatorUtil.isNotEmpty(mensagemSistemas)) {
            getMessagesSystem().addMensagem(mensagemSistemas);
        }
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

    public void selecionaCargo() {
        CargoDao cdDao = getDao(CargoDao.class);
        Cargo cargo = new Cargo();
        if (ValidatorUtil.isNotEmpty(objMovimentado.getCargo().getId())) {
            cargo = cdDao.findById(objMovimentado.getCargo().getId());
            objMovimentado.setCargo(cargo);
            if (cargo.getId() == Cargo.GERENTE) {
                objMovimentado.getUsuario().setTipoUsuario(TipoUsuario.ADMINISTRADOR);
            } else {
                objMovimentado.getUsuario().setTipoUsuario(TipoUsuario.CONSULTOR);
            }
        } else {
            objMovimentado.setCargo(cargo);
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

    public void carregarMunicipios() {
        if (ValidatorUtil.isEmpty(idEstado))
            idEstado = 0;
        carregarMunicipiosCombo(idEstado);
    }

    public void carregarMunicipiosCombo(int idEstado) {
        CidadeDao cdDao = getDao(CidadeDao.class);
        List<Cidade> cidades = new ArrayList<>();
        municipiosCombo = new ArrayList<>();
        try {
            cidades = cdDao.findAllByEstado(idEstado);
            if (ValidatorUtil.isNotEmpty(cidades)) {
                for (Cidade cidade : cidades) {
                    municipiosCombo.add(new SelectItem(cidade.getId(), cidade.getNome()));
                }
            }
        } finally {
            cdDao.close();
        }
    }

    public String cancelar() {
        try {
            init();
            setbotaoNavegacaoClicado(BOTAO_NAVEGACAO_LISTA_FUNCIONARIOS);
            redirectPage(PAGINA_PESSOAS_SALARIOS);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

}
