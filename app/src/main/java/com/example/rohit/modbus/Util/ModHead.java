package com.example.rohit.modbus.Util;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.rohit.modbus.Data.RegisterValue;

import net.wimpi.modbus.ModbusException;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Rohit on 20/03/16.
 */
public class ModHead {
    int pollingInterval = 5000;
    ExecutorService ModAnaconda = Executors.newSingleThreadExecutor();
    ExecutorService WriteHead = Executors.newSingleThreadExecutor();

    Handler anaconda;

    private ModHead(){
        // fuck the default constructor
    }

    public ModHead(Handler anaconda) {
        this.anaconda = anaconda;
    }

    public void startPolling() {
        ModAnaconda.execute(reading);
    }

    public void connect() {
        ModAnaconda.execute(connect);
    }

    public void write(RegisterValue regs) {
        if (!Mod.getInstance().isConnected()) {
            WriteHead.execute(connect);
        }
        WriteHead.execute(new Write(regs));
    }

    Runnable connect = new Runnable() {
        @Override
        public void run() {
            try {
                Mod.getInstance().connect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    Runnable reading = new Runnable() {
        @Override
        public void run() {
            while (Mod.getInstance().isConnected()) {
                try {
                    ArrayList<RegisterValue> vals = Mod.getInstance().readRegister();
                    Message message = Message.obtain();
                    Bundle data = new Bundle();
                    data.putParcelableArrayList("regs", vals);
                    message.setData(data);
                    anaconda.sendMessage(message);
                    try {
                        Thread.sleep(pollingInterval);
                    } catch (Exception e) {
                        // That's fine
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    };


    class Write implements Runnable {
        RegisterValue writeReg;
        public Write(RegisterValue writeReg) {
            this.writeReg = writeReg;
        }

        @Override
        public void run() {
            try {
                Mod.getInstance().writeRegistes(this.writeReg);
            } catch (ModbusException e) {
                e.printStackTrace();
            }
        }
    }
}
