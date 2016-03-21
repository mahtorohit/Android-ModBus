package com.example.rohit.modbus.Util;

import com.example.rohit.modbus.Data.EndPoint;
import com.example.rohit.modbus.Data.RegisterValue;

import net.wimpi.modbus.Modbus;
import net.wimpi.modbus.ModbusException;
import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.msg.ModbusRequest;
import net.wimpi.modbus.msg.ReadMultipleRegistersRequest;
import net.wimpi.modbus.msg.ReadMultipleRegistersResponse;
import net.wimpi.modbus.msg.WriteSingleRegisterRequest;
import net.wimpi.modbus.net.TCPMasterConnection;
import net.wimpi.modbus.procimg.Register;
import net.wimpi.modbus.procimg.SimpleRegister;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by Rohit on 20/03/16.
 */
public class Mod {
    private int port = Modbus.DEFAULT_PORT;
    private int length = 100;
    private int slaveId = 1;
    private static Mod mod;
    private InetAddress addr;
    private TCPMasterConnection con;
    private ModbusTCPTransaction trans = null;
    private int startAddress;
    private Boolean disconnect;

    private Mod() {
        // fuck the default constructor
    }

    public void config(EndPoint endpoint) throws UnknownHostException {
        if (mod == null) {
            mod = new Mod();
        }
        this.slaveId = endpoint.getSlaveId();
        this.addr = InetAddress.getByName(endpoint.getIp());
        this.port = endpoint.getPort();
        this.startAddress = endpoint.getAddress();
        this.length = endpoint.getLength();
    }

    public static Mod getInstance() {
        if (mod == null) {
            mod = new Mod();
        }
        return mod;
    }

    public synchronized void connect() throws Exception {
        if (con != null && con.isConnected()) {
            throw new Exception("Alredy connected to :" + addr);
        }
        con = new TCPMasterConnection(addr);
        con.setPort(port);
        con.connect();
        con.setTimeout(15000);
        trans = new ModbusTCPTransaction(con);
        trans.setRetries(5);
        trans.setReconnecting(false);
        disconnect = false;
    }

    public synchronized void disconnect() throws UnknownHostException {
        if (con != null && con.isConnected()) {
            con.close();
            disconnect = false;
        }
    }

    public boolean isConnected() {
        if (con != null && con.isConnected()) {
            return true;
        }
        return false;
    }

    public synchronized ArrayList<RegisterValue> readRegister()
            throws ModbusException {
        return readRegister(this.startAddress, this.length);
    }

    public synchronized ArrayList<RegisterValue> readRegister(int address,
                                                              int length) throws ModbusException {
        if (!isConnected()) {
            throw new ModbusException(
                    "Not connected, ensure you connected to slave before perform any action ");
        }
        ArrayList<RegisterValue> retunValue = new ArrayList<RegisterValue>();
        ReadMultipleRegistersRequest Rreq = new ReadMultipleRegistersRequest(
                address, length);
        ReadMultipleRegistersResponse Rres = new ReadMultipleRegistersResponse();
        Rreq.setUnitID(slaveId);
        Rres.setUnitID(slaveId);
        trans.setRequest(Rreq);
        trans.execute();
        /* Print Response */
        Rres = (ReadMultipleRegistersResponse) trans.getResponse();
        System.out.println("Connected to=  " + this.addr.getHostAddress()
                + con.isConnected() + " / Start Register " + (startAddress));

        for (int k = 0; k < length; k++) {
            RegisterValue reg = new RegisterValue(startAddress + k,
                    Rres.getRegisterValue(k));
            retunValue.add(reg);
            System.out.println("Register ID :" + startAddress + k
                    + " value READ: " + Rres.getRegisterValue(k));
        }
        return retunValue;
    }

    public synchronized void writeRegistes(RegisterValue readRegister)
            throws ModbusException {
        if (readRegister == null) {
            throw new ModbusException("Empty RegisterValue , Write abort !");
        }

        ArrayList<RegisterValue> writeRegisters = new ArrayList<RegisterValue>();
        writeRegisters.add(readRegister);
        this.writeRegistes(writeRegisters);
    }

    public synchronized void writeRegistes(ArrayList<RegisterValue> readRegister)
            throws ModbusException {
        if (!isConnected()) {
            throw new ModbusException(
                    "Not connected, ensure you connected to slave before perform any action ");
        }
        if (readRegister == null || readRegister.size() == 0) {
            throw new ModbusException("Empty RegisterValue , Write abort !");
        }
        for (RegisterValue reg : readRegister) {

            Register newValue = new SimpleRegister();
            newValue.setValue((int) (reg.getRegValue()));
            ModbusRequest request = null;
            // ToDO : -1 is adjustment , need to find root cause . Known issue Need to be fixed
            request = new WriteSingleRegisterRequest(reg.getRegId() - 1, newValue);
            request.setUnitID(this.slaveId);
            trans.setRequest(request);
            trans.execute();
            System.out.println("ModbusSlave: FC: " + request.getFunctionCode()
                    + " ref=" + reg.getRegId() + " value="
                    + newValue.getValue());
        }
    }

}
