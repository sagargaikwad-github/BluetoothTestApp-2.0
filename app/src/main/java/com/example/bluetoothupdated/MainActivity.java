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
import android.content.IntentFilter;
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
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener , SensorEventListener {
    LinearLayout clientLL;
    LinearLayout serverLL;

    ListView sensorListView;
    List<Sensor> sensorList;
    ArrayAdapter arrayAdapterSensor;


    Boolean CoundownPause=false;
    private SensorManager sensorManager;
    private Sensor envSense;
   // private Sensor pressure;

    Spinner spinner;
    String[] DeviceName = {"Select Device", "A", "B", "C"};
    Button serverStartBTN, clientStartBTN, fourthDeviceClientBTN,fourthDeviceServerBTN;
    private static final String APP_NAME = "Bluetooth App";
    private static final java.util.UUID UUID4th = java.util.UUID.fromString("9bbb4aaa-c772-4e30-853a-e6a64f5e30f3");
    BluetoothAdapter bluetoothAdapter;

    TextView statusOfBluetooth, CounterTV, CounterTVB, CounterTVC;
    TextView fourthDeviceTV,countAllData;

    int Time = 50;
    int TimeB = 500;
    int TimeC = 50000;

    int seconds = 0;
    String DevName;
    String ClientName;
    CountDownTimer countDownTimer;
    BluetoothDevice[] paired_device_array;

    String ServerName;

    private ArrayAdapter arrayAdapter;

    String ThreadName = null;
    final int STATE_CONNECTING = 1;
    final int STATE_CONNECTING_A = 2;
    final int STATE_CONNECTING_B = 3;
    final int STATE_CONNECTING_C = 4;

    final int STATE_CONNECTED = 11;
    final int STATE_CONNECTED_A = 12;
    final int STATE_CONNECTED_B = 13;
    final int STATE_CONNECTED_C = 14;

    final int STATE_CONNECTION_FAILED = 21;
    final int STATE_BLUETOOTH_OFF = 23;

    final int STATE_MESSAGE_RECIEVED_FROM_A = 31;
    final int STATE_MESSAGE_RECIEVED_FROM_B = 32;
    final int STATE_MESSAGE_RECIEVED_FROM_C = 33;
    final int STATE_MESSAGE_RECIEVED_FROM_4TH_DEVICE = 34;

    final int STATE_CONNECTION_FAILED_CLIENT_A = 41;
    final int STATE_CONNECTION_FAILED_CLIENT_B = 42;
    final int STATE_CONNECTION_FAILED_CLIENT_C = 43;


    TextView DeviceAStatus, DeviceACounter;
    TextView DeviceBStatus, DeviceBCounter;
    TextView DeviceCStatus, DeviceCCounter;

    private final ArrayList<UUID> uuidList = new ArrayList<>();

    private BluetoothServerSocket serverSocket = null;


    int CountOfDeviceA = -1;
    int CountOfDeviceB = -1;
    int CountOfDeviceC = -1;


    Button DeviceA, DeviceB, DeviceC;
    //static MainActivity.SendRecieve4thDevice sendRecieve4thDevice;


    static String DeviceAName;
    static String DeviceBName;
    static String DeviceCName;

    SendReceieveA sendReceieveA;
    SendReceieveB sendReceieveB;
    SendReceieveC sendReceieveC;

    SendReceieve4thDevice sendReceieve4thDevice;



    Button TemperatureBTN,HumidityBTN;
    TextView TemperatureTV,HumidityTV;

    Button Calculations;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TemperatureBTN=findViewById(R.id.getTemp);
        HumidityBTN=findViewById(R.id.getHumidity);
        TemperatureTV=findViewById(R.id.getTempTV);
        HumidityTV=findViewById(R.id.getHumidityTV);
        sensorListView = findViewById(R.id.ListOfSensor);

        Calculations=findViewById(R.id.Calculations);
        Calculations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,CalculationsActivity.class);
                startActivity(intent);
            }
        });

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        sensorList=sensorManager.getSensorList(Sensor.TYPE_ALL);
        arrayAdapterSensor = new ArrayAdapter(MainActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, sensorList);
        sensorListView.setAdapter(arrayAdapterSensor);






        clientLL = findViewById(R.id.clientLL);
        serverLL = findViewById(R.id.serverLL);


        spinner = findViewById(R.id.Spinner);
        serverStartBTN = findViewById(R.id.serverStartBTN);
        clientStartBTN = findViewById(R.id.clientStartBTN);
        statusOfBluetooth = findViewById(R.id.statusOfBluetooth);
        CounterTV = findViewById(R.id.counterTV);

        fourthDeviceClientBTN = findViewById(R.id.fourthDeviceClientBTN);
        fourthDeviceServerBTN = findViewById(R.id.fourthDeviceServerBTN);
        fourthDeviceTV = findViewById(R.id.fourthDeviceTV);

        CounterTVB = findViewById(R.id.counterTVB);
        CounterTVC = findViewById(R.id.counterTVC);


        countAllData=findViewById(R.id.countAllData);

        DeviceA = findViewById(R.id.deviceA);
        DeviceB = findViewById(R.id.deviceB);
        DeviceC = findViewById(R.id.deviceC);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        spinner.setOnItemSelectedListener(MainActivity.this);

        DeviceAStatus = findViewById(R.id.deviceAStatus);
        DeviceACounter = findViewById(R.id.deviceACounter);

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
        TemperatureBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                envSense = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
                if(envSense==null)
                    Toast.makeText(MainActivity.this,
                            "No Temperature Sensor",
                            Toast.LENGTH_SHORT).show();
                else
                    sensorManager.registerListener(MainActivity.this, envSense, SensorManager.SENSOR_DELAY_NORMAL);

            }
        });

        HumidityBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                envSense = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
                if(envSense==null)
                    Toast.makeText(MainActivity.this,
                            "No Temperature Sensor",
                            Toast.LENGTH_SHORT).show();
                else
                    sensorManager.registerListener(MainActivity.this, envSense, SensorManager.SENSOR_DELAY_NORMAL);
            }
        });


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

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

     int SesnsorType=sensorEvent.sensor.getType();
     switch (SesnsorType)
     {
         case Sensor.TYPE_LIGHT:
             float lux = sensorEvent.values[0];
             TemperatureTV.setText(String.valueOf(lux));
             break;
         case Sensor.TYPE_AMBIENT_TEMPERATURE:
             float lux1 = sensorEvent.values[0];
             HumidityTV.setText(String.valueOf(lux1));
             break;
     }
