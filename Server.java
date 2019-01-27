import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

public class Server {
    private static Hashtable<Integer, Integer> accounts = new Hashtable<>();
    private static Integer INITIAL_BALANCE = 0;

    public static void main(String args[]) {

        ServerSocket serverSocket = null;
        InetAddress addr;
        Socket clientSocket = null;

        DataInputStream inputStream = null;
        DataOutputStream outputStream = null;
        String command = "E";

        System.out.println("Server starting...");


        try {
            addr = InetAddress.getLocalHost();
            serverSocket = new ServerSocket(13346, 3, addr);

        } catch (Exception ex) {
            System.out.println("Creation of server socket failed");
            System.out.println(ex.getStackTrace());
            System.exit(1);
        }


        while (true) {

            try {
                clientSocket = serverSocket.accept();

            } catch (Exception ex) {
                System.out.println("Accept failed");
                System.exit(1);
            }


            try {
                inputStream = new DataInputStream(clientSocket.getInputStream());

            } catch (Exception ex) {
                System.out.println("Socket input stream failed");
                System.exit(1);
            }

            // Read integers from the connection and print them until
            // a zero is received
            try {
                command = inputStream.readUTF();
                char c = processCommand(command);

                // logic

            } catch (Exception e) {
                System.out.println("Socket input failed.");
                System.exit(1);
            }


            // close the stream and sockets
            try {
                inputStream.close();
                outputStream.close();
                clientSocket.close();
                serverSocket.close();
            } catch (Exception e) {
                System.out.println("Server couldn't close a socket.");
                System.exit(1);
            }
        }

        System.out.println("Server finished.");
    }


    public static char processCommand(String command) {
        String c;
        String[] toDo = command.split(",");

        c = toDo[0];
        switch (c) {
            case "C":
                createAccount(toDo[1]);
            case "R":
                return retrieveAccount(toDo[1]);
            case "D":
                return 'D';
            case "W":
                return 'W';
            default:
                return 'E';
        }
    }

    public static void createAccount(String account) {

        Integer accountNum = Integer.parseInt(account);

        if (!(accounts.containsKey(accountNum))) {
            accounts.put(accountNum, INITIAL_BALANCE);
        }
    }

    public static Integer retrieveAccount(String account) {
        Integer accountNum = Integer.parseInt(account);

        if (accounts.containsKey(accountNum)) {
            return accounts.get(accountNum);
        }
        return -1;
    }
}
