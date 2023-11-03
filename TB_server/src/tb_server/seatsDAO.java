package tb_server;

import entities.seats;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
/**
 *
 * @author Aryan Mehta
 */
public class seatsDAO {
    public byte[] serializeObject(seats s) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(s);
            oos.flush();
            return bos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public seats deserializeObject(byte[] bytes){
        try {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            return (seats) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    public ArrayList<seats> deserializeObject(byte[] data) {
//        try {
//            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
//            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
//            ArrayList<seats> object = (ArrayList<seats>) objectInputStream.readObject();
//            objectInputStream.close();
//            return object;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    public byte[] serializeObject(ArrayList<seats> object) {
//        try {
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
//            objectOutputStream.writeObject(object);
//            objectOutputStream.flush();
//            objectOutputStream.close();
//            return byteArrayOutputStream.toByteArray();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//    public byte[] serializeObject(HashMap<String, ArrayList<seats>> object) {
//        try {
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
//            objectOutputStream.writeObject(object);
//            objectOutputStream.flush();
//            objectOutputStream.close();
//            return byteArrayOutputStream.toByteArray();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    public HashMap<String, ArrayList<seats>> deserializeObject(byte[] data){
//        try {
//            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
//            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
//            HashMap<String, ArrayList<seats>> object = (HashMap<String, ArrayList<seats>>) objectInputStream.readObject();
//            objectInputStream.close();
//            return object;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
}
