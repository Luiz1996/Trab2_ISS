package br.uem.din.bibliotec.config.controller;

import br.uem.din.bibliotec.config.dao.*;
import br.uem.din.bibliotec.config.model.Cotacao;
import br.uem.din.bibliotec.config.model.Emprestimo;
import br.uem.din.bibliotec.config.model.Multa;
import br.uem.din.bibliotec.config.model.Reserva;
import br.uem.din.bibliotec.config.services.FormataData;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

@Named
@SessionScoped
public class EmprestimoController implements Serializable {
    private Cotacao cotacao = new Cotacao();
    private Emprestimo emprestimo = new Emprestimo();
    private Reserva reserva = new Reserva();
    private Multa multa = new Multa();
    private CotacaoDao cotacaoDao = new CotacaoDao();
    private EmprestimoDao emprestimoDao = new EmprestimoDao();
    private ReservaDao reservaDao = new ReservaDao();
    private MultaDao multaDao = new MultaDao();
    private UsuarioDao usuarioDao = new UsuarioDao();
    private FormataData dtFormat = new FormataData();
    private final String FALHA = "red";
    private final String SUCESSO = "green";

    public EmprestimoController() {
    }

    public Emprestimo getEmprestimo() {
        return emprestimo;
    }

    public void setEmprestimo(Emprestimo emprestimo) {
        this.emprestimo = emprestimo;
    }

    public EmprestimoDao getEmprestimoDao() {
        return emprestimoDao;
    }

    public void setEmprestimoDao(EmprestimoDao emprestimoDao) {
        this.emprestimoDao = emprestimoDao;
    }

    public UsuarioDao getUsuarioDao() {
        return usuarioDao;
    }

    public void setUsuarioDao(UsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    public String getFALHA() {
        return FALHA;
    }

    public String getSUCESSO() {
        return SUCESSO;
    }

    public int consultaQtdeEmpAtrasados() {
        return emprestimoDao.consultarMeusEmpAtrasados(emprestimo);
    }

    public List<Emprestimo> consultarMeusEmprestimos() throws SQLException {
        return emprestimoDao.meusEmprestimos(emprestimo);
    }

    public List<Emprestimo> consultaEmprestimos() {
        return emprestimoDao.consultarEmprestimos(emprestimo);
    }

    public List<Emprestimo> consultaEmprestimosEmVigor() {
        return emprestimoDao.consultarEmprestimosAtivos(emprestimo);
    }

    public String cadastrarEmprestimo() throws SQLException {
        reserva.setCodLivro(emprestimo.getCodLivro());

        if(multaDao.valorTotalMultas(emprestimo.getCodUsuario()) > 0.0){
            emprestimo.setMsgRetorno("FALHA: Este usuário possui multas a serem quitadas.");
            emprestimo.setColorMsgRetorno(FALHA);
        }else{
            if (emprestimoDao.consultarEmpAtrasados(emprestimo.getCodUsuario()) > 0) {
                emprestimo.setMsgRetorno("FALHA: Este usuário possui empréstimos em atraso.");
                emprestimo.setColorMsgRetorno(FALHA);
            } else {
                if (reservaDao.livroJaReservadoQualquerUsuario(reserva) > 0) {
                    if (emprestimo.getCodUsuario() == reservaDao.obterDadosReserva(reserva, true)) {
                        reservaDao.cancelarReserva(reserva);
                        emprestimoDao.cadastrarEmprestimo(emprestimo);
                        emprestimo.setMsgRetorno("SUCESSO: O empréstimo foi efetuado com sucesso.");
                        emprestimo.setColorMsgRetorno(SUCESSO);
                    } else {
                        emprestimo.setMsgRetorno("FALHA: O livro encontra-se reservado para outro usuário.");
                        emprestimo.setColorMsgRetorno(FALHA);
                    }
                } else {
                    emprestimoDao.cadastrarEmprestimo(emprestimo);
                    emprestimo.setMsgRetorno("SUCESSO: O empréstimo foi efetuado com sucesso.");
                    emprestimo.setColorMsgRetorno(SUCESSO);
                }
            }
        }
        return usuarioDao.homePage();
    }

    public String renovarEmprestimo() throws ParseException {
        reserva.setCodLivro(emprestimo.getCodLivro());
        emprestimo.setDataDev(dtFormat.formatadorDatasMySQL(emprestimo.getDataDev().trim()).trim());

        if (emprestimoDao.validaNovaDataDevolucao(emprestimo) <= 0) {
            emprestimo.setMsgRetorno("FALHA: A nova data de devolução é menor ou igual a data de devolução original, use a função de devolver livro.");
            emprestimo.setColorMsgRetorno(FALHA);
        } else {
            if (reservaDao.livroJaReservadoQualquerUsuario(reserva) > 0) {
                emprestimo.setMsgRetorno("FALHA: O livro encontra-se reservado para outro usuário, não é possível renovar o empréstimo.");
                emprestimo.setColorMsgRetorno(FALHA);
            } else {
                if (dtFormat.validaDatas(emprestimo.getDataDev()) < 0) {
                    emprestimo.setMsgRetorno("FALHA: A data de devolução informada é inválida.");
                    emprestimo.setColorMsgRetorno(FALHA);
                } else {
                    emprestimoDao.renovarEmprestimo(emprestimo);
                    emprestimo.setMsgRetorno("SUCESSO: O empréstimo foi renovado com sucesso, a nova data de entrega pode sem vista clicando em 'Meus Empréstimos'.");
                    emprestimo.setColorMsgRetorno(SUCESSO);
                }
            }
        }
        return usuarioDao.homePage();
    }

    public String devolverLivro(){
        multa.setValorCotacao(cotacaoDao.consultarCotacaoEmVigor(cotacao));
        multa.setCodEmprestimo(emprestimo.getCodEmprestimo());
        multa.setDiasAtraso(emprestimoDao.quantidadeDiasAtrasados(emprestimo));
        multa.setCodCotacao(cotacao.getCodcotacao());
        multa.setCodLivro(emprestimo.getCodLivro());
        multa.setCodUsuario(emprestimo.getCodUsuario());
        multa.setValor(multa.getValorCotacao() * multa.getDiasAtraso());

        if(multa.getDiasAtraso() > 0){
            if(multa.getValorCotacao() <= 0.0){
                emprestimo.setMsgRetorno("FALHA: Não existe cotação cadastrada no sistema.");
                emprestimo.setColorMsgRetorno(FALHA);
            }else{
                multaDao.cadastrarMulta(multa);
                emprestimoDao.devolverLivroDeEmprestimo(emprestimo);
                emprestimo.setMsgRetorno("SUCESSO: O livro foi devolvido com sucesso e também foi gerado multa ao usuário.");
                emprestimo.setColorMsgRetorno(SUCESSO);
            }
        }else{
            emprestimoDao.devolverLivroDeEmprestimo(emprestimo);
            emprestimo.setMsgRetorno("SUCESSO: O livro foi devolvido com sucesso.");
            emprestimo.setColorMsgRetorno(SUCESSO);
        }
        return usuarioDao.homePage();
    }
}