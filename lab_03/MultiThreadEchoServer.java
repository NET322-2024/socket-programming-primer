import java.net.ServerSocket;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.*;

public class MultiThreadEchoServer {
    public static void main(String[] args) {
        try {
            try (ServerSocket serverSocket = new ServerSocket(6800)) {
                // serverSocket.setLocalAddress("127.0.0.1");
                serverSocket.setReuseAddress(true);

                // Associates socket with IP address and port number
                // serverSocket.bind(new InetSocketAddress("localhost", 35000));

                System.out.printf("SUCCESS : Server bound and listening at %s\n",
                        serverSocket.getLocalSocketAddress().toString().split("/")[1]);

                while (true) {
                    try {
                       
                        // Accept incoming connections
                        Socket clientSocket = serverSocket.accept();
                        
                        try {
                            ExecutorService threadPoolService = Executors.newFixedThreadPool(5);
                            threadPoolService.execute(new ClientWorker(clientSocket));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        

                    } catch (Exception e) {
                        // Terminates loop
                        break;
                    }

                }
            }

            
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }
}

class ClientWorker implements Runnable {
    protected Socket clientSocket = null;

    public ClientWorker(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        try {
            String clientIdentifier = this.clientSocket.getRemoteSocketAddress().toString().split("/")[1];
            System.out.printf("\tACCEPTED connection from client<%s>\n", clientIdentifier);

            DataInputStream inputStream = new DataInputStream(this.clientSocket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(this.clientSocket.getOutputStream());
            // Access underlying socket streams
            String message = "";
            while (true) {
                message = inputStream.readUTF();
                System.out.printf("\t%s says %s\n",
                        clientIdentifier, message);
                if (message.equalsIgnoreCase("close")) {
                    break;
                }
                outputStream.writeUTF(message +
                        String.format("\n<RCPT> : %s", new Date().toString()));
                outputStream.flush();
            }
            // outputStream.writeUTF("Hello there!");
            outputStream.flush();
            outputStream.close();
            inputStream.close();
            clientSocket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

}
