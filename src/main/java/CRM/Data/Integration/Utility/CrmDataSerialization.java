package CRM.Data.Integration.Utility;

import org.springframework.stereotype.Component;

import java.io.*;
import java.util.HashMap;

@Component
public class CrmDataSerialization {
    public byte[] serializeCrmData(HashMap<String, Object> crmData, String crmDataName) {
        byte[] serializedData = null;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(crmData);
            oos.flush();
            serializedData = bos.toByteArray();

            try (FileOutputStream fio = new FileOutputStream(crmDataName)) {
                fio.write(serializedData);
                System.out.println("Serialized data is saved in: " + crmDataName);
            }
        } catch (IOException i) {
            i.printStackTrace();
        }
        return serializedData;
    }

//    public void deserialize(String filename){
//
//        CrmData crmData = new CrmData();
//        try (FileInputStream fis = new FileInputStream(filename);
//             ObjectInputStream ois = new ObjectInputStream(fis)){
//            crmData = (CrmData) ois.readObject();
//            System.out.println(crmData);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
}