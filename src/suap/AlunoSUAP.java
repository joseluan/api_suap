package suap;

import java.io.IOException;
import java.util.HashMap;

public class AlunoSUAP {

    private String matricula;
    private String senha;
    private String nome;
    private String vinculo;
    private String copia = "2000";
    

    public AlunoSUAP(String matricula, String senha) throws IOException {
        this.matricula = matricula;
        this.senha = senha;
        String url = "https://suap.ifrn.edu.br/api/v2/edu/alunos/"; 
        
        API api = new API(matricula, senha);
        api.buscarDados(url, matricula);
        HashMap dados = api.getMeus_dados();
        this.nome = dados.get("nome").toString();
        this.vinculo = dados.get("curso").toString();
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getVinculo() {
        return vinculo;
    }

    public void setVinculo(String vinculo) {
        this.vinculo = vinculo;
    }

    public String getCopia() {
        return copia;
    }

    public void setCopia(String copia) {
        this.copia = copia;
    }
    
}
