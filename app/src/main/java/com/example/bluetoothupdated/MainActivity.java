package com.example.bluetoothupdated;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.xml.transform.Result;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, CaluclationResult {
    LinearLayout clientLL;
    LinearLayout serverLL;
    //    Boolean CountDownPause = false;
    Spinner spinner;
    String[] DeviceNameArray = {"Select Device", "Temperature", "Pressure", "PPM"};
    Button serverStartBTN, clientStartBTN, fourthDeviceClientBTN, fourthDeviceServerBTN;
    private static final String APP_NAME = "Bluetooth App";
    private static final java.util.UUID UUID4th = java.util.UUID.fromString("9bbb4aaa-c772-4e30-853a-e6a64f5e30f3");
    BluetoothAdapter bluetoothAdapter;

    TextView  CounterTV_Temperature, CounterTV_Pressure, CounterTV_PPM;
    TextView statusOfBluetooth,fourthDeviceServerTV;
    TextView fourthDeviceClientTV, countAllData;

    int Time = 50;
    int TimeB = 500;
    int TimeC = 50000;

    int seconds = 0;
    String DevName;
    String ClientName;
    //CountDownTimer countDownTimer;
    BluetoothDevice[] paired_device_array;

    String ServerName;
    String CalculationResult = null;

    private ArrayAdapter arrayAdapter;

    String ThreadName = null;
    final int STATE_CONNECTING = 1;
    final int STATE_CONNECTING_A = 2;
    final int STATE_CONNECTING_B = 3;
    final int STATE_CONNECTING_C = 4;
    final int STATE_CONNECTING_Device4 = 5;
    final int STATE_CONNECTING_Clinet_Device4 = 6;

    //Server Device Connection Update
    final int STATE_CONNECTED_Temperature = 51;
    final int STATE_CONNECTED_Pressure = 52;
    final int STATE_CONNECTED_PPM = 53;
    final int STATE_CONNECTED_SERVER_FOR_4TH_DEVICE = 54;

    final int STATE_CONNECTION_FAILED_Temperature = 24;
    final int STATE_CONNECTION_FAILED_Pressure = 25;
    final int STATE_CONNECTION_FAILED_PPM = 26;



    //Client Device Connection Update via Handler
    final int STATE_CONNECTED_A = 12;
    final int STATE_CONNECTED_B = 13;
    final int STATE_CONNECTED_C = 14;
    final int STATE_CONNECTED_CLIENT_FOR_4TH_DEVICE = 15;

    //final int STATE_CONNECTION_FAILED = 21;
    final int STATE_BLUETOOTH_OFF = 23;

    final int STATE_MESSAGE_RECIEVED_FROM_A = 31;
    final int STATE_MESSAGE_RECIEVED_FROM_B = 32;
    final int STATE_MESSAGE_RECIEVED_FROM_C = 33;
    final int STATE_MESSAGE_RECIEVED_FROM_4TH_DEVICE = 34;

    final int STATE_CONNECTION_FAILED_CLIENT_A = 41;
    final int STATE_CONNECTION_FAILED_CLIENT_B = 42;
    final int STATE_CONNECTION_FAILED_CLIENT_C = 43;


    TextView TemperatureDeviceStatus_TV, TemperatureDeviceCounter_TV;
    TextView DeviceBStatus, DeviceBCounter;
    TextView DeviceCStatus, DeviceCCounter;

    private final ArrayList<UUID> uuidList = new ArrayList<>();

    private BluetoothServerSocket serverSocket = null;

    int CountOfDeviceA = -1;
    int CountOfDeviceB = -1;
    int CountOfDeviceC = -1;


    Button TemperatureDevice , PressureDevice, PPMDevice;
    //static MainActivity.SendRecieve4thDevice sendRecieve4thDevice;


    static String DeviceAName;
    static String DeviceBName;
    static String DeviceCName;

    SendReceiveTemperature sendReceiveTemperature;
    SendReceivePressure sendReceivePressure;
    SendReceivePPM sendReceivePPM;

    SendReceieve4thDevice sendReceieve4thDevice;


    Button Calculations;
    SqliteData sqliteData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sqliteData = new SqliteData(MainActivity.this);

        Calculations = findViewById(R.id.Calculations);
        Calculations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Calculation calculation = new Calculation(sqliteData,CountOfDeviceA,CountOfDeviceB,CountOfDeviceC);
//                String text = calculation.method();
//                Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });

        Button SavedValues = findViewById(R.id.savedValues);
        SavedValues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CalculationsActivity.class);
                startActivity(intent);

                //  CalculationsActivity calculationsActivity=new CalculationsActivity("0");
