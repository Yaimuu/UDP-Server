import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Main {

    public static void main(String[] args)
    {
        System.out.println("Ports utilisés : " + Arrays.toString(Tool.portDiscovery(1024,2048).toArray()));
    }

    private static class Tool
    {
        public static boolean portAvailable(int port) {
            DatagramSocket ds = null;
            try {
                ds = new DatagramSocket(port);
                ds.setReuseAddress(true);
                return true;
            }
            catch (SocketException e)
            {
                return false;
            }
            finally {
                if (ds != null) {
                    ds.close();
                }
            }
        }

        public static List<Integer> portDiscovery(int first, int last)
        {
            List<Integer> portUsed = new ArrayList<>();

            for(int port = first; port < last; port++)
            {
                if(!portAvailable(port))
                {
                    portUsed.add(port);
                }
            }

            return portUsed;
        }


    }
}
