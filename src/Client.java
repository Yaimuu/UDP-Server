import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class Client extends UDP {

    protected Com com;

    public Client()
    {
        super();

        this.distantPort = 1026;
        this.com = new Com();

        try {
            this.socket = new DatagramSocket();
            this.port = this.socket.getPort();
            this.distantIp = InetAddress.getByName("localhost");
//            this.distantIp = InetAddress.getByName("yanis-ouled-moussa.fr");
//            this.distantIp = InetAddress.getByName("51.83.99.30");
//            this.distantIp = InetAddress.getByName("35.189.209.247");
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

    }

    public static void main(String[] args)
    {
        Client client = new Client();

        Scanner scan = new Scanner(System.in);

        String username;
        String newMessage;

        System.out.println("Username : ");

        username = scan.nextLine();

        client.sendMessage(username);
        client.receiveMessage();

        int newport = Integer.parseInt(client.getLastMessage().split("\n")[1]);
        client.changePort(newport);

        client.com.newListener(client);
        try {
            client.com.invokeLast();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        do {
            System.out.println("Message for the server : ");

            newMessage = scan.nextLine();

            client.sendMessage(username + "\n" + newMessage);

        } while (!newMessage.equals("STOP"));

        System.out.println("Déconnexion client...");
        try {
            client.getSocket().setSoTimeout(10);
            client.com.shutdown();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        client.getSocket().close();
    }


}

