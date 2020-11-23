import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.Pattern;

import db.*;

public class servidor{
  static int puerto = 1400;
  static int puerto2 = 1500;
  static int puerto3 = 1200;
  // static int ip = 127.0.0.1;
  DB myDb = new DB("servidor.db");
  if(!myDb.connect()){//create actual connection to db
    System.out.println("Error en db"+myDb.getError());
    System.exit(0);
  }

  public static void main(String[] args) throws IOException {
    try{

      ServerSocket serverC = new ServerSocket(puerto);
      Socket serverS2 = new Socket(127.0.0.1,puerto2);
      ServerSocket serverD = new ServerSocket(puerto3);

      System.out.println("Server Online, Puerto " + puerto + " ...");
      Socket socketC = serverC.accept();
      Socket socketD = serverD.accept();

      PrintWriter out = new PrintWriter(socketC.getOutputStream(), true);
      PrintWriter outD = new PrintWriter(socketD.getOutputStream(), true);
      PrintWriter outS2 = new PrintWriter(socketS2.getOutputStream(), true);


      System.out.println("Cliente Online");
      outC.println("Conexion exitosa con Servidor");

      InputStreamReader isr = new InputStreamReader(socketC.getInputStream());
      InputStreamReader isrD = new InputStreamReader(socketD.getInputStream());
      InputStreamReader isrS2 = new InputStreamReader(socketS2.getInputStream());

      BufferedReader in = new BufferedReader(isr);
      BufferedReader inD = new BufferedReader(isrD);
      BufferedReader inS2 = new BufferedReader(isrS2);

      String regex = "\\w+[@][g][m][a][i][l]";
      String regex2 = "\\w+[@]\\w+";
      String peticion, user, id, query, query2, peticionS2;

      //ArrayList<String> lista2, lista3, usuarios = new ArrayList<String>();
      ArrayList<String> lista = new ArrayList<String>();
      // ArrayList<String> lista = new ArrayList<String>();
      ArrayList<String> lista2 = new ArrayList<String>();
      ArrayList<String> lista3 = new ArrayList<String>();
      ArrayList<String> usuarios = new ArrayList<String>();
      ArrayList<String[]> comando = new ArrayList<String[]>();


      while(true) {
        lista.clear();
        lista2.clear();
        comando.clear();
        myDb.executeQuery("SELECT * FROM usuarios;", "rs1");
        //EXTRAER INFO DE BASE DE DATOS A LISTAS.
        while (myDb.next("rs1")) {
          lista.add(myDb.getString("user", "rs1"));
          // lista.add(myDb.getString("servidor", "rs1"));
          lista2.add(myDb.getString("password", "rs1"));
        }


        peticion = in.readLine();
        System.out.println("CLIENT: LOGIN " + peticion);
        comando.add(peticion.split(" "));

        //PARA ENTRAR A UN USUARIO
        if (lista.contains(comando.get(0))) {
          if (lista2.contains(comando.get(1))) {
            boolean flag = true;
            System.out.println("SERVER: OK LOGIN");
            outC.println("Sesion Iniciada para " + Arrays.toString(comando.get(0)));

            //INICIO MENU
            while (flag) {
              user = Arrays.toString(comando.get(0));
              query = "SELECT id FROM usuarios WHERE user = '%s';";
              query2 = String.format(query, user);
              //Prueba de get ID
              id = myDb.getString(query, user);
              peticion = in.readLine();
              comando.clear();
              comando.add(peticion.split(" "));

              //DESPLEGAR LISTA DE CONTACTOS
              if (comando.get(0).equals("CLIST") && comando.get(1).equals(user)) {
                query = "SELECT contacto FROM contact WHERE id = '%s';";
                query2 = String.format(query, id);
                if (myDb.executeQuery(query2, "rs2")) {
                  System.out.println("CLIST SUCCESFULL");
                  while (myDb.next("rs2")) {
                    usuarios.add(myDb.getString("contacto", "rs2"));
                  }
                  System.out.println(usuarios);
                  out.println("OK CLIST "+usuarios);
                  /*for(int i=0;i <=usuarios.length;i++){
                    if (i=usuarios.length){
                      out.println(usuario[i] + "*");
                    }else{
                      out.println(usuarios[i]);
                    }
                  }*/
                } else {
                  System.out.println("CLIST ERROR 103: no existen contactos.");
                  out.println("LOGIN ERROR 103: no existen contactos.");
                }
              }

              //ENVIAR CORREO
              else if (comando.get(0).equals("SEND") && comando.get(1).equals("MAIL")){
                out.println("nombre de usuario");
                String nombreContacto = in.readLine();
                if (nombreContacto != null){
                  out.println("Subject");
                  String subject = in.readLine();
                  out.println("Body");
                  String body = in.readLine();
                  if(Pattern.matches(regex,nombreContacto)){
                    if (subject != null) {
                      if (body != null) {
                        if (lista.contains(nombreContacto)){
                          String correo = nombreContacto + " ~ " + subject + " ~ " + body;
                          query = "INSERT INTO correos('id','destino','subject', 'body') VALUES('%s', '%s','s%','%s');";
                          query2 = String.format(query, id, nombreContacto, subject, body);
                          myDb.executeNonQuery(query2);
                        } else {System.out.println("ERROR 104: unknown contact.");out.println("ERROR 104: unknown contact.");}
                      }else{System.out.println("ERROR 108: no body.");out.println("ERROR 108: no body.");}
                    }else{System.out.println("ERROR 107: no Subject.");out.println("ERROR 107: no Subject.");}
                  }if (Pattern.matches(regex2, nombreContacto)){
                    outS2.println("CHECK CONTACT "+ nombreContacto);
                    peticionS2 = inS2.readLine();
                    if (!peticionS2.equals("CHECK ERROR 206")){
                      if(!peticionS2.equals("CHECK ERROR 205")){
                        if (subject != null) {
                          if (body != null) {
                            outS2.println("MAIL "+ nombreContacto);
                            outS2.println("MAIL FROM "+ nombreContacto);
                            outS2.println("MAIL SUBJECT "+ subject);
                            outS2.println("MAIL BODY "+ body);
                            outS2.println("END SEND MAIL");
                            peticionS2 = inS2.readLine();
                            if (peticionS2.equals("SEND ERROR 201 "+ nombreContacto)){System.out.println("ERROR 201: unknown contact");out.println("ERROR 201: unknown contact");}
                            else if (peticionS2.equals("SEND ERROR 202")){System.out.println("ERROR 202: no sender.");out.println("ERROR 202: no sender");}
                            else if (peticionS2.equals("SEND ERROR 203")){System.out.println("ERROR 203: no subject.");out.println("ERROR 203: no subject");}
                            else if (peticionS2.equals("SEND ERROR 204")){System.out.println("ERROR 204: no body.");out.println("ERROR 204: no body");}
                          }else{System.out.println("ERROR 108: no body.");out.println("ERROR 108: no body.");}
                        } else {System.out.println("ERROR 107: no Subject.");out.println("ERROR 107: no Subject.");}
                      }else {System.out.println("ERROR 205: unknown contact");out.println("ERROR 205: unknown contact");}
                    }else {System.out.println("ERROR 206: unknown server");out.println("ERROR 105: unknown server");}
                  }
                }else{System.out.println("ERROR 106: No hay recipiente.");out.println("ERROR 107: no hay recipiente.");}
            }

              //AGREGAR CONTACTO
              else if (comando.get(0).equals("NEWCONT")){
                String nuevoCont = Arrays.toString(comando.get(1));
                if (Pattern.matches(regex,nuevoCont)){
                  if (lista.contains(nuevoCont)){
                    query = "INSERT INTO contact('id','contacto') VALUSE('%s','%s');";
                    query2 = String.format(query, id, nuevoCont);
                    myDb.executeNonQuery(query2);
                    out.println("OK NEWCONT "+ nuevoCont);
                  } else {System.out.println("ERROR 109: contact not found.");out.println("ERROR 109: contact not found.");}
                }else{
                  outS2.println("CHECK CONTACT " + nuevoCont);
                  peticionS2 = inS2.readLine();
                  if (!peticionS2.equals("CHECK ERROR 206")){
                    if (!peticionS2.equals("CHECK ERROR 205")){
                      query = "INSERT INTO contact('id','contacto') VALUSE('%s','%s');";
                      query2 = String.format(query, id, nuevoCont);
                      myDb.executeNonQuery(query2);
                      out.println("OK NEWCONT "+ nuevoCont);
                    }else{System.out.println("ERROR 109: unknown contact");out.println("ERROR 109: unknown contact");}
                  }else{System.out.println("ERROR 110: unknown server");out.println("ERROR 110: unknown server");}
                }
              }

              //NOOP
              if (comando.get(0).equals("NOOP")) {
                System.out.println("NOOP");
                out.println("OK NOOP");
              }

              //EXIT USUARIO
              if (comando.get(0).equals("LOGOUT")) {
                out.println("OK LOGOUT");
                flag = false;
              }
              //despues hacemos mensaje de opcion equivocada
            }
          } else {
            System.out.println("server: LOGIN ERROR 102");
            out.println("LOGIN ERROR 102\n Password Incorrecta");
          }
        } else {
          System.out.println("server: LOGIN ERROR 101");
          out.println("LOGIN ERROR 101\n Usuario no encontrado");
        }
        if (comando.get(0).equals("LOGOUT")){
          out.println("OK LOGOUT");
          break;
        }else{System.out.println("INVALID COMMAND ERROR"); out.println("INVALID COMMAND ERROR");}
      }


      in.close();
      inS.close();
      inD.close();
      inS2.close();
      out.close();
      outS.close();
      outD.close();
      outS2.close();
      socketC.close();
      socketS.close();
      socketD.close();
      myDb.close();

  }catch(Exception e){
    e.printStackTrace();
    System.out.println(e.getClass());
    System.out.println(e.getMessage());}
  }

public void run(){

}
}

public class MyThread extends Threads{
  int puerto;
  public MyThread(int puerto){
    super();
    this.puerto = puerto;
  }

  public void run(){
    ServerSocket serverS = new ServerSocket(puerto);
    System.out.println("puerto 1500 abierto...");
    Socket socketS = serverS.accept();
    System.out.println("conectando con otro servidor..."); out.println("Coneccion exitosa con gmail server.")
    PrintWriter out = new PrintWriter(socketS.getOutputStream(), true);
    InputStreamReader isr = new InputStreamReader(socketS.getInputStream());
    BufferedReader in = new BufferedReader(isr);

    ArrayList<String[]> comandoo = new ArrayList<String[]>();

    while(true){
      String opcion = in.readLine();
      comandoo.add(opcion.split(" "));
      if (comandoo.get(0)){
        
      }

    }
  }

}
