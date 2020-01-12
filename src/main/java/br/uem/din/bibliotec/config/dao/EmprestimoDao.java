package br.uem.din.bibliotec.config.dao;

import br.uem.din.bibliotec.config.conexao.Conexao;
import br.uem.din.bibliotec.config.model.Emprestimo;
import br.uem.din.bibliotec.config.model.Livro;
import br.uem.din.bibliotec.config.model.Reserva;
import br.uem.din.bibliotec.config.model.Usuario;
import br.uem.din.bibliotec.config.services.Email;
import br.uem.din.bibliotec.config.services.FormataData;
import br.uem.din.bibliotec.config.services.FormataDocs;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EmprestimoDao {
    private static Email email = new Email();
    private FormataData dtFormat = new FormataData();
    private FormataDocs docsFormat = new FormataDocs();
    private final String FALHA = "red";

    public void cadastrarEmprestimo(Emprestimo emprestimo) throws SQLException{
        try{
            Conexao con = new Conexao();
            con.conexao.setAutoCommit(true);
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            st.executeUpdate("INSERT INTO `bibliotec`.`emprestimo` (`codusuario`, `codlivro`, `dataemp`, `datadev`, `ativo`) VALUES ('"+emprestimo.getCodUsuario()+"', '"+emprestimo.getCodLivro()+"', current_date(), DATE_ADD(current_date(), INTERVAL 7 DAY) , '1');");

            st.executeUpdate("UPDATE `bibliotec`.`livro` SET `disponibilidade` = '0' WHERE (`codlivro` = '"+emprestimo.getCodLivro()+"');");

            st.execute( "select\n" +
                            "\tMAX(e.codemprestimo) as ultimoEmprestimo,\n" +
                            "\tu.nome,\n" +
                            "    u.email,\n" +
                            "    l.titulo,\n" +
                            "    e.dataemp,\n" +
                            "    e.datadev\n" +
                            "from\n" +
                            "\temprestimo e\n" +
                            "inner join\n" +
                            "\tusuarios   u on u.codusuario = e.codusuario\n" +
                            "inner join\n" +
                            "\tlivro      l on l.codlivro = e.codlivro\n" +
                            "where\n" +
                            "\te.ativo = '1' and\n" +
                            "    e.codusuario = '"+emprestimo.getCodUsuario()+"' and\n" +
                            "    e.codlivro = '"+emprestimo.getCodLivro()+"';");

            ResultSet rs = st.getResultSet();

            while (rs.next()){
                emprestimo.setCodEmprestimo(rs.getInt("ultimoEmprestimo"));
                emprestimo.setNomeUsuario(rs.getString("nome"));
                emprestimo.setEmailUsuario(rs.getString("email"));
                emprestimo.setTituloLivro(rs.getString("titulo"));
                emprestimo.setDataEmp(dtFormat.formatadorDatasBrasil(rs.getString("dataemp")));
                emprestimo.setDataDev(dtFormat.formatadorDatasBrasil(rs.getString("datadev")));
            }

            st.close();
            rs.close();
            con.conexao.close();

            email.setAssunto("Empréstimo de Livro - Biblioteca X");
            email.setEmailDestinatario(emprestimo.getEmailUsuario());
            email.setMsg("Olá "+emprestimo.getNomeUsuario()+", <br><br>O empréstimo do livro <b>'"+emprestimo.getTituloLivro()+"'</b> foi realizado com sucesso! <br><br> Data do Empréstimo: <b>"+emprestimo.getDataEmp()+"</b>. <br> Data da Devolução...: <b>"+emprestimo.getDataDev()+"</b>. <br><br>Fique atento à data de devolução.");

            new Thread(enviarEmail).start();
        }catch (SQLException e){
            System.out.println(e.getMessage());
            emprestimo.setMsgRetorno("FALHA: Ocorreu uma falha ao cadastrar o empréstimo, contacte o administrador.");
            emprestimo.setColorMsgRetorno(FALHA);
        }
    }

    public List<Emprestimo> meusEmprestimos(Emprestimo emprestimo) {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        String usuarioLogado = (String)session.getAttribute("usuario");
        List<Emprestimo> meusEmprestimos = new ArrayList<Emprestimo>();

        try {
            Conexao con = new Conexao();
            con.conexao.setAutoCommit(true);
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = null;

            st.execute( "SELECT \n" +
                            "    l.titulo, \n" +
                            "    l.autor, \n" +
                            "    l.editora, \n" +
                            "    e.dataemp, \n" +
                            "    e.datadev,\n" +
                            "    CASE\n" +
                            "\t\tWHEN e.datadev < current_date() then 1\n" +
                            "        ELSE 0\n" +
                            "    END as atrasado\n" +
                            "FROM\n" +
                            "    emprestimo e\n" +
                            "LEFT JOIN\n" +
                            "    livro l ON l.codlivro = e.codlivro\n" +
                            "LEFT JOIN\n" +
                            "    usuarios u ON u.codusuario = e.codusuario\n" +
                            "WHERE\n" +
                            "    u.usuario LIKE '"+usuarioLogado+"' AND \n" +
                            "    e.ativo = '1';");

            rs = st.getResultSet();

            while(rs.next()){
                Emprestimo emprestimoTemp = new Emprestimo(dtFormat.formatadorDatasBrasil(rs.getString("dataemp")),
                                                           dtFormat.formatadorDatasBrasil(rs.getString("datadev")),
                                                           rs.getString("titulo"),
                                                           rs.getString("autor"),
                                                           rs.getString("editora"),
                                                           rs.getInt("atrasado"));
                meusEmprestimos.add(emprestimoTemp);
            }

            st.close();
            rs.close();
            con.conexao.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            emprestimo.setMsgRetorno("FALHA: Não foi possível consultar seus empréstimos, contacte o administrador.");
            emprestimo.setColorMsgRetorno(FALHA);
        }
        return meusEmprestimos;
    }

    public List<Emprestimo> consultarEmprestimos(Emprestimo emprestimo){
        List<Emprestimo> relacaoEmprestimos = new ArrayList<Emprestimo>();

        try {
            Conexao con = new Conexao();
            con.conexao.setAutoCommit(true);
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs;

            emprestimo.setTituloLivro(emprestimo.getNomeUsuario());

            st.execute( "select\n" +
                            "\t  e.codemprestimo,\n" +
                            "    u.codusuario,\n" +
                            "    u.nome,\n" +
                            "    u.email,\n" +
                            "    u.rg,\n" +
                            "    u.cpf,\n" +
                            "    l.codlivro,\n" +
                            "    l.titulo,\n" +
                            "    l.autor,\n" +
                            "    l.editora,\n" +
                            "    l.anolancamento,\n" +
                            "    e.dataemp,\n" +
                            "    e.datadev,\n" +
                            "    e.dataalt," +
                            "    e.dataentr,   \n" +
                            "  case\n" +
                            "\t\twhen e.ativo = 1 then 'Em vigor'\n" +
                            "    when e.ativo = 0 then 'Finalizado'\n" +
                            "\tend as status\n" +
                            "from\n" +
                            "\temprestimo e\n" +
                            "left join\n" +
                            "\tlivro l on l.codlivro = e.codlivro\n" +
                            "left join\n" +
                            "\tusuarios u on u.codusuario = e.codusuario\n" +
                            "where\n" +
                            "\tu.nome like '%"+emprestimo.getNomeUsuario()+"%' or\n" +
                            "\tl.titulo like '%"+emprestimo.getTituloLivro()+"%' order by 1;");

            rs = st.getResultSet();

            while(rs.next()){
                Emprestimo emprestimoTemp = new Emprestimo( rs.getInt("codemprestimo"),
                                                            rs.getInt("codusuario"),
                                                            rs.getInt("codlivro"),
                                                            dtFormat.formatadorDatasBrasil(rs.getString("dataemp")),
                                                            dtFormat.formatadorDatasBrasil(rs.getString("datadev")),
                                                            dtFormat.formatadorDatasBrasil(rs.getString("dataalt")),
                                                            rs.getString("nome"),
                                                            rs.getString("email"),
                                                            rs.getString("titulo"),
                                                            rs.getString("autor"),
                                                            rs.getString("editora"),
                                                            rs.getString("anolancamento"),
                                                            rs.getString("status"),
                                                            docsFormat.formataRg(rs.getString("rg")),
                                                            docsFormat.formataCpf(rs.getString("cpf")),
                                                            dtFormat.formatadorDatasBrasil(rs.getString("dataentr"))
                );

                relacaoEmprestimos.add(emprestimoTemp);
            }

            st.close();
            rs.close();
            con.conexao.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            emprestimo.setMsgRetorno("FALHA: Não foi possível consultar a relação de empréstimos, contacte o administrador.");
            emprestimo.setColorMsgRetorno(FALHA);
        }

        return relacaoEmprestimos;
    }

    public List<Emprestimo> consultarEmprestimosAtivos(Emprestimo emprestimo){
        List<Emprestimo> relacaoEmprestimos = new ArrayList<Emprestimo>();

        try {
            Conexao con = new Conexao();
            con.conexao.setAutoCommit(true);
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs;

            emprestimo.setTituloLivro(emprestimo.getNomeUsuario());

            st.execute( "select\n" +
                            "\t  e.codemprestimo,\n" +
                            "    u.codusuario,\n" +
                            "    u.nome,\n" +
                            "    u.email,\n" +
                            "    u.rg,\n" +
                            "    u.cpf,\n" +
                            "    l.codlivro,\n" +
                            "    l.titulo,\n" +
                            "    l.autor,\n" +
                            "    l.editora,\n" +
                            "    l.anolancamento,\n" +
                            "    e.dataemp,\n" +
                            "    e.datadev,\n" +
                            "    e.dataalt," +
                            "    e.dataentr,\n" +
                            "  case\n" +
                            "\t\twhen e.ativo = 1 then 'Em vigor'\n" +
                            "    when e.ativo = 0 then 'Finalizado'\n" +
                            "\tend as status\n" +
                            "from\n" +
                            "\temprestimo e\n" +
                            "left join\n" +
                            "\tlivro l on l.codlivro = e.codlivro\n" +
                            "left join\n" +
                            "\tusuarios u on u.codusuario = e.codusuario\n" +
                            "where\n" +
                             "\t(u.nome like '%"+emprestimo.getNomeUsuario()+"%' or\n" +
                            "\tl.titulo like '%"+emprestimo.getTituloLivro()+"%') and e.ativo = '1' order by 1;");

            rs = st.getResultSet();

            while(rs.next()){
                Emprestimo emprestimoTemp = new Emprestimo( rs.getInt("codemprestimo"),
                        rs.getInt("codusuario"),
                        rs.getInt("codlivro"),
                        dtFormat.formatadorDatasBrasil(rs.getString("dataemp")),
                        dtFormat.formatadorDatasBrasil(rs.getString("datadev")),
                        dtFormat.formatadorDatasBrasil(rs.getString("dataalt")),
                        rs.getString("nome"),
                        rs.getString("email"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("editora"),
                        rs.getString("anolancamento"),
                        rs.getString("status"),
                        docsFormat.formataRg(rs.getString("rg")),
                        docsFormat.formataCpf(rs.getString("cpf")),
                        dtFormat.formatadorDatasBrasil(rs.getString("dataentr"))
                );

                relacaoEmprestimos.add(emprestimoTemp);
            }

            st.close();
            rs.close();
            con.conexao.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            emprestimo.setMsgRetorno("FALHA: Não foi possível consultar a relação de empréstimos, contacte o administrador.");
            emprestimo.setColorMsgRetorno(FALHA);
        }

        return relacaoEmprestimos;
    }

    public int consultarMeusEmpAtrasados(Emprestimo emprestimo){
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        String usuarioLogado = (String)session.getAttribute("usuario");
        int qtdeAtrasado = 0;

        try{
            Conexao con = new Conexao();
            con.conexao.setAutoCommit(true);
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = null;

            st.execute( "select\n" +
                            "\tcount(coalesce(e.codemprestimo,0)) as qtde\n" +
                            "from\n" +
                            "\temprestimo e\n" +
                            "inner join\n" +
                            "\tusuarios   u on u.codusuario = e.codusuario\t\n" +
                            "where\n" +
                            "\te.ativo = '1' and\n" +
                            "    e.datadev < current_date() and\n" +
                            "    u.usuario = '" + usuarioLogado.trim() + "';");

            rs = st.getResultSet();

            while(rs.next()){
                qtdeAtrasado = rs.getInt("qtde");
            }

            st.close();
            rs.close();
            con.conexao.close();
        }catch (SQLException e) {
            System.out.println(e.getMessage());
            emprestimo.setMsgRetorno("FALHA: Não foi possível consultar se há empréstimos atrasados em seu nome, contacte o administrador.");
            emprestimo.setColorMsgRetorno(FALHA);
        }
        return qtdeAtrasado;
    }

    public int consultarEmpAtrasados(int codUsuario){
        int emprestimosAtrasados = 0;

        try{
            Conexao con = new Conexao();
            con.conexao.setAutoCommit(true);
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs;

            st.execute( "select\n" +
                            "\tcount(coalesce(e.codemprestimo,0)) as qtde\n" +
                            "from\n" +
                            "\temprestimo e\n" +
                            "inner join\n" +
                            "\tusuarios   u on u.codusuario = e.codusuario\t\n" +
                            "where\n" +
                            "\te.ativo = '1' and\n" +
                            "    e.datadev < current_date() and\n" +
                            "    u.codusuario = '" + codUsuario + "';");

            rs = st.getResultSet();

            while(rs.next()){
                emprestimosAtrasados = rs.getInt("qtde");
            }

            st.close();
            rs.close();
            con.conexao.close();
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return emprestimosAtrasados;
    }

    public int livroJaEmprestado(Reserva reserva){
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        String usuarioLogado = (String)session.getAttribute("usuario");
        int jaEmprestado = 0;

        try{
            Conexao con = new Conexao();
            con.conexao.setAutoCommit(true);
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs;

            st.execute( "select\n" +
                            "\tcoalesce(e.codemprestimo,0) jaEmprestado\n" +
                            "from\n" +
                            "\temprestimo e\n" +
                            "inner join\n" +
                            "\tusuarios   u on u.codusuario = e.codusuario\n" +
                            "where\n" +
                            "\te.codlivro = '"+reserva.getCodLivro()+"' and\n" +
                            "\te.ativo = '1' and\n" +
                            "    u.usuario = '"+usuarioLogado+"';");

            rs = st.getResultSet();

            while(rs.next()){
                jaEmprestado = rs.getInt("jaEmprestado");
            }

            st.close();
            rs.close();
            con.conexao.close();
        }catch(SQLException e){
            System.out.println(e.getMessage());
            reserva.setMsgRetorno("FALHA: Este livro já está reservado para você.");
            reserva.setColorMsgRetorno(FALHA);
        }
        return jaEmprestado;
    }

    public void renovarEmprestimo(Emprestimo emprestimo) {
        try{
            Conexao con = new Conexao();
            con.conexao.setAutoCommit(true);
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            st.executeUpdate("UPDATE `bibliotec`.`emprestimo` SET `datadev` = '"+emprestimo.getDataDev()+"' WHERE (`codemprestimo` = '"+emprestimo.getCodEmprestimo()+"') and (`ativo` = '1');\n");

            st.close();
            con.conexao.close();
        }catch (SQLException e){
            System.out.println(e.getMessage());
            emprestimo.setMsgRetorno("FALHA: Ocorreu uma falha ao renovar o empréstimo, contacte o administrador.");
            emprestimo.setColorMsgRetorno(FALHA);
        }
    }

    public void devolverLivroDeEmprestimo(Emprestimo emprestimo){
        Livro livroObserver = new Livro();
        Usuario usuarioObserver = new Usuario(livroObserver);
        Reserva reserva = new Reserva();
        ReservaDao reservaDao = new ReservaDao();

        try{
            Conexao con = new Conexao();
            con.conexao.setAutoCommit(true);
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            st.executeUpdate("UPDATE \n" +
                                "\temprestimo e \n" +
                                "SET \n" +
                                "    e.dataentr = CURRENT_DATE(),\n" +
                                "    e.dataalt = CURRENT_DATE(),\n" +
                                "    ativo = '0'\n" +
                                "WHERE\n" +
                                "    e.codemprestimo = '"+emprestimo.getCodEmprestimo()+"' AND \n" +
                                "    e.ativo = '1';");

            st.executeUpdate("UPDATE \n" +
                                "\tlivro l \n" +
                                "SET \n" +
                                "    l.disponibilidade = '1'\n" +
                                "WHERE\n" +
                                "    l.codlivro = '"+emprestimo.getCodLivro()+"' and\n" +
                                "    l.ativo = '1';");

            st.execute("SELECT \n" +
                            "    u.nome,\n" +
                            "    u.email,\n" +
                            "    l.titulo,\n" +
                            "    e.dataemp,\n" +
                            "    e.dataentr\n" +
                            "FROM\n" +
                            "    emprestimo e\n" +
                            "INNER JOIN\n" +
                            "    usuarios u ON u.codusuario = e.codusuario\n" +
                            "INNER JOIN\n" +
                            "    livro l ON l.codlivro = e.codlivro\n" +
                            "WHERE\n" +
                            "    e.ativo = '0' and \n" +
                            "    e.codemprestimo = '"+emprestimo.getCodEmprestimo()+"';");

            ResultSet rs = st.getResultSet();

            while (rs.next()){
                emprestimo.setNomeUsuario(rs.getString("nome"));
                emprestimo.setEmailUsuario(rs.getString("email"));
                emprestimo.setTituloLivro(rs.getString("titulo"));
                emprestimo.setDataEmp(dtFormat.formatadorDatasBrasil(rs.getString("dataemp")));
                emprestimo.setDataEntrega(dtFormat.formatadorDatasBrasil(rs.getString("dataentr")));
            }

            email.setAssunto("Devolução de Livro - Biblioteca X");
            email.setEmailDestinatario(emprestimo.getEmailUsuario());
            email.setMsg("Olá "+emprestimo.getNomeUsuario()+", <br><br>O livro <b>'"+emprestimo.getTituloLivro()+"'</b> foi devolvido com sucesso! <br><br> Data do Empréstimo: <b>"+emprestimo.getDataEmp()+"</b>. <br> Data da Entrega...: <b>"+emprestimo.getDataEntrega()+"</b>.");

            new Thread(enviarEmail).start();

            //tratando caso o livro esteja reservado(OBSERVER)
            reserva.setCodLivro(emprestimo.getCodLivro());
            if(reservaDao.livroJaReservadoQualquerUsuario(reserva) > 0){
                reserva.setCodUsuario(reservaDao.obterDadosReserva(reserva, false));
                reservaDao.atualizarStatusReserva(reserva);
                reservaDao.obterDadosAlunoReservaObserver(reserva, livroObserver);
            }

            st.close();
            rs.close();
            con.conexao.close();
        }catch (SQLException e){
            System.out.println(e.getMessage());
            emprestimo.setMsgRetorno("FALHA: Ocorreu uma falha ao dar baixa em livro.");
            emprestimo.setColorMsgRetorno(FALHA);
        }
    }

    public int quantidadeDiasAtrasados(Emprestimo emprestimo){
        int qtdeDiasAtrasados = 0;
        try{
            Conexao con = new Conexao();
            con.conexao.setAutoCommit(true);
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs;

            st.execute( "SELECT \n" +
                            "    CASE\n" +
                            "\t\tWHEN COALESCE(DATEDIFF (CURRENT_DATE(), e.datadev),0) > 0 THEN COALESCE(DATEDIFF (CURRENT_DATE(), e.datadev),0)\n" +
                            "        ELSE 0\n" +
                            "    END AS qtdeDiasAtrasados,\n" +
                            "    e.codlivro,\n" +
                            "    e.codusuario\n" +
                            "FROM\n" +
                            "    emprestimo e\n" +
                            "        INNER JOIN\n" +
                            "    usuarios u ON u.codusuario = e.codusuario\n" +
                            "WHERE\n" +
                            "    e.ativo = '1' AND\n" +
                            "    e.codemprestimo = '"+emprestimo.getCodEmprestimo()+"';");

            rs = st.getResultSet();

            while (rs.next()){
                qtdeDiasAtrasados = rs.getInt("qtdeDiasAtrasados");
                emprestimo.setCodLivro(rs.getInt("codlivro"));
                emprestimo.setCodUsuario(rs.getInt("codusuario"));
            }

            st.close();
            rs.close();
            con.conexao.close();
        }catch (SQLException e){
            System.out.println(e.getMessage());
            emprestimo.setMsgRetorno("FALHA: Ocorreu uma falha ao consultar dias de atraso do empréstimo, contacte o administrador.");
            emprestimo.setColorMsgRetorno(FALHA);
        }
        return qtdeDiasAtrasados;
    }

    private static final Runnable enviarEmail = new Runnable() {
        @Override
        public void run() {
            email.enviarGmail();
        }
    };

    public int validaNovaDataDevolucao(Emprestimo emprestimo) {
        int validacao = 0;

        try{
            Conexao con = new Conexao();
            con.conexao.setAutoCommit(true);
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs;

            st.execute( "SELECT \n" +
                            "    COALESCE(e.codemprestimo, 0) AS codEmprestimo\n" +
                            "FROM\n" +
                            "    emprestimo e\n" +
                            "WHERE\n" +
                            "    e.codemprestimo = '"+emprestimo.getCodEmprestimo()+"' AND \n" +
                            "    e.ativo = 1 AND \n" +
                            "    e.datadev < '"+emprestimo.getDataDev()+"';");

            rs = st.getResultSet();

            while (rs.next()){
                validacao = rs.getInt("codEmprestimo");
            }

            st.close();
            rs.close();
            con.conexao.close();
        }catch (SQLException e){
            System.out.println(e.getMessage());
            emprestimo.setMsgRetorno("FALHA: Ocorreu uma falha ao validar nova data de devolução, consulte o administrador.");
            emprestimo.setColorMsgRetorno(FALHA);
        }
        return validacao;
    }
}