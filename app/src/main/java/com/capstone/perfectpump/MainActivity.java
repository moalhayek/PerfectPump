package com.capstone.perfectpump;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.security.KeyStore;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.TextView;
import android.content.Intent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

public class MainActivity extends AppCompatActivity {

    Button btnPaired;
    ListView devicelist;

    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;
    private LineChart lineChart;
    public static String EXTRA_ADDRESS = "device_address";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPaired = (Button)findViewById(R.id.button);
        devicelist = (ListView)findViewById(R.id.listView);
        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        lineChart = (LineChart) findViewById(R.id.line_chart);
        float yValues [] = {10,20,30,0,40,60};
        String xValues [] = {"first","second","third","fourth","fifth","sixth"};
        drawLineChart(yValues, xValues);

        if(myBluetooth == null){
            // display no bluetooth adapter message here
            // note to self, figure out what toast does/is
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();
            finish();
        }
        else if (!myBluetooth.isEnabled()){

            // tell user to turn on bluetooth
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon,1);

        }

        //start listening for paired devices
        btnPaired.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                pairedDevicesList();
            }
        });

    }

    private void pairedDevicesList(){
        pairedDevices = myBluetooth.getBondedDevices();
        ArrayList<String> list = new ArrayList();

        if (pairedDevices.size() > 0){
            for (BluetoothDevice bt : pairedDevices){
                list.add(bt.getName() + "\n" + bt.getAddress());
            }
        }
        else{
            // notify user no pared bluetooth devices found
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }

        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);

        devicelist.setAdapter(adapter);
        devicelist.setOnItemClickListener(myListClickListener); // method called when the item is clicked from the list
    }

    private void drawLineChart(float[] yValues, String[] xValues){
        Description desc = new Description();
        desc.setText("Test Line Chart");
        lineChart.setDescription(desc);
        ArrayList<Entry> yData = new ArrayList<>();
        for (int i = 0; i < yValues.length; i++){
            yData.add(new Entry(yValues[i],i));
        }
        ArrayList<String> xData = new ArrayList<>();
        for (int i = 0; i < xValues.length; i++){
            xData.add(xValues[i]);
        }
        LineDataSet lineDataSet = new LineDataSet(yData, "Test Line Chart");
        lineDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        LineData lineData = new LineData((ILineDataSet)xData, lineDataSet);
        lineData.setValueTextSize(13f);
        lineData.setValueTextColor(Color.BLACK);

        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick (AdapterView av, View v, int arg2, long arg3)
        {
            // Get the device MAC address, the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);
            // Make an intent to start next activity.
            Intent i = new Intent(MainActivity.this, ledControl.class);
            //Change the activity.
            i.putExtra(EXTRA_ADDRESS, address); //this will be received at ledControl (class) Activity
            startActivity(i);
        }
    };
}
