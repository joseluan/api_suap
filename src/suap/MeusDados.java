package suap;
//para poder funcionar é necessario a biblioteca org.apache.http 
//baixe neste link: https://hc.apache.org/httpcomponents-client-4.5.x/download.html
//baixe também o Gson da google: https://github.com/google/gson

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Scanner;
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

public class MeusDados {

    public static void main(String[] args) throws IOException, URISyntaxException, Exception {
        System.out.println("---------- API SUAP - Java ----------");
        System.out.println("----------------------------------------");
        System.out.println("");
        Scanner sc = new Scanner(System.in);
        //pegando a matricula do aluno
        System.out.println("Digite sua matricula: ");
        String matricula = sc.nextLine();
        //pegando a senha do aluno
        System.out.println("Digite sua senha: ");
        String senha = sc.nextLine();

        System.out.println("");

        //Pegando o Token com POST
        System.out.println("Pegando seu Token!");
        Form form = Form.form();
        form.add("username", matricula);
        form.add("password", senha);

        HttpResponse response = Request.Post("https://suap.ifrn.edu.br/api/v2/autenticacao/token/")
                .bodyForm(form.build()).execute().returnResponse();
        HashMap tokenHM;
        String token = "";
        if (response != null) {
            InputStream source = response.getEntity().getContent();
            Reader reader = new InputStreamReader(source);
            Gson gson = new Gson();
            tokenHM = gson.fromJson(reader, HashMap.class); //você pode ultilizar o Gson ou org.json 
            token = tokenHM.get("token").toString();
            System.out.println("Seu token = " + token);
            System.out.println("-------------------------------------");
        }

        //Buscando Meus Dados com GET
        System.out.println("Buscando seus dados na API");
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            String url = "https://suap.ifrn.edu.br/api/v2/minhas-informacoes/meus-dados/";
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
            HashMap meusdados = gson.fromJson(responseGET, HashMap.class);
            //tratando o campo vinculo
            String vinculoS = vinculoToJson(meusdados.get("vinculo").toString());
            //converte o Json em HashMap
            HashMap vinculo = gson.fromJson(vinculoS ,
                              HashMap.class);
            System.out.println("ID = "+meusdados.get("id"));
            System.out.println("tipo_vinculo = "+meusdados.get("tipo_vinculo"));
            System.out.println("Nome completo = "+vinculo.get("nome"));
            System.out.println("Matricula = "+vinculo.get("matricula"));
            System.out.println("email = "+meusdados.get("email"));
            System.out.println("Curso = "+vinculo.get("curso"));
        
        //tratando exceções
        } catch (IOException io) {
            System.out.println(io.getMessage());
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                System.out.println("Exceção: " + e.getMessage());
            }
        }

    }
    public static String vinculoToJson(String vinculo){
        
        String retorno = "";
        vinculo = vinculo.replace("{","");
        vinculo = vinculo.replace("}","");
        String[] quebra1 = vinculo.split(",");
        for (int i = 0; i < quebra1.length; i++) {
            String[] chave_valor = quebra1[i].split("=");
            if (i != quebra1.length-1) {
                if (i == 0) {
                    retorno += "{";
                }
                retorno += "\""+chave_valor[0].trim()+"\":";
                retorno += "\""+chave_valor[1]+"\",";
            }else{
                retorno += "\""+chave_valor[0]+"\":";
                retorno += "\""+chave_valor[1]+"\"}";
            }
        }
        return retorno;
    }
}
