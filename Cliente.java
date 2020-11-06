import java.io.*;
import java.net.*;
public class Cliente{
  public static void main(String[] args) {
    try{
      InputStreamReader r = new InputStreamReader(System.in);
      BufferedReader br = new BufferedReader(r);

      //obteniendo IP
      System.out.print("Ingrese IP del servidor: ");
      String ip = br.readLine();
      Socket serverSocket = new Socket(ip,1200);

      InputStreamReader isr = new InputStreamReader(serverSocket.getInputStream());
      BufferedReader entrada = new BufferedReader(isr);
      PrintWriter salida = new PrintWriter(serverSocket.getOutputStream(), true);
      String comando = "";

      //obteniendo Usuario
      System.out.print("Ingrese usuario: ");
      comando = br.readLine() + " ";
      //Obteniendo contraseña
      System.out.print("Ingrese clave: ");
      comando = comando + br.readLine();
      salida.println(comando);

      System.out.println(entrada.readLine());
      System.out.println(entrada.readLine());
      /*System.out.println("SERVER: " + entrada.readLine());
      System.out.println("Ingrese datos de inicio de sesion");
      comando = br.readLine();
      salida.println(comando);
      System.out.println("SERVER: " + entrada.readLine());*/




      entrada.close();
      salida.close();
      serverSocket.close();
    }catch(Exception e){
      e.printStackTrace();
    }
  }
}