//        float millibarsOfPressure = sensorEvent.values[0];
//        TemperatureTV.setText(String.valueOf(millibarsOfPressure));

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {


    }


    private void coundownTimerA() {
        countDownTimer = new CountDownTimer(Time * 1000, 1000) {
            @Override
            public void onTick(long l) {
                seconds = (int) (l / 1000);
                try {
                    CounterTV.setText(String.valueOf(seconds));

                    sendReceieveA.write(CounterTV.getText().toString().getBytes());

                } catch (Exception e) {

                }
            }

            @Override
            public void onFinish() {
                coundownTimerA();
            }
        }.start();
    }

    private void coundownTimerB() {
        countDownTimer = new CountDownTimer(TimeB * 1000, 1000) {
            @Override
            public void onTick(long l) {
                seconds = (int) (l / 1000);
                try {
                    CounterTVB.setText(String.valueOf(seconds));

                    sendReceieveB.write(CounterTVB.getText().toString().getBytes());

                } catch (Exception e) {

                }
            }

            @Override
            public void onFinish() {
                coundownTimerB();
            }
        }.start();
    }

    private void coundownTimerC() {
        countDownTimer = new CountDownTimer(TimeC * 1000, 1000) {
            @Override
            public void onTick(long l) {
                seconds = (int) (l / 1000);
                try {
                    CounterTVC.setText(String.valueOf(seconds));

                    sendReceieveC.write(CounterTVC.getText().toString().getBytes());

                } catch (Exception e) {
//                    Message message = Message.obtain();
//                    message.what = STATE_CONNECTING_C;
//                    handler.sendMessage(message);
                }
            }

            @Override
            public void onFinish() {
                coundownTimerC();
            }
        }.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, DeviceName);
        spinner.setAdapter(arrayAdapter);


        if(CoundownPause==true)
        {
            CoundownPause=false;
            countDownTimer.start();
        }
        else
        {

        }
        serverStartBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!bluetoothAdapter.isEnabled()) {
                    checkBluetoothIsOn();
                } else {
                    ThreadName = spinner.getSelectedItem().toString();
                    startServer(ThreadName);
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

    public void handlerCountData(){
        Thread thread= new Thread(new Runnable() {
            @Override
            public void run() {
                if(CountOfDeviceA!=-1 && CountOfDeviceB!=-1 && CountOfDeviceC!=-1)
                {
                    int add=CountOfDeviceA+CountOfDeviceB+CountOfDeviceC;

                    try {
                        countAllData.setText(String.valueOf(add));
                        sendReceieve4thDevice.write(String.valueOf(add).getBytes());
                    }
                    catch (Exception e)
                    {

                    }
                }
                else
                {
                    countAllData.setText("All Devices Not Connected");
                    fourthDeviceServerBTN.setVisibility(View.GONE);
                }
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
            if (servername == "A") {
                ServerClassA serverClassA = new ServerClassA();
                serverClassA.start();
                CounterTV.setVisibility(View.VISIBLE);
                CounterTVB.setVisibility(View.GONE);
                CounterTVC.setVisibility(View.GONE);

                coundownTimerA();
            }
            if (servername == "B") {
                ServerClassB serverClassB = new ServerClassB();
                serverClassB.start();
                CounterTV.setVisibility(View.GONE);
                CounterTVB.setVisibility(View.VISIBLE);
                CounterTVC.setVisibility(View.GONE);

                coundownTimerB();
            }
            if (servername == "C") {
                ServerClassC serverClassC = new ServerClassC();
                serverClassC.start();
                CounterTV.setVisibility(View.GONE);
                CounterTVB.setVisibility(View.GONE);
                CounterTVC.setVisibility(View.VISIBLE);

                coundownTimerC();
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
                    DeviceAStatus.setText("Connecting...");
                    break;
                case STATE_CONNECTING_B:
                    DeviceBStatus.setText("Connecting...");
                    break;
                case STATE_CONNECTING_C:
                    DeviceCStatus.setText("Connecting...");
                    break;

                case STATE_CONNECTED:
                    statusOfBluetooth.setText("Connected To : " + DevName);
                    break;
                case STATE_CONNECTED_A:
                    DeviceAStatus.setText("Connected To : " + ClientName);
                    ClientName="";
                    break;
                case STATE_CONNECTED_B:
                    DeviceBStatus.setText("Connected To : " + ClientName);
                    ClientName="";
                    break;
                case STATE_CONNECTED_C:
                    DeviceCStatus.setText("Connected To : " + ClientName);
                    ClientName="";
                    break;

                case STATE_CONNECTION_FAILED:
                    statusOfBluetooth.setText("Connection Failed");
                    CounterTV.setVisibility(View.GONE);
                    CounterTVB.setVisibility(View.GONE);
                    CounterTVC.setVisibility(View.GONE);

                    onPause();
                    break;

                case STATE_CONNECTION_FAILED_CLIENT_A:
                    if(ClientName!="")
                    {
                        DeviceAStatus.setText("Connection Failed\nWith\n"+ClientName);
                    }
                    else
                    {
                        DeviceAStatus.setText("Connection Failed");
                    }

                    ClientName="";
                    try {
                        sendReceieveA.cancel();
                    }catch (Exception e)
                    {
                    }
                    break;
                case STATE_CONNECTION_FAILED_CLIENT_B:
                    if(ClientName!="")
                    {
                        DeviceBStatus.setText("Connection Failed\nWith\n"+ClientName);
                    }
                    else
                    {
                        DeviceBStatus.setText("Connection Failed");
                    }

                    ClientName="";
                    try {
                        sendReceieveB.cancel();
                    }catch (Exception e)
                    {
                    }
                    break;
                case STATE_CONNECTION_FAILED_CLIENT_C:
                    if(ClientName!="")
                    {
                        DeviceCStatus.setText("Connection Failed\nWith\n"+ClientName);
                    }
                    else
                    {
                        DeviceCStatus.setText("Connection Failed");
                    }

                    ClientName="";
                    try {
                        sendReceieveC.cancel();
                    }catch (Exception e)
                    {
                    }
                    break;

                case STATE_BLUETOOTH_OFF:
                    statusOfBluetooth.setText("Bluetooth is Off, Please Turned on");
                    countDownTimer.cancel();
                    break;

                case STATE_MESSAGE_RECIEVED_FROM_A:
                    byte[] readBuffA = (byte[]) message.obj;
                    String tempMsgA = new String(readBuffA, 0, message.arg1);
                    DeviceACounter.setText(tempMsgA);
                    CountOfDeviceA = Integer.parseInt(DeviceACounter.getText().toString());
                    handlerCountData();
                    break;

                case STATE_MESSAGE_RECIEVED_FROM_B:
                    byte[] readBuffB = (byte[]) message.obj;
                    String tempMsgB = new String(readBuffB, 0, message.arg1);
                    DeviceBCounter.setText(tempMsgB);
                    CountOfDeviceB = Integer.parseInt(DeviceBCounter.getText().toString());
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
                    fourthDeviceTV.setText(tempMsgD);
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
            ThreadName = DeviceName[i];
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

        if (countDownTimer != null) {
            countDownTimer.cancel();
            CoundownPause=true;
        }
        sensorManager.unregisterListener(this);

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
        DeviceA.setOnClickListener(new View.OnClickListener() {
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
                        builder.setTitle("Choose Device A");
                        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                try {
                                    sendReceieveA.cancel();
                                } catch (Exception e) {

                                }
                                ClientAClass connectThreadA = new ClientAClass(paired_device_array[item]);
                                connectThreadA.start();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();

                    }

                }
            }
        });
        DeviceB.setOnClickListener(new View.OnClickListener() {
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
                        builder.setTitle("Choose Device B");
                        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                try {
                                    sendReceieveB.cancel();
                                } catch (Exception e) {

                                }
                                ClientBClass connectThreadB = new ClientBClass(paired_device_array[item]);
                                connectThreadB.start();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();

                    }

                }
            }
        });
        DeviceC.setOnClickListener(new View.OnClickListener() {
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
                        builder.setTitle("Choose Device C");
                        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                try {
                                    sendReceieveC.cancel();
                                } catch (Exception e) {

                                }
                                ClientCClass connectThreadC = new ClientCClass(paired_device_array[item]);
                                connectThreadC.start();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();

                    }

                }
            }
        });



    }


    public class ServerClassA extends Thread {
        @SuppressLint("MissingPermission")
        public ServerClassA() {
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
                    message2.what = STATE_CONNECTION_FAILED;
                    handler.sendMessage(message2);
                }

                if (socket != null) {

                    Message message = Message.obtain();
                    message.what = STATE_CONNECTED;
                    handler.sendMessage(message);

                    sendReceieveA = new SendReceieveA(socket);
                    sendReceieveA.start();

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
    private class ClientAClass extends Thread {
        private BluetoothDevice device;
        private BluetoothSocket socket;

        @SuppressLint("MissingPermission")
        public ClientAClass(BluetoothDevice device1) {
            device = device1;
            try {
                if (bluetoothAdapter.isEnabled()) {
                    // for (int i = 0; i < uuidList.size(); i++) {
                    socket = device.createRfcommSocketToServiceRecord(uuidList.get(0));
                    ClientName=device.getName();
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

                SendReceieveA sendRecieveA = new SendReceieveA(socket);
                sendRecieveA.start();

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
    public class SendReceieveA extends Thread {

        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceieveA(BluetoothSocket socket) {
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
                    message2.what = STATE_CONNECTION_FAILED;
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


    public class ServerClassB extends Thread {
        @SuppressLint("MissingPermission")
        public ServerClassB() {
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
                    message2.what = STATE_CONNECTION_FAILED;
                    handler.sendMessage(message2);
                }

                if (socket != null) {

                    Message message = Message.obtain();
                    message.what = STATE_CONNECTED;
                    handler.sendMessage(message);

                    sendReceieveB = new SendReceieveB(socket);
                    sendReceieveB.start();

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
    private class ClientBClass extends Thread {
        private BluetoothDevice device;
        private BluetoothSocket socket;

        @SuppressLint("MissingPermission")
        public ClientBClass(BluetoothDevice device1) {
            device = device1;
            try {
                if (bluetoothAdapter.isEnabled()) {
                    // for (int i = 0; i < uuidList.size(); i++) {
                    socket = device.createRfcommSocketToServiceRecord(uuidList.get(1));
                    ClientName=device.getName();
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

                SendReceieveB sendRecieveB = new SendReceieveB(socket);
                sendRecieveB.start();

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
    public class SendReceieveB extends Thread {

        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceieveB(BluetoothSocket socket) {
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
                    message2.what = STATE_CONNECTION_FAILED;
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


    public class ServerClassC extends Thread {
        @SuppressLint("MissingPermission")
        public ServerClassC() {
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
                    message2.what = STATE_CONNECTION_FAILED;
                    handler.sendMessage(message2);
                }

                if (socket != null) {

                    Message message = Message.obtain();
                    message.what = STATE_CONNECTED;
                    handler.sendMessage(message);

                    sendReceieveC = new SendReceieveC(socket);
                    sendReceieveC.start();

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
    private class ClientCClass extends Thread {
        private BluetoothDevice device;
        private BluetoothSocket socket;

        @SuppressLint("MissingPermission")
        public ClientCClass(BluetoothDevice device1) {
            device = device1;
            try {
                if (bluetoothAdapter.isEnabled()) {
                    // for (int i = 0; i < uuidList.size(); i++) {
                    socket = device.createRfcommSocketToServiceRecord(uuidList.get(2));
                    ClientName=device.getName();
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

                SendReceieveC sendRecieveC = new SendReceieveC(socket);
                sendRecieveC.start();

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
    public class SendReceieveC extends Thread {

        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceieveC(BluetoothSocket socket) {
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
                    message2.what = STATE_CONNECTION_FAILED;
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
//                    Message message1 = Message.obtain();
//                    message1.what = STATE_CONNECTING;
//                    handler.sendMessage(message1);

                    socket = serverSocket.accept();

                    DevName = socket.getRemoteDevice().getName();

                    serverSocket.close();

                } catch (IOException e) {
//                    Message message2 = Message.obtain();
//                    message2.what = STATE_CONNECTION_FAILED;
//                    handler.sendMessage(message2);
                }

                if (socket != null) {

//                    Message message = Message.obtain();
//                    message.what = STATE_CONNECTED;
//                    handler.sendMessage(message);

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
//                Message connecting = Message.obtain();
//                connecting.what = STATE_CONNECTING_C;
//                handler.sendMessage(connecting);

                socket.connect();

//                Message message = Message.obtain();
//                message.what = STATE_CONNECTED_C;
//                handler.sendMessage(message);

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