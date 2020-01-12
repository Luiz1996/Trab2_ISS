package br.uem.din.bibliotec.config.controller;


import br.uem.din.bibliotec.config.model.RegistroFalha;

import javax.servlet.ServletRequest;
import java.util.HashMap;
import java.util.Map;

public class ForcaBrutaController {

    //Map que guardará as tentativas que derem erro
    private Map<String, RegistroFalha> tentativaPorIP = new HashMap<String, RegistroFalha>();
    private long tempoMilisegundos;
    private int numeroTentativas;

    /**
     * Método que criará um registro para o ip no map caso não exista Ou se
     * existir incrementa a falha e seta o hora em milisegundos
     *
     * @param request
     */
    public ForcaBrutaController(long tempoMilisegundos, int numeroTentativas) {
        this.tempoMilisegundos = tempoMilisegundos;
        this.numeroTentativas = numeroTentativas;
    }

    //Método que criará um registro para o ip no map caso não exista
    //Ou se existir incrementa a falha e seta o hora em milisegundos
    public void registraFalha(ServletRequest request) {
        if (possuiIP(request) && !isTempoExpirado(request)) {
            RegistroFalha falha = tentativaPorIP.get(request.getRemoteHost());
            falha.setTentativa(falha.getTentativa() + 1);
            falha.setUltimaTentativa(System.currentTimeMillis());
        } else {
            RegistroFalha falha = RegistroFalha.criarRegistroFalha(request);
            tentativaPorIP.put(request.getRemoteHost(), falha);
        }
    }

    /**
     * Método que verifica se o usuário pode logar validando se o usuário tem o
     * ip no Map se o tempo está expirado e se não atingiu o limite de
     * tentanvidas
     *
     * @param request
     * @return
     */
    public boolean podeLoggar(ServletRequest request) {
        return !possuiIP(request) || isTempoExpirado(request) || !atingiuMaximoTentativas(request);
    }

    /**
     * Verifica se já tem o ip no Map
     *
     * @param request
     * @return
     */
    private boolean possuiIP(ServletRequest request) {

        return tentativaPorIP.containsKey(request.getRemoteHost());
    }

    /**
     * Verifica se já não atingiu o limite de tentativas de loggin
     *
     * @param request
     * @return
     */
    private boolean atingiuMaximoTentativas(ServletRequest request) {

        return tentativaPorIP.get(request.getRemoteHost()).getTentativa() > numeroTentativas;
    }

    /**
     * Método que verifica se o já passou o tempo desde o bloqueio do usuário
     *
     * @param request
     * @return
     */
    private boolean isTempoExpirado(ServletRequest request) {

        return tentativaPorIP.get(request.getRemoteHost()).getTempo() >= tempoMilisegundos;
    }

    /**
     * Método bonus que retorna o tempo restante para o usuário poder tentar
     * logar novamente
     *
     * @param request
     * @return
     */
    public String minutosRestantes(ServletRequest request) {
        long ms = tempoMilisegundos - tentativaPorIP.get(request.getRemoteHost()).getTempo();
        return String.format("%02d", (ms / 60000) % 60);
    }
}
