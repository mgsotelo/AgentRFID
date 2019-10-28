package com.mg.rfid.java;

import com.impinj.octane.ImpinjReader;
import com.mg.rfid.java.beans.Equipment;
import com.mg.rfid.java.beans.Technician;
import com.mg.rfid.java.beans.Temporal;
import com.google.cloud.firestore.Firestore;
import com.mg.rfid.java.daos.*;
import com.mg.rfid.java.rfid.UtilRFID;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Optional;

public class AgentRFID {

    private static Dao eqdao, tempdao, techdao;


    public static void main(String[] args)  {

        //TODO añadir log

        try {
            eqdao = new EquipmentDao();
            tempdao = new TemporalDao();
            techdao = new TechnicianDao();

            Firestore fdb = BaseDao.getFirestoreDB();
            UtilRFID myrfid = new UtilRFID();
            String s, hostname="";
            Process p;

            try {


                p = Runtime.getRuntime().exec("sudo arp-scan 192.168.35.0/24 | grep -i impinj | cut -d ' ' -f1 | head -1");
                BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                while( ((s = br.readLine())!= null)){
                    if(s.contains("192")){
                        System.out.println("[AgentRFID] Hostname detectado. El hostname es "+ hostname);
                        hostname = s;
                    }else {
                        System.out.println("[AgentRFID] Hostname no ha podido ser detectado.");
                        hostname = "192.168.35.239";
                        System.out.println("[AgentRFID] Hostname por default es "+ hostname);
                    }
                }
                p.waitFor();
                System.out.println("[AgentRFID] Finalizando ejecución de comandos en vm linux");
                p.destroy();


            } catch (Exception e) {
                e.printStackTrace();
            }

            myrfid.setHostname(hostname);
            myrfid.setReader(new ImpinjReader());
            //myrfid.setHostname("SpeedwayIoT");

            AgentRFID agent = new AgentRFID();
            agent.run(eqdao, tempdao, techdao, fdb, myrfid);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private synchronized void run(Dao eqdao, Dao tempdao, Dao techdao, Firestore fdb, UtilRFID myrfid) {

        while(true){
            try {
                //System.out.println("always running program ==> " + Calendar.getInstance().getTime());
                String epc = myrfid.epcRead();
                this.wait(5000);
                myrfid.getReader().stop();
                myrfid.getReader().disconnect();

                if(!epc.equals("")){
                    Optional<Equipment> currenteq= eqdao.get(epc, fdb);

                    Optional<Technician> currentTech = techdao.get(epc,fdb);

                    Optional<Temporal> temp = tempdao.get(epc,fdb);

                    if(currenteq.isPresent()){

                        if(temp.isPresent()){

                            System.out.println(temp.get().getEpc() + " ya se encuentra en tabla temporal");

                        } else{

                            Temporal myoldeq = temp.get();
                            tempdao.insert(myoldeq,fdb);

                        }

                    } else if (currentTech.isPresent()){

                        Technician mytech = currentTech.get();
                        tempdao.insert(mytech,fdb);

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
