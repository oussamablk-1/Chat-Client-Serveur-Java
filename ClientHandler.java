import javax.imageio.IIOException;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/***
 * Class ClientHandler qui est la class contenant tous les clients
 */
public class ClientHandler implements Runnable{

    public static ArrayList<ClientHandler> clientHandlers= new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader; //Message des autres clients
    private BufferedWriter bufferedWriter; //Message de ce client

    /***
     * Constructeur de la class ClientHandler
     * @param socket de type Socket qui est le socket du client
     */
    public ClientHandler(Socket socket) {
        try{
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            clientHandlers.add(this);

        }catch (IOException e){
            Fermer(socket,bufferedReader, bufferedWriter);
        }
    }

    /***
     * Methode de thread au niveau du client pour recevoir les messages
     */
    @Override
    public void run(){
        String msgClient;

        while(socket.isConnected()){
            try{
                msgClient = bufferedReader.readLine();
                MessageDefaut(msgClient);
            }catch (IOException e){
                Fermer(socket,bufferedReader,bufferedWriter);
                break;
            }
        }
    }

    public void MessageDefaut(String message){
        for(ClientHandler clientHandler: clientHandlers){
            try{
                clientHandler.bufferedWriter.write(message);
                clientHandler.bufferedWriter.newLine();
                clientHandler.bufferedWriter.flush();
            }catch (IOException e){
                Fermer(socket,bufferedReader,bufferedWriter);
            }
        }
    }

    /***
     * Methoe qui ferme le socket
     * @param socket de type Socket
     * @param br de type BufferedReader
     * @param bw de type BufferedWriter
     */
    public void Fermer(Socket socket, BufferedReader br, BufferedWriter bw){
        try{
            if(br != null){
                br.close();
            }
            if(bw != null){
                bw.close();
            }
            if(socket != null){
                socket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
