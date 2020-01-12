package br.uem.din.bibliotec.config.controller;

import br.uem.din.bibliotec.config.dao.CotacaoDao;
import br.uem.din.bibliotec.config.model.Cotacao;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.text.DecimalFormat;

@Named
@SessionScoped
public class CotacaoController implements Serializable {
    private Cotacao cotacao = new Cotacao();
    private CotacaoDao cotacaoDao = new CotacaoDao();

    public CotacaoController(){}

    public Cotacao getCotacao() {
        return cotacao;
    }

    public void setCotacao(Cotacao cotacao) {
        this.cotacao = cotacao;
    }

    public CotacaoDao getCotacaoDao() {
        return cotacaoDao;
    }

    public void setCotacaoDao(CotacaoDao cotacaoDao) {
        this.cotacaoDao = cotacaoDao;
    }

    public String cadastrarCotacao(){
        if(cotacao.getValor() <= 0.0){
            cotacao.setMsgRetorno("FALHA: Valor de cotação inválido(inserir valores maiores que R$0,00).");
            cotacao.setColorMsgRetorno("red");
        }else{
            cotacaoDao.cadastrarNovaCotacao(cotacao);
            cotacao.setMsgRetorno("SUCESSO: A cotação de R$"+formataDoubleCasasDecimais(cotacao.getValor())+" foi cadastrada com sucesso.");
            cotacao.setColorMsgRetorno("green");
        }
        //resetando valor da cotação
        cotacao.setValor(00.00);

        return "/acessoBalconista?faces-redirect=true";
    }

    public String consultarCotacao(){

        return formataDoubleCasasDecimais(cotacaoDao.consultarCotacaoEmVigor(cotacao));
    }

    public String formataDoubleCasasDecimais(Double valorDouble){
        DecimalFormat dF = new DecimalFormat("0.00");
        return dF.format(valorDouble);
    }
}
