import beans.Equipment;
import beans.Temporal;
import com.google.cloud.firestore.Firestore;
import daos.BaseDao;
import daos.Dao;
import daos.EquipmentDao;
import daos.TemporalDao;
import rfid.UtilRFID;

import java.util.Calendar;
import java.util.Optional;

public class AgentRFID {

    private static Dao eqdao, tempdao;


    public static void main(String[] args)  {

        //TODO añadir log

        try {
            eqdao = new EquipmentDao();
            tempdao = new TemporalDao();
            Firestore fdb = BaseDao.getFirestoreDB();
            UtilRFID myrfid = new UtilRFID();
            myrfid.setHostname("TEST_HOSTNAME");

            AgentRFID agent = new AgentRFID();
            agent.run(eqdao, tempdao, fdb, myrfid);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private synchronized void run(Dao eqdao, Dao tempdao, Firestore fdb, UtilRFID myrfid) {

        while(true){
            try {
                System.out.println("always running program ==> " + Calendar.getInstance().getTime());
                String epc = myrfid.epcRead();
                this.wait(5000);
                myrfid.getReader().stop();
                myrfid.getReader().disconnect();
                Optional<Equipment> currenteq= eqdao.get(epc, fdb);

                if(currenteq.isPresent()){

                    Temporal myoldeq = new Temporal();
                    myoldeq.setEpc(epc);
                    tempdao.insert(myoldeq,fdb);

                } else {

                    Equipment myneweq = new Equipment();
                    myneweq.setEpc(epc);
                    eqdao.insert(myneweq, fdb);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


}
