package Sockets;
import java.net.*;
import java.io.*;
import Sockets.Helpers.*;

public class Server {
    private final int PELICANDELAY = 10000;
    private final int TRAFFICDELAY = 7000;

    public static final int PORT = 9090;
    private ServerSocket serverSocket;


    private static final Runnable Trafficlight = () -> {
        // while (true) {}


        try {
            System.out.println("start traffic light");

                //        green / off
                PedestrianHandler.changeWaitingState(); // start waiting state
                //System.out.println("check traffic start waiting here?????");
                Thread.sleep(7000);
                //System.out.println("traffic waited 7 secs");
                PedestrianHandler.ChangeTrafficLight();// green - amber
                //System.out.println("green amber");


                Thread.sleep(3000);
                //System.out.println("traffic waited 3 secs");
                PedestrianHandler.ChangeTrafficLight();// amber - red
                PedestrianHandler.changeWaitingState(); // end waiting state
                //System.out.println("traffic left waiting");


                //        off
                // turn red{
                Thread.sleep(10000); // red
                System.out.println("traffic waited 10 secs");
                PedestrianHandler.ChangeTrafficLight();// red - flashing amber
                System.out.println("traffic red flashing amber");
                // turn amber flashing
                Thread.sleep(5000);
                System.out.println("traffic waited 5 secs");
                TrafficStandby();
                //standby / end - green




                // waiting
                // for 10 seconds (green lights for 7 secs and amber lights for 3 secs)
                // red-man for 10 secs



                // off
                // for 15 secs  (red light for 10 secs and flashing amber for 5 secs)
                // (green man 10 secs )    (flashing green man for 5 secs)




//                PedestrianHandler.takeCoffeeFromWaitingArea();
                //System.out.println("traffic light green");

            System.out.println("end traffic light");
            } catch (InterruptedException e) {
                System.out.println("Traffic light was interrupted");
            }
        System.out.println("left out of here");
    };

    private static final Runnable PelicanLight = () -> {
        //while (true) {}
            try {
                //System.out.println("start  pelican light");

                // red / off
                // start waiting state
                //System.out.println("check pelican here?????");
                Thread.sleep(7000);
                //System.out.println("pelican waited 7 secs");
                PedestrianHandler.ChangePelicanLight();// red - green
                //System.out.println("pelican left waiting");

                // green
                Thread.sleep(3000);
                PedestrianHandler.ChangePelicanLight();// green - flashing green
                //System.out.println(" pelican walk");
                startwalking();
                Thread.sleep(10000);


                // green flashing
                PedestrianHandler.ChangePelicanLight(); // green - flashing green
                startHurryUp();
                Thread.sleep(5000);
                // hurry up


                // red / off



                // waiting
                // for 10 seconds (green lights for 7 secs and amber lights for 3 secs)
                // red-man for 10 secs



                // off
                // for 15 secs  (red light for 10 secs and flashing amber for 5 secs)
                // (green man 10 secs )    (flashing green man for 5 secs)
                //System.out.println("end pelican light");
                // initiateStandbyState
                PelicanStandby();

                //PedestrianHandler.OUT.println();
            } catch (InterruptedException e) {
                System.out.println("Pelican light was interrupted");
            }

    };

    public static void startwalking(){PedestrianHandler.walk();}

    public static void startHurryUp(){PedestrianHandler.hurryUp();}

    public static void PelicanStandby() throws InterruptedException {PedestrianHandler.initiateStandbyState(1);}
    public static void TrafficStandby() throws InterruptedException {PedestrianHandler.initiateStandbyState(0);}

    public void turnWaitingStateOn(){}

    public void turnWaitingStateOff(){}

    public Server(){
        try {
            System.out.println("Server Started");
            serverSocket = new ServerSocket(9091);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to initialise server socket");
            System.out.println("Terminating JVM...");
            System.exit(-1);
        }
        System.out.println("after constructor");
//        startLights();
        startServer();

    }



     public static void startLights() {
        new Thread(Trafficlight).start();
        new Thread(PelicanLight).start();

    }

    public void initiateStandbyState(){
        //PedestrianHandler.OFF;
    }

    private void startServer() {
        try {
            System.out.println("Welcome to the Pedestrian Lights");
            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("Customer has entered the coffee shop");
                //PedestrianHandler.printLog();
                PedestrianHandler pedestrian = new PedestrianHandler(client);
                Thread thread = new Thread(pedestrian);
                thread.start();
            }
        } catch (IOException e) {
            closeServerSocket();
            System.out.println("Failed to start server");
        }
    }




//    public void turnWaitingStateOff(){
//        PedestrianHandler.inWaitingState = false;
//    }
//
//    public void turnWaitingStateOn(){}

    private void closeServerSocket() {
        try {
            if(serverSocket != null)
                serverSocket.close();
        } catch (IOException e) {
            System.out.println("Failed to close barista socket");
        }
    }

    public static void main(String[] args) {Server server = new Server();}
}
//    public static void main(String[] args)throws IOException {
//        ServerSocket buttonPressed = new ServerSocket(9090);
//        Socket s = buttonPressed.accept();
//        //once server has established connection set the thing to standby
//        System.out.println("Booted");
//        panels panel = new panels("standby");
//        //pedestrianSignals ps = new pedestrianSignal("red");
//        //trafficLights ps = new trafficLight("green");
//
//        //priority queue or array of users
//        //if (newConnection is established && priorityQueue.length < 2){
//        //user new = new user("usrName");
//        //"client connected"
//
//        //}
//        //else { print("connection rejected")
//        //      send full to client
//        //} - send this to the client
//
//        //
//
//        InputStreamReader in = new InputStreamReader(s.getInputStream());
//        BufferedReader bf = new BufferedReader(in);
//
//        String userOption = bf.readLine();
//        //check for connection
//
//        System.out.println("client option: " + userOption);
//
//        switch (userOption.charAt(0)){
//
//            case '1':
//             String curStatus = panel.returnStatus();
//            if (curStatus == "standby");
//            {
//            panels.startWaiting();
//            //pedestrianSignals.ChangeStatus("red")
//            //trafficSignals("green")
//            System.out.println("status = " + panel.returnStatus());
//
//            try {
//                Thread.sleep(10000);
//            } catch (InterruptedException e) {}
//            panels.turnOff();
//
//            System.out.println("status = " + panel.returnStatus());
//            }
//
//
//
//            case '2': //report panel Status
//
//            case '3': //report panel Status
//
//            default:
//            break;
//
//
//        }

//if the button is pressed (userOption == 1), then check to see the status of the traffic light

//the status of the light by sending a message to panel
//panel will then feed bck the status
//if panel == standby then send message to it to go to waiting



//    }