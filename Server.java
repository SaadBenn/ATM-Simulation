import java.io.*;
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

        BufferedReader strm = null;
        OutputStream outputStream = null;
        String command = "";

        System.out.println("Server starting...");

        // Create Socket
        try {
            addr = InetAddress.getLocalHost();
            serverSocket = new ServerSocket(13346, 3, addr);

        } catch (Exception ex) {
            System.out.println("Creation of server socket failed");
            System.out.println(ex.getStackTrace());
            System.exit(1);
        }

        while (true) {
            // Accept Connection
            try {
                clientSocket = serverSocket.accept();
            } catch (Exception ex) {
                System.out.println("Accept failed");
                System.exit(1);
            }

            try {
                strm = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                outputStream = clientSocket.getOutputStream();
            } catch (Exception ex) {
                System.out.println("Socket input stream failed");
                System.exit(1);
            }

            // Read integers from the connection and print them until
            // a zero is received
            do {
                try {
                    command = strm.readLine();
                    command = command.trim().replaceAll("\n", "");

                    if (command != null && command != "" && !command.equals("E")) {
                        processCommand(command, outputStream);
                    }
                } catch (Exception e) {
                    System.out.println("Socket input failed.");
                    System.exit(1);
                }

                if (command != null && command != "" && !command.equals("E")) {
                    try {
                        outputStream.write("\n".getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } while (!command.equals("E"));

            try {
                outputStream.write("Closing connection".getBytes());
                strm.close();
                outputStream.close();
                clientSocket.close();
            } catch (Exception e) {
                System.out.println("Server couldn't close the socket.");
                System.exit(1);
            }
        }
        // close the stream and sockets
    } // main method


    public static void processCommand(String command, OutputStream outputStream) {
        String[] toDo = command.split(",");
        String clientCommand = toDo[0];
        String accountNum = toDo[1];

        switch (clientCommand) {
            case "C":
                createAccount(accountNum, outputStream);
                break;
            case "R":
                retrieveAccount(accountNum, outputStream);
                break;
            case "D":
                processDeposit(accountNum, Integer.parseInt(toDo[2]), outputStream);
                break;
            case "W":
                processWithdrawl(accountNum, Integer.parseInt(toDo[2]), outputStream);
                break;
            default:
                try {
                    outputStream.write("Invalid command".getBytes());
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    } // process command


    public static void createAccount(String account, OutputStream outputStream) {

        Integer accountNum = Integer.parseInt(account);
        try {
            if (!(accounts.containsKey(accountNum))) {
                accounts.put(accountNum, INITIAL_BALANCE);
                String output = "Account: " + accountNum + " created successfully with initial balance of 0";
                outputStream.write(output.getBytes());
                outputStream.flush();
            } else {
                outputStream.write("Account already exists".getBytes());
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    } // create account


    public static void retrieveAccount(String account, OutputStream outputStream) {
        Integer accountNum = Integer.parseInt(account);
        try {
            if (accounts.containsKey(accountNum)) {
                String out = "Account retrieved " + "Balance is: " + accounts.get(accountNum);
                outputStream.write(out.getBytes());
                outputStream.flush();
            } else {
                outputStream.write("Account does not exist".getBytes());
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    } // retrieve account


    public static void processDeposit(String account, Integer amount, OutputStream outputStream) {
        Integer accountNum = Integer.parseInt(account);
        try {
            if (accounts.containsKey(accountNum)) {
                accounts.put(accountNum, accounts.get(accountNum) + amount);
                String out = "Deposit successful: " + "New balance is: " + accounts.get(accountNum);
                outputStream.write(out.getBytes());
                outputStream.flush();
            } else {
                outputStream.write("Deposit Failed: Account does not exist.".getBytes());
                outputStream.flush();
            }
        } catch (IOException io) {
            io.printStackTrace();
        }
    } // process deposit


    public static void processWithdrawl(String account, Integer amount, OutputStream outputStream) {
        Integer accountNum = Integer.parseInt(account);

        try {
            if (accounts.containsKey(accountNum)) {
                if (accounts.get(accountNum) >= amount) {
                    Integer newBalance = ((accounts.get(accountNum)) - amount);
                    accounts.put(accountNum, newBalance);
                    String out = "Withdrawl successful " + "New balance is: " + accounts.get(accountNum);
                        outputStream.write(out.getBytes());
                        outputStream.flush();
                } else {
                    outputStream.write("Withdrawl Failed: Insufficient balance".getBytes());
                    outputStream.flush();
                }
            } else {
                outputStream.write("Withdrawl Failed: Account does not exist.".getBytes());
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    } // process withdrawl
} // Server class
