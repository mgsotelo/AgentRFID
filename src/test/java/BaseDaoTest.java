import com.google.cloud.firestore.Firestore;
import com.mg.rfid.java.daos.BaseDao;
import org.junit.Test;

import static org.junit.Assert.*;

public class BaseDaoTest {

    @Test
    public void getFirestoreDB() {
        try{
            Firestore fdb = BaseDao.getFirestoreDB();
            assertEquals("alumnos", fdb.collection("alumnos").getPath());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}