package br.uem.din.bibliotec.config.dao;

import br.uem.din.bibliotec.config.conexao.Conexao;
import br.uem.din.bibliotec.config.model.Livro;
import br.uem.din.bibliotec.config.services.Email;
import br.uem.din.bibliotec.config.services.FormataData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LivroDao {
    private Email email = new Email();
    private FormataData dtFormat =  new FormataData();

    //método de cadastramento de livro
    public int cadastrarLivro(Livro livro) throws SQLException {
        try {
            //realiza conexão com banco de dados
            Conexao con = new Conexao();
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            con.conexao.setAutoCommit(true);

            //realiza insert no banco e retorna mensagem de sucesso na cor verde
            st.executeUpdate("INSERT INTO `bibliotec`.`livro` (`codcatalogacao`, `numchamada`, `titulo`, `autor`, `editora`, `anolancamento`, `cidade`, `volume`, `ativo`, `datacad`, `disponibilidade`) VALUES ('"+livro.getCodcatalogacao()+"', '"+livro.getNumchamada()+"', '"+livro.getTitulo()+"', '"+livro.getAutor()+"', '"+livro.getEditora()+"', '"+livro.getAnolancamento()+"', '"+livro.getCidade()+"', "+livro.getVolume()+", '1', current_date(), '1');");

            st.close();
            con.conexao.close();

            return 1;
        } catch (SQLException e) {
            return 0;
        }
    }


    public List<Livro> consultarLivro(Livro livro, int soDisponiveis) throws SQLException {
        //realiza conexão com banco de dados
        Conexao con = new Conexao();
        con.conexao.setAutoCommit(true);
        Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

        //setando os valores obtidos no front-end para realizar busca no banco de dados
        livro.setEditora(livro.getTitulo());
        livro.setAutor(livro.getTitulo());

        //busca todas as informações de acordo com os dados fornecidos
        if(soDisponiveis == 0){
            st.execute("select * from `bibliotec`.`livro` where (titulo like \"%"+ livro.getTitulo() +"%\" or autor like \"%" + livro.getAutor() + "%\" or editora like \"%" + livro.getEditora() + "%\") and ativo = '1' order by 2;");
        }else{
            st.execute("select * from `bibliotec`.`livro` where (titulo like \"%"+ livro.getTitulo() +"%\" or autor like \"%" + livro.getAutor() + "%\" or editora like \"%" + livro.getEditora() + "%\") and ativo = '1' and disponibilidade = '1' order by 2;");
        }

        ResultSet rs = st.getResultSet();

        //declaração do arrayList para auxiliar na impressão da dataTable do consultar acervo do Visitante
        List<Livro> livros = new ArrayList<>();

        //obtendo os valores carregados no result set e carregado no arrayList
        while (rs.next()) {
            Livro livro_temporario = new Livro(rs.getInt("codlivro"),
                    rs.getString("codcatalogacao"),
                    rs.getString("numchamada"),
                    rs.getString("titulo"),
                    rs.getString("autor"),
                    rs.getString("editora"),
                    rs.getString("anolancamento"),
                    rs.getString("cidade"),
                    rs.getInt("volume"),
                    rs.getInt("ativo"),
                    "",
                    "",
                    "",
                    rs.getString("datacad"),
                    rs.getString("dataalt"),
                    rs.getString("disponibilidade")
                    );

            livros.add(livro_temporario);
        }

        //fechando as conexões em aberto para evitar locks infinitos no banco de dados
        st.close();
        rs.close();
        con.conexao.close();

        return livros;
    }

    public List<Livro> consultarLivroBibliotecario(Livro livro, int ativo) throws SQLException {
        //realizando a conexão com banco de dados
        Conexao con = new Conexao();
        con.conexao.setAutoCommit(true);
        Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

        System.out.println();

        //buscando as informações no banco de dados de acordo com o titulo do livro informado pelo usupario, essa busca possui diferencial da coluna Status (Ativo/Inativo)
        if(ativo == 0){
            st.execute("select l.codlivro, l.codcatalogacao, l.numchamada, l.titulo, l.autor, l.editora, l.anolancamento, l.cidade, l.volume, l.ativo, case when l.ativo = 1 then \" Ativo \" else \" Inativo \" end as status, datacad, dataalt, case when disponibilidade = 1 then \" Sim \" when disponibilidade = 0 then \" Não \" end as disponibilidade  from livro l where (l.titulo like \"%"+ livro.getBusca().trim() +"%\" or l.autor like \"%" + livro.getBusca().trim() + "%\" or l.editora like \"%" + livro.getBusca().trim() + "%\") order by 1 ;");
        }else{
            st.execute("select l.codlivro, l.codcatalogacao, l.numchamada, l.titulo, l.autor, l.editora, l.anolancamento, l.cidade, l.volume, l.ativo, case when l.ativo = 1 then \" Ativo \" else \" Inativo \" end as status, datacad, dataalt, case when disponibilidade = 1 then \" Sim \" when disponibilidade = 0 then \" Não \" end as disponibilidade  from livro l where ativo = '1' order by 4 ;");
        }

        ResultSet rs = st.getResultSet();

        //declara o arrayList que será usado no dataTable do Bibliotecário
        List<Livro> livros = new ArrayList<>();

        //carregando o arrayList com os valores obtidos no resultSet
        while (rs.next()) {
            Livro livro_temporario = new Livro
                (   rs.getInt("codlivro"),
                    rs.getString("codcatalogacao"),
                    rs.getString("numchamada"),
                    rs.getString("titulo"),
                    rs.getString("autor"),
                    rs.getString("editora"),
                    rs.getString("anolancamento"),
                    rs.getString("cidade"),
                    rs.getInt("volume"),
                    rs.getInt("ativo"),
                    rs.getString("status"),
                   "",
                   "",
                   dtFormat.formatadorDatasBrasil(rs.getString("datacad")),
                   dtFormat.formatadorDatasBrasil(rs.getString("dataalt")),
                   rs.getString("disponibilidade")
                );

            livros.add(livro_temporario);
        }

        //fechando todas as conexões para evitar lock nas tabelas do banco de dados
        st.close();
        rs.close();
        con.conexao.close();

        return livros;
    }

    public int deletarLivro(Livro livro){
        try {
            //abre conexão com banco de dados
            Conexao con = new Conexao();
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            con.conexao.setAutoCommit(true);

            //validando se o livro possui alguma reserva ou empréstimos em vigor
            st.execute( "SELECT \n" +
                    "    COALESCE(MAX(e.ativo),0) AS ha_emp\n" +
                    "FROM\n" +
                    "    livro l\n" +
                    "LEFT JOIN\n" +
                    "    emprestimo e ON e.codlivro = l.codlivro\n" +
                    "WHERE\n" +
                    "    l.codlivro = '" + livro.getCodlivro() + "';");

            ResultSet rs = st.getResultSet();

            int ha_reserva = 0, ha_emp = 0;
            while(rs.next()){
                ha_emp = rs.getInt("ha_emp");
            }

            //se possuir emprestimo ou reserva em vigor, a deleção é abortada
            if(ha_reserva > 0){
                return -1;
            }

            if(ha_emp > 0){
                return -2;
            }

            //executa a EXCLUSÃO LÓGICA do livro no banco de dados, ou seja, ativo recebe 0
            st.executeUpdate("UPDATE `bibliotec`.`livro` SET `ativo` = '0', dataalt = current_date(), disponibilidade = '0' WHERE (`codlivro` =" + livro.getCodlivro() + ");");

            //fecha conexões
            st.close();
            con.conexao.close();

            return 1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public int editarLivro(Livro livro){
        Integer codlivro = 0;

        try {
            //realiza conexão com banco de dados
            Conexao con = new Conexao();
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            con.conexao.setAutoCommit(true);

            st.execute( "SELECT \n" +
                            "    COALESCE(l.codlivro, 0) AS codlivro\n" +
                            "FROM\n" +
                            "    livro l\n" +
                            "WHERE\n" +
                            "    l.codlivro = '" + livro.getCodlivro() + "';");

            ResultSet rs = st.getResultSet();

            while(rs.next()){
                codlivro = rs.getInt("codlivro");
            }

            //valida se o código do livro não foi fornecido ou se foi fornecido um código inválido
            if(codlivro == 0){
                return -1;
            }

            st.executeUpdate("UPDATE `bibliotec`.`livro` \n" +
                                "SET \n" +
                                "    `codcatalogacao` = '"+livro.getCodcatalogacao().trim()+"',\n" +
                                "    `numchamada` = '"+livro.getNumchamada().trim()+"',\n" +
                                "    `titulo` = '"+livro.getTitulo().trim()+"',\n" +
                                "    `autor` = '"+livro.getAutor().trim()+"',\n" +
                                "    `editora` = '"+livro.getEditora().trim()+"',\n" +
                                "    `anolancamento` = '"+livro.getAnolancamento().trim()+"',\n" +
                                "    `cidade` = '"+livro.getCidade().trim()+"',\n" +
                                "    `volume` = '"+livro.getVolume()+"',\n" +
                                "    `ativo` = '"+livro.getAtivo()+"',\n" +
                                "    `dataalt` = current_date(),\n" +
                                "    `disponibilidade` = '"+livro.getDisponibilidade().trim()+"'\n" +
                                "WHERE\n" +
                                "    (`codlivro` = '"+livro.getCodlivro()+"');");

            //fechando as conexões para evitar lock
            st.close();
            con.conexao.close();

            return 1;
        } catch (SQLException e) {
            return 0;
        }
    }


    public List<Livro> consultaLivrosDisponiveis(Livro livro) throws SQLException {
        List<Livro> livros = new ArrayList<Livro>();

        try{
            //realiza conexão com banco de dados
            Conexao con = new Conexao();
            con.conexao.setAutoCommit(true);
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            //busca todas as informações de acordo com os dados fornecidos
            st.execute("SELECT * FROM `bibliotec`.`livro` l WHERE l.ativo = '1' and l.disponibilidade = '1';");
            ResultSet rs = st.getResultSet();

            //obtendo os valores carregados no result set e carregado no arrayList
            while (rs.next()) {
                Livro livros_temp = new Livro(
                        rs.getInt("codlivro"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("editora"),
                        rs.getString("anolancamento"),
                        rs.getString("codcatalogacao")
                );
                livros.add(livros_temp);
            }

            st.close();
            rs.close();
            con.conexao.close();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        return livros;
    }

    public int carregarDadosLivro(Livro livro){
        try {
            //realiza conexão com banco de dados
            Conexao con = new Conexao();
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            con.conexao.setAutoCommit(true);

            st.execute("SELECT L.* FROM `bibliotec`.`livro` L WHERE L.CODLIVRO = '" + livro.getCodlivro() + "';");
            ResultSet rs = st.getResultSet();

            while(rs.next()){
                livro.setTitulo(rs.getString("titulo").trim());
                livro.setAutor(rs.getString("autor").trim());
                livro.setEditora(rs.getString("editora").trim());
                livro.setAnolancamento(rs.getString("anolancamento").trim());
                livro.setCidade(rs.getString("cidade").trim());
                livro.setVolume(rs.getInt("volume"));
                livro.setCodcatalogacao(rs.getString("codcatalogacao").trim());
                livro.setNumchamada(rs.getString("numchamada").trim());
                livro.setAtivo(rs.getInt("ativo"));
                livro.setDisponibilidade(rs.getString("disponibilidade").trim());
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            return 0;
        }
        return 1;
    }

    public int verificaDispLivro(int codLivro) {
        int dispLivro = 0;
        try{
            Conexao con = new Conexao();
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            con.conexao.setAutoCommit(true);

            st.execute("select disponibilidade from livro where codlivro = '"+codLivro+"';");

            ResultSet rs = st.getResultSet();

            while(rs.next()){
                dispLivro = rs.getInt("disponibilidade") ;
            }

            st.close();
            rs.close();
            con.conexao.close();
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return dispLivro;
    }
}
