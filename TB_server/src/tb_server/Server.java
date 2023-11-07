package tb_server;

import entities.seats;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
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
    private HashMap<String, ArrayList<seats>> seatLists;

    private String[] movieLists = {"LEO", "TITANIC", "ENDGAME", "MASTERS", "WIFI IN LEHENGA"};

    private HashMap<String, ReentrantReadWriteLock> seatLocks;

    public Server() {
        seatLocks = new HashMap<>();
        seatLists = new HashMap<>();
        for (int i = 0; i < 5; i++) {
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

    private void generateSeats(String movieName) {
        ArrayList<seats> arr = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            seats sts = new seats(Integer.toString(i), "NOT_BOOKED", "650Rs", movieName);
            ReentrantReadWriteLock rl = new ReentrantReadWriteLock();
            arr.add(sts);
            seatLocks.put(sts.getMovie_name() + ":" + sts.getSeat_number(), rl);
        }
        seatLists.put(movieName, arr);
    }

    private void changeSeatStatus(String seatNum, String status) {
        String[] strs = seatNum.split(":");
        ArrayList<seats> list = seatLists.get(strs[0]);

        for (seats st : list) {
            if (st.getSeat_number().equals(strs[1])) {
                st.setSeat_status(status);
                System.out.println("Status Changed!");
                break;
            }
        }
    }

    class clientHandler extends Thread {

        private String clientUsername;
        private DataInputStream data_in;
        private DataOutputStream data_out;
        private Socket clientSocket;

        private HashMap<clientHandler, ReentrantReadWriteLock> heldLocks;

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

                    switch (res) {
                        case "CHECK_SEAT_WRITE_STATUS":
                            handleCheckRequest();
                            break;
                        case "SEAT_UPDATE_REQUEST":
                            sendSeatList();
                            break;
                        case "SEAT_WRITE_LOCK":
                            handleWriteLock();
                            break;
                        case "SEAT_BOOKING_REQ":
                            handleSeatBooking();
                            break;

                        case "SEAT_WRITE_UNLOCK":
                            handleWriteUnlock();
                            break;
                        default:
                            break;
                    }
                }
            } catch (SocketException se) {
                System.out.println(clientUsername + " disconnected!");
                shutDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void sendSeatList() throws IOException {
            data_out.writeInt(seatLists.size());

            for (Map.Entry<String, ArrayList<seats>> entry : seatLists.entrySet()) {
                data_out.writeUTF(entry.getKey());
                for (seats s : seatLists.get(entry.getKey())) {
                    byte[] data = sd.serializeObject(s);
                    data_out.writeInt(data.length);
                    data_out.write(data);
                }
            }
        }

        private void handleCheckRequest() throws IOException {
            String seatName = data_in.readUTF();

            ReentrantReadWriteLock rll = seatLocks.get(seatName);
            if (rll.isWriteLocked()) {
                data_out.writeUTF("SEAT_WRITE_LOCKED");
            } else {
                data_out.writeUTF("SEAT_OPEN");
            }
        }

        private void handleWriteLock() throws IOException {
            String seatName = data_in.readUTF();
            ReentrantReadWriteLock rll = seatLocks.get(seatName);

            if (rll.isWriteLocked()) {
                data_out.writeUTF("SEAT_WRITE_LOCKED");
            } else {
                Lock wl = rll.writeLock();
                wl.lock();
                data_out.writeUTF("SEAT_LOCK_SUCCESS");
                heldLocks.put(this, rll);
            }
        }

        private void handleSeatBooking() throws IOException {
            String seatNum = data_in.readUTF();

            changeSeatStatus(seatNum, "BOOKED");
            data_out.writeUTF("SEAT_BOOKING_SUCCESS");
            heldLocks.remove(this, seatLocks.get(seatNum));
        }

        private void handleWriteUnlock() throws IOException {
            String seatNum = data_in.readUTF();
            
            ReentrantReadWriteLock rll = seatLocks.get(seatNum);
            Lock wl = rll.writeLock();
            wl.unlock();
            heldLocks.remove(this,rll);
            data_out.writeUTF("UNLOCKED");
        }

        private void shutDown() {
            try {
                clientSocket.close();
                data_in.close();
                data_out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
