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

public class API {

    private static String token;
    private HashMap meus_dados;
    private static String matricula;
    private static String senha;
    
    public API(String matricula, String senha) throws IOException {
        API.matricula = matricula;
        API.senha = senha;
        
        token = buscarToken();
    }
    
    public static String buscarToken() throws IOException {
        //Pegando o Token com POST
        Form form = Form.form();
        form.add("username", API.matricula);
        form.add("password", API.senha);

        HttpResponse response = Request.Post("https://suap.ifrn.edu.br/api/v2/autenticacao/token/")
                                .bodyForm(form.build()).execute().returnResponse();
        HashMap tokenHM;
        if (response != null) {
            InputStream source = response.getEntity().getContent();
            Reader reader = new InputStreamReader(source);
            Gson gson = new Gson();
            tokenHM = gson.fromJson(reader, HashMap.class); //você pode ultilizar o Gson ou org.json 
            return tokenHM.get("token").toString();
        }
        return "";
    }
    
    public void buscarDados(String url, String matricula) throws IOException {
        //Buscando Meus Dados com GET
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String urlLocal = url + matricula + "/";
        String auth = "Basic MjAxNDEwNjQwMTAyMjA6RG91cmFkbzEw";//fixo para alunos
        HttpGet httpget = new HttpGet(urlLocal);
        
        httpget.addHeader("Accept", "application/json");
        httpget.addHeader("X-CSRFToken", buscarToken());
        httpget.addHeader("Authorization", auth);

        //System.out.println("Executing request " + httpget.getRequestLine());
        
        //verifica se a api está funcionando
        ResponseHandler<String> responseHandler = (final HttpResponse response) -> {
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity) : null;
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
        };
        String responseGET = httpclient.execute(httpget, responseHandler);
        Gson gson = new Gson();
        meus_dados = gson.fromJson(responseGET, HashMap.class);
    }
    
    //get e set
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        API.token = token;
    }

    public HashMap getMeus_dados() {
        return meus_dados;
    }

    public void setMeus_dados(HashMap meus_dados) {
        this.meus_dados = meus_dados;
    }

    public String getMatricula() {
        return matricula;
    }
    public void setMatricula(String matricula) {
        API.matricula = matricula;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        API.senha = senha;
    }    
}
