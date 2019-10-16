package com.mg.rfid.java.rfid;

import com.impinj.octane.*;

import java.util.List;

public class UtilRFID {

    /*
    Point your PC browser to https://speedwayr-xx-xx-xx.local where xx-xx-xx are the last 6 characters of the MAC.
    You can also use the reader IP address if it is known.  https://xarray-xx-xx-xx.local for xArray
    or https://xspan-xx-xx-xx.local for xSpan.   Browser supported: Chrome, FIrefox, and Edge.
    For example, if connecting to a Speedway Revolution reader with a MAC address of 00:16:25:10:54:F9, the url to
    enter in your browser would be: https://speedwayr-10-54-f9.local
    In some instances the '.local' is not required (i.e. reader is on an enterprise network)
    Be sure to include the HTTPS (secure browser) in order to get the Speedway Connect Web UI.
    A message may appear warning about security certificate, select 'Continue Anyway' or 'Ignore'
    (exact message and continue option will depend on your browser).
    Enter the user name and password, the default is:
    Username: root
    Password: impinj
    Configure the application as desired.
    Enter the license key if available and click [Save].  The license keys are loaded in the
    "Speedway Connect Administration" section of the UI. If you need a license key for Connect 2.X, please purchase
    SKU IPJ-S4001.
     */

    private String hostname = "";
    private String epc = "";
    private ImpinjReader reader;

    public String epcRead() throws Exception {
        //System.out.println(reader.toString());
        //this.setReader(new ImpinjReader());
        System.out.println(this.getReader().getAddress() + " = address");

        this.getReader().connect(getHostname());

        Settings settings = getReader().queryDefaultSettings();
        settings.setReaderMode(ReaderMode.AutoSetDenseReader);

        AntennaConfigGroup antennas = settings.getAntennas();
        antennas.disableAll();
        antennas.enableById(new short[]{1});
        antennas.getAntenna((short) 1).setIsMaxRxSensitivity(false);
        antennas.getAntenna((short) 1).setIsMaxTxPower(false);
        antennas.getAntenna((short) 1).setTxPowerinDbm(20.0);
        antennas.getAntenna((short) 1).setRxSensitivityinDbm(-70);

        //settings.getAutoStart().setMode(AutoStartMode.Periodic);
        //settings.getAutoStart().setPeriodInMs(10000);
        //settings.getAutoStop().setMode(AutoStopMode.Duration);
        //settings.getAutoStop().setDurationInMs(5000);

        getReader().setTagReportListener((impinjReader, tagReport) -> {
            List<Tag> tags = tagReport.getTags();

            for (Tag t : tags) {
                System.out.print(" EPC: " + t.getEpc().toString());
                setEpc(t.getEpc().toString());

                if (getReader().getName() != null) {
                    System.out.print(" Reader_name: " + getReader().getName());
                } else {
                    System.out.print(" Reader_ip: " + getReader().getAddress());
                }

                if (t.isAntennaPortNumberPresent()) {
                    System.out.print(" antenna: " + t.getAntennaPortNumber());
                }

                if (t.isFirstSeenTimePresent()) {
                    System.out.print(" first: " + t.getFirstSeenTime().ToString());
                }

                if (t.isLastSeenTimePresent()) {
                    System.out.print(" last: " + t.getLastSeenTime().ToString());
                }

                if (t.isSeenCountPresent()) {
                    System.out.print(" count: " + t.getTagSeenCount());
                }

                if (t.isRfDopplerFrequencyPresent()) {
                    System.out.print(" doppler: " + t.getRfDopplerFrequency());
                }

                if (t.isPeakRssiInDbmPresent()) {
                    System.out.print(" peak_rssi: " + t.getPeakRssiInDbm());
                }

                if (t.isChannelInMhzPresent()) {
                    System.out.print(" chan_MHz: " + t.getChannelInMhz());
                }

                if (t.isRfPhaseAnglePresent()) {
                    System.out.print(" phase angle: " + t.getPhaseAngleInRadians());
                }

                if (t.isFastIdPresent()) {
                    System.out.print("\n     fast_id: " + t.getTid().toHexString());

                    System.out.print(" model: " +
                            t.getModelDetails().getModelName());

                    System.out.print(" epcsize: " +
                            t.getModelDetails().getEpcSizeBits());

                    System.out.print(" usermemsize: " +
                            t.getModelDetails().getUserMemorySizeBits());
                }

                System.out.println("");
            }
        });

        getReader().applySettings(settings);
        getReader().start();

        //reader.stop();
        //reader.disconnect();

        return getEpc();
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public ImpinjReader getReader() {
        return reader;
    }

    public void setReader(ImpinjReader reader) {
        this.reader = reader;
    }
}
