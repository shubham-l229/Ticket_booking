package tb_client;

import entities.seats;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Aryan Mehta
 */
public class Client extends Thread {

    private Socket clientSoc;
    private DataInputStream data_in;
    private DataOutputStream data_out;
    private String clientUsername;

    private boolean threadStatus = true;

    private ScheduledExecutorService src;

    private frame_main fr;
    private seatsDAO sd;
    private HashMap<String, ArrayList<seats>> seatsList;

    public Client(frame_main frame) {
        this.fr = frame;
        this.clientUsername = fr.getClientUsername();
        sd = new seatsDAO();
        src = Executors.newSingleThreadScheduledExecutor();
    }

    public void setThreadStatus(boolean threadStatus) {
        this.threadStatus = threadStatus;
    }

    private final int PORT_NUM = 14000;

    private boolean status = true;

    @Override
    public void run() {
        try {
            clientSoc = new Socket("127.0.0.1", PORT_NUM);

            data_in = new DataInputStream(clientSoc.getInputStream());
            data_out = new DataOutputStream(clientSoc.getOutputStream());
            seatsList = new HashMap<>();

            data_out.writeUTF(clientUsername);
            getSeatsList();

            fr.addMoviePanels(seatsList);
            src.scheduleAtFixedRate(updateThread, 0, 1, TimeUnit.SECONDS);

            while (!clientSoc.isClosed());

            shutDown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void shutDown() {
        try {
            data_in.close();
            data_out.close();
            clientSoc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getSeatsList() {
        try {
            int movieNum = data_in.readInt();

            for (int i = 0; i < movieNum; i++) {
                String movieName = data_in.readUTF();
                ArrayList<seats> arr = new ArrayList<>();

                for (int j = 0; j < 30; j++) {
                    byte[] data = new byte[data_in.readInt()];
                    data_in.readFully(data);
                    seats ns = sd.deserializeObject(data);
                    arr.add(ns);
                }
                seatsList.put(movieName, arr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }   
    
    public int checkSeatWriteStatus(seats s){
        try {
            data_out.writeUTF("CHECK_SEAT_WRITE_STATUS");
            data_out.writeUTF(s.getMovie_name()+":"+s.getSeat_number());
            
            String res = data_in.readUTF();
            if(res.equals("SEAT_WRITE_LOCKED")){
                return 0;
            }else if(res.equals("SEAT_OPEN")){
                return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 10;
    }
    
    public int sendWriteLockRequest(seats s){
        try {
            data_out.writeUTF("SEAT_WRITE_LOCK");
            data_out.writeUTF(s.getMovie_name()+":"+s.getSeat_number());
            
            String res = data_in.readUTF();
            if(res.equals("SEAT_WRITE_LOCKED")){
                return 0;
            }else if(res.equals("SEAT_LOCK_SUCCESS")){
                return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 10;
    }
    
    public int sendSeatBookingReq(seats s){
        try {
            data_out.writeUTF("SEAT_BOOKING_REQ");
            data_out.writeUTF(s.getMovie_name()+":"+s.getSeat_number());
            
            String res = data_in.readUTF();
            if(res.equals("SEAT_BOOKING_SUCCESS")){
                return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 10;
    }
    
    public int sendWriteUnlock(seats s){
        try {
            data_out.writeUTF("SEAT_WRITE_UNLOCK");
            data_out.writeUTF(s.getMovie_name()+":"+s.getSeat_number());
            
            if((data_in.readUTF()).equals("UNLOCKED")){
                return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    private Thread updateThread = new Thread(() -> {
        if (threadStatus) {
            try {
                data_out.writeUTF("SEAT_UPDATE_REQUEST");
                getSeatsList();
                System.out.println("Got List");
                fr.addMoviePanels(seatsList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    });
}