//                CalculationsActivity calculationsActivity = new CalculationsActivity();
                //Toast.makeText(MainActivity.this, calculationsActivity.Round, Toast.LENGTH_SHORT).show();
            }
        });


        clientLL = findViewById(R.id.clientLL);
        serverLL = findViewById(R.id.serverLL);


        spinner = findViewById(R.id.Spinner);
        serverStartBTN = findViewById(R.id.serverStartBTN);
        clientStartBTN = findViewById(R.id.clientStartBTN);

        //For Server Side Checking if Bluetooth Current State;
        statusOfBluetooth = findViewById(R.id.statusOfBluetooth);



        fourthDeviceClientBTN = findViewById(R.id.fourthDeviceClientBTN);
        fourthDeviceServerBTN = findViewById(R.id.fourthDeviceServerBTN);
        fourthDeviceClientTV = findViewById(R.id.fourthDeviceTV);
        fourthDeviceServerTV=findViewById(R.id.fourthDeviceServerTV);


        CounterTV_Temperature = findViewById(R.id.counterTV);
        CounterTV_Pressure = findViewById(R.id.counterTVB);
        CounterTV_PPM = findViewById(R.id.counterTVC);

        countAllData = findViewById(R.id.countAllData);

        TemperatureDevice = findViewById(R.id.deviceA);
        PressureDevice = findViewById(R.id.deviceB);
        PPMDevice = findViewById(R.id.deviceC);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        spinner.setOnItemSelectedListener(MainActivity.this);

        TemperatureDeviceStatus_TV = findViewById(R.id.deviceAStatus);
        TemperatureDeviceCounter_TV = findViewById(R.id.deviceACounter);

        DeviceBStatus = findViewById(R.id.deviceBStatus);
        DeviceBCounter = findViewById(R.id.deviceBCounter);

        DeviceCStatus = findViewById(R.id.deviceCStatus);
        DeviceCCounter = findViewById(R.id.deviceCCounter);

        uuidList.clear();
        uuidList.add(UUID.fromString("fe964a9c-184c-11e6-b6ba-3e1d05defe78"));
        uuidList.add(UUID.fromString("fe964e02-184c-11e6-b6ba-3e1d05defe78"));
        uuidList.add(UUID.fromString("fe964f9c-184c-11e6-b6ba-3e1d05defe78"));
        uuidList.add(UUID.fromString("fe965438-184c-11e6-b6ba-3e1d05defe78"));


        checkBluetoothIsOn();
        //IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        //registerReceiver(mReceiver, filter);
        btnClicks();
    }

    private void btnClicks() {

        fourthDeviceServerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Server4thDevice server4thDevice = new Server4thDevice();
                server4thDevice.start();
            }
        });

        fourthDeviceClientBTN.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                if (!bluetoothAdapter.isEnabled()) {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, 100);
                } else {
                    @SuppressLint("MissingPermission") Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                    String[] strings = new String[pairedDevices.size()];
                    paired_device_array = new BluetoothDevice[pairedDevices.size()];
                    int i = 0;

                    if (pairedDevices.size() > 0) {
                        for (BluetoothDevice device : pairedDevices) {
                            paired_device_array[i] = device;
                            strings[i] = device.getName();
                            i++;
                        }
                        arrayAdapter = new ArrayAdapter(MainActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, strings);
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Choose Device For Result");
                        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                try {
                                    sendReceieve4thDevice.cancel();
                                } catch (Exception e) {

                                }
                                Client4thDevice client4thDevice = new Client4thDevice(paired_device_array[item]);
                                client4thDevice.start();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();

                    }

                }

            }
        });

    }


    private void TemperatureCountDown() {
//        countDownTimer = new CountDownTimer(Time * 1000, 1000) {
//            @Override
//            public void onTick(long l) {
//                seconds = (int) (l / 1000);
//                try {
//                    CounterTV_Temperature.setText(String.valueOf(seconds));
//
//                    sendReceiveTemperature.write(CounterTV_Temperature.getText().toString().getBytes());
//
//                } catch (Exception e) {
//
//                }
//            }
//
//            @Override
//            public void onFinish() {
//                TemperatureCountDown();
//            }
//        }.start();

        int min = -20;
        int max = 50;
        int RandomInt = (int) Math.floor(Math.random() * (max - min + 1) + min);
        CounterTV_Temperature.setText(String.valueOf(RandomInt));

//       try {
//           sendReceiveTemperature.write(CounterTV_Temperature.getText().toString().getBytes());
//       }catch (Exception e)
//       {
//
//       }

    }

    private void PressureCountDown() {
//        countDownTimer = new CountDownTimer(TimeB * 1000, 1000) {
//            @Override
//            public void onTick(long l) {
//                seconds = (int) (l / 1000);
//                try {
//                    CounterTV_Pressure.setText(String.valueOf(seconds));
//
//                    sendReceivePressure.write(CounterTV_Pressure.getText().toString().getBytes());
//
//                } catch (Exception e) {
//
//                }
//            }
//
//            @Override
//            public void onFinish() {
//                PressureCountDown();
//            }
//        }.start();
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Random random = new Random();
                int upperbound = 50;
                int Pressure = random.nextInt(upperbound);
                CounterTV_Pressure.setText(String.valueOf(Pressure));
                try {
                    sendReceivePressure.write(CounterTV_Pressure.getText().toString().getBytes());
                } catch (Exception e) {
                }
                PressureCountDown();
            }
        };
        handler.postDelayed(runnable, 5000);

    }

    private void PPMCountDown() {
//        countDownTimer = new CountDownTimer(TimeC * 1000, 1000) {
//            @Override
//            public void onTick(long l) {
//                seconds = (int) (l / 1000);
//                try {
//                    CounterTV_PPM.setText(String.valueOf(seconds));
//
//                    sendReceivePPM.write(CounterTV_PPM.getText().toString().getBytes());
//
//                } catch (Exception e) {
////                    Message message = Message.obtain();
////                    message.what = STATE_CONNECTING_C;
////                    handler.sendMessage(message);
//                }
//            }
//
//            @Override
//            public void onFinish() {
//                PPMCountDown();
//            }
//        }.start();
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Random random = new Random();
                int upperbound = 1000;
                int PPM = random.nextInt(upperbound);
                CounterTV_PPM.setText(String.valueOf(PPM));
                try {
                    sendReceivePPM.write(CounterTV_PPM.getText().toString().getBytes());
                } catch (Exception e) {
                }
                PPMCountDown();
            }
        };
        handler.postDelayed(runnable, 5000);
    }

    private void CalulationData()
    {
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    sendReceieve4thDevice.write(countAllData.getText().toString().getBytes());
                } catch (Exception e) {
                }
                CalulationData();
            }
        };
        handler.postDelayed(runnable, 2000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, DeviceNameArray);
        spinner.setAdapter(arrayAdapter);


