package br.uem.din.bibliotec.config.dao;

import br.uem.din.bibliotec.config.conexao.Conexao;
import br.uem.din.bibliotec.config.model.Multa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MultaDao {
    public void cadastrarMulta(Multa multa){
        try{
            Conexao con = new Conexao();
            con.conexao.setAutoCommit(true);
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            st.executeUpdate("INSERT INTO `bibliotec`.`multa` (`codlivro`, `codusuario`, `codemprestimo`, `codcotacao`, `datacad`, `diasatraso`, `valor`, `ativo`) VALUES ('"+multa.getCodLivro()+"', '"+multa.getCodUsuario()+"', '"+multa.getCodEmprestimo()+"', '"+multa.getCodCotacao()+"', current_date(), '"+multa.getDiasAtraso()+"', '"+multa.getValor()+"', '1');");

            st.close();
            con.conexao.close();
        }catch (SQLException e){
            System.out.println(e.getMessage());
            multa.setColorMsgRetorno("red");
            multa.setMsgRetorno("FALHA: Ocorreu um erro inserir multa, contacte o administrador.");
        }
    }

    public double valorTotalMultas(int codUsuario){
        double valorTotal = 0.0;

        try{
            Conexao con = new Conexao();
            con.conexao.setAutoCommit(true);
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs;

            st.execute( "select\n" +
                            "\tcast(coalesce(sum(m.valor),0.0) as decimal(15,2)) as vlrMulta\n" +
                            "from\n" +
                            "\tmulta m\n" +
                            "where\n" +
                            "\tm.ativo = '1' and\n" +
                            "    m.codusuario = '"+codUsuario+"';");

            rs = st.getResultSet();

            while(rs.next()){
                valorTotal = rs.getDouble("vlrMulta");
            }

            st.close();
            rs.close();
            con.conexao.close();
        }catch (SQLException e){

        }
        return valorTotal;
    }

    public List<Multa> consultarMultasCpf(Multa multa) {
        List<Multa> multas =  new ArrayList<>();

        try{
            Conexao con = new Conexao();
            con.conexao.setAutoCommit(true);
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs;

            st.execute( "SELECT \n" +
                            "    m.codmulta,\n" +
                            "    u.nome,\n" +
                            "    l.titulo,\n" +
                            "    l.autor,\n" +
                            "    l.editora,\n" +
                            "    c.valor AS vlrCotacao,\n" +
                            "    m.diasatraso,\n" +
                            "    coalesce(m.valor,0.0) AS vlrMulta,\n" +
                            "    (\n" +
                            "\t\tSELECT \n" +
                            "            coalesce(SUM(m1.valor),0.0) as valor\n" +
                            "        FROM\n" +
                            "            multa m1\n" +
                            "        INNER JOIN\n" +
                            "            usuarios u1 ON u1.codusuario = m1.codusuario\n" +
                            "        WHERE\n" +
                            "            m1.ativo = '1' AND \n" +
                            "            u1.cpf LIKE '%"+multa.getCpfUsuario()+"%'\n" +
                            "\t) AS totMulta\n" +
                            "FROM\n" +
                            "    multa m\n" +
                            "        INNER JOIN\n" +
                            "    livro l ON l.codlivro = m.codlivro\n" +
                            "        INNER JOIN\n" +
                            "    usuarios u ON u.codusuario = m.codusuario\n" +
                            "        INNER JOIN\n" +
                            "    cotacao c ON c.codcotacao = m.codcotacao\n" +
                            "WHERE\n" +
                            "    m.ativo = '1' AND " +
                            "    u.cpf LIKE '%"+multa.getCpfUsuario()+"%';");

            rs = st.getResultSet();

            while(rs.next()){
                Multa multaTemp = new Multa(rs.getInt("codmulta"),
                                            rs.getInt("diasatraso"),
                                            rs.getDouble("vlrMulta"),
                                            rs.getString("nome"),
                                            rs.getString("titulo"),
                                            rs.getString("autor"),
                                            rs.getString("editora"),
                                            rs.getDouble("vlrCotacao"),
                                            rs.getString("totMulta")
                );
                multa.setTotMulta(multaTemp.getTotMulta());
                multas.add(multaTemp);
            }

            st.close();
            rs.close();
            con.conexao.close();
        }catch (SQLException e){
            System.out.println(e.getMessage());
            multa.setColorMsgRetorno("red");
            multa.setMsgRetorno("FALHA: Ocorreu um erro ao consultar multas, contacte o administrador.");
        }
        return multas;
    }

    public void baixarMultas(Multa multa) {
        try{
            Conexao con = new Conexao();
            con.conexao.setAutoCommit(true);
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            st.executeUpdate(   "UPDATE multa m \n" +
                                    "SET \n" +
                                    "    m.datapag = CURRENT_DATE(),\n" +
                                    "    m.ativo = '0'\n" +
                                    "WHERE\n" +
                                    "    m.codmulta IN ("+multa.getIdMultasSeparadosVirgula()+") and\n" +
                                    "    m.ativo = '1';");

            st.close();
            con.conexao.close();
        }catch (SQLException e){
            System.out.println(e.getMessage());
            multa.setColorMsgRetorno("red");
            multa.setMsgRetorno("FALHA: Ocorreu um erro ao baixar as multas, contacte o administrador.");
        }
    }

    public List<Multa> consultarMinhasMultasCpf(Multa multa) {
        List<Multa> multas =  new ArrayList<>();

        try{
            Conexao con = new Conexao();
            con.conexao.setAutoCommit(true);
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs;

            st.execute( "SELECT \n" +
                    "    m.codmulta,\n" +
                    "    u.nome,\n" +
                    "    l.titulo,\n" +
                    "    l.autor,\n" +
                    "    l.editora,\n" +
                    "    c.valor AS vlrCotacao,\n" +
                    "    m.diasatraso,\n" +
                    "    m.valor AS vlrMulta,\n" +
                    "    (\n" +
                    "\t\tSELECT \n" +
                    "            SUM(m1.valor)\n" +
                    "        FROM\n" +
                    "            multa m1\n" +
                    "        INNER JOIN\n" +
                    "            usuarios u1 ON u1.codusuario = m1.codusuario\n" +
                    "        WHERE\n" +
                    "            m1.ativo = '1' AND \n" +
                    "            u1.cpf LIKE '%"+multa.getCpfUsuario()+"%'\n" +
                    "\t) AS totMulta\n" +
                    "FROM\n" +
                    "    multa m\n" +
                    "        INNER JOIN\n" +
                    "    livro l ON l.codlivro = m.codlivro\n" +
                    "        INNER JOIN\n" +
                    "    usuarios u ON u.codusuario = m.codusuario\n" +
                    "        INNER JOIN\n" +
                    "    cotacao c ON c.codcotacao = m.codcotacao\n" +
                    "WHERE\n" +
                    "    m.ativo = '1' AND " +
                    "    u.codusuario LIKE '%"+multa.getCodUsuario()+"%';");

            rs = st.getResultSet();

            while(rs.next()){
                Multa multaTemp = new Multa(rs.getInt("codmulta"),
                        rs.getInt("diasatraso"),
                        rs.getDouble("vlrMulta"),
                        rs.getString("nome"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("editora"),
                        rs.getDouble("vlrCotacao"),
                        rs.getString("totMulta")
                );
                multa.setTotMulta(multaTemp.getTotMulta());
                multas.add(multaTemp);
            }

            st.close();
            rs.close();
            con.conexao.close();
        }catch (SQLException e){
            System.out.println(e.getMessage());
            multa.setColorMsgRetorno("red");
            multa.setMsgRetorno("FALHA: Ocorreu um erro ao consultar multas, contacte o administrador.");
        }
        return multas;
    }
}
