function validaAnoLanc(ano_informado){
    var ano_atual = (new Date()).getFullYear();
    if(ano_atual < ano_informado){
        alert("O ano de lançamento não pode ser maior que " + ano_atual + ".");
        document.getElementById("Livro-AnoLanc").value = "";
        document.getElementById("Livro-AnoLanc").focus();
    }
}

function validaVolume(volume){
    if(volume <= 0){
        alert("O volume informado é inválido!");
        document.getElementById("Livro-Volume").value = "";
        document.getElementById("Livro-Volume").focus();
    }
}

function validaCodLivro(cod){
    if(cod <= 0 || cod == ""){
        /*alert("Por favor, informar um Id válido!");*/
        document.getElementById("Codlivro-codlivro").value = "";
        document.getElementById("Codlivro-codlivro").focus();
    }
}
