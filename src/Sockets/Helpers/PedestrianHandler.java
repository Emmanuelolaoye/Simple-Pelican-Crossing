package Sockets.Helpers;

import Sockets.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

public class PedestrianHandler implements Runnable {


    private static Set<PedestrianHandler> pedestrian = ConcurrentHashMap.newKeySet();


    private static BlockingQueue<String> PelicanLight = new LinkedBlockingDeque<String>();
    private static BlockingQueue<String> TrafficLight = new LinkedBlockingDeque<String>();

    private static BlockingQueue<String> PelicanLightOff = new LinkedBlockingDeque<String>();
    private static BlockingQueue<String> TrafficLightOff = new LinkedBlockingDeque<String>();


    // waiting
    // for 10 seconds (green lights for 7 secs and amber lights for 3 secs)
    // red-man for 10 secs


    // off
    // for 15 secs  (red light for 10 secs and flashing amber for 5 secs)
    // (green man 10 secs )    (flashing green man for 5 secs)

    private Socket socket;
    private BufferedReader in;


    private static PrintWriter OUT;
    private String NAME;
    private String ID;

    public static boolean inWaitingState = false;

    public static boolean buttonPressed = false;

    public static boolean TrafficLightOn = false;
    public static boolean PelicanLightOn = false;

    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";

    private enum LightType {

        PelicanLight,
        TraficLight,
        Pelican,
        Traffic,
        EXIT,
        STATUS,
        INVALID
    }

    public PedestrianHandler(Socket socket) {
        this.socket = socket;
        try {
            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            OUT = new PrintWriter(this.socket.getOutputStream(), true);
            getPedestrianName();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to initialize client handler reader and writer");
        }
        System.out.println("done ped contructor");

    }

    public void getPedestrianName() {
        OUT.println("> What is your name?");
        while (true) {
            try {
                NAME = in.readLine();
                ID = String.valueOf(new Random().nextInt(99999999));
                //OUT.println("hi "+ NAME);
                if (addPedestrian(ID)) {
                    OUT.println("> This is a Pedestrian Crossing " + NAME + ", Would you like to cross?");
                    return;
                }
                OUT.println("> Invalid name, please try again");
            } catch (IOException e) {
                System.out.println("Failed to assign a client name");
            }
        }
    }

    public boolean addPedestrian(String id) {
        Iterator<PedestrianHandler> clientIterator = pedestrian.iterator();
        while (clientIterator.hasNext()) {
            PedestrianHandler pedestrian = clientIterator.next();
            if (pedestrian.getId().equals(id))
                return false;
        }
        pedestrian.add(this);


        return true;
    }

    public void broadcastTrafficLight(String message) throws IOException {
        //        Iterator<CustomerHandler> customerIterator = customers.iterator();
        for (PedestrianHandler pedestrian : pedestrian) {
            if (pedestrian.getId().equals(ID)) {
                OUT.println(message);
            }
        }
    }


    @Override
    public void run() {
        try {
            while (socket.isConnected()) {
                //System.out.println("run loop start");
                String request = in.readLine();

                OUT.println("mnnn");

                if (request == null)
                    break;
                if (request.equalsIgnoreCase("exit")) {
                    break;
                }
                if (request.contains("button")) {
                    //System.out.println("--------- \n button pressed \n ---------");
                    buttonPressed();
                } else if (request.contains("status")) {
                    status();
                } else {
                    OUT.println("invalid invalid");

                }
                //System.out.println("run loop end");
            }
        } catch (IOException e) {
            System.out.println("client handler '" + ID + "' failed to retrieve " + NAME + " request");
        }
        close();
    }

    public void buttonPressed() throws IOException {
        broadcastTrafficLight("button pressed to everyone");

        if (inWaitingState) {
            System.out.println("ignored button press");
            OUT.println(" > button has already been pressed");
            return;
        } else {
            PelicanLightOn = true;
            TrafficLightOn = true;
            //System.out.println("panel");
            turnPanelOn();
        }

    }

    public void status() {
        int TrafficLightStatus = TrafficLightOff.size();
        int PelicalLightStatus = PelicanLightOff.size();

        if (PelicalLightStatus == 1) {
            OUT.println("Red light");
        } else if (PelicalLightStatus == 2) {
            OUT.println("green light");
        }else if (PelicalLightStatus == 3) {System.out.println("flashing light");}
        else {
            OUT.println("Red light");
        }
    }

    public void OFF(){
        OUT.println("done");
    }


    public void turnPanelOn(){

        for (int i = 0; i <= 3; i++ ){
            PelicanLight.add("pelican");
            TrafficLight.add("traffic");
        }
        OUT.println(ANSI_RED + "> RED LIGHT "+ ANSI_RESET +" Please Wait \n");
        Server.startLights();
    }

    public static void walk(){
        final String ANSI_GREEN = "\u001B[32m";
        OUT.println(ANSI_GREEN + "> Green Light  "+ ANSI_RESET + " Start Walking\n" );
    }

    public static void hurryUp(){
        final String ANSI_YELLOW = "\u001B[33m";
        OUT.println(ANSI_YELLOW + "> Flashing Green Light " + ANSI_RESET +  " You better hurry up!!!\n" );
    }


    public static void ChangePelicanLight() throws InterruptedException {
        try {
            PelicanLightOff.put(PelicanLight.take());
            //OUT.println("changing");
        } catch (Exception e){
            System.out.println("Pelican light was interrupted");
        }
    }

    public static void ChangeTrafficLight() throws InterruptedException {
        try {
            TrafficLightOff.put(TrafficLight.take());
        } catch (Exception e){
            System.out.println("Traffic light was interrupted");
        }
    }


    public static void initiateStandbyState(int Light) throws InterruptedException {
        if (Light == 0) {PelicanLightOn = false;}
        if (Light == 1) {TrafficLightOn = false;}

        confirmStandbyState();
    }

    public static void confirmStandbyState(){
        if (!PelicanLightOn && !TrafficLightOn){
            OUT.println(ANSI_RED + "> RED LIGHT "+ ANSI_RESET +"  STOP!!! \n");
            getWaitingState();

        }
    }

    public static void changeWaitingState(){
       if (inWaitingState){
           inWaitingState = false;
       }else{
           inWaitingState = true;
       }

    }

    public static void getWaitingState(){
        System.out.println(inWaitingState);
    }

    public String getId() {
        return ID;
    }
    public void close() {
        //removeclient();
        System.out.println(NAME + " has left the crossing");
        OUT.println("Thank you for crossing with us " + NAME + "!");

        try {
            if(socket != null)
                socket.close();
            OUT.close();
            in.close();
        } catch (IOException e) {
            System.out.println("Failed to ");
        }

    }
    public void getLog(){


    }


}
