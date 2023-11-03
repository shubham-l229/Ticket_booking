package tb_server;

import entities.seats;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 * @author Aryan Mehta
 */
public class Server extends Thread {

    private ServerSocket ss;
    private final int PORT_NUM = 14000;
    private HashMap<String,ArrayList<seats>> seatLists;

    private String[] movieLists = {"LEO","TITANIC","ENDGAME","MASTERS","WIFI IN LEHENGA"};
    
    private HashMap<String,ReentrantReadWriteLock> seatLocks;
    
    public Server() {
        seatLocks = new HashMap<>();
        seatLists = new HashMap<>();
        for(int i=0;i<5;i++){
            generateSeats(movieLists[i]);
        }
    }
    
    @Override
    public void run() {
        try {
            ss = new ServerSocket(PORT_NUM);

            while (true) {
                Socket s = ss.accept();

                System.out.println("Client Connected via :" + s.getInetAddress());
                clientHandler cl = new clientHandler(s);
                cl.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generateSeats(String movieName){
        ArrayList<seats> arr = new ArrayList<>();
        for(int i=1;i<=30;i++){
            seats sts = new seats(Integer.toString(i), "NOT_BOOKED","650Rs",movieName);
            ReentrantReadWriteLock rl = new ReentrantReadWriteLock();
            arr.add(sts);
            seatLocks.put(sts.getMovie_name()+sts.getSeat_number(), rl);
        }
        seatLists.put(movieName, arr);
    }
    
    private void changeSeatStatus(String seatNum,String status){
        
    }
    
    class clientHandler extends Thread {

        private String clientUsername;
        private DataInputStream data_in;
        private DataOutputStream data_out;
        private Socket clientSocket;

        private HashMap<clientHandler,ReentrantReadWriteLock> heldLocks;
        
        private seatsDAO sd;
        
        public clientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
            sd = new seatsDAO();
            heldLocks = new HashMap<>();
        }

        @Override
        public void run() {
            try {
                data_in = new DataInputStream(clientSocket.getInputStream());
                data_out = new DataOutputStream(clientSocket.getOutputStream());

                clientUsername = data_in.readUTF();

                System.out.println(clientUsername + " Connected!");
                
                //send seats list
                sendSeatList();
                
                while (!clientSocket.isClosed()) {
                    String res = data_in.readUTF();
                    
                    if(res.equals("SEAT_READ_INIT")){
                        handleReadRequest();
                    }else if(res.equals("SEAT_UPDATE_REQUEST")){
                        sendSeatList();
                    }else if(res.equals("SEAT_READ_UNLOCK")){
                        handleReadUnlock();
                    }else if(res.equals("SEAT_WRITE_INIT")){
                        handleWriteRequest();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        private void sendSeatList()throws IOException{
            data_out.writeInt(seatLists.size());
            
            for (Map.Entry<String, ArrayList<seats>> entry : seatLists.entrySet()) {
                data_out.writeUTF(entry.getKey());
                for(seats s : seatLists.get(entry.getKey())){
                    byte[] data = sd.serializeObject(s);
                    data_out.writeInt(data.length);
                    data_out.write(data);
                }
            }
        }
        
        private void handleReadRequest() throws IOException{
            String seatNum = data_in.readUTF();
            
            ReentrantReadWriteLock rll = seatLocks.get(seatNum);
            
            if(rll.isWriteLocked()){
                data_out.writeUTF("SEAT_WRITE_LOCKED");
            }else{
                Lock readLock = rll.readLock();
                readLock.lock();
                data_out.writeUTF("SEAT_READ_AQUIRED");
                heldLocks.put(this, rll);
            }
        }
        
        private void handleReadUnlock() throws IOException{
            String seatNum = data_in.readUTF();
            
            ReentrantReadWriteLock rll = seatLocks.get(seatNum);
            
            Lock rl = rll.readLock();
            rl.unlock();
            data_out.writeUTF("SEAT_READ_UNLOCKED");
            heldLocks.remove(this,rl);
        }
        
        private void handleWriteRequest() throws IOException{
            String seatNum = data_in.readUTF();
            
            ReentrantReadWriteLock rll = seatLocks.get(seatNum);
            
            if(rll.isWriteLocked()||rll.getReadLockCount()!=0){
                data_out.writeUTF("FILE_IN_USE");
            }else{
                Lock wl = rll.writeLock();
                wl.lock();
                data_out.writeUTF("SEAT_WRITE_SUCCESS");
                heldLocks.put(this, rll);
            }
        }
    }
}
