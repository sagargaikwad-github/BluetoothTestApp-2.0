package com.example.bluetoothupdated;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class CalculationsActivity extends AppCompatActivity    {
    Button Save;
    TextInputEditText d, D, C, E, DeltaP, Temperature;
    TextView FlowRateTV;
    SqliteData sqliteData;
    String Round;
    Float finalValue;
    CaluclationResult caluclationResult;


    //    public CalculationsActivity(String temp) {
//        this.temp = temp;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculations);

        Save = findViewById(R.id.SaveBTN);
        d = findViewById(R.id.d);
        D = findViewById(R.id.D);
        C = findViewById(R.id.C);
        E = findViewById(R.id.E);
        DeltaP = findViewById(R.id.DeltaP);
        Temperature = findViewById(R.id.T);

        FlowRateTV = findViewById(R.id.FlowrateTV);

        sqliteData=new SqliteData(CalculationsActivity.this);

        String diameterOfOrifee = sqliteData.get_Diameter_of_Orifice();
        d.setText(diameterOfOrifee);
        float DiameterOfOrifee=Float.parseFloat(diameterOfOrifee);

        String upstreamPipeDiameter = sqliteData.get_Upstream_lateral_pipe_diameter();
        D.setText(upstreamPipeDiameter);
        Float UpstreamPipeDiameter = Float.parseFloat(upstreamPipeDiameter);


        String  coefficientOfDischarge = sqliteData.get_Coefficient_of_discharge();
        C.setText(coefficientOfDischarge);
        Float CoefficientOfDischarge =Float.parseFloat(coefficientOfDischarge);


        String exponentialFactor = sqliteData.get_Expansion_factor();
        E.setText(exponentialFactor);
        Float ExponentialFactor =Float.parseFloat(exponentialFactor);


        int DiffrentialPressure = 10;
        DeltaP.setText(String.valueOf(DiffrentialPressure));

        String temp="27";
        Temperature.setText(temp);

        Float tempMain=Float.parseFloat(temp);
        Float val= Float.valueOf(Math.round(tempMain));

        Float density=sqliteData.getDensity(val);
        //float density=1.3F;
        Float DensityOfFluid = density;

        Float DiameterRatio = DiameterRatio(DiameterOfOrifee, UpstreamPipeDiameter);

        Float RootOf_OneMinus_DM_RaiseFour = RootOf_OneMinus_DM_RaiseFour(DiameterRatio);

        Float PiByFourMultiplySquareDiamterofOrifee = DiameterOfOrifee(DiameterOfOrifee);

        Float RootOfDensityandPressure = RootOfDensityandPressure(DiffrentialPressure, DensityOfFluid);

        finalValue =CoefficientOfDischarge * RootOf_OneMinus_DM_RaiseFour * ExponentialFactor*PiByFourMultiplySquareDiamterofOrifee * RootOfDensityandPressure;

        Round=String.format("%.6f", finalValue);
        Toast.makeText(this, Round, Toast.LENGTH_SHORT).show();



        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String diameter=d.getText().toString().trim();
              //Float DiameterOfOrifee = Float.parseFloat(diameter);

                String upstreamPipeDiameter =D.getText().toString().trim();
               // Float UpstreamPipeDiameter = Float.parseFloat(D.getText().toString());

                String coefficientOfDischarge = C.getText().toString().trim();
                //Float CoefficientOfDischarge = Float.parseFloat(C.getText().toString());

                String exponentialFactor = E.getText().toString();
               // Float ExponentialFactor = Float.parseFloat(E.getText().toString());

                int DiffrentialPressure = Integer.parseInt(DeltaP.getText().toString());

                boolean updateValues=sqliteData.updateEquationValues(diameter,upstreamPipeDiameter,coefficientOfDischarge,exponentialFactor);
                if(updateValues==true)
                {
                    Toast.makeText(CalculationsActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(CalculationsActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                }

//               String temp=Temperature.getText().toString();
//
//                Float tempMain=Float.parseFloat(temp);
//                Float val= Float.valueOf(Math.round(tempMain));
//
//                Float density=sqliteData.getDensity(val);
//             // Float density=1.176F;
//                Float DensityOfFluid = density;
//
//                Float DiameterRatio = DiameterRatio(DiameterOfOrifee, UpstreamPipeDiameter);
//
//                Float RootOf_OneMinus_DM_RaiseFour = RootOf_OneMinus_DM_RaiseFour(DiameterRatio);
//
//                Float PiByFourMultiplySquareDiamterofOrifee = DiameterOfOrifee(DiameterOfOrifee);
//
//                Float RootOfDensityandPressure = RootOfDensityandPressure(DiffrentialPressure, DensityOfFluid);
//
//               finalValue =CoefficientOfDischarge * RootOf_OneMinus_DM_RaiseFour * PiByFourMultiplySquareDiamterofOrifee * RootOfDensityandPressure;
//
//                Round=String.format("%.6f", finalValue);
//
//                FlowRateTV.setText("Orifiee Mass FlowRate : "+Round +" Kg/s");


            }
        });
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