import java.net.ServerSocket;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Server {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(35000,1);
            // serverSocket.setLocalAddress("127.0.0.1");
            serverSocket.setReuseAddress(true);

            // Associates socket with IP address and port number
            //serverSocket.bind(new InetSocketAddress("localhost", 35000));

            System.out.printf("SUCCESS : Server bound and listening at %s\n",
                    serverSocket.getLocalSocketAddress().toString().split("/")[1]);

            // System.out.printf("Server's remote address info:  %s\n",
            //         serverSocket.getRemoteSocketAddress().toString());
            

            DataInputStream inputStream = null;
            DataOutputStream outputStream = null;
            while (true) {
                try {
                    // Accept incoming connections
                    Socket clientSocket = serverSocket.accept();
                    System.out.printf("\tACCEPTED connection from client<%s>\n",
                            clientSocket.getRemoteSocketAddress().toString().split("/")[1]);

                    inputStream = new DataInputStream(clientSocket.getInputStream());
                    outputStream = new DataOutputStream(clientSocket.getOutputStream());
                    // Access underlying socket streams
                    String message = "";
                    while (true) {
                        message = inputStream.readUTF();
                        System.out.printf("\tClient says %s\n",
                                message);
                        if (message.equalsIgnoreCase("close")) {
                            break;
                        }
                        outputStream.writeUTF(message);
                        outputStream.flush();
                    }
                    outputStream.writeUTF("Hello there!");
                    outputStream.flush();
                    outputStream.close();
                    inputStream.close();
                    clientSocket.close();
                } catch (Exception e) {
                    // Terminates loop
                    break;
                }

            }
            serverSocket.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }
}
