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
        String peticion;

        while(true){
          out.println("usuario y password");
          out.println("crear cuenta");
          out.println("salir");
          peticion = in.readLine();
          System.out.println("CLIENT: LOGIN " + Peticion);
          String comando[] = peticion.split(" ");
          if(comando.length == 2){
            if(comando[0].equals(user)){
              if(comando[1].equals(contra)){
                System.out.println("SERVER: OK LOGIN");
                out.println("Sesion Iniciada para " + comando[0]);
              }else{System.out.println("LOGIN ERROR 102");out.println("LOGIN ERROR 102\n Password Incorrecta");}
            }else{System.out.println("LOGIN ERROR 101");out.println("LOGIN ERROR 101\n Usuario no encontrado");}
          }else{System.out.println("ERROR");out.println("ERROR, Login Invalido");}



          // String user = "User123";
          // String contra = "pass";
          // String contacts[] ={"user1@server", "user2@server", "user3@server"};
          // String msg[][] ={{"user1@server", "Test", "Prueba de texto"},{"user2@server", "Test2", "Prueba de texto 2"}};


          while(true){
            peticion = in.readLine();
            comando[] = peticion.split(" ");
            if (comando[0].equals("CLIST") && comando[1].equals(user)){
              String usuarios[] = lista de usuarios;
            }else{System.out.println("CLIST ERROR 103");out.println("LOGIN ERROR 103\n no existen contactos.");}
            if (comando[0].equals("SEND") && comando[1].equals("MAIL")) {
              //enviar correo ...
            }else{
              /*ERROR 104 CONTACTO DESCONOCIDO
                ERROR 105 SERVIDOR DESCONOCIDO
                ERROR 106 NO HAY RECIPIENTE
                ERROR 107 NO SUBJECT
                ERROR 108 NO BODY*/
            }
            if (comando[0].equals("NEWCONT")) {
              //agregar contacto de comando[1]

            }else{
              /*ERROR 109 CONTACT NOT FOUND
                ERROR 110 SERVER NOT FOUND*/
            }
            if (comando[0].equals("NOOP")) {
              System.out.println("NOOP"); out.println("OK NOOP")
            }
            //despues hacemos mensaje de opcion equivocada
          }
      }
        in.close();
        out.close();
        socketClient.close();

    }catch(Exception e){
      e.printStackTrace();
    }
  }
}
