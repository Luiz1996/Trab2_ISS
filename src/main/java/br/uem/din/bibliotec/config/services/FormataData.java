package br.uem.din.bibliotec.config.services;

import br.uem.din.bibliotec.config.conexao.Conexao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class FormataData {

    //como o dado é informado como DD/MM/AAAA precisamos convertê-la para o formato do banco de dados
    public String formatadorDatasMySQL(String data) {
        String[] dataSeparada = data.split("/");
        LocalDate data_formatada = LocalDate.of(Integer.parseInt(dataSeparada[2]), Integer.parseInt(dataSeparada[1]), Integer.parseInt(dataSeparada[0]));

        return data_formatada.toString().trim();
    }

    //como o dado é informado como AAAA-MM-DD precisamos convertê-la para o formato do brasileiro ao imprimir no front-end ao usuário
    public String formatadorDatasBrasil(String data) {
        if (data == null || data.trim().length() == 0) {
            return "";
        } else {
            String[] dataSeparada = data.split("-");
            String dataPadraoBrasil = dataSeparada[2] + "/" + dataSeparada[1] + "/" + dataSeparada[0];
            return dataPadraoBrasil.trim();
        }
    }

    public int validaDatas(String dataRecebida) throws ParseException{
        //formatador a data em AAAA-MM-DD
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        //obtendo as datas no formato desejado
        Date dateRecebida = dateFormat.parse(dataRecebida);
        Date dateAtual = new Date();

        try{
            //realiza conexão com banco de dados
            Conexao con = new Conexao();
            con.conexao.setAutoCommit(true);
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            st.execute("SELECT CURRENT_DATE() AS DATA_ATUAL;");
            ResultSet rs = st.getResultSet();
            while(rs.next()){
                dateAtual = dateFormat.parse(rs.getString("DATA_ATUAL").trim());
            }

            st.close();
            rs.close();
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        /*
         * RETORNOS POSSÍVEIS:
         * NEGATIVO = dateRecebida < dateAtual
         * POSITIVO = dateRecebida > dateAtual
         *     ZERO = dateRecebida = dateAtual
         */

        return (dateRecebida.compareTo(dateAtual));
    }
}
