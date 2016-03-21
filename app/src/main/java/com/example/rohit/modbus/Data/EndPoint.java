package com.example.rohit.modbus.Data;

/**
 * Created by Rohit on 20/03/16.
 */
public class EndPoint {
    private int port;
    private String ip;
    private int address;
    private int length;
    private int slaveId;

    /**
     * @param ip      Target machine IP address
     * @param port    Target machine Port Number
     * @param address Register address
     * @param length  Number of Register for read / write
     * @param slaveId Target Slave ID for Communication
     */
    public EndPoint(String ip, int port, int address, int length, int slaveId) {
        this.ip = ip;
        this.port = port;
        this.address = address;
        this.length = length;
        this.slaveId = slaveId;
    }

    public EndPoint(int port, String ip) {
        this.port = port;
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }


    public int getSlaveId() {
        return slaveId;
    }

    public void setSlaveId(int slaveId) {
        this.slaveId = slaveId;
    }
}
