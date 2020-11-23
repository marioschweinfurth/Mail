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
      ArrayList<String> correos = new ArrayList<String>();
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
            out.println("OK LOGIN");

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
            //      System.out.println(usuarios);
                  out.println("OK CLIST ");
                  for(int i=0;i <=usuarios.size();i++){
                    System.out.println(usuarios.get(i));
                    out.println(usuarios.get(i));
                  }
                  out.println(" *");
                  System.out.print(" *");
                } else {
                  System.out.println("CLIST ERROR 103");
                  out.println("LOGIN ERROR 103");
                }
              }

              //DESPLEGAR LISTA DE CORREOS
              if (comando.get(0).equals("GETNEWMAILS")){
                query = "SELECT mail FROM correos WHERE id = '%s';";
                query2 = String.format(query, id);
                if (myDb.executeQuery(query2, "rs3")) {
                  System.out.println("CLIST SUCCESFULL");
                  while (myDb.next("rs3")) {
                    correos.add(myDb.getString("contacto", "rs3"));
                  }
                }
                    out.println("OK GETNEWMAILS ");
                    for(int i = 0; correos.size(); i++){
                      System.out.println(correos.get(i));
                      out.println(correos.get(i));
                    }system.out.print(" *");out.println(" *");
                  }


              //ENVIAR CORREO
              else if (comando.get(0).equals("SEND") && comando.get(1).equals("MAIL")){
                String nombreContacto = in.readLine();
                if (nombreContacto != null){
                  String subject = in.readLine();
                  String body = in.readLine();
                  if(Pattern.matches(regex,nombreContacto)){
                    if (subject != null) {
                      if (body != null) {
                        if (lista.contains(nombreContacto)){
                          String correo = nombreContacto + subject + body;
                          query = "INSERT INTO correos('id','mail') VALUES('%s', '%s');";
                          query2 = String.format(query, id, correo);
                          myDb.executeNonQuery(query2);
                          out.println("OK SEND MAIL"); System.out.println("END SEND MAIL");
                        } else {System.out.println("SEND ERROR 104");out.println("SEND ERROR 104");}
                      }else{System.out.println("SEND ERROR 108");out.println("SEND ERROR 108");}
                    }else{System.out.println("SEND ERROR 107");out.println("SEND ERROR 107");}
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
                            outS2.println("OK SEND MAIL");
                            peticionS2 = inS2.readLine();
                            if (peticionS2.equals("SEND ERROR 201 "+ nombreContacto)){System.out.println("SEND ERROR 201: unknown contact");out.println("SEND ERROR 201: unknown contact");}
                            else if (peticionS2.equals("SEND ERROR 202")){System.out.println("SEND ERROR 202");out.println("SEND ERROR 202");}
                            else if (peticionS2.equals("SEND ERROR 203")){System.out.println("SEND ERROR 203");out.println("SEND ERROR 203");}
                            else if (peticionS2.equals("SEND ERROR 204")){System.out.println("SEND ERROR 204");out.println("SEND ERROR 204");}
                          }else{System.out.println("SEND ERROR 108");out.println("SEND ERROR 108");}
                        } else {System.out.println("SEND ERROR 107");out.println("SEND ERROR 107");}
                      }else {System.out.println("SEND ERROR 205");out.println("SEND ERROR 205");}
                    }else {System.out.println("SEND ERROR 206");out.println("SEND ERROR 105");}
                  }
                }else{System.out.println("SEND ERROR 106");out.println("SEND ERROR 106");}
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
                  } else {System.out.println("NEWCONT ERROR 109");out.println("NEWCONT ERROR 109");}
                }else{
                  outS2.println("CHECK CONTACT " + nuevoCont);
                  peticionS2 = inS2.readLine();
                  if (!peticionS2.equals("CHECK ERROR 206")){
                    if (!peticionS2.equals("CHECK ERROR 205")){
                      query = "INSERT INTO contact('id','contacto') VALUSE('%s','%s');";
                      query2 = String.format(query, id, nuevoCont);
                      myDb.executeNonQuery(query2);
                      out.println("OK NEWCONT "+ nuevoCont);
                    }else{System.out.println("NEWCONT ERROR 109");out.println("NEWCONT ERROR 109");}
                  }else{System.out.println("NEWCONT ERROR 110");out.println("NEWCONT ERROR 110");}
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
            out.println("LOGIN ERROR 102");
          }
        } else {
          System.out.println("server: LOGIN ERROR 101");
          out.println("LOGIN ERROR 101");
        }
        if (comando.get(0).equals("LOGOUT")){
          out.println("OK LOGOUT");
          break;
        }else{System.out.println("INVALID COMMAND ERROR"); out.println("INVALID COMMAND ERROR");}
      }


      in.close();

      inD.close();
      inS2.close();
      out.close();
      outD.close();
      outS2.close();
      socketC.close();
      socketD.close();
      myDb.close();

  }catch(Exception e){
    e.printStackTrace();
    System.out.println(e.getClass());
    System.out.println(e.getMessage());}
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
      if (comandoo.get(0).equals("SEND")){



      }

    }
  }

}
