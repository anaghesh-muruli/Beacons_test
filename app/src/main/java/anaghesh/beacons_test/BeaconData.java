package anaghesh.beacons_test;

import android.util.Log;

import java.util.ArrayList;

public class BeaconData {
    String BeaconPublicId,MacId,UUID, Battery;


    public BeaconData(String BeaconPublicId,String MacId,String UUID,String Battery) {

        this.BeaconPublicId = BeaconPublicId;
        this.MacId=MacId;
        this.UUID=UUID;
        this.Battery=Battery;
    }

    public String getBeaconPublicId() { return BeaconPublicId;    }
    public String getMacId() { return MacId; }
    public String getUUID() {
        return UUID;
    }
    public String getBattery() {return String.valueOf(Battery);    }


}
