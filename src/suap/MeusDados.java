package suap;

//classe de teste
//para poder funcionar é necessario a biblioteca org.apache.http 
//baixe neste link: https://hc.apache.org/httpcomponents-client-4.5.x/download.html
//baixe também o Gson da google: https://github.com/google/gson
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

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

        try {
            AlunoSUAP aluno = new AlunoSUAP(matricula, senha);
            System.out.println(aluno.getNome());
        } catch (org.apache.http.client.ClientProtocolException e) {
            ServidorSUAP aluno = new ServidorSUAP(matricula, senha);
            System.out.println(aluno.getNome());
        }
 
    }
}
