package br.uem.din.bibliotec.config.dao;

import br.uem.din.bibliotec.config.conexao.Conexao;
import br.uem.din.bibliotec.config.model.Livro;
import br.uem.din.bibliotec.config.model.Reserva;
import br.uem.din.bibliotec.config.model.Usuario;
import br.uem.din.bibliotec.config.services.FormataData;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ReservaDao {
    private Livro livroObserver = new Livro();
    private Usuario usuarioObserver = new Usuario(livroObserver);
    private FormataData dtFormat = new FormataData();
    private final String SUCESSO = "green";
    private final String FALHA = "red";

    public void cadastrarReserva(Reserva reserva) throws SQLException {
        LivroDao livroDao = new LivroDao();
        Livro livroObserver = new Livro();
        Usuario usuarioObserver = new Usuario(livroObserver);
        try {
            Conexao con = new Conexao();
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            con.conexao.setAutoCommit(true);

            st.executeUpdate("INSERT INTO `bibliotec`.`reserva` (`codlivro`, `codusuario`, `datacad`, `dataalt`,`ativo`) VALUES ('" + reserva.getCodLivro() + "', '" + reserva.getCodUsuario() + "', current_Date, NULL,'1');");

			//tratando caso o livro esteja reservado(OBSERVER)
            if(livroJaReservadoQualquerUsuario(reserva) == 1 && livroDao.verificaDispLivro(reserva.getCodLivro()) == 1){
                reserva.setCodUsuario(obterDadosReserva(reserva, false));
                atualizarStatusReserva(reserva);
                obterDadosAlunoReservaObserver(reserva, livroObserver);
            }

            st.close();
            con.conexao.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            reserva.setColorMsgRetorno(FALHA);
            reserva.setMsgRetorno("FALHA: Ocorreu uma falha ao cadastrar a reserva, contacte o administrador.");
        }
    }

    public List<Reserva> consultarReserva(Reserva reserva) {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        String usuarioLogado = (String) session.getAttribute("usuario");
        List<Reserva> minhasReservas = new ArrayList<>();

        try{
            Conexao con = new Conexao();
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            con.conexao.setAutoCommit(true);
            ResultSet rs;

            st.execute( "select\n" +
                            "\tr.codreserva,\n" +
                            "    r.datacad,\n" +
                            "    r.datares,\n" +
                            "    l.titulo,\n" +
                            "    l.autor,\n" +
                            "    l.editora\n" +
                            "from\n" +
                            "\treserva  r\n" +
                            "inner join\n" +
                            "\tusuarios u on u.codusuario = r.codusuario\n" +
                            "inner join\n" +
                            "\tlivro    l on l.codlivro = r.codlivro\t\n" +
                            "where\n" +
                            "\tr.ativo = '1' and\n" +
                            "    u.usuario = '"+usuarioLogado+"'\n" +
                            "order by\n" +
                            "\tl.titulo;");

            rs = st.getResultSet();

            while(rs.next()){
                Reserva reservaTemp = new Reserva(rs.getInt("codreserva"),
                                                  dtFormat.formatadorDatasBrasil(rs.getString("datacad")),
                                                  dtFormat.formatadorDatasBrasil(rs.getString("datares")),
                                                  rs.getString("titulo"),
                                                  rs.getString("autor"),
                                                  rs.getString("editora"));

                minhasReservas.add(reservaTemp);
            }

            st.close();
            rs.close();
            con.conexao.close();
        }catch (SQLException e){
            System.out.println(e.getMessage());
            reserva.setColorMsgRetorno(FALHA);
            reserva.setMsgRetorno("FALHA: Ocorreu uma falha ao consultar as reservas, contacte o administrador.");
        }
        return minhasReservas;
    }

    public void cancelarReserva(Reserva reserva) {
        LivroDao livroDao = new LivroDao();
        try{
            Conexao con = new Conexao();
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            con.conexao.setAutoCommit(true);

            //inativando reserva
            st.executeUpdate("UPDATE `bibliotec`.`reserva` SET `dataalt` = current_date(), `ativo` = '0' WHERE (`codreserva` = '"+reserva.getCodReserva()+"');");

            //tratando caso o livro tenha outra reserva, usuário deve ser informado
            st.execute( "select\n" +
                            "\tr.codlivro\n" +
                            "from\n" +
                            "\treserva r\n" +
                            "where\n" +
                            "\tr.codreserva = '" + reserva.getCodReserva() + "';");

            ResultSet rs = st.getResultSet();

            while(rs.next()){
                reserva.setCodLivro(rs.getInt("codlivro"));
            }

            //tratando caso o livro esteja reservado(OBSERVER)
            if(livroJaReservadoQualquerUsuario(reserva) > 0 && livroDao.verificaDispLivro(reserva.getCodLivro()) == 1){
                reserva.setCodUsuario(obterDadosReserva(reserva, false));
                atualizarStatusReserva(reserva);
                obterDadosAlunoReservaObserver(reserva, livroObserver);
            }

            st.close();
            rs.close();
            con.conexao.close();
        }catch (SQLException e){
            System.out.println(e.getMessage());
            reserva.setColorMsgRetorno(FALHA);
            reserva.setMsgRetorno("FALHA: Ocorreu uma falha ao deletar a reserva, contacte o administrador.");
        }
    }

    public int livroJaReservadoUsuarioLogado(Reserva reserva) {
        int jaReservado = 0;

        try {
            Conexao con = new Conexao();
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            con.conexao.setAutoCommit(true);
            ResultSet rs;

            st.execute(   "select\n" +
                              "\tcoalesce(count(r.codreserva),0) as ja_reservado\n" +
                              "from\n" +
                              "\treserva r \n" +
                              "where\n" +
                              "\tr.codlivro = '"+reserva.getCodLivro()+"' and\n" +
                              "    r.codusuario = '"+reserva.getCodUsuario()+"' and\n" +
                              "    r.ativo = '1';");

            rs = st.getResultSet();

            while (rs.next()){
                jaReservado = rs.getInt("ja_reservado");
            }

            st.close();
            rs.close();
            con.conexao.close();
        }catch (SQLException e) {
            System.out.println(e.getMessage());
            reserva.setMsgRetorno("FALHA: Ocorreu uma falha ao consultar se livro já está reservado para você, contacte o administrador.");
            reserva.setColorMsgRetorno(FALHA);
        }
        return jaReservado;
    }

    public int livroJaReservadoQualquerUsuario(Reserva reserva) {
        int jaReservado = 0;

        try {
            Conexao con = new Conexao();
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            con.conexao.setAutoCommit(true);
            ResultSet rs = null;

            st.execute(     "select\n" +
                                "\tcoalesce(count(r.codreserva),0) as ja_reservado\n" +
                                "from\n" +
                                "\treserva r \n" +
                                "where\n" +
                                "\tr.codlivro = '"+reserva.getCodLivro()+"' and\n" +
                                "    r.ativo = '1';");

            rs = st.getResultSet();

            while (rs.next()){
                jaReservado = rs.getInt("ja_reservado");
            }

            st.close();
            rs.close();
            con.conexao.close();
        }catch (SQLException e) {
            System.out.println(e.getMessage());
            reserva.setMsgRetorno("FALHA: Ocorreu uma falha ao consultar se livro já está reservado para você, contacte o administrador.");
            reserva.setColorMsgRetorno(FALHA);
        }
        return jaReservado;
    }

    public int obterDadosReserva(Reserva reserva, boolean emprestandoLivro) {
        try {
            Conexao con = new Conexao();
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            con.conexao.setAutoCommit(true);
            ResultSet rs;

            if(emprestandoLivro){
                st.execute( "SELECT \n" +
                                "    COALESCE(MIN(r.codreserva), 0) AS codReserva,\n" +
                                "    r.codusuario AS codUsuario\n" +
                                "FROM\n" +
                                "    reserva r\n" +
                                "WHERE\n" +
                                "    r.codlivro = '"+reserva.getCodLivro()+"'\n" +
                                "        AND r.ativo = '1'\n" +
                                "        AND NOT r.datares IS NULL;"); //considera reserva onde o livro ja esteja disponível para retirada, ou seja, datares <> null
            }else{
                st.execute( "SELECT \n" +
                                "    COALESCE(MIN(r.codreserva), 0) AS codReserva,\n" +
                                "    r.codusuario                   AS codUsuario\t\n" +
                                "FROM\n" +
                                "    reserva r\n" +
                                "WHERE\n" +
                                "    r.codlivro = '"+reserva.getCodLivro()+"' AND \n" +
                                "    r.ativo = '1' and r.datares is null;"); //considera reservas onde o livro não está disponivel para retirada, datares == null
            }

            rs = st.getResultSet();

            while (rs.next()){
                reserva.setCodReserva(rs.getInt("codReserva"));
                reserva.setCodUsuario(rs.getInt("codUsuario"));
            }

            st.close();
            rs.close();
            con.conexao.close();
        }catch (SQLException e) {
            System.out.println(e.getMessage());
            reserva.setMsgRetorno("FALHA: Ocorreu uma falha ao consultar o usuário da reserva, contacte o administrador.");
            reserva.setColorMsgRetorno(FALHA);
        }
        return reserva.getCodUsuario();
    }

    public void obterDadosAlunoReservaObserver(Reserva reserva, Livro livroObserver) {
        try {
            Conexao con = new Conexao();
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            con.conexao.setAutoCommit(true);
            ResultSet rs;

            st.execute("SELECT \n" +
                            "    u.nome, \n" +
                            "    u.email, \n" +
                            "    l.titulo, \n" +
                            "    r.datares," +
                            "    l.disponibilidade\n" +
                            "FROM\n" +
                            "    reserva  r\n" +
                            "INNER JOIN\n" +
                            "    livro    l ON l.codlivro = r.codlivro\n" +
                            "INNER JOIN\n" +
                            "    usuarios u ON u.codusuario = r.codusuario\n" +
                            "WHERE\n" +
                            "    r.codreserva = '"+reserva.getCodReserva()+"' AND \n" +
                            "    r.ativo = '1';");

            rs = st.getResultSet();

            while(rs.next()){
                livroObserver.setNomeUsuarioRes(rs.getString("nome"));
                livroObserver.setEmailUsuarioRes(rs.getString("email"));
                livroObserver.setTitulo(rs.getString("titulo"));
                livroObserver.setDatares(dtFormat.formatadorDatasBrasil(rs.getString("datares")));
                livroObserver.setNovaDisp(rs.getString("disponibilidade").trim());
            }

            st.close();
            rs.close();
            con.conexao.close();
        }catch (SQLException e) {
            System.out.println(e.getMessage());
            reserva.setMsgRetorno("FALHA: Ocorreu uma falha ao consultar o usuário da reserva, contacte o administrador.");
            reserva.setColorMsgRetorno(FALHA);
        }
    }

    public void atualizarStatusReserva(Reserva reserva) {
        try {
            Conexao con = new Conexao();
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            con.conexao.setAutoCommit(true);

            st.executeUpdate("UPDATE \n" +
                                "\t`bibliotec`.`reserva` \n" +
                                "SET \n" +
                                "    `datares` = DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY)\n" +
                                "WHERE\n" +
                                "    codreserva = '"+reserva.getCodReserva()+"' AND \n" +
                                "    ativo = '1';");

            st.close();
            con.conexao.close();
        }catch (SQLException e) {
            System.out.println(e.getMessage());
            reserva.setMsgRetorno("FALHA: Ocorreu uma falha ao atualizar a próxima reserva, contacte o administrador.");
            reserva.setColorMsgRetorno(FALHA);
        }
    }
}
