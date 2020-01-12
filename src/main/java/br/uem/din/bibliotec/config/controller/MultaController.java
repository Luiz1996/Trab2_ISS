package br.uem.din.bibliotec.config.controller;

import br.uem.din.bibliotec.config.dao.MultaDao;
import br.uem.din.bibliotec.config.dao.UsuarioDao;
import br.uem.din.bibliotec.config.model.Multa;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@SessionScoped
public class MultaController implements Serializable {
    private Multa multa = new Multa();
    private MultaDao multaDao = new MultaDao();
    private UsuarioDao usuarioDao = new UsuarioDao();

    public MultaController(){}

    public Multa getMulta() {
        return multa;
    }

    public void setMulta(Multa multa) {
        this.multa = multa;
    }

    public MultaDao getMultaDao() {
        return multaDao;
    }

    public void setMultaDao(MultaDao multaDao) {
        this.multaDao = multaDao;
    }

    public List<Multa> consultarMultasPorCpf(){
        multa.setCpfUsuario(multa.getCpfUsuario().replace(".",""));
        multa.setCpfUsuario(multa.getCpfUsuario().replace("-",""));

        return multaDao.consultarMultasCpf(multa);
    }

    public String baixarMultas(){
        multaDao.baixarMultas(multa);
        multa.setMsgRetorno("SUCESSO: As multas selecionadas foram baixadas com sucesso.");
        multa.setColorMsgRetorno("green");
        return usuarioDao.homePage();
    }

    public double totalEmMultas(int codUsuario){

        return multaDao.valorTotalMultas(codUsuario);
    }

    public List<Multa> consultarMinhasMultas(){
        multa.setCodUsuario(usuarioDao.obterCodUsuario());
        return multaDao.consultarMinhasMultasCpf(multa);
    }
}
