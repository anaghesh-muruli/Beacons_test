package anaghesh.beacons_test;

import android.util.Log;

import java.util.ArrayList;

public class CarData {
    String carVin,carChassisNumber,carModel;


    public CarData(String carVin,String carChassisNumber,String carModel) {

        this.carVin = carVin;
        this.carChassisNumber=carChassisNumber;
        this.carModel=carModel;
    }

    public String getcarVin() {
        return String.valueOf(carVin);
    }
    public String getCarChassisNumber() { return carChassisNumber; }
    public String getCarModel() {
        return carModel;
    }


}
