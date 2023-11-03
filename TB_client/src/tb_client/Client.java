package tb_client;

import entities.seats;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Aryan Mehta
 */
public class Client extends Thread {

    private Socket clientSoc;
    private DataInputStream data_in;
    private DataOutputStream data_out;
    private String clientUsername;

    private frame_main fr;
    private seatsDAO sd;
    private HashMap<String, ArrayList<seats>> seatsList;

    public Client(frame_main frame) {
        this.fr = frame;
        this.clientUsername = fr.getClientUsername();
        sd = new seatsDAO();
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
    
    public int sendReadRequest(seats s){
        try {
            data_out.writeUTF("SEAT_READ_INIT");
            data_out.writeUTF(s.getMovie_name()+s.getSeat_number());
            
            String res = data_in.readUTF();
            if(res.equals("SEAT_WRITE_LOCKED")){
                return 0;
            }else if(res.equals("SEAT_READ_AQUIRED")){
                return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
