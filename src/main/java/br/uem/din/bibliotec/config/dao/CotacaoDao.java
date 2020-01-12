package br.uem.din.bibliotec.config.dao;

import br.uem.din.bibliotec.config.conexao.Conexao;
import br.uem.din.bibliotec.config.model.Cotacao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CotacaoDao {
    public void cadastrarNovaCotacao(Cotacao cotacao) {
        try{
            Conexao con = new Conexao();
            con.conexao.setAutoCommit(true);
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            st.executeUpdate("UPDATE cotacao SET ativo = '0', datafim = CURRENT_DATE() WHERE ativo = '1' and codcotacao > 0;");

            st.executeUpdate("INSERT INTO `bibliotec`.`cotacao` (`valor`, `dataini`, `ativo`) VALUES ('"+cotacao.getValor()+"', current_Date(), '1');");

            st.close();
            con.conexao.close();
        }catch (SQLException e){
            System.out.println(e.getMessage());
            cotacao.setMsgRetorno("FALHA: Ocorreu uma falha ao cadastrar nova cotação, contacte o administrador.");
            cotacao.setColorMsgRetorno("red");
        }
    }

    public double consultarCotacaoEmVigor(Cotacao cotacao) {
        try{
            Conexao con = new Conexao();
            con.conexao.setAutoCommit(true);
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs;

            st.execute( "SELECT \n" +
                            "    MAX(codcotacao) \t as cotVigor, \n" +
                            "    COALESCE(valor, 0.0) as valor\n" +
                            "FROM\n" +
                            "    cotacao\n" +
                            "WHERE\n" +
                            "    ativo = '1';");

            rs = st.getResultSet();

            while(rs.next()){
                cotacao.setCodcotacao(rs.getInt("cotVigor"));
                cotacao.setValor(rs.getDouble("valor"));
            }

            st.close();
            rs.close();
            con.conexao.close();
        }catch (SQLException e){
            System.out.println(e.getMessage());
            cotacao.setMsgRetorno("FALHA: Ocorreu uma falha ao consultar cotação em vigor, contacte o administrador.");
            cotacao.setColorMsgRetorno("red");
        }
        return cotacao.getValor();
    }
}
