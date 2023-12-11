package Sockets.Helpers;

public class panels implements Runnable{
    
    static String state;

    public panels (String state){
        this.state = state;
    }

    public String returnStatus(){
    return state;
    }

    public static void startWaiting(){
        // the state changes to waiting 
        // the light becomes 
        state = "waiting";
        
    }

    public static void turnOff(){
        state = "off";
    }

    //this will start a 10 second tmer and make the 
    //panel turn off after
    // in this state the 
    public void run(){

        

    }

    
}
