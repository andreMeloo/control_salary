package util.messagesSystem;

import controller.AbstractControllerBean;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import util.ValidatorUtil;

@Getter
@Setter
@AllArgsConstructor
public class MensagemSistema extends AbstractControllerBean {

    public static final String BUSCA_SEM_RESULTADOS = "Não foram encontrados resultados com os parâmetros informados.";
    public static final String FALHA_AUTENTICACAO_LOGIN_INCORRETO = "Falha na autenticação: Login incorreto.";
    public static final String FALHA_AUTENTICACAO_SENHA_INCORRETA = "Falha na autenticação: Senha incorreta.";
    public static final String TRANSACAO_EM_ANDAMENTO = "Já existe uma transação em andamento sendo executada, aguarde a finalização para tentar novamente.";
    public static final String CAMPO_OBRIGATORIO = "Campo obrigatório não informado.";
    public static final String CALCULO_REALIZADO = "Salários Calculados com sucesso!";

    private String mensagem;
    private TipoMensagem tipoMensagem;

    public boolean isErro() {
        return tipoMensagem == TipoMensagem.ERROR;
    }

    public boolean isSucesso() {
        return tipoMensagem == TipoMensagem.SUCCESS;
    }

    public boolean isEmpty() {
        return ValidatorUtil.isEmpty(mensagem);
    }

}
