import java.io.*;
import java.net.*;
import java.util.*;
import db.*;

public class servidor{
  static int puerto = 1200;
  public static void main(String[] args){
    try{
      DB myDb = new DB("testDb.db");
      if(!myDb.connect()){//create actual connection to db
  			System.out.println("Error en db"+myDb.getError());
  			System.exit(0);
  		}

      ServerSocket server = new ServerSocket(puerto);
      System.out.println("Server Online, Puerto " + puerto + " ...");
        Socket socketClient = server.accept();
        PrintWriter out = new PrintWriter(socketClient.getOutputStream(), true);
        System.out.println("Cliente Online");
        out.println("Conexion exitosa con Servidor");
        InputStreamReader isr = new InputStreamReader(socketClient.getInputStream());
        BufferedReader in = new BufferedReader(isr);

        String regex = "\\w*+[@]\\d{3}[.]\\d{1}[.]\\d{1}[.]\\d{1,3}";
        String peticion, user, id, query, query2;
        ArrayList<String> lista, lista2, lista3, comando, usuarios;


        while(true){
          lista.clear();
          lista2.clear();
          comando.clean();
          myDb.executeQuery("SELECT * FROM usuarios;", "rs1");
          //EXTRAER INFO DE BASE DE DATOS A LISTAS.
          while(myDb.next("rs1")){
            lista.add(myDb.getString("user", "rs1"));
            lista2.add(myDb.getString("password","rs1"));
          }


          peticion = in.readLine();
          System.out.println("CLIENT: LOGIN " + Peticion);
          comando.add(peticion.split(" "));

          //PARA ENTRAR A UN USUARIO
          if(comando[0] in lista){
            if(comando[1] in lista2){
              System.out.println("SERVER: OK LOGIN");
              out.println("Sesion Iniciada para " + comando[0]);

              //INICIO MENU
              while(true){
                user = comando[0];
                query = "SELECT id FROM usuarios WHERE user = '%s';";
                query2 = String.format(query,user);
                id = myDb.executeQuery(query2)
                peticion = in.readLine();
                comando.clear();
                comando.add(peticion.split(" "));

                //DESPLEGAR LISTA DE CONTACTOS
                if (comando[0].equals("CLIST") && comando[1].equals(user)){
                  query = "SELECT contacto FROM contact WHERE id = '%s';";
                  query2 = String.format(query,id);
                  if(myDb.executeQuery(query2, "rs2")){
                    system.out.println("CLIST SUCCESFULL");
                    while(myDb.next("rs2")){
                      usuarios.add(myDb.getString("contacto","rs2"));
                    }
                    System.out.println(usuarios);
                    out.usuarios;
                    /*for(int i=0;i <=usuarios.length;i++){
                      if (i=usuarios.length){
                        out.println(usuario[i] + "*");
                      }else{
                        out.println(usuarios[i]);
                      }
                    }*/
                  }else{System.out.println("CLIST ERROR 103: no existen contactos.");out.println("LOGIN ERROR 103: no existen contactos.");}
                }

                //ENVIAR CORREO
                else if (comando[0].equals("SEND") && comando[1].equals("MAIL")){
                  out.println("nombre de usuario");
                  String nombreContacto = in.readLine();
                  if(pattern.matches(regex,nombreContacto)){
                    if(nombreContacto in lista){//falta verificar que exista en otro servidor
                      out.println("Subject");
                      String subject = in.readLine();
                      if (subject != null){
                        out.println("Body");
                        String body = in.readLine();
                        if(body != null){
                          if(nombreContacto in lista){
                            String correo = nombreContacto + " ~ "+ subject+" ~ "+body;
                            query = "INSERT INTO correos('id','destino','subject', 'body') VALUES('%s', '%s','s%','%s');"
                            query2 = String.format(query,id,nombreContacto,subject,body);
                            myDb.executeNonQuery(query2);
                          }else{/*if{servidor conocido.} else{ERROR 105 SERVIDOR DESCONOCIDO}*/}
                        }else{System.out.println("ERROR 108: no body."); out.println("ERROR 108: no body.");}
                      }else{System.out.println("ERROR 107: no Subject."); out.println("ERROR 107: no Subject.");}
                    }else{System.out.println("ERROR 104: unknown contact"); out.println("ERROR 104: unknown contact");}
                  }else{System.out.println("ERROR 106: No hay recipiente."); out.println("ERROR 107: no hay recipiente.");}
                }

                //AGREGAR CONTACTO
                else if (comando[0].equals("NEWCONT")) {//falta buscar en otro server
                  String nuevoCont = comando[1];
                  if (nuevoCont in lista){
                    query = "INSERT INTO contact('id','contacto') VALUSE('%s','%s');";
                    query2 = String.format(query,id,nuevoCont);
                    mydb.executeNonQuery(query2);
                    out.println("Contacto agregado!");
                  }else{System.out.println("ERROR 109: contact not found."); out.println("ERROR 109: contact not found.");}
                }

                //NOOP
                if (comando[0].equals("NOOP")) {
                  System.out.println("NOOP"); out.println("OK NOOP")
                }

                //EXIT USUARIO
                if (comando[0].equals("EXIT")){
                  break;
                }
                //despues hacemos mensaje de opcion equivocada
              }
            }else{System.out.println("server: LOGIN ERROR 102");out.println("LOGIN ERROR 102\n Password Incorrecta");}
          }else{System.out.println("server: LOGIN ERROR 101");out.println("LOGIN ERROR 101\n Usuario no encontrado");}

          //CREAR USUARIO
          if (comando[0].equals("NEW")){
            out.println("Ingrese nombre de usuario")
            String usuario = in.readLine();
            if(pattern.matches(regex,usuario)){
              if(!usuario in lista){
                out.println("ingrese password");
                String pass = in.readLine();
                if (pass != null){
                  String nueva = "INSERT INTO usuarios(user,password)VALUES('%s','%s');";
                  nueva= String.format(nueva,usuario,pass);
                  myDb.executeNonQuery(nueva);
                  System.out.println("SERVER: USUARIO CREADO");out.println("usuario creado exitosamente!");
                }
              }
            }
          }

          //EXIT PROGRAMA
          if (comando[0].equals("EXIT")){
            break;
          }


          // String user = "User123";
          // String contra = "pass";
          // String contacts[] ={"user1@server", "user2@server", "user3@server"};
          // String msg[][] ={{"user1@server", "Test", "Prueba de texto"},{"user2@server", "Test2", "Prueba de texto 2"}};


        in.close();
        out.close();
        socketClient.close();
        myDb.close();

    }catch(Exception e){
      e.printStackTrace();
      System.out.println(e.getClass());
      System.out.println(e.getMessage());}
    }
  }
}
