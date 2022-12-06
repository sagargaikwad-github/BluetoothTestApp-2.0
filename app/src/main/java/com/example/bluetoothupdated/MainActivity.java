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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    LinearLayout clientLL;
    LinearLayout serverLL;


    Spinner spinner;
    String[] DeviceName = {"Select Device", "A", "B", "C"};
    Button serverStartBTN, clientStartBTN, fourthDeviceBTN;
    private static final String APP_NAME = "Bluetooth App";
    private static final java.util.UUID UUID4th = java.util.UUID.fromString("9bbb4aaa-c772-4e30-853a-e6a64f5e30f3");
    BluetoothAdapter bluetoothAdapter;

     TextView statusOfBluetooth, CounterTV, CounterTVB, CounterTVC;
     TextView fourthDeviceTV;

    int Time = 50;
    int TimeB = 500;
    int TimeC = 50000;
    int seconds = 0;
     String DevName;
     CountDownTimer countDownTimer;
    BluetoothDevice[] paired_device_array;


    private ArrayAdapter arrayAdapter;

    String ThreadName = null;
    final int STATE_CONNECTING = 1;
    final int STATE_CONNECTING_A = 11;
    final int STATE_CONNECTED = 3;
    final int STATE_CONNECTED_A = 13;
    final int STATE_CONNECTION_FAILED = 4;
    final int STATE_BLUETOOTH_OFF = 5;
    final int STATE_MESSAGE_RECIEVED_FROM_A = 7;
    final int STATE_CONNECTION_FAILED_CLIENT_A = 9;


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

    SendReceieve sendReceieveA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        clientLL = findViewById(R.id.clientLL);
        serverLL = findViewById(R.id.serverLL);


        spinner = findViewById(R.id.Spinner);
        serverStartBTN = findViewById(R.id.serverStartBTN);
        clientStartBTN = findViewById(R.id.clientStartBTN);
        statusOfBluetooth = findViewById(R.id.statusOfBluetooth);
        CounterTV = findViewById(R.id.counterTV);

        fourthDeviceBTN = findViewById(R.id.fourthDeviceBTN);
        fourthDeviceTV = findViewById(R.id.fourthDeviceTV);

        CounterTVB = findViewById(R.id.counterTVB);
        CounterTVC = findViewById(R.id.counterTVC);


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
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        //registerReceiver(mReceiver, filter);
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
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTING;
                    handler.sendMessage(message);
                }
            }

            @Override
            public void onFinish() {
                coundownTimerA();
            }
        }.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, DeviceName);
        spinner.setAdapter(arrayAdapter);


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
                case STATE_CONNECTED:
                    statusOfBluetooth.setText("Connected To : " + DevName);
                    break;
                case STATE_CONNECTED_A:
                    DeviceAStatus.setText("Connected To : " + DevName);
                    break;
                case STATE_CONNECTION_FAILED:
                    statusOfBluetooth.setText("Connection Failed");
                    CounterTV.setVisibility(View.GONE);
                    break;
                case STATE_CONNECTION_FAILED_CLIENT_A:
                    DeviceAStatus.setText("Connection Failed");
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
        }
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

                    sendReceieveA = new SendReceieve(socket);
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
                    for (int i = 0; i < uuidList.size(); i++) {
                        socket = device.createRfcommSocketToServiceRecord(uuidList.get(i));

                        return;
                    }
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

                SendReceieve sendRecieveA = new SendReceieve(socket);
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

    public class SendReceieve extends Thread {

        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceieve(BluetoothSocket socket) {
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