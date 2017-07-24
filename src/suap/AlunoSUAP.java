package suap;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class AlunoSUAP {

    private String matricula;
    private String senha;
    private String token;
    private HashMap meusdados;

    public void buscarToken() throws IOException {
        //Pegando o Token com POST
        System.out.println("Pegando seu Token!");
        Form form = Form.form();
        form.add("username", matricula);
        form.add("password", senha);

        HttpResponse response = Request.Post("https://suap.ifrn.edu.br/api/v2/autenticacao/token/")
                                .bodyForm(form.build()).execute().returnResponse();
        HashMap tokenHM;
        if (response != null) {
            InputStream source = response.getEntity().getContent();
            Reader reader = new InputStreamReader(source);
            Gson gson = new Gson();
            tokenHM = gson.fromJson(reader, HashMap.class); //você pode ultilizar o Gson ou org.json 
            this.token = tokenHM.get("token").toString();
        }
    }

    public void buscarDados() throws IOException {
        //Buscando Meus Dados com GET
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String url = "https://suap.ifrn.edu.br/api/v2/edu/alunos/" + this.matricula + "/";
        String auth = "Basic MjAxNDEwNjQwMTAyMjA6RG91cmFkbzEw";//fixo para alunos
        HttpGet httpget = new HttpGet(url);
        
        httpget.addHeader("Accept", "application/json");
        httpget.addHeader("X-CSRFToken", token);
        httpget.addHeader("Authorization", auth);

        //System.out.println("Executing request " + httpget.getRequestLine());
        
        //verifica se a api está funcionando
        ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
            @Override
            public String handleResponse(
                    final HttpResponse response) throws ClientProtocolException, IOException {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            }

        };
        String responseGET = httpclient.execute(httpget, responseHandler);
        Gson gson = new Gson();
        this.meusdados = gson.fromJson(responseGET, HashMap.class);
    }

    public AlunoSUAP() {
    }

    public AlunoSUAP(String matricula, String senha) {
        this.matricula = matricula;
        this.senha = senha;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public HashMap getMeusdados() {
        return meusdados;
    }

    public void setMeusdados(HashMap meusdados) {
        this.meusdados = meusdados;
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

}
