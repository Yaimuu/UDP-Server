import java.net.DatagramSocket;
import java.util.*;
import java.util.concurrent.*;

public class Com implements Executor {

    private ExecutorService executorService;
    private Set<Callable<String>> callables;
    private List<Future<String>> futures;
    private int userLimit = Runtime.getRuntime().availableProcessors();
    private UDP udpService;
    private Server server;

    public Com()
    {
        this.executorService = new ThreadPoolExecutor(userLimit, userLimit, 1000, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(10000));
        this.callables = new LinkedHashSet<>();
        this.udpService = new UDP();
    }

    public Com(Server server)
    {
        this.server = server;
        this.executorService = new ThreadPoolExecutor(userLimit, userLimit, 1000, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(10000));
        this.callables = new LinkedHashSet<>();
        this.udpService = new UDP();
    }

    public Com(int port, Server server)
    {
        this(server);
        this.udpService = new UDP(port);
    }

    public Com(int port, int queueLimit, Server server)
    {
        this(port, server);
        this.executorService = new ThreadPoolExecutor(userLimit, userLimit, 1000, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(queueLimit));
    }

    @Override
    public void execute(Runnable command) {
        command.run();
    }

    public void newListener(UDP udp)
    {
        this.callables.add(new Callable<String>() {
            @Override
            public String call() throws Exception {
//                udpService = new UDP(port);
//                System.out.println(port);
                do {
                    if(udp.socket.isClosed())
                        break;
                    udp.receiveMessage();
                }while (!udp.socket.isClosed());

                return null;
            }
        });
    }

    public void newConnection(String firstUsername, String userInformations)
    {
        this.callables.add(new Callable<String>() {
            @Override
            public String call() throws Exception {

                Server.State userState = Server.State.Up;

                int port = Integer.parseInt(userInformations.split(":")[1]);
                System.out.println(firstUsername + " communication port : " + port);

                UDP threadedUDP = new UDP(port);
                String username = firstUsername;

                while (userState == Server.State.Up)
                {
                    threadedUDP.receiveMessage();

                    username = threadedUDP.lastMessage.split("\n")[0];
                    String message = threadedUDP.lastMessage.split("\n")[1];

                    System.out.println(username + " a envoyé : " + message);
                    for (Map.Entry<String, String> user : server.getUsersConnected().entrySet())
                    {
                        System.out.println("Client : " + user.getKey() + " | IP + ports : " + user.getValue());

                        int newPort = Integer.parseInt(user.getValue().split(":")[2]);
                        threadedUDP.changePort(newPort);

                        threadedUDP.sendMessage("[MESSAGE]" + username + ": " + message + " - "
                                + threadedUDP.socket.getInetAddress() + ":" + threadedUDP.socket.getLocalPort());
                    }

                    Server.writeFile(threadedUDP.getLastMessage());

                    if(threadedUDP.getLastMessage().equals("STOP"))
                    {
                        userState = Server.State.Down;
                        System.out.println(username + " se déconnecte...");
                    }
                }

                threadedUDP.socket.close();
                server.removeUser(username);

                return username;
            }
        });
    }

    public void newTask(Callable task)
    {
        this.callables.add(task);
    }

    public void invokeAll() throws InterruptedException {
//        this.futures = executorService.invokeAll(this.callables);
//        String result = executorService.invokeAny(callables);
        for (Callable<String> call:
                this.callables) {
            Future<String> future = executorService.submit(call);
        }


    }

    public void invokeLast() throws InterruptedException {

        List<Callable<String>> tmpCalls = new ArrayList<>(this.callables);
        Future<String> future = executorService.submit(tmpCalls.get(tmpCalls.size()-1));

    }

    public void shutdown()
    {
        this.udpService.socket.close();
        this.executorService.shutdown();
    }

//    public ExecutorService getExecutorService() {
//        return executorService;
//    }
//
//    public void setExecutorService(ExecutorService executorService) {
//        this.executorService = executorService;
//    }

    public Set<Callable<String>> getCallables() {
        return callables;
    }

    public void setCallables(Set<Callable<String>> callables) {
        this.callables = callables;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public int getUserLimit() {
        return userLimit;
    }

    public void setUserLimit(int userLimit) {
        this.userLimit = userLimit;
    }

    public String getLastMessage()
    {
        return this.udpService.getLastMessage();
    }
}
