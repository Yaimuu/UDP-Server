import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class Server extends UDP {

    public static Server server;
    public static String logFile = "logs.txt";

    protected State state;

    protected Com com;
    private int firstClientPort = 15000;

    private HashMap<String, String> usersConnected = new HashMap<>();

    public enum State {
        Up,
        Down
    }

    public Server()
    {
        super();
        try {
            this.port = 1026;
            this.socket = new DatagramSocket(this.port);
            this.com = new Com(this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        server = new Server();
        server.run();

        try {
            if(server != null)
            {
                System.out.println("Server is running...");

                while (server.getState() == State.Up)
                {
                    System.out.println("Able to listen to new client : " + server.usersConnected.size() + "/" + server.com.getUserLimit());

                    server.receiveMessage();

                    String username = server.lastMessage;
                    int newPort = server.firstClientPort + server.usersConnected.size();

                    if(server.usersConnected.size() < server.com.getUserLimit())
                    {
                        server.addUser(username, InetAddress.getLocalHost().getHostAddress() + ":" + newPort + ":" + server.getDistantPort());
                        server.sendMessage("Server says : " + username + " is connected ! " + server.usersConnected.size() + "/" + server.com.getUserLimit() + "\n"
                                                + newPort);

                        server.com.newConnection(username, server.usersConnected.get(username));
                        server.com.invokeLast();
                    }
                    else
                    {
                        server.sendMessage("Sorry " + username + ", server is full !");
                    }


                }
            }
        }
        catch (Exception e)
        {
            System.out.println("Server ERROR : " + e.getMessage());
            e.printStackTrace();
        }
    }



    public void run()
    {
        this.state = State.Up;
    }

    public void exit()
    {
        this.state = State.Down;
        this.com.shutdown();
        this.socket.close();
    }

    public static void writeFile(String message)
    {
        File logs = new File(logFile);
        try {
            if (!logs.exists() ) {
                logs.createNewFile();
                System.out.println("File created: " + logs.getName());
            }
            FileWriter myWriter = new FileWriter(logFile, true);
            myWriter.write(message + "\n");
            myWriter.close();


        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void addUser(String name, String ip_port)
    {
        this.usersConnected.put(name, ip_port);
    }

    public void removeUser(String name)
    {
        this.usersConnected.remove(name);
    }

    public HashMap<String, String> getUsersConnected() {
        return usersConnected;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }


}
