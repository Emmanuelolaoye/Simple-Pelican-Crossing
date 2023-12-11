package Sockets;
import java.net.*;
import java.io.*;

//This file represents the user interface

//question ... will i be establishing connections with the sever every
//single time 

public final class Client {
//    Scanner sc = new Scanner(System.in);
//
//    Socket button = new Socket("localhost",4999);
//    PrintWriter pr = new PrintWriter(button.getOutputStream());
//
//        while (true){
//        System.out.println("pick a button:");
//        System.out.println("1      Button");
//        System.out.println("2      Status");
//        System.out.println("3        Exit");
//        String num = sc.nextLine();
//        //button pressed
//        //read the bufferReader
//
//
//        switch (num){
//            case "1":
//
//                //originally i ha
//                pr.print('1');
//                pr.flush();
//                break;
//
//            case "2":
//                //status update
//                pr.print('2');
//                pr.flush();
//                break;
//
//            case "3":
//                //status update
//                pr.print('3');
//                pr.flush();
//                break;
//
//            default:
//                break;
//
//        }
//    }

    private Socket socket;
    private BufferedReader input ;
    private BufferedReader keyboard;
    private PrintWriter out;
    private Thread readThread;
    private Thread writeThread;
    public static int waitCounter;

    private final Runnable serverReader = () -> {
        while (socket.isConnected()){
            //System.out.println("read loop start");
            try {
                String serverResponse = input.readLine();
                if(serverResponse!=null) {
                    if (serverResponse.toLowerCase().contains("thank")) {
                        System.out.println(serverResponse);
                        Client.leaveCrossing();
                        return;
                    } else {
                        System.out.println(serverResponse);
                    }
                }
            } catch (IOException e) {
                waitCounter = 0;
                System.out.println("Failed to retrieve server response in server reader");
                try {
                    tryToReconnectToServer();
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                closeThreads();
                leaveCrossing();

            }
            //System.out.println("read loop end");
        }

    };

    private final Runnable serverWriter = () -> {
        while (socket.isConnected()) {
            //System.out.println("write loop start \n menu place holder");
           // System.out.println("menu place holder");
        try {
            String button = keyboard.readLine();
            if(button.isEmpty())
                continue;
            out.println(button);
        } catch (IOException e) {
            System.out.println("Failed to retrieve server response in server writter");
        }

            //System.out.println("write loop end");
    }};

    public Client(){
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(this::closeThreads));
            socket = new Socket("localhost", 9091);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            keyboard = new BufferedReader(new InputStreamReader(System.in));
            out = new PrintWriter(socket.getOutputStream(), true);
            setName(); // TODO: change this
            readThread = new Thread(serverReader);
            writeThread = new Thread(serverWriter);
            readThread.start();
            writeThread.start();
            //System.out.println("left client Constuctor");
        } catch (IOException e) {
            System.out.println("Failed to initialize customer");
            System.out.println("No Traffic Light Server Found \n Good bye  :(");
            leaveCrossing();
        }
    }

    private void setName() {
        try {
            String serverResponse = input.readLine();
            System.out.println(serverResponse);
            while (socket.isConnected()) {
                String nameRequest = keyboard.readLine();
                out.println(nameRequest);
                serverResponse = input.readLine();
                System.out.println(serverResponse);
                if(serverResponse != null){
                    if (serverResponse.toLowerCase().contains("this is"))
                        //System.out.println("come here");
                        return;}
            }
        } catch (IOException e) {
            System.out.println("Failed to retrieve server response");
            leaveCrossing();
        }
    }


    private static void leaveCrossing() {
        System.exit(0);
    }

    private static void tryToReconnectToServer() throws InterruptedException {
        waitCounter = 0;

        while (waitCounter!=10){
            System.out.println("trying " + waitCounter + "timer" );
            Thread.sleep(1000);
            waitCounter ++;
        }

    }
    public void closeThreads() {
        try {
            if(readThread != null)
                readThread.interrupt();
            if(writeThread != null)
                writeThread.interrupt();
            if(out != null)
                out.close();
            if(input != null)
                input.close();
        } catch (IOException e) {
            System.out.println("Failed to close client print writer and two buffered readers and threads ");
        }
    }

    public static void main(String[] args) {Client client = new Client();}

}
