package model;


public enum TipoUsuario {
    ADMINISTRADOR(1, "Administrador"),
    CONSULTOR(2, "Consultor");

    private final int valor;
    private final String descricao;

    TipoUsuario(int valor, String descricao) {
        this.valor = valor;
        this.descricao = descricao;
    }

    public int getValor() {
        return valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public static TipoUsuario fromValor(int valor) {
        for (TipoUsuario tipoUsuario : TipoUsuario.values()) {
            if (tipoUsuario.getValor() == valor) {
                return tipoUsuario;
            }
        }
        throw new IllegalArgumentException("Valor inválido para TipoUsuario: " + valor);
    }
}
