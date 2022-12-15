package com.example.bluetoothupdated;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

public class Calculation {
    SqliteData sqliteData;
    int Temperature,Pressure,PPM;


    public Calculation(SqliteData sqliteData, int temperature, int pressure, int PPM) {
        this.sqliteData = sqliteData;
        Temperature = temperature;
        Pressure = pressure;
        this.PPM = PPM;
    }

    public String method() {

        String diameterOfOrifee = sqliteData.get_Diameter_of_Orifice();
        float DiameterOfOrifee = Float.parseFloat(diameterOfOrifee);

        String upstreamPipeDiameter = sqliteData.get_Upstream_lateral_pipe_diameter();
        Float UpstreamPipeDiameter = Float.parseFloat(upstreamPipeDiameter);

        String coefficientOfDischarge = sqliteData.get_Coefficient_of_discharge();
        Float CoefficientOfDischarge = Float.parseFloat(coefficientOfDischarge);


        String exponentialFactor = sqliteData.get_Expansion_factor();
        Float ExponentialFactor = Float.parseFloat(exponentialFactor);


        int DiffrentialPressure = Pressure;
        String temp = String.valueOf(Temperature);

        Float DensityOfFluid=DensityOfFluid(temp);
        Float DiameterRatio = DiameterRatio(DiameterOfOrifee, UpstreamPipeDiameter);
        Float RootOf_OneMinus_DM_RaiseFour = RootOf_OneMinus_DM_RaiseFour(DiameterRatio);
        Float PiByFourMultiplySquareDiamterofOrifee = DiameterOfOrifee(DiameterOfOrifee);
        Float RootOfDensityandPressure = RootOfDensityandPressure(DiffrentialPressure, DensityOfFluid);


        Float finalValue = CoefficientOfDischarge * RootOf_OneMinus_DM_RaiseFour * ExponentialFactor * PiByFourMultiplySquareDiamterofOrifee * RootOfDensityandPressure;


        String Round = String.format("%.6f", finalValue);
        return Round;
    }

    private Float DensityOfFluid(String temp) {
        Float tempMain = Float.parseFloat(temp);
        Float val = Float.valueOf(Math.round(tempMain));

        Float density = sqliteData.getDensity(val);
        // Float density=1.176F;
        //Float DensityOfFluid = density;
        return density;
    }

    private float DiameterRatio(Float diameterOfOrifee, Float upstreamPipeDiameter) {
        Float DiameterRatio = diameterOfOrifee / upstreamPipeDiameter;
        String RatioString = String.format("%.4f", DiameterRatio);
        Float Ratio = Float.parseFloat(RatioString);
        return Ratio;
    }

    private Float RootOf_OneMinus_DM_RaiseFour(Float diameterRatio) {
        float DiameterRaiseToFour = (float) Math.pow(diameterRatio, 4);
        float MinusFrom1 = 1 - DiameterRaiseToFour;
        float Sqroot = (float) Math.sqrt(MinusFrom1);
        float OneDivideBysqRoot = 1 / Sqroot;
        return OneDivideBysqRoot;
    }


    private Float DiameterOfOrifee(Float diameterOfOrifee) {
        float piByFour = (float) (3.14 / 4);
        float val = diameterOfOrifee * diameterOfOrifee;
        return piByFour * val;
    }

    private float RootOfDensityandPressure(int diffrentialPressure, Float densityOfFluid) {
        float Multiply = 2 * diffrentialPressure * densityOfFluid;

        float sqreRoot = (float) Math.sqrt(Multiply);
        String RatioString = String.format("%.6f", sqreRoot);

//        DecimalFormat decimalFormat=new DecimalFormat("00.0000");
//        String finalPercentage = decimalFormat.format(sqreRoot);

        float ratio = Float.parseFloat(RatioString);
        return ratio;
    }

}


