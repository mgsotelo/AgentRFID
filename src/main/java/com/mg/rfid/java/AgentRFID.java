package com.mg.rfid.java;

import com.impinj.octane.ImpinjReader;
import com.mg.rfid.java.beans.Equipment;
import com.mg.rfid.java.beans.Temporal;
import com.google.cloud.firestore.Firestore;
import com.mg.rfid.java.daos.BaseDao;
import com.mg.rfid.java.daos.Dao;
import com.mg.rfid.java.daos.EquipmentDao;
import com.mg.rfid.java.daos.TemporalDao;
import com.mg.rfid.java.rfid.UtilRFID;

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
            myrfid.setHostname("192.168.35.239");
            myrfid.setReader(new ImpinjReader());
            //myrfid.setHostname("SpeedwayIoT");

            AgentRFID agent = new AgentRFID();
            agent.run(eqdao, tempdao, fdb, myrfid);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private synchronized void run(Dao eqdao, Dao tempdao, Firestore fdb, UtilRFID myrfid) {

        while(true){
            try {
                //System.out.println("always running program ==> " + Calendar.getInstance().getTime());
                String epc = myrfid.epcRead();
                this.wait(5000);
                myrfid.getReader().stop();
                myrfid.getReader().disconnect();

                if(!epc.equals("")){
                    Optional<Equipment> currenteq= eqdao.get(epc, fdb);

                    if(currenteq.isPresent()){

                        Optional<Temporal> temp = tempdao.get(epc,fdb);

                        if(temp.isPresent()){

                            System.out.println(temp.get().getEpc() + " ya se encuentra en tabla temporal");

                        } else{

                            Temporal myoldeq = new Temporal();
                            myoldeq.setEpc(epc);
                            tempdao.insert(myoldeq,fdb);


                        }

                    } else {

                        Equipment myneweq = new Equipment();
                        myneweq.setEpc(epc);
                        eqdao.insert(myneweq, fdb);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


}
