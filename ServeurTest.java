import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

public class ServeurTest {

    private ServerSocket serveur;

    /***
     * Contrusteur de la class ServeurTest
     * @param serveurSocket de type serverSocket
     */
    public ServeurTest(ServerSocket serveurSocket){
        this.serveur = serveurSocket;
    }

    /***
     * Methode qui lance le serveur et affiche quand un client se connect
     * Elle lance egalement ClientHandler ou serotn stocke tous les clients
     */
    public void LancerServeur(){
        try{
            while (!serveur.isClosed()) {
                Socket client = serveur.accept();
                System.out.println("Un nouveau client s'est connecter");

                ClientHandler clientHandler = new ClientHandler(client);

                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        }catch(IOException e){
        }
    }

    /***
     * Methode qui ferme le serveur
     */
    public void FermerServeurSocket(){
        try{
            if(serveur != null){
                serveur.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

     public static void main(String[] args) throws IOException {
        ServerSocket serveurSocket = new ServerSocket(6500);
        ServeurTest serveur = new ServeurTest(serveurSocket);
        serveur.LancerServeur();
     }

}
