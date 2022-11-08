import java.io.*;
import java.net.Socket;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

public class ClientTest {

    public static void main(String[] args) {


        /* Choisir sa couleur*/
        Color newColor = JColorChooser.showDialog(null, "Choisissez votre couleur", Color.RED);

        JFrame fram = new JFrame();

        /*Creation du menu*/
        JMenu menu;
        JMenuItem couleurs, sauvegarde;
        JMenuBar menubar  = new JMenuBar();
        menu = new JMenu("Menu");
        couleurs = new JMenuItem("Couleurs");
        sauvegarde = new JMenuItem("Sauvegarder");
        menu.add(couleurs);
        menu.add(sauvegarde);
        menubar.add(menu);
        fram.setJMenuBar(menubar);



        /*Creation des boutons et des champs de text*/
        JButton boutonConnexion = new JButton("Connexion");
        JLabel Nom = new JLabel ("Nom");
        JTextField EcrireNom = new JTextField (5);
        JLabel IP = new JLabel ("IP");
        JTextField EcrireIP = new JTextField (5);
        JLabel Port = new JLabel ("Port");
        JTextField EcrirePort = new JTextField (7);
        JTextArea AffConnectes = new JTextArea(5, 5);
        JLabel Connectes = new JLabel ("Connectes");
        JTextArea AffDiscussion = new JTextArea (5, 5);
        JLabel Discussion = new JLabel ("Discussion");
        JTextArea EcrireMessage = new JTextArea (5, 5);
        JLabel Message = new JLabel ("Message");
        JButton boutonEnvoyer = new JButton ("Envoyer");


        EcrireMessage.setForeground(newColor);

        /*initialisation de la fenetre*/
        fram.setTitle("Multichat client");
        fram.setSize(new Dimension(550,600));
        fram.setResizable(false); // Fenetre non redimensionnable
        fram.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fram.setLayout(null);
        fram.setLocationRelativeTo(null);// fenetre placée au milieu de l'ecran
        fram.setVisible (true);

        /* Placement des boutons sur la fenetre*/
        EcrireNom.setBounds (70, 40, 175, 25);
        fram.add(EcrireNom);
        Nom.setBounds (20, 40, 50, 25);
        fram.add(Nom);
        boutonConnexion.setBounds (290, 40, 240, 20);
        fram.add (boutonConnexion);
        IP.setBounds (20, 85, 50, 25);
        fram.add (IP);
        EcrireIP.setBounds (70, 85, 175, 25);
        fram.add (EcrireIP);
        Port.setBounds (275, 85, 50, 25);
        fram.add (Port);
        EcrirePort.setBounds (315, 85, 175, 25);
        fram.add (EcrirePort);
        AffConnectes.setBounds (30, 170, 175, 335);
        fram.add (AffConnectes);
        Connectes.setBounds (80, 140, 100, 25);
        fram.add(Connectes);
        AffDiscussion.setBounds (230, 170, 265, 175);
        fram.add(AffDiscussion);
        Discussion.setBounds (325, 140, 100, 25);
        fram.add(Discussion);
        EcrireMessage.setBounds (230, 380, 265, 90);
        fram.add(EcrireMessage);
        Message.setBounds (230, 350, 100, 25);
        fram.add(Message);
        boutonEnvoyer.setBounds (230, 485, 265, 20);
        fram.add(boutonEnvoyer);

        class Listener implements DocumentListener{
            @Override
            public void changedUpdate(DocumentEvent arg0) {}
            @Override
            public void insertUpdate(DocumentEvent arg0) {
                verif();
            }
            @Override
            public void removeUpdate(DocumentEvent arg0) {
                verif();
            }

            /**Methode qui verifie si les champs Nom, IP et Port sont vide ou non
             *
             */
            private void verif() {
                if((!EcrireIP.getText().isEmpty() && !EcrireNom.getText().isEmpty() && !EcrirePort.getText().isEmpty())) {
                    boutonConnexion.setEnabled(true);
                }else {
                    boutonConnexion.setEnabled(false);
                }
            }
        }


        /*Arrondir le bouton*/
        boutonConnexion.setBorder(new BoutonArrondi(15));
        boutonEnvoyer.setBorder(new BoutonArrondi(15));

        /*rendre le bouton non cliquable tant que IP, Port et Nom sont vide*/
        EcrireNom.getDocument().addDocumentListener(new Listener());
        EcrireIP.getDocument().addDocumentListener(new Listener());
        EcrirePort.getDocument().addDocumentListener(new Listener());
        boutonConnexion.setEnabled(false);

        /* rendre inneccessible les champs de saisie de texte tant que le bouton de connexion n'est pas clique*/
        AffDiscussion.setEditable(false);
        EcrireMessage.setEditable(false);
        AffConnectes.setEditable(false);


        boutonConnexion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //changement du bouton connexion en bouton de deconnexion*/
                boutonConnexion.setText("Deconnexion");
                /*desactive les champs IP, Nom et Port*/
                EcrireIP.setEnabled(false);
                EcrireNom.setEnabled(false);
                EcrirePort.setEnabled(false);
                /*Active les message, les personnes connectes et les discussions*/
                EcrireMessage.setEditable(true);

                try {
                    /* Creation du socket en recuperant l'ip et le port dans la fenetre*/
                    Socket socket = new Socket(EcrireIP.getText(), Integer.parseInt(EcrirePort.getText()));
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    AffConnectes.setText(EcrireNom.getText());

                    /* Action sur le bouton envoyer qui envoie le text et le nom du client dans le chat*/
                    boutonEnvoyer.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent ex) {
                            try {
                                if (!socket.isClosed()) {
                                    String msgEnvoyer = EcrireMessage.getText();
                                    bufferedWriter.write(EcrireNom.getText() + " : " + msgEnvoyer);
                                    //AffDiscussion.setText(AffDiscussion.getText() + msgEnvoyer + "\n");
                                    bufferedWriter.newLine();
                                    bufferedWriter.flush();
                                }
                                EcrireMessage.setText(" ");
                            } catch (IOException ioe) {
                                ioe.printStackTrace();
                            }
                        }
                    });

                    /* Thread pour recevoir les messages des autres clients tout en envoyant des msg en meme temps*/
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String msgGroupeChat;

                            while (!socket.isClosed()) {
                                try {
                                    msgGroupeChat = bufferedReader.readLine();
                                    AffDiscussion.setForeground(newColor);
                                    AffDiscussion.setText(AffDiscussion.getText() + msgGroupeChat + "\n");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();

                } catch (IOException ioe) {
                    System.out.println("probleme 0");
                }

            }
        });


        /*pouvoir ecrire que des lettres dans noms*/
        EcrireNom.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent e){
                char c = e.getKeyChar();
                if (!(Character.isAlphabetic(c) || (c == KeyEvent.VK_BACK_SPACE) || c == KeyEvent.VK_DELETE)){
                    e.consume(); // ignorer le caractere
                }
            }
        });

        /*pouvoir ecrire que des chiffres dans Port*/
        EcrirePort.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent e){
                char c = e.getKeyChar();
                if ( ((c < '0') || (c > '9')) && (c != KeyEvent.VK_BACK_SPACE)) {
                    e.consume();  // ignorer l'événement
                }
            }
        });

        /*pouvoir ecrire que des chiffres et des points dans IP*/
        EcrireIP.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent e){
                char c = e.getKeyChar();
                if ( ((c < '0') || (c > '9')) && (c != '.')  && (c != KeyEvent.VK_BACK_SPACE)) {
                    e.consume();  // ignorer l'événement
                }
            }
        });
    }
}
