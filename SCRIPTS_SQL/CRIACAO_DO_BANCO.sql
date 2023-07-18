CREATE TABLE public.cargo (
	id serial4 NOT NULL,
	nome varchar(255) NULL,
	salario numeric(38, 2) NULL,
	CONSTRAINT cargo_pkey PRIMARY KEY (id)
);

CREATE TABLE public.estado (
	id int4 NOT NULL DEFAULT nextval('estados_id_seq'::regclass),
	nome varchar(255) NULL,
	uf bpchar(2) NULL,
	CONSTRAINT estados_pkey PRIMARY KEY (id)
);

CREATE TABLE public.cidade (
	id int4 NOT NULL DEFAULT nextval('cidades_id_seq'::regclass),
	nome varchar(255) NULL,
	id_estado int4 NULL,
	CONSTRAINT cidades_pkey PRIMARY KEY (id),
	CONSTRAINT cidades_id_estado_fkey FOREIGN KEY (id_estado) REFERENCES public.estado(id)
);

CREATE TABLE public.endereco (
	id int4 NOT NULL DEFAULT nextval('enderecos_id_seq'::regclass),
	descricao varchar(255) NULL,
	cep varchar(255) NULL,
	pais varchar(255) NULL,
	id_cidade int4 NULL,
	CONSTRAINT enderecos_pkey PRIMARY KEY (id),
	CONSTRAINT enderecos_id_cidade_fkey FOREIGN KEY (id_cidade) REFERENCES public.cidade(id)
);

CREATE TABLE public.pessoa (
	id serial4 NOT NULL,
	nome varchar(255) NULL,
	data_nascimento date NULL,
	email varchar(255) NULL,
	telefone varchar(255) NULL,
	id_cargo int4 NULL,
	id_endereco int4 NULL,
	CONSTRAINT pessoa_pkey PRIMARY KEY (id),
	CONSTRAINT uk_7c0e7b1vxce8872uxqgbfqpl3 UNIQUE (id_endereco),
	CONSTRAINT fkcx83noq326h8bblpfqvlds6yf FOREIGN KEY (id_endereco) REFERENCES public.endereco(id),
	CONSTRAINT pessoa_id_cargo_fkey FOREIGN KEY (id_cargo) REFERENCES public.cargo(id)
);

CREATE TABLE public.usuario (
	id serial4 NOT NULL,
	login varchar(255) NULL,
	senha varchar(255) NULL,
	tipo int4 NULL,
	id_pessoa int4 NULL,
	CONSTRAINT uk_qxu4oqcfwibvtudkocbtqgkua UNIQUE (id_pessoa),
	CONSTRAINT usuario_pkey PRIMARY KEY (id),
	CONSTRAINT fk9b5ep6wuhibyj4jvphdn1kx4w FOREIGN KEY (id) REFERENCES public.pessoa(id),
	CONSTRAINT fkncvcoai97k0bryv90rc1j5pmp FOREIGN KEY (id_pessoa) REFERENCES public.pessoa(id)
);

CREATE TABLE public.pessoasalario (
	id serial4 NOT NULL,
	nome varchar(255) NULL,
	salario numeric(38, 2) NULL,
	id_pessoa serial4 NOT NULL,
	CONSTRAINT pessoasalario_pkey PRIMARY KEY (id),
	CONSTRAINT uk_8f3b0s8pex6t48tsuk9r2b2di UNIQUE (id_pessoa),
	CONSTRAINT fk799p3q1n39murbqa5lxsxxj2n FOREIGN KEY (id_pessoa) REFERENCES public.pessoa(id)
);