//        if (CountDownPause == true) {
//            CountDownPause = false;
//            countDownTimer.start();
//        } else {
//
//        }
        serverStartBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!bluetoothAdapter.isEnabled()) {
                    checkBluetoothIsOn();
                } else {
                    ThreadName = spinner.getSelectedItem().toString();
                    startServer(ThreadName);

                    statusOfBluetooth.setVisibility(View.VISIBLE);

                }

            }
        });

        clientStartBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serverLL.setVisibility(View.GONE);
                clientLL.setVisibility(View.VISIBLE);
                startDevice();
            }
        });

        startDevice();

        //handlerCountData();

    }

    public void handlerCountData() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //   if (CountOfDeviceA != -1 && CountOfDeviceB != -1 && CountOfDeviceC != -1) {
//                    int add = CountOfDeviceA + CountOfDeviceB + CountOfDeviceC;
//                    try {
//                        countAllData.setText(String.valueOf(add));
//                        sendReceieve4thDevice.write(String.valueOf(add).getBytes());
//                    } catch (Exception e) {
//
//                    }

                //CountOfDeviceA=0;
               // CountOfDeviceB = 10;


                Calculation calculation = new Calculation(sqliteData, CountOfDeviceA, CountOfDeviceB, CountOfDeviceC);
                String text = calculation.method();

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        countAllData.setText(text);
                    }
                });


//                } else {
//                    countAllData.setText("All Devices Not Connected");
//                    fourthDeviceServerBTN.setVisibility(View.GONE);
//                }
            }
        });
        thread.start();
