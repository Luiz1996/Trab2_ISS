package br.uem.din.bibliotec.config.conexao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
    String servername = "localhost";
    String schema = "bibliotec";
    String user = "root";
    String password = "";

    public Connection conexao;
    public Conexao() throws SQLException{
        conexao = DriverManager.getConnection("jdbc:mysql://".concat(servername).concat("/").concat(schema), user, password);
    }
}
