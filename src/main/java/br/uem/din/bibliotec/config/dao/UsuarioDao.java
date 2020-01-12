package br.uem.din.bibliotec.config.dao;

import br.uem.din.bibliotec.config.conexao.Conexao;
import br.uem.din.bibliotec.config.model.Usuario;
import br.uem.din.bibliotec.config.services.*;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UsuarioDao {
    private static Email email = new Email();
    private FormataData dtFormat =  new FormataData();
    private FormataDocs docsFormat = new FormataDocs();
    private CriptografiaMd5 cript = new CriptografiaMd5();
    private ValidaCpf validaDados = new ValidaCpf();
    private boolean validaCpf = false;
    private final String FALHA = "red";
    private final String SUCESSO = "green";

    //método que realiza a autenticação do usuário retornando a permissão correta do usuário
    public int buscaPermissao(Usuario user, String usuario, String senha) throws SQLException {
        user.setPermissao(0);
        user.setAtivo(0);
        user.setUsuario("");

        try {
            //realizando conexão com banco de dados
            Conexao con = new Conexao();
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            con.conexao.setAutoCommit(true);

            senha = cript.makeEncryptionMd5(senha.trim());

            //consultando se usuário está ativo e sua devida permissão
            st.execute( "SELECT \n" +
                            "    ativo, \n" +
                            "    permissao, \n" +
                            "    usuario, \n" +
                            "    COALESCE(bloq, 0) AS bloq,\n" +
                            "    COALESCE((TIMESTAMPDIFF(MINUTE, CURRENT_TIMESTAMP(), DATE_ADD(datahorabloq, INTERVAL 5 MINUTE)) + 1/*TRATANDO OS SEGUNDOS*/),0) AS minutos\n" +
                            "FROM\n" +
                            "    `bibliotec`.`usuarios`\n" +
                            "WHERE\n" +
                            "    (\n" +
                            "\t\temail LIKE '"+usuario.trim()+"' OR \n" +
                            "\t\tusuario LIKE '"+usuario.trim()+"' OR \n" +
                            "        cpf LIKE '"+usuario.trim()+"'\n" +
                            "    ) AND \n" +
                            "        senha = '"+senha.trim()+"';");

            //obtendo dados
            ResultSet rs = st.getResultSet();
            while (rs.next()) {
                user.setAtivo(rs.getInt("ativo"));
                user.setPermissao(rs.getInt("permissao"));
                user.setUsuario(rs.getString("usuario").trim());
                user.setBloq(rs.getInt("bloq"));
                user.setMinutos(rs.getInt("minutos"));
            }

            //tratando possíveis falhas de autenticações
            if(user.getBloq() == 1){
                user.setMsg_autenticacao("IP BLOQUEADO! Aguarde "+user.getMinutos()+"min.");
                return -4;
            }

            if (user.getAtivo() == 0 && !user.getUsuario().equals("".trim())) {
                user.setMsg_autenticacao("Usuário inativo.");
                return -1;
            }

            if (user.getPermissao() == 0 && !user.getUsuario().equals("".trim())) {
                user.setMsg_autenticacao("Usuário sem permissão.");
                return 0;
            }

            if (user.getPermissao() != 0) {
                HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
                session.setAttribute("usuario", user.getUsuario().trim());
                senha = "";
                usuario = "";
                user.setUsuario("");

                return 1;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            user.setMsg_autenticacao("FALHA: Contacte o Administrador.");
            return -3;
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e.getMessage());
            user.setMsg_autenticacao("FALHA: Contacte o Administrador.");
            return -3;
        }
        user.setMsg_autenticacao("Usuário/Senha inválido(s).");
        return -2;
    }

    public void bloquearUsuario(String usuario) throws NoSuchAlgorithmException {
        try{
            Conexao con = new Conexao();
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            con.conexao.setAutoCommit(true);

            st.executeUpdate(   "UPDATE `bibliotec`.`usuarios` \n" +
                                    "SET \n" +
                                    "    `bloq` = '1',\n" +
                                    "    `datahorabloq` = CURRENT_TIMESTAMP()\n" +
                                    "WHERE\n" +
                                    "    (`email` LIKE '"+usuario.trim()+"'   OR \n" +
                                    "     `usuario` LIKE '"+usuario.trim()+"' OR \n" +
                                    "     `cpf` LIKE '"+usuario.trim()+"'\n" +
                                    "    );");

            st.close();
            con.conexao.close();
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public int cadastrarUsuario(Usuario user) throws ParseException{
        //convertendo a data para padrão do banco de dados
        user.setDatanasc(dtFormat.formatadorDatasMySQL(user.getDatanasc()));

        try {
            //realizando conexão com banco de dados
            Conexao con = new Conexao();
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            con.conexao.setAutoCommit(true);

            //corrigindo CPF, RG e Cep
            user.setCep(user.getCep().replace("-", ""));
            user.setRg(user.getRg().replace("-", ""));
            user.setCpf(user.getCpf().replace(".", ""));
            user.setRg(user.getRg().replace(".", ""));
            user.setCpf(user.getCpf().replace("-", ""));


            //validando data de nascimento válida
            if(dtFormat.validaDatas(user.getDatanasc().trim()) > 0){
                return -2;
            }

            //validando se CPF fornecido é válido
            validaCpf = validaDados.validCpf(user.getCpf().trim());
            if(!validaCpf){
                return -1;
            }

            //realizando a inserção do novo cadastro no banco de dados
            st.executeUpdate("insert into `bibliotec`.`usuarios` (`email`, `usuario`, `senha`, `nome`, `rg`, `cpf`, `endereco`, `cep`, `cidade`, `estado`, `permissao`, `ativo`, `datacad`, `datanasc`, `jaativado`) values ('" + user.getEmail() + "', '" + user.getUsuario() + "', '" + cript.makeEncryptionMd5(user.getSenha().trim()) + "', '" + user.getNome() + "', '" + user.getRg() + "', '" + user.getCpf() + "', '" + user.getEndereco() + "', '" + user.getCep() + "', '" + user.getCidade() + "', '" + user.getEstado().toUpperCase() + "', '" + user.getPermissao() + "', '" + user.getAtivo() + "', current_date(), '" + user.getDatanasc() + "', '" + user.getJaativado() + "');");

            //enviando e-mail para confirma cadastramento de novo usuário.
            email.setAssunto("Confirmação de Cadastro - Biblioteca X");
            email.setEmailDestinatario(user.getEmail().trim());
            email.setMsg("Olá " + user.getNome().trim() + ", <br><br>Seu cadastro foi realizado com sucesso.<br><br>Username: <i>" + user.getUsuario().trim() + "</i>.<br>Senha: <i>" + user.getSenha().trim() + "</i>.");

            new Thread(enviarEmail).start();

            st.close();
            con.conexao.close();
        } catch (SQLException e) {
            return 0;
        } catch (NoSuchAlgorithmException e) {
            return 0;
        }
        return 1;
    }

    public List<Usuario> consultaUsuariosAtivos(Usuario usuario) throws SQLException {
        List<Usuario> usuarios = new ArrayList<Usuario>();

        try{
            //realiza conexão com banco de dados
            Conexao con = new Conexao();
            con.conexao.setAutoCommit(true);
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            //busca todas as informações de acordo com os dados fornecidos
            st.execute("SELECT * FROM `bibliotec`.`usuarios` u WHERE u.ativo = '1';");
            ResultSet rs = st.getResultSet();

            //obtendo os valores carregados no result set e carregado no arrayList
            while (rs.next()) {
                Usuario usuarioTemp = new Usuario(
                        rs.getString("nome"),
                        rs.getInt("codusuario"),
                        docsFormat.formataCpf(rs.getString("cpf")),
                        rs.getString("email"),
                        docsFormat.formataRg(rs.getString("rg")),
                        dtFormat.formatadorDatasBrasil(rs.getString("datanasc"))
                );
                usuarios.add(usuarioTemp);
            }

            st.close();
            rs.close();
            con.conexao.close();
        }catch (SQLException e){
            System.out.println(e.getMessage());
            usuario.setMsg_autenticacao("FALHA: Ocorreu uma falha ao consultar usuários, contacte o administrador.");
            usuario.setColor_msg(FALHA);

        }


        return usuarios;
    }

    public List<Usuario> consultarUsuarioBalconista(Usuario user, int ativo) throws SQLException {
        //declaração do arrayList para auxiliar na impressão da dataTable do consultar usuarios
        List<Usuario> usuarios = new ArrayList<>();

        //realiza conexão com banco de dados
        Conexao con = new Conexao();
        con.conexao.setAutoCommit(true);
        Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

        //busca todas as informações de acordo com os dados fornecidos
        if (ativo == 0) {
            st.execute("SELECT u.email, u.usuario, u.nome, u.rg, u.cpf, u.endereco, u.cep, u.cidade, u.estado, CASE WHEN u.permissao = 1 THEN 'Bibliotecário' WHEN u.permissao = 2 THEN 'Balconista' WHEN u.permissao = 3 THEN 'Aluno' WHEN u.permissao = 0 THEN 'Sem Permissões' END AS perfil, CASE WHEN u.ativo = 1 THEN 'Ativo' ELSE 'Inativo' END AS status, u.codusuario, u.datacad, u.dataalt, u.datanasc FROM `bibliotec`.`usuarios` u WHERE u.nome LIKE '%" + user.getBusca().trim() + "%' or u.email LIKE '%" + user.getBusca().trim() + "%' or u.cpf LIKE '" + user.getBusca().trim() + "' or u.usuario LIKE '" + user.getBusca().trim() + "';");
        } else {
            st.execute("SELECT u.email, u.usuario, u.nome, u.rg, u.cpf, u.endereco, u.cep, u.cidade, u.estado, CASE WHEN u.permissao = 1 THEN 'Bibliotecário' WHEN u.permissao = 2 THEN 'Balconista' WHEN u.permissao = 3 THEN 'Aluno' WHEN u.permissao = 0 THEN 'Sem Permissões' END AS perfil, CASE WHEN u.ativo = 1 THEN 'Ativo' ELSE 'Inativo' END AS status, u.codusuario, u.datacad, u.dataalt, u.datanasc FROM `bibliotec`.`usuarios` u WHERE u.ativo = '1' order by 3;");
        }

        ResultSet rs = st.getResultSet();

        //obtendo os valores carregados no result set e carregado no arrayList
        while (rs.next()) {
            Usuario usuario_temp = new Usuario(
                    rs.getString("email"),
                    rs.getString("usuario"),
                    "",
                    rs.getString("nome"),
                    docsFormat.formataRg(rs.getString("rg")),
                    docsFormat.formataCpf(rs.getString("cpf")),
                    rs.getString("endereco"),
                    rs.getString("cep"),
                    rs.getString("cidade"),
                    rs.getString("estado"),
                    "",
                    "",
                    0,
                    0,
                    rs.getString("status"),
                    rs.getString("perfil"),
                    rs.getInt("codusuario"),
                    dtFormat.formatadorDatasBrasil(rs.getString("datacad")),
                    dtFormat.formatadorDatasBrasil(rs.getString("dataalt")),
                    dtFormat.formatadorDatasBrasil(rs.getString("datanasc"))
            );

            usuarios.add(usuario_temp);
        }

        //fechando as conexões em aberto para evitar locks infinitos no banco de dados
        st.close();
        rs.close();
        con.conexao.close();

        return usuarios;
    }

    public int deletarUsuario(Usuario user) {
        int codusuario = 0;

        try {
            //abre conexão com banco de dados
            Conexao con = new Conexao();
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            con.conexao.setAutoCommit(true);

            //não pode deletar usuário se ele possuir emprestimos em vigor
            if(verificarExistenciaEmps(user) > 0){
                return -2;
            }

            st.execute("select nome, coalesce(codusuario,0) as codusuario from `bibliotec`.`usuarios` where codusuario = " + user.getCodusuario() + ";");
            ResultSet rs = st.getResultSet();

            //carrega os dados do resultSet dentro das variáveis locais
            while (rs.next()) {
                user.setNome(rs.getString("nome"));
                codusuario = rs.getInt("codusuario");
            }

            if (codusuario == 0) {
                return -1;
            }

            if(obterCodUsuario() == user.getCodusuario()){
                return -3;
            }

            //executa a EXCLUSÃO LÓGICA do usuário no banco de dados, ou seja, ativo recebe 0 e permissao recebe 0 (isso impossibilitará o usuário de efetuar login)
            st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET `ativo` = '0', `permissao` = '0', dataalt = current_date() WHERE (`codusuario` =" + user.getCodusuario() + ");");

            //fecha conexões para evitar lock nas tabelas do banco de dados
            st.close();
            rs.close();
            con.conexao.close();

            return 1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }


    public int obterCodUsuario(){
        int codUser = 0;

        try {
            //abre conexão com banco de dados
            Conexao con = new Conexao();
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            con.conexao.setAutoCommit(true);
            ResultSet rs;

            //obtendo codusuario do usuario logado no sistema
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
            String login = (String) session.getAttribute("usuario");

            st.execute(     "select\n" +
                            "\tu.codusuario\n" +
                            "from\n" +
                            "\tusuarios u\n" +
                            "where\n" +
                            "\tu.ativo = '1' and\n" +
                            "\tu.permissao <> '0' and\n" +
                            "    u.usuario = '" + login.trim() + "';");

            rs = st.getResultSet();

            while(rs.next()){
                codUser = rs.getInt("codusuario");
            }

            //fecha conexões para evitar lock nas tabelas do banco de dados
            st.close();
            rs.close();
            con.conexao.close();
        } catch (SQLException e) {
            return codUser;
        }
        return codUser;
    }

    public int verificarExistenciaEmps(Usuario user){
        int qtde_emp = 0;
        try {
            //realiza conexão com banco de dados
            Conexao con = new Conexao();
            con.conexao.setAutoCommit(true);
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = null;

            st.execute( "select\n" +
                            "\tcount(coalesce(e.codemprestimo,0)) as qtde_emp\n" +
                            "from\n" +
                            "\temprestimo e\n" +
                            "where\n" +
                            "\te.ativo = '1' and\n" +
                            "\te.codusuario = '" + user.getCodusuario() + "';");

            rs = st.getResultSet();

            while(rs.next()){
                qtde_emp = rs.getInt("qtde_emp");
            }

            //fechando as conexões
            st.close();
            rs.close();
            con.conexao.close();
        } catch (SQLException e) {
            return qtde_emp;
        }
        return qtde_emp;
    }

    public int editarUsuario(Usuario user) throws ParseException{
        //declaração de varáveis locais que nos ajudará nas tratativas de erros
        Integer codusuario = 0;

        try {
            //realiza conexão com banco de dados
            Conexao con = new Conexao();
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            con.conexao.setAutoCommit(true);

            //valida se o código do usuário foi fornecido de forma incorreta, ou seja, usuário inexistente na base de dados
            if (obterCodUsuario() == 0) {
                return -1;
            }

            //verificando se o usuario logado esta tentando remover suas proprias permissões ou tentanto auto-deletar-se
            if(obterCodUsuario() == user.getCodusuario() && (user.getAtivo() == 0 || user.getPermissao() == 0)){
                return -3;
            }

            //Convertendo data e vendo se data informada é válida
            user.setDatanasc(dtFormat.formatadorDatasMySQL(user.getDatanasc()));
            if(dtFormat.validaDatas(user.getDatanasc().trim()) > 0){
                return -4;
            }

            //removendo caracteres especiais
            user.setCep(user.getCep().replace("-", ""));
            user.setRg(user.getRg().replace(".", ""));
            user.setRg(user.getRg().replace("-", ""));
            user.setCpf(user.getCpf().replace(".", ""));
            user.setCpf(user.getCpf().replace("-", ""));

            //verificando se CPF fornecido é válido
            validaCpf = validaDados.validCpf(user.getCpf().trim());
            if(!validaCpf){
                return -2;
            }

            //realizando alteração
            if(!user.getSenha().equals("")){
                st.executeUpdate("UPDATE `bibliotec`.`usuarios` \n" +
                                    "SET \n" +
                                    "    `email` = '"+user.getEmail().trim()+"',\n" +
                                    "    `usuario` = '"+user.getUsuario().trim()+"',\n" +
                                    "    `senha` = '"+user.getSenha().trim()+"',\n" +
                                    "    `nome` = '"+user.getNome().trim()+"',\n" +
                                    "    `rg` = '"+user.getRg().trim()+"',\n" +
                                    "    `cpf` = '"+user.getCpf().trim()+"',\n" +
                                    "    `endereco` = '"+user.getEndereco().trim()+"',\n" +
                                    "    `cep` = '"+user.getCep().trim()+"',\n" +
                                    "    `cidade` = '"+user.getCidade().trim()+"',\n" +
                                    "    `estado` = '"+user.getEstado().trim()+"',\n" +
                                    "    `permissao` = '"+user.getPermissao()+"',\n" +
                                    "    `ativo` = '"+user.getAtivo()+"',\n" +
                                    "    `datanasc` = '"+user.getDatanasc().trim()+"',\n" +
                                    "    `dataalt` = current_date()\n" +
                                    "WHERE\n" +
                                    "    (`codusuario` = '"+user.getCodusuario()+"');");
            }

            if(user.getAtivo() == 1 && user.getJaativado() == 0){
                st.executeUpdate("UPDATE `bibliotec`.`usuarios` \n" +
                                    "SET \n" +
                                    "    `jaativado` = '1'\n" +
                                    "WHERE\n" +
                                    "    (`codusuario` = '"+user.getCodusuario()+"');");

                //enviando e-mail comunicando ativação do cadastro do usuário(este e-mail só será enviado no máxima uma vez)
                email.setAssunto("Confirmação de Cadastro - Biblioteca X");
                email.setEmailDestinatario(user.getEmail().trim());
                email.setMsg("Olá " + user.getNome().trim() + ", <br><br>Seu cadastro foi ativado com sucesso.<br>Agora você tem acesso ao nosso acervo de livros e demais funcionalidades, aproveite!");

                new Thread(enviarEmail).start();
            }

            //fechando as conexões para evitar lock
            st.close();
            con.conexao.close();
            return 1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public List<Usuario> meusDados() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        String login = (String) session.getAttribute("usuario");

        List<Usuario> dados = new ArrayList<Usuario>();

        try {
            //realiza conexão com banco de dados
            Conexao con = new Conexao();
            con.conexao.setAutoCommit(true);
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = null;

            //busca todas as informações de acordo com os dados de acesso do usuário
            st.execute("SELECT * FROM `bibliotec`.`usuarios` WHERE usuario = '" + login + "';");
            rs = st.getResultSet();

            while (rs.next()) {
                Usuario user = new Usuario(
                        rs.getString("email"),
                        rs.getString("usuario"),
                        rs.getString("senha"),
                        rs.getString("nome"),
                        docsFormat.formataRg(rs.getString("rg")),
                        docsFormat.formataCpf(rs.getString("cpf")),
                        rs.getString("endereco"),
                        rs.getString("cep"),
                        rs.getString("cidade"),
                        rs.getString("estado"),
                        "",
                        "",
                        0,
                        0,
                        "",
                        "",
                        rs.getInt("codusuario"),
                        "",
                        "",
                        dtFormat.formatadorDatasBrasil(rs.getString("datanasc")));
                dados.add(user);
            }
            //fechando as conexões em aberto para evitar locks infinitos no banco de dados
            st.close();
            rs.close();
            con.conexao.close();
            return dados;
        } catch (SQLException e) {
            System.out.println("Dados informados são inválidos!");
        }
        return dados;
    }

    public String homePage() {
        try {
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
            String login = (String) session.getAttribute("usuario");

            //realiza conexão com banco de dados
            Conexao con = new Conexao();
            con.conexao.setAutoCommit(true);
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            //busca todas as informações de acordo com os dados de acesso do usuário
            st.execute("SELECT permissao FROM `bibliotec`.`usuarios` WHERE usuario = '" + login + "';");
            ResultSet rs = st.getResultSet();

            int minhaPermissao = 0;
            while (rs.next()) {
                minhaPermissao = rs.getInt("permissao");
            }

            //fechando as conexões em aberto para evitar locks infinitos no banco de dados
            st.close();
            rs.close();
            con.conexao.close();

            //redirecionando para HomePage correta
            if (minhaPermissao == 1) {
                return "/acessoBibliotecario.xhtml?faces-redirect=true";
            }

            if (minhaPermissao == 2) {
                return "/acessoBalconista.xhtml?faces-redirect=true";
            }

            if (minhaPermissao == 3) {
                return "/acessoAluno.xhtml?faces-redirect=true";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "/gestaoBibliotecas?faces-redirect=true";
        }
        return "/gestaoBibliotecas?faces-redirect=true";
    }

    public int atualizaMeusDados(Usuario user) throws ParseException {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        String login = (String) session.getAttribute("usuario");

        try {
            //realiza conexão com banco de dados
            Conexao con = new Conexao();
            con.conexao.setAutoCommit(true);
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            //formatando o RG
            user.setRg(user.getRg().replace(".", ""));
            user.setRg(user.getRg().replace("-", ""));

            //formatando o CPF
            user.setCpf(user.getCpf().replace(".", ""));
            user.setCpf(user.getCpf().replace("-", ""));

            //formatando o CEP
            user.setCep(user.getCep().replace("-", ""));

            //realizando alterações
            st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET `email` = '"+user.getEmail().trim()+"', `usuario` = '"+user.getUsuario().trim()+"', `senha` = '"+cript.makeEncryptionMd5(user.getSenha().trim())+"', `nome` = '"+user.getNome().trim()+"', `rg` = '"+user.getRg().trim()+"', `cpf` = '"+user.getCpf().trim()+"', `endereco` = '"+user.getEndereco()+"', `cep` = '"+user.getCep().trim()+"', `cidade` = '"+user.getCidade().trim()+"', `estado` = '"+user.getEstado().trim()+"', `datanasc` = '"+dtFormat.formatadorDatasMySQL(user.getDatanasc().trim())+"' WHERE (`codusuario` = '"+user.getCodusuario()+"');");

            //enviando e-mail com novo username e nova senha
            email.setAssunto("Atualização Cadastral - Biblioteca X");
            email.setEmailDestinatario(user.getEmail().trim());
            email.setMsg("Olá " + user.getNome().trim() + ", <br><br>Seus dados de acesso foram alterados com sucesso.<br><br>Username: <i>" + user.getUsuario().trim() + "</i>.<br>Senha: <i>" + user.getSenha().trim() + "</i>.");

            new Thread(enviarEmail).start();

            //fechando as conexões em aberto para evitar locks infinitos no banco de dados
            st.close();
            con.conexao.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return -1;
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e.getMessage());
            return -1;
        }
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return 1;
    }

    public int redefSenha(Usuario user) throws NoSuchAlgorithmException {
        //removendo caracteres da inputMask
        user.setCpf(user.getCpf().replace(".", ""));
        user.setCpf(user.getCpf().replace("-", ""));

        if(user.getSenha().trim().equals("")){
            //gerando nova senha randomicamente
            UUID uuid = UUID.randomUUID();
            String myRandom = uuid.toString();
            user.setSenha(myRandom.substring(0,20));
        }

        //validando se CPF é válido
        validaCpf = validaDados.validCpf(user.getCpf().trim());
        if(!validaCpf){
            return -1;
        }

        try {
            //realiza conexão com banco de dados
            Conexao con = new Conexao();
            con.conexao.setAutoCommit(true);
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = null;

            st.execute( "SELECT \n" +
                            "    COALESCE(u.nome, '') as nome, COALESCE(u.ativo,0) as ativo, COALESCE(u.permissao,0) as permissao\n" +
                            "FROM\n" +
                            "    usuarios u\n" +
                            "WHERE\n" +
                            "    u.cpf = '" + user.getCpf().trim() + "'      AND\n" +
                            "\tu.email = '" + user.getEmail().trim() + "' AND\n" +
                            "    u.datanasc = '" + dtFormat.formatadorDatasMySQL(user.getDatanasc().trim()) + "';");

            rs = st.getResultSet();

            while(rs.next()){
                user.setNome(rs.getString("nome").trim());
                user.setAtivo(rs.getInt("ativo"));
                user.setPermissao(rs.getInt("permissao"));
            }

            if(user.getNome().equals("".trim())){
                return -4;
            }

            if(user.getAtivo() == 0){
                return -2;
            }

            if(user.getPermissao() == 0){
                return -3;
            }

            st.executeUpdate("update\n" +
                                "\tusuarios u\n" +
                                "set\n" +
                                "\tu.senha = '" + cript.makeEncryptionMd5(user.getSenha().trim()).trim() + "'\n" +
                                "where\n" +
                                "    u.cpf = '" + user.getCpf().trim() + "'      AND\n" +
                                "\tu.email = '" + user.getEmail().trim() + "' AND\n" +
                                "    u.datanasc = '" + dtFormat.formatadorDatasMySQL(user.getDatanasc().trim()) + "';");

            //Enviando e-mail contendo nova senha
            email.setAssunto("Redefinição de Senha - Biblioteca X");
            email.setEmailDestinatario(user.getEmail().trim());
            email.setMsg("Olá " + user.getNome() + ", <br><br>Sua senha foi redefinida com sucesso!<br><br>Nova Senha: <b>" + user.getSenha().trim() + "</b>");

            new Thread(enviarEmail).start();

            st.close();
            rs.close();
            con.conexao.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
        return 1;
    }

    public int carregarDadosUsuario(Usuario user){
        try {
            //realiza conexão com banco de dados
            Conexao con = new Conexao();
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            con.conexao.setAutoCommit(true);

            st.execute("SELECT U.* FROM `bibliotec`.`usuarios` U WHERE U.CODUSUARIO = '" + user.getCodusuario() + "';");
            ResultSet rs = st.getResultSet();

            while(rs.next()){
                user.setNome(rs.getString("nome"));
                user.setUsuario(rs.getString("usuario"));
                user.setRg(rs.getString("rg"));
                user.setCpf(rs.getString("cpf"));
                user.setEndereco(rs.getString("endereco"));
                user.setCep(rs.getString("cep"));
                user.setSenha(rs.getString("senha").trim());
                user.setCidade(rs.getString("cidade"));
                user.setEstado(rs.getString("estado"));
                user.setEmail(rs.getString("email"));
                user.setDatanasc(dtFormat.formatadorDatasBrasil(rs.getString("datanasc")));
                user.setPermissao(rs.getInt("permissao"));
                user.setAtivo(rs.getInt("ativo"));
                user.setJaativado(rs.getInt("jaativado"));
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            return 0;
        }
        return 1;
    }

    private static final Runnable enviarEmail = new Runnable() {
        @Override
        public void run() {
            email.enviarGmail();
        }
    };
}
