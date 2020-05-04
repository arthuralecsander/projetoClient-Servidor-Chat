package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread implements Runnable {
    
    ServerSocket server;
    MainForm main;
    boolean continuar = true;
    // Inicio da thread do servidor
    public ServerThread(int port, MainForm main){
        main.appendMessage("[Server]: Iniciando servidor na porta: "+ port);
        try {
            this.main = main;
            server = new ServerSocket(port);
            main.appendMessage("[Server]: Servidor online");
        } 
        catch (IOException e) { main.appendMessage("[IOException]: "+ e.getMessage()); }
    }

    @Override
    public void run() {
        try {
            while(continuar){
                Socket socket = server.accept();
                //main.appendMessage("[Socket]: "+ socket);
                /** Thread do socket  **/
                new Thread(new SocketThread(socket, main)).start();
            }
        } catch (IOException e) {
            main.appendMessage("[ServerThreadIOException]: "+ e.getMessage());
        }
    }
    
    
    public void stop(){
        try {
            server.close();
            continuar = false;
            System.out.println("Servidor agora esta fechado..!");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
}
