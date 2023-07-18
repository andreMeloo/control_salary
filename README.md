# Sistema Salary v1.0

O Sistema Salary v1.0 é uma aplicação que permite o cadastro e a alteração de funcionários e seus cargos, além de listar os mesmos e mostrar seus salários contabilizados. Caso um funcionário não tenha salários contabilizados, é possível realizar essa contabilização.

## Get Started

Para executar a aplicação, siga as etapas abaixo:

### Requisitos de Sistema

Certifique-se de ter instalado o seguinte software em seu ambiente de desenvolvimento:

- Java 17 Development Kit (JDK)
- TomCat 10
- IDE de sua escolha (recomendado: IntelliJ)
- Dbeaver (para gerenciamento de banco de dados)
- JasperSoft Studio (para criação de relatórios)

### Configuração do Banco de Dados

O Sistema Salary v1.0 utiliza o banco de dados PostgreSQL. Certifique-se de ter uma instância do PostgreSQL instalada e configurada corretamente.

1. Crie um banco de dados no PostgreSQL para a aplicação Salary (por exemplo, "salary_db").
2. Na pasta SCRIPTS_SQL contém toda a estrutura do banco e seus INSERTS para testes.
3. Em caso de ter dificuldade de criar o banco, pode-se editar o arquivo "persistence.xml" e altrar a forma de inicialização do banco mudando o atributo "value" de validate para update.
4. Edite o arquivo de configuração da aplicação (geralmente chamado de "persistence.xml") para fornecer as informações de conexão corretas (como URL do banco de dados, nome de usuário e senha).

### Executando a Aplicação

1. Importe o projeto em sua IDE preferida.
2. Certifique-se de ter configurado corretamente o servidor TomCat na IDE.
3. Compile e execute o projeto no servidor TomCat.
4. Acesse a aplicação no navegador utilizando o URL fornecido pelo servidor TomCat.

Agora você está pronto para usar o Sistema Salary v1.0 e aproveitar seus recursos de cadastro, alteração e listagem de funcionários e seus cargos, além de visualizar relatórios de salários.

Divirta-se usando o Sistema Salary v1.0!

## Tecnologias/Bibliotecas Utilizadas

- Java 17
- JSF (Biblioteca Mojarra 4.0)
- JPA
- Javascript
- HTML
- CSS
- JasperReports
- SQL + Postgres
- PrimeFaces

**Servidor**: TomCat 10

## Ferramentas/IDE Utilizadas

- IntelliJ
- Dbeaver
- JasperSoft Studio
