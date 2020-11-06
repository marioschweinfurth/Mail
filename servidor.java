import java.io.*;
import java.net.*;
import java.util.*;

public class servidor{
  static int puerto = 1200;
  public static void main(String[] args){
    try{
      ServerSocket server = new ServerSocket(puerto);
      System.out.println("Server Online, Puerto " + puerto + " ...");
        Socket socketClient = server.accept();
        PrintWriter out = new PrintWriter(socketClient.getOutputStream(), true);
        System.out.println("Cliente Online");
        out.println("Conexion exitosa con Servidor");
        InputStreamReader isr = new InputStreamReader(socketClient.getInputStream());
        BufferedReader in = new BufferedReader(isr);


        String user = "User123";
        String contra = "pass";
        String contacts[] ={"user1@server", "user2@server", "user3@server"};
        String msg[][] ={{"user1@server", "Test", "Prueba de texto"},{"user2@server", "Test2", "Prueba de texto 2"}};

        String Peticion = in.readLine();
        System.out.println("CLIENT: LOGIN " + Peticion);
        String comando[] = Peticion.split(" ");
        if(comando.length == 2){
          if(comando[0].equals(user)){
            if(comando[1].equals(contra)){
              System.out.println("SERVER: OK LOGIN");
              out.println("Sesion Iniciada para " + comando[0]);
              //Acciones de login
              System.out.println("CLIENT: CLIST " + comando[0]);
              for(String i:contacts){
                System.out.println("SERVER: OK CLIST " + i);
              }System.out.print(" *");
              System.out.println("CLIENT: GETNEWMAILS " + comando[0]);
              for(String[] i:msg){
                System.out.println("SERVER: OK CLIST " + Arrays.deepToString(i));
              }System.out.print(" *");

            }else{System.out.println("ERROR");out.println("ERROR, Password Incorrecta");}
          }else{System.out.println("ERROR");out.println("ERROR, Usuario no encontrado");}
        }else{System.out.println("ERROR");out.println("ERROR, Login Invalido");}

        in.close();
        out.close();
        socketClient.close();

    }catch(Exception e){
      e.printStackTrace();
    }
  }
}
