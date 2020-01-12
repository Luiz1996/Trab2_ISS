package br.uem.din.bibliotec.config.services;

public class FormataDocs {
    public String formataCpf(String cpf) {
        if (cpf.trim() == null) {
            return " ";
        } else {
            String[] cpfSeparado =  new String[4];

            cpfSeparado[0] = cpf.trim().substring(0,3);
            cpfSeparado[1] = cpf.trim().substring(3,6);
            cpfSeparado[2] = cpf.trim().substring(6,9);
            cpfSeparado[3] = cpf.trim().substring(9,11);

            return cpfSeparado[0].concat(".") + cpfSeparado[1].concat(".") + cpfSeparado[2].concat("-") + cpfSeparado[3];
        }
    }

    public String formataRg(String rg) {
        if (rg.trim() == null) {
            return " ";
        } else {
            String[] rgSeparado =  new String[4];

            rgSeparado[0] = rg.trim().substring(0,2);
            rgSeparado[1] = rg.trim().substring(2,5);
            rgSeparado[2] = rg.trim().substring(5,8);
            rgSeparado[3] = rg.trim().substring(8,9);

            return rgSeparado[0].concat(".") + rgSeparado[1].concat(".") + rgSeparado[2].concat("-") + rgSeparado[3];
        }
    }
}
