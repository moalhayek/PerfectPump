package com.capstone.perfectpump;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;
import java.io.IOException;

import java.util.ArrayList;
import java.util.UUID;
import android.os.Handler;
import java.io.InputStream;
import java.io.OutputStream;


public class ledControl extends AppCompatActivity {

    Button btnOn, btnOff, btnDis;
    SeekBar brightness;
    TextView lumn;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter btAdapter = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // not quite sure how this string was generated

    //added for receiving data:
    TextView sensor, txtString;
    Handler bluetoothIn;
    final int handlerState = 0;                        //used to identify handler message
    private StringBuilder recDataString = new StringBuilder();
    private ConnectedThread mConnectedThread;

    ArrayList<Integer> sensorVals = new ArrayList<Integer>();

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led_control);

        //receive the address of the bluetooth device
        Intent newint = getIntent();
        address = newint.getStringExtra(MainActivity.EXTRA_ADDRESS);

        //view of teh ledControl layout
        setContentView(R.layout.activity_led_control);
        //call the widgets
        btnOn = (Button)findViewById(R.id.turnOn);
        btnOff = (Button)findViewById(R.id.turnOff);
        btnDis = (Button)findViewById(R.id.disconnect);
        brightness = (SeekBar)findViewById(R.id.seekBar);
        lumn = (TextView)findViewById(R.id.lumn);
        sensor = (TextView)findViewById(R.id.sens);
        txtString = (TextView) findViewById(R.id.txtString);

        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg){
                if (msg.what == handlerState){      //if message is what we want
                    String readMessage = (String) msg.obj;
                    recDataString.append(readMessage);      //keep appending to string until ~
                    int endOfLineIndex = recDataString.indexOf("~");    //determine the end of line
                    if (endOfLineIndex > 0) {               // make sure there is data before ~
                        String dataInPrint = recDataString.substring(0, endOfLineIndex);    // extract string
                        txtString.setText("Data Received = " + dataInPrint);

                        if (recDataString.charAt(0) == '#')                             //if it starts with # we know it is what we are looking for
                        {
                            String sensor0 = recDataString.substring(1, 4);             //get sensor value from string between indices 1-5

                            sensor.setText("Sensor Value: " + sensor0);    //update the textviews with sensor values
                            sensorVals.add(Integer.parseInt(sensor0));
                        }
                        recDataString.delete(0, recDataString.length());                    //clear all string data
                        dataInPrint = "";
                    }



                }
            }
        };

        new ConnectBT().execute();

        btnOn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d("LISTENER", "turn on led button pressed");
                turnOnLed();    //method to turn on
            }

        });

        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                turnOffLed();   //method to turn off
            }
        });

        btnDis.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Disconnect(); //close connection
            }
        });

        brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser ){
                if (fromUser==true){
                    lumn.setText(String.valueOf(progress));
                    try{
                        btSocket.getOutputStream().write(String.valueOf(progress).getBytes());
                    }
                    catch (IOException e){

                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar){

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar){

            }
        });

    }

    private void Disconnect() {
        if (btSocket != null) { //If the btSocket is busy
            try{
                btSocket.close(); //close connection
            }
            catch (IOException e){
                msg("Error");
            }
        }
        finish();
    }

    private void turnOffLed(){
//        if (btSocket!=null){
//            try
//            {
//                btSocket.getOutputStream().write("TF".toString().getBytes());
//            }
//            catch (IOException e){
//                msg("Error");
//            }
//        }
        mConnectedThread.write("0");
        msg("Turn off LED");
    }

    private void turnOnLed()
    {
//        Log.d("BUTTON", "turn on led button pressed");
//        if (btSocket!=null)
//        {
//            Log.d("IF", "in the if statement");
//            try
//            {
//                btSocket.getOutputStream().write("TO".toString().getBytes());
//                Log.d("BT", "sent to output stream");
//            }
//            catch (IOException e)
//            {
//                msg("Error");
//            }
//        }

        mConnectedThread.write("1");
        msg("Turn on LED");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        try
        {
            //Don't leave Bluetooth sockets open when leaving activity
            btSocket.close();
        } catch (IOException e2) {
            //insert code to deal with this
        }
    }

    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> //UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(ledControl.this, "Connecting...", "Please wait!!!"); // show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try {
                if (btSocket == null || !isBtConnected) {
                    btAdapter = BluetoothAdapter.getDefaultAdapter(); // get the mobile bluetooth device
                    BluetoothDevice dispositivo = btAdapter.getRemoteDevice(address); // connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);// create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect(); //start connection
                }
            }
            catch(IOException e){
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }

            mConnectedThread = new ConnectedThread(btSocket);
            mConnectedThread.start();
            mConnectedThread.write("x");
            
            return null;
        }

        @Override
        protected void onPostExecute(Void result){  //after the doInBackground, it checks if everything went fine
            super.onPostExecute(result);

            if (!ConnectSuccess){
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }
            else{
                msg("Connected.");
                isBtConnected = true;
            }

            progress.dismiss();
        }
    }

    //create new class for connect thread
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);            //read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                finish();

            }
        }
    }
}