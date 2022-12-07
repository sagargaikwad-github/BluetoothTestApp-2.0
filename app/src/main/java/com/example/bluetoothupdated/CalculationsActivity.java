package com.example.bluetoothupdated;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class CalculationsActivity extends AppCompatActivity {
    Button Flowrate;
    TextInputEditText d, D, C, E, DeltaP, P;
    TextView FlowRateTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculations);

        Flowrate = findViewById(R.id.CalculateFlowRate);
        d = findViewById(R.id.d);
        D = findViewById(R.id.D);
        C = findViewById(R.id.C);
        E = findViewById(R.id.E);
        DeltaP = findViewById(R.id.DeltaP);
        P = findViewById(R.id.P);

        FlowRateTV = findViewById(R.id.FlowrateTV);

        Flowrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Float DiameterOfOrifee = Float.parseFloat(d.getText().toString());
                Float UpstreamPipeDiameter = Float.parseFloat(D.getText().toString());
                Float CoefficientOfDischarge = Float.parseFloat(C.getText().toString());
                Float ExponentialFactor = Float.parseFloat(E.getText().toString());
                int DiffrentialPressure = Integer.parseInt(DeltaP.getText().toString());
                Float DensityOfFluid = Float.parseFloat(P.getText().toString());


                Float DiameterRatio = DiameterRatio(DiameterOfOrifee, UpstreamPipeDiameter);

                Float RootOf_OneMinus_DM_RaiseFour = RootOf_OneMinus_DM_RaiseFour(DiameterRatio);

                Float PiByFourMultiplySquareDiamterofOrifee = DiameterOfOrifee(DiameterOfOrifee);


                Float RootOfDensityandPressure = RootOfDensityandPressure(DiffrentialPressure, DensityOfFluid);

                Float finalValue =CoefficientOfDischarge * RootOf_OneMinus_DM_RaiseFour * PiByFourMultiplySquareDiamterofOrifee * RootOfDensityandPressure;
                String Round=String.format("%.6f",finalValue);
                FlowRateTV.setText("Orifiee Mass FlowRate : "+Round +" Kg/s");
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
        String RatioString = String.format("%.5f", sqreRoot);

        float ratio = Float.parseFloat(RatioString);
        return ratio;
    }


}