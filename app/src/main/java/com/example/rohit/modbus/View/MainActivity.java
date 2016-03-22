package com.example.rohit.modbus.View;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rohit.modbus.Data.EndPoint;
import com.example.rohit.modbus.Data.ListAdapter;
import com.example.rohit.modbus.Data.ModListAdapter;
import com.example.rohit.modbus.Data.RegisterValue;
import com.example.rohit.modbus.R;
import com.example.rohit.modbus.Util.DividerItemDecoration;
import com.example.rohit.modbus.Util.Mod;
import com.example.rohit.modbus.Util.ModHead;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Context context;
    ListView listView;
    ListAdapter adapter;
    Dialog dialog;
    ArrayList<RegisterValue> anacondaDataSource;
    Button connect, writeBtn;
    EditText port, ip, address, length,slaveId;
    ModHead head;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        port = (EditText) findViewById(R.id.textPort);
        ip = (EditText) findViewById(R.id.textIp);
        connect = (Button) findViewById(R.id.connectBtn);
        writeBtn = (Button) findViewById(R.id.writeBtn);
        length = (EditText) findViewById(R.id.textlength);
        address = (EditText) findViewById(R.id.textaddress);
        slaveId = (EditText) findViewById(R.id.textslave);
        anacondaDataSource = new ArrayList<RegisterValue>();
        listView = (ListView) findViewById(R.id.registerListView);
        adapter = new ListAdapter(this, R.layout.recyclerview_item, anacondaDataSource);
        context = this;
        listView.setAdapter(adapter);
        closeKeyBored();
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    EndPoint endpoint = new EndPoint(ip.getText().toString(), Integer.parseInt(port.getText().toString()), Integer.parseInt(address.getText().toString()), Integer.parseInt(length.getText().toString()), Integer.parseInt(slaveId.getText().toString()));
                    Mod.getInstance().config(endpoint);
                    head = new ModHead(anaCondaHandler);
                    head.connect();
                    head.startPolling();
                    closeKeyBored();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog = new Dialog(context);
                dialog.setContentView(R.layout.writ_dialog);
                dialog.setTitle("Write Register");
                RegisterValue obj = anacondaDataSource.get(position);
                final EditText regId = (EditText) dialog.findViewById(R.id.regId);
                final EditText regVal = (EditText) dialog.findViewById(R.id.regValue);
                regId.setText(String.valueOf(obj.getRegId()));
                regVal.setText(String.valueOf(obj.getRegValue()));

                final Button writeBtn = (Button) dialog.findViewById(R.id.writeBtn);
                Button closeBtn = (Button) dialog.findViewById(R.id.CanselBtn);
                // if button is clicked, close the custom dialog
                writeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try{
                            RegisterValue reg = new RegisterValue(Integer.parseInt(regId.getText().toString()), Integer.parseInt(regVal.getText().toString()));
                            ArrayList<RegisterValue> regs = new ArrayList<RegisterValue>();
                            head.write(reg);
                            dialog.dismiss();
                        }catch(Exception e){
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                        }

                    }
                });

                closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }

    public Handler anaCondaHandler = new Handler() {
        public void handleMessage(Message msg) {
            ArrayList<RegisterValue> vals = msg.getData().getParcelableArrayList("regs");
            if (vals != null && vals.size() != 0) {
                anacondaDataSource.clear();
                anacondaDataSource.addAll(vals);
                adapter.notifyDataSetChanged();
            }
        }
    };

    public Handler connectionHandler = new Handler() {
        public void handleMessage(Message msg) {
            int connected = msg.arg1;
            if (connected == 0) {
                connect.setText("Connect");
            } else {
                connect.setText("Disconnect");
            }
        }
    };

    public void closeKeyBored(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