//
//        try {
//            Thread.sleep(1);
//        } catch (InterruptedException e) {}
        thread.interrupt();
    }

    private void startServer(String servername) {
        if (servername == "Select Device") {
            Toast.makeText(MainActivity.this, "Please select server from list", Toast.LENGTH_SHORT).show();
        } else {
            if (servername == "Temperature") {

                try {
                    sendReceiveTemperature.cancel();
                    sendReceivePressure.cancel();
                    sendReceivePPM.cancel();
                } catch (Exception e) {
                }

                ServerTemperature serverTemperature = new ServerTemperature();
                serverTemperature.start();
            }
            if (servername == "Pressure") {

                try {
                    sendReceiveTemperature.cancel();
                    sendReceivePressure.cancel();
                    sendReceivePPM.cancel();
                } catch (Exception e) {
                }


                ServerPressure serverPressure = new ServerPressure();
                serverPressure.start();
            }
            if (servername == "PPM") {
                try {
                    sendReceiveTemperature.cancel();
                    sendReceivePressure.cancel();
                    sendReceivePPM.cancel();
                } catch (Exception e) {
                }


                ServerPPM serverPPM = new ServerPPM();
                serverPPM.start();



            }
        }

    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("MissingPermission")
        @Override
        public void handleMessage(@NonNull Message message) {
            switch (message.what) {
                case STATE_CONNECTING:
                    statusOfBluetooth.setText("Connecting...");
                    break;

                case STATE_CONNECTING_A:
                    TemperatureDeviceStatus_TV.setText("Connecting...");
                    break;
                case STATE_CONNECTING_B:
                    DeviceBStatus.setText("Connecting...");
                    break;
                case STATE_CONNECTING_C:
                    DeviceCStatus.setText("Connecting...");
                    break;

                case STATE_CONNECTING_Device4:
                    fourthDeviceServerTV.setText("Connecting...");
                    break;
                case STATE_CONNECTING_Clinet_Device4:
                    fourthDeviceClientTV.setText("Connecting...");
                    break;


                case STATE_CONNECTED_Temperature:
                    CounterTV_Temperature.setVisibility(View.VISIBLE);
                    CounterTV_Pressure.setVisibility(View.GONE);
                    CounterTV_PPM.setVisibility(View.GONE);

                    statusOfBluetooth.setVisibility(View.VISIBLE);
                    statusOfBluetooth.setText("Connected To : " + DevName);

                    TemperatureCountDown();

                    try {
                        sendReceiveTemperature.write(CounterTV_Temperature.getText().toString().getBytes());
                    }catch (Exception e)
                    {

                    }
                    break;


                case STATE_CONNECTED_Pressure:

                    CounterTV_Temperature.setVisibility(View.GONE);
                    CounterTV_Pressure.setVisibility(View.VISIBLE);
                    CounterTV_PPM.setVisibility(View.GONE);

                    statusOfBluetooth.setVisibility(View.VISIBLE);
                    statusOfBluetooth.setText("Connected To : " + DevName);

                    PressureCountDown();
                    break;

                case STATE_CONNECTED_PPM:
                    CounterTV_Temperature.setVisibility(View.GONE);
                    CounterTV_Pressure.setVisibility(View.GONE);
                    CounterTV_PPM.setVisibility(View.VISIBLE);

                    statusOfBluetooth.setVisibility(View.VISIBLE);
                    statusOfBluetooth.setText("Connected To : " + DevName);
                    PPMCountDown();
                    break;

                case STATE_CONNECTED_CLIENT_FOR_4TH_DEVICE:
                    fourthDeviceClientTV.setText("Connected Sucessfully 4th Server");
                    break;

                case STATE_CONNECTED_A:
                    TemperatureDeviceStatus_TV.setText("Connected To : " + ClientName);
                    ClientName = "";
                    break;
                case STATE_CONNECTED_B:
                    DeviceBStatus.setText("Connected To : " + ClientName);
                    ClientName = "";
                    break;
                case STATE_CONNECTED_C:
                    DeviceCStatus.setText("Connected To : " + ClientName);
                    ClientName = "";
                    break;

                case STATE_CONNECTED_SERVER_FOR_4TH_DEVICE:
                    fourthDeviceServerTV.setText("Connected 4th Client");
                    CalulationData();

                    break;



                case STATE_CONNECTION_FAILED_Temperature:
                    CounterTV_Temperature.setVisibility(View.GONE);
                    CounterTV_Pressure.setVisibility(View.GONE);
                    CounterTV_PPM.setVisibility(View.GONE);

                    statusOfBluetooth.setVisibility(View.VISIBLE);
                    statusOfBluetooth.setText("Connection Failed from Temperature");
                    break;
                case STATE_CONNECTION_FAILED_Pressure:
                    CounterTV_Temperature.setVisibility(View.GONE);
                    CounterTV_Pressure.setVisibility(View.GONE);
                    CounterTV_PPM.setVisibility(View.GONE);

                    statusOfBluetooth.setVisibility(View.VISIBLE);
                    statusOfBluetooth.setText("Connection Failed from Pressure");
                    break;
                case STATE_CONNECTION_FAILED_PPM:
                    CounterTV_Temperature.setVisibility(View.GONE);
                    CounterTV_Pressure.setVisibility(View.GONE);
                    CounterTV_PPM.setVisibility(View.GONE);

                    statusOfBluetooth.setVisibility(View.VISIBLE);
                    statusOfBluetooth.setText("Connection Failed From PPM");
                    break;

                case STATE_CONNECTION_FAILED_CLIENT_A:
                    if (ClientName != "") {
                        TemperatureDeviceStatus_TV.setText("Connection Failed\nWith\n" + ClientName);
                    } else {
                        TemperatureDeviceStatus_TV.setText("Connection Failed");
                    }

                    ClientName = "";
                    try {
                        sendReceiveTemperature.cancel();
                    } catch (Exception e) {
                    }
                    break;
                case STATE_CONNECTION_FAILED_CLIENT_B:
                    if (ClientName != "") {
                        DeviceBStatus.setText("Connection Failed\nWith\n" + ClientName);
                    } else {
                        DeviceBStatus.setText("Connection Failed");
                    }

                    ClientName = "";
                    try {
                        sendReceivePressure.cancel();
                    } catch (Exception e) {
                    }
                    break;
                case STATE_CONNECTION_FAILED_CLIENT_C:
                    if (ClientName != "") {
                        DeviceCStatus.setText("Connection Failed\nWith\n" + ClientName);
                    } else {
                        DeviceCStatus.setText("Connection Failed");
                    }

                    ClientName = "";
                    try {
                        sendReceivePPM.cancel();
                    } catch (Exception e) {
                    }
                    break;

                case STATE_BLUETOOTH_OFF:
                    statusOfBluetooth.setText("Bluetooth is Off, Please Turned on");
                    //countDownTimer.cancel();
                    break;

                case STATE_MESSAGE_RECIEVED_FROM_A:
                    byte[] readBuffA = (byte[]) message.obj;
                    String tempMsgA = new String(readBuffA, 0, message.arg1);
                    TemperatureDeviceCounter_TV.setText(tempMsgA);
                    CountOfDeviceA = Integer.parseInt(TemperatureDeviceCounter_TV.getText().toString());
                    handlerCountData();
                    break;

                case STATE_MESSAGE_RECIEVED_FROM_B:
                    byte[] readBuffB = (byte[]) message.obj;
                    String tempMsgB = new String(readBuffB, 0, message.arg1);
                    DeviceBCounter.setText(tempMsgB);
                    if (DeviceBCounter.equals("")) {

                    } else {
                        CountOfDeviceB = Integer.parseInt(DeviceBCounter.getText().toString());
                        handlerCountData();
                    }
                    break;

                case STATE_MESSAGE_RECIEVED_FROM_C:
                    byte[] readBuffC = (byte[]) message.obj;
                    String tempMsgC = new String(readBuffC, 0, message.arg1);
                    DeviceCCounter.setText(tempMsgC);
                    CountOfDeviceC = Integer.parseInt(DeviceCCounter.getText().toString());
                    break;

                case STATE_MESSAGE_RECIEVED_FROM_4TH_DEVICE:
                    byte[] readBuffD = (byte[]) message.obj;
                    String tempMsgD = new String(readBuffD, 0, message.arg1);
                    fourthDeviceClientTV.setText(tempMsgD);
                    break;

            }
        }
    };


    @SuppressLint("MissingPermission")
    private void checkBluetoothIsOn() {
        if (!bluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, 100);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (i != 0) {
            ThreadName = DeviceNameArray[i];
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            Toast.makeText(this, "Bluetooth is Turned on", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(MainActivity.this, "You need to Turn on Bluetooth", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

//        if (countDownTimer != null) {
//            countDownTimer.cancel();
//            CountDownPause = true;
//        }


    }

//    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            final String action = intent.getAction();
//
//            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
//                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
//                        BluetoothAdapter.ERROR);
//                switch (state) {
//                    case BluetoothAdapter.STATE_OFF:
//                        statusOfBluetooth.setText("off");
//                        break;
//                    case BluetoothAdapter.STATE_ON:
//                        break;
//                }
//            }
//        }
//    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Time = 0;
        TimeB = 0;
        TimeC = 0;
        // unregisterReceiver(mReceiver);
    }

    private void startDevice() {
        TemperatureDevice.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                if (!bluetoothAdapter.isEnabled()) {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, 100);
                } else {
                    @SuppressLint("MissingPermission") Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                    String[] strings = new String[pairedDevices.size()];
                    paired_device_array = new BluetoothDevice[pairedDevices.size()];
                    int i = 0;

                    if (pairedDevices.size() > 0) {
                        for (BluetoothDevice device : pairedDevices) {
                            paired_device_array[i] = device;
                            strings[i] = device.getName();
                            i++;
                        }
                        arrayAdapter = new ArrayAdapter(MainActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, strings);
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Choose Device For Temperature");
                        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                try {
                                    sendReceiveTemperature.cancel();
                                } catch (Exception e) {

                                }
                                ClientTemperature clientTemperature = new ClientTemperature(paired_device_array[item]);
                                clientTemperature.start();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();

                    }

                }
            }
        });
        PressureDevice.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                if (!bluetoothAdapter.isEnabled()) {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, 100);
                } else {
                    @SuppressLint("MissingPermission") Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                    String[] strings = new String[pairedDevices.size()];
                    paired_device_array = new BluetoothDevice[pairedDevices.size()];
                    int i = 0;

                    if (pairedDevices.size() > 0) {
                        for (BluetoothDevice device : pairedDevices) {
                            paired_device_array[i] = device;
                            strings[i] = device.getName();
                            i++;
                        }
                        arrayAdapter = new ArrayAdapter(MainActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, strings);
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Choose Device For Pressure");
                        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                try {
                                    sendReceivePressure.cancel();
                                } catch (Exception e) {

                                }
                                ClientPressure clientPressure = new ClientPressure(paired_device_array[item]);
                                clientPressure.start();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();

                    }

                }
            }
        });
        PPMDevice.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                if (!bluetoothAdapter.isEnabled()) {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, 100);
                } else {
                    @SuppressLint("MissingPermission") Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                    String[] strings = new String[pairedDevices.size()];
                    paired_device_array = new BluetoothDevice[pairedDevices.size()];
                    int i = 0;

                    if (pairedDevices.size() > 0) {
                        for (BluetoothDevice device : pairedDevices) {
                            paired_device_array[i] = device;
                            strings[i] = device.getName();
                            i++;
                        }
                        arrayAdapter = new ArrayAdapter(MainActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, strings);
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Choose Device For PPM");
                        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                try {
                                    sendReceivePPM.cancel();
                                } catch (Exception e) {

                                }
                                ClientPPM clientPPM = new ClientPPM(paired_device_array[item]);
                                clientPPM.start();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();

                    }

                }
            }
        });

    }

    @Override
    public void Result(String Result) {
        CalculationResult = Result;
    }


    public class ServerTemperature extends Thread {
        @SuppressLint("MissingPermission")
        public ServerTemperature() {
            try {
                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, uuidList.get(0));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @SuppressLint("MissingPermission")
        public void run() {
            BluetoothSocket socket = null;
            while (socket == null) {
                try {
                    Message message1 = Message.obtain();
                    message1.what = STATE_CONNECTING;
                    handler.sendMessage(message1);

                    socket = serverSocket.accept();

                    DevName = socket.getRemoteDevice().getName();

                    serverSocket.close();

                } catch (IOException e) {
                    Message message2 = Message.obtain();
                    message2.what = STATE_CONNECTION_FAILED_Temperature;
                    handler.sendMessage(message2);
                }

                if (socket != null) {

                    Message message = Message.obtain();
                    message.what = STATE_CONNECTED_Temperature;
                    handler.sendMessage(message);

                    sendReceiveTemperature = new SendReceiveTemperature(socket);
                    sendReceiveTemperature.start();

                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }
    private class ClientTemperature extends Thread {
        private BluetoothDevice device;
        private BluetoothSocket socket;

        @SuppressLint("MissingPermission")
        public ClientTemperature(BluetoothDevice device1) {
            device = device1;
            try {
                if (bluetoothAdapter.isEnabled()) {
                    // for (int i = 0; i < uuidList.size(); i++) {
                    socket = device.createRfcommSocketToServiceRecord(uuidList.get(0));
                    ClientName = device.getName();
                    return;
                    // }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @SuppressLint("MissingPermission")
        public void run() {
            bluetoothAdapter.cancelDiscovery();
            try {
                Message connecting = Message.obtain();
                connecting.what = STATE_CONNECTING_A;
                handler.sendMessage(connecting);

                socket.connect();

                Message message = Message.obtain();
                message.what = STATE_CONNECTED_A;
                handler.sendMessage(message);

                DeviceAName = socket.getRemoteDevice().getName();

                // SendRecieveTemperature sendRecieveTemperature = new SendRecieveTemperature(socket);
                sendReceiveTemperature = new SendReceiveTemperature(socket);
                sendReceiveTemperature.start();

            } catch (IOException e) {
                e.printStackTrace();
                try {
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                Message message = Message.obtain();
                message.what = STATE_CONNECTION_FAILED_CLIENT_A;
                handler.sendMessage(message);
            }
        }

    }
    public class SendReceiveTemperature extends Thread {

        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceiveTemperature(BluetoothSocket socket) {
            bluetoothSocket = socket;
            InputStream tempIn = null;
            OutputStream tempOut = null;

            try {
                tempIn = bluetoothSocket.getInputStream();
                tempOut = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            inputStream = tempIn;
            outputStream = tempOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = inputStream.read(buffer);
                    handler.obtainMessage(STATE_MESSAGE_RECIEVED_FROM_A, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                    Message message1 = Message.obtain();
                    message1.what = STATE_CONNECTION_FAILED_CLIENT_A;
                    handler.sendMessage(message1);

                    Message message2 = Message.obtain();
                    message2.what = STATE_CONNECTION_FAILED_Temperature;
                    handler.sendMessage(message2);
                    break;
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
                //cancel();
            }
        }

        public void cancel() {
            try {
                inputStream.close();
                outputStream.close();
                bluetoothSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }

        }
    }


    public class ServerPressure extends Thread {
        @SuppressLint("MissingPermission")
        public ServerPressure() {
            try {
                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, uuidList.get(1));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @SuppressLint("MissingPermission")
        public void run() {
            BluetoothSocket socket = null;
            while (socket == null) {
                try {
                    Message message1 = Message.obtain();
                    message1.what = STATE_CONNECTING;
                    handler.sendMessage(message1);

                    socket = serverSocket.accept();

                    DevName = socket.getRemoteDevice().getName();

                    serverSocket.close();

                } catch (IOException e) {
                    Message message2 = Message.obtain();
                    message2.what = STATE_CONNECTION_FAILED_Pressure;
                    handler.sendMessage(message2);
                }

                if (socket != null) {

                    Message message = Message.obtain();
                    message.what = STATE_CONNECTED_Pressure;
                    handler.sendMessage(message);

                    sendReceivePressure = new SendReceivePressure(socket);
                    sendReceivePressure.start();

                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }
    private class ClientPressure extends Thread {
        private BluetoothDevice device;
        private BluetoothSocket socket;

        @SuppressLint("MissingPermission")
        public ClientPressure(BluetoothDevice device1) {
            device = device1;
            try {
                if (bluetoothAdapter.isEnabled()) {
                    // for (int i = 0; i < uuidList.size(); i++) {
                    socket = device.createRfcommSocketToServiceRecord(uuidList.get(1));
                    ClientName = device.getName();
                    return;
                    // }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @SuppressLint("MissingPermission")
        public void run() {
            bluetoothAdapter.cancelDiscovery();
            try {
                Message connecting = Message.obtain();
                connecting.what = STATE_CONNECTING_B;
                handler.sendMessage(connecting);

                socket.connect();

                Message message = Message.obtain();
                message.what = STATE_CONNECTED_B;
                handler.sendMessage(message);

                DeviceAName = socket.getRemoteDevice().getName();

                SendReceivePressure sendReceivePressure = new SendReceivePressure(socket);
                sendReceivePressure.start();

            } catch (IOException e) {
                e.printStackTrace();
                try {
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                Message message = Message.obtain();
                message.what = STATE_CONNECTION_FAILED_CLIENT_B;
                handler.sendMessage(message);
            }
        }

    }
    public class SendReceivePressure extends Thread {

        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceivePressure(BluetoothSocket socket) {
            bluetoothSocket = socket;
            InputStream tempIn = null;
            OutputStream tempOut = null;

            try {
                tempIn = bluetoothSocket.getInputStream();
                tempOut = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            inputStream = tempIn;
            outputStream = tempOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = inputStream.read(buffer);
                    handler.obtainMessage(STATE_MESSAGE_RECIEVED_FROM_B, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                    Message message1 = Message.obtain();
                    message1.what = STATE_CONNECTION_FAILED_CLIENT_B;
                    handler.sendMessage(message1);

                    Message message2 = Message.obtain();
                    message2.what = STATE_CONNECTION_FAILED_Pressure;
                    handler.sendMessage(message2);
                    break;
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
                //cancel();
            }
        }

        public void cancel() {
            try {
                inputStream.close();
                outputStream.close();
                bluetoothSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }

        }
    }


    public class ServerPPM extends Thread {
        @SuppressLint("MissingPermission")
        public ServerPPM() {
            try {
                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, uuidList.get(2));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @SuppressLint("MissingPermission")
        public void run() {
            BluetoothSocket socket = null;
            while (socket == null) {
                try {
                    Message message1 = Message.obtain();
                    message1.what = STATE_CONNECTING;
                    handler.sendMessage(message1);

                    socket = serverSocket.accept();

                    DevName = socket.getRemoteDevice().getName();

                    serverSocket.close();

                } catch (IOException e) {
                    Message message2 = Message.obtain();
                    message2.what = STATE_CONNECTION_FAILED_PPM;
                    handler.sendMessage(message2);
                }

                if (socket != null) {

                    Message message = Message.obtain();
                    message.what = STATE_CONNECTED_PPM;
                    handler.sendMessage(message);

                    sendReceivePPM = new SendReceivePPM(socket);
                    sendReceivePPM.start();

                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }
    private class ClientPPM extends Thread {
        private BluetoothDevice device;
        private BluetoothSocket socket;

        @SuppressLint("MissingPermission")
        public ClientPPM(BluetoothDevice device1) {
            device = device1;
            try {
                if (bluetoothAdapter.isEnabled()) {
                    // for (int i = 0; i < uuidList.size(); i++) {
                    socket = device.createRfcommSocketToServiceRecord(uuidList.get(2));
                    ClientName = device.getName();
                    return;
                    // }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @SuppressLint("MissingPermission")
        public void run() {
            bluetoothAdapter.cancelDiscovery();
            try {
                Message connecting = Message.obtain();
                connecting.what = STATE_CONNECTING_C;
                handler.sendMessage(connecting);

                socket.connect();

                Message message = Message.obtain();
                message.what = STATE_CONNECTED_C;
                handler.sendMessage(message);

                DeviceAName = socket.getRemoteDevice().getName();

                SendReceivePPM sendReceivePPM = new SendReceivePPM(socket);
                sendReceivePPM.start();

            } catch (IOException e) {
                e.printStackTrace();
                try {
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                Message message = Message.obtain();
                message.what = STATE_CONNECTION_FAILED_CLIENT_C;
                handler.sendMessage(message);
            }
        }

    }
    public class SendReceivePPM extends Thread {

        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceivePPM(BluetoothSocket socket) {
            bluetoothSocket = socket;
            InputStream tempIn = null;
            OutputStream tempOut = null;

            try {
                tempIn = bluetoothSocket.getInputStream();
                tempOut = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            inputStream = tempIn;
            outputStream = tempOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = inputStream.read(buffer);
                    handler.obtainMessage(STATE_MESSAGE_RECIEVED_FROM_C, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                    Message message1 = Message.obtain();
                    message1.what = STATE_CONNECTION_FAILED_CLIENT_C;
                    handler.sendMessage(message1);

                    Message message2 = Message.obtain();
                    message2.what = STATE_CONNECTION_FAILED_PPM;
                    handler.sendMessage(message2);
                    break;
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
                //cancel();
            }
        }

        public void cancel() {
            try {
                inputStream.close();
                outputStream.close();
                bluetoothSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }

        }
    }


    public class Server4thDevice extends Thread {
        @SuppressLint("MissingPermission")
        public Server4thDevice() {
            try {
                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, uuidList.get(3));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @SuppressLint("MissingPermission")
        public void run() {
            BluetoothSocket socket = null;
            while (socket == null) {
                try {
                    Message message1 = Message.obtain();
                    message1.what = STATE_CONNECTING_Device4;
                    handler.sendMessage(message1);

                    socket = serverSocket.accept();

                    DevName = socket.getRemoteDevice().getName();

                    serverSocket.close();

                } catch (IOException e) {
//                    Message message2 = Message.obtain();
//                    message2.what = STATE_CONNECTION_FAILED;
//                    handler.sendMessage(message2);
                }

                if (socket != null) {

                    Message message = Message.obtain();
                    message.what = STATE_CONNECTED_SERVER_FOR_4TH_DEVICE;
                    handler.sendMessage(message);

                    sendReceieve4thDevice = new SendReceieve4thDevice(socket);
                    sendReceieve4thDevice.start();

                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }
    private class Client4thDevice extends Thread {
        private BluetoothDevice device;
        private BluetoothSocket socket;

        @SuppressLint("MissingPermission")
        public Client4thDevice(BluetoothDevice device1) {
            device = device1;
            try {
                if (bluetoothAdapter.isEnabled()) {
                    // for (int i = 0; i < uuidList.size(); i++) {
                    socket = device.createRfcommSocketToServiceRecord(uuidList.get(3));
                    return;
                    // }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @SuppressLint("MissingPermission")
        public void run() {
            bluetoothAdapter.cancelDiscovery();
            try {
                Message connecting = Message.obtain();
                connecting.what = STATE_CONNECTING_Clinet_Device4;
                handler.sendMessage(connecting);

                socket.connect();

                Message message = Message.obtain();
                message.what = STATE_CONNECTED_CLIENT_FOR_4TH_DEVICE;
                handler.sendMessage(message);

                DeviceAName = socket.getRemoteDevice().getName();

                sendReceieve4thDevice = new SendReceieve4thDevice(socket);
                sendReceieve4thDevice.start();

            } catch (IOException e) {
                e.printStackTrace();
                try {
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
//                Message message = Message.obtain();
//                message.what = STATE_CONNECTION_FAILED_CLIENT_C;
//                handler.sendMessage(message);
            }
        }

    }
    public class SendReceieve4thDevice extends Thread {

        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceieve4thDevice(BluetoothSocket socket) {
            bluetoothSocket = socket;
            InputStream tempIn = null;
            OutputStream tempOut = null;

            try {
                tempIn = bluetoothSocket.getInputStream();
                tempOut = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            inputStream = tempIn;
            outputStream = tempOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = inputStream.read(buffer);
                    handler.obtainMessage(STATE_MESSAGE_RECIEVED_FROM_4TH_DEVICE, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
//                    Message message1 = Message.obtain();
//                    message1.what = STATE_CONNECTION_FAILED_CLIENT_C;
//                    handler.sendMessage(message1);
//
//                    Message message2 = Message.obtain();
//                    message2.what = STATE_CONNECTION_FAILED;
//                    handler.sendMessage(message2);
                    break;
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
                cancel();
            }
        }

        public void cancel() {
            try {
                inputStream.close();
                outputStream.close();
                bluetoothSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }

        }
    }


}