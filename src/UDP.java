import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDP {

    protected DatagramSocket socket;
    protected InetAddress distantIp;
    protected int port = 0;
    protected int distantPort = 0;
    protected String lastMessage;

    protected UDP()
    {
        try {
            this.socket = new DatagramSocket();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    protected UDP(int port)
    {
        try {
            System.out.println("Port : " + port);
            this.port = port;
            this.socket = new DatagramSocket(port);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void changePort(int port)
    {
        try {
            this.distantPort = port;
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message)
    {
        DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), this.distantIp, this.distantPort);

        try{
            this.socket.send(packet);
        }
        catch (Exception e)
        {
            System.out.print("UDP Send Error : ");
            e.printStackTrace();
        }
    }

    public void receiveMessage()
    {
        byte[] buf = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buf, 1024);
        try
        {
            this.socket.receive(packet);
        }
        catch (Exception e)
        {
            System.out.print("UDP Receive Error : ");
            e.printStackTrace();
        }
        this.distantIp = packet.getAddress();
        this.distantPort = packet.getPort();

        this.lastMessage = new String(packet.getData(), 0, packet.getLength());

        if(!this.lastMessage.isEmpty())
            System.out.println(this.lastMessage);
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public void setSocket(DatagramSocket socket) {
        this.socket = socket;
    }

    public InetAddress getDistantIp() {
        return distantIp;
    }

    public void setDistantIp(InetAddress distantIp) {
        this.distantIp = distantIp;
    }

    public int getLocalPort() {
        return port;
    }

    public void setLocalPort(int port) {
        this.port = port;
    }

    public int getDistantPort() {
        return distantPort;
    }

    public void setDistantPort(int distantPort) {
        this.distantPort = distantPort;
    }

    public String getLastMessage() {

        return lastMessage != null ? lastMessage : "";
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }


}
