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
  static DB myDb = new DB("servidor.db");


  public static void main(String[] args) throws Exception {

    if(!(myDb.connect())){//create actual connection to db
      System.out.println("Error en db"+myDb.getError());
      System.exit(0);
    }

    MyThread2 sc = new MyThread2(1400);
    MyThread ss = new MyThread(1500);

    sc.run();
    sc.run2();

    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    while(true){
      String opcion = br.readLine();
      if (opcion.equals("exit")){
        break;
      }

    }

  }

  static class MyThread extends Thread{
    int puerto;
    public MyThread(int puerto){
      super();
      this.puerto = puerto;
    }

    public void run(){
      try {
        ServerSocket serverS = new ServerSocket(puerto);
        System.out.println("puerto 1500 abierto...");
        Socket socketS = serverS.accept();
        PrintWriter out = new PrintWriter(socketS.getOutputStream(), true);
        System.out.println("conectando con otro servidor..."); out.println("Coneccion exitosa con gmail server.");
        InputStreamReader isr = new InputStreamReader(socketS.getInputStream());
        BufferedReader in = new BufferedReader(isr);

        //ArrayList<String> comandoo = new ArrayList<String>();
        ArrayList<String> cuentas = new ArrayList<String>();
        //ArrayList<String> cuentas2 = new ArrayList<String>();


        //comandoo.clear();
        cuentas.clear();
        //cuentas2.clear();
        String opcion = in.readLine();
        String[] comandoo = opcion.split("@");
        //comandoo.get(opcion.split(" "));
        if (comandoo[0].equals("SEND")){
          String destino = in.readLine();
          String sujeto = in.readLine();
          String Body = in.readLine();
          if (destino != null){
            if (sujeto != null){
              if (Body != null){
                myDb.executeQuery("SELECT user FROM usuarios;","rs4");
                while(myDb.next("rs4")){
                  cuentas.add(myDb.getString("user","rs4"));
                }
                if (cuentas.contains(comandoo[1])){
                  String query = "SELECT id FROM usuarios WHERE user = '%s';";
                  String query2 = String.format(query, comandoo[1]);
                  String id = myDb.getString(query, comandoo[1]);
                  String agregar = String.format("INSERT INTO correos('id','mail') VALUES('%s','%s')", id, comandoo[1]);
                  myDb.executeNonQuery(agregar);
                  out.println("OK SEND MAIL"); System.out.println("OK SEND MAIL");
                }else{out.println("SEND ERROR 201");System.out.println("SEND ERROR 201");}
              }else{System.out.println("SEND ERROR 204"); out.println("SEND ERROR 204");}
            }else{System.out.println("SEND ERROR 203"); out.println("SEND ERROR 203");}
          }else{System.out.println("SEND ERROR 202"); out.println("SEND ERROR 202");}
        }
        else if(comandoo[0].equals("CHECK")){
          String[] cuentas2 = comandoo[2].split("@");
          //cuentas2.add(comandoo[2]);
          if (cuentas2[1].equals("gmail")){
            myDb.executeQuery("SELECT user FROM usuarios;","rs4");
            while(myDb.next("rs4")){
              cuentas.add(myDb.getString("user","rs4"));
            }
            if(cuentas.contains(comandoo[2])){
              out.println("OK CHECK"); System.out.println("OK CHECK");
            }else{out.println("CHECK ERROR 205"); System.out.println("CHECK ERROR 205");}
          }else{out.println("CHECK ERROR 206"); System.out.println("CHECK ERROR 206");}
        }
      }catch (Exception e){

      }
    }
  }

  static class MyThread2 extends Thread{
    int puerto;
    public MyThread2(int puerto){
      super();
      this.puerto = puerto;
    }

    public void run2(){
      try{

        ServerSocket serverC = new ServerSocket(puerto);
        Socket serverS2 = new Socket("127.0.0.1",puerto2);
        ServerSocket serverD = new ServerSocket(puerto3);

        System.out.println("Server Online, Puerto " + puerto + " ...");
        Socket socketC = serverC.accept();
        Socket socketD = serverD.accept();

        PrintWriter out = new PrintWriter(socketC.getOutputStream(), true);
        PrintWriter outD = new PrintWriter(socketD.getOutputStream(), true);
        PrintWriter outS2 = new PrintWriter(serverS2.getOutputStream(), true);


        System.out.println("Cliente Online");
        System.out.println("Conexion exitosa con Servidor");

        InputStreamReader isr = new InputStreamReader(socketC.getInputStream());
        InputStreamReader isrD = new InputStreamReader(socketD.getInputStream());
        InputStreamReader isrS2 = new InputStreamReader(serverS2.getInputStream());

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
              System.out.println("Sesion Iniciada para " + Arrays.toString(comando.get(0)));
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
                  for(int i = 0; i<correos.size(); i++){
                    System.out.println(correos.get(i));
                    out.println(correos.get(i));
                  }System.out.print(" *");out.println(" *");
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
                            String correo = nombreContacto + " ~ "+ subject +" ~ "+ body;
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
}
