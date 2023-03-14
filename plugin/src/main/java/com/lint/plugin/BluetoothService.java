package com.lint.plugin;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.ExpandableListAdapter;

import com.unity3d.player.UnityPlayer;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class BluetoothService {
    public static final String UNITY_OBJECT_NAME = "Bluetooth";
    public Context unityContext = UnityPlayer.currentActivity.getApplicationContext();
    private BluetoothAdapter bluetoothAdapter;
    BluetoothDevice selectDevice;
    Set<BluetoothDevice> pairDevices;

    OutputStream outputStream;
    InputStream inputStream;
    BluetoothDevice bluetoothDevice;

    public BluetoothService(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public boolean CheckBluetoothAvailable(){
        if(bluetoothAdapter == null){
            UnityPlayer.UnitySendMessage(UNITY_OBJECT_NAME, "ReciveMessage", "블루투스 미지원 기기");
            return false;
        }else {
            UnityPlayer.UnitySendMessage(UNITY_OBJECT_NAME, "ReciveMessage", "블루투스 지원 기기");
            return true;
        }
    }

    public String SearchDevice(){
        UnityPlayer.UnitySendMessage(UNITY_OBJECT_NAME, "ReciveMessage", "디바이스 찾기");
        String connetedDeviceInfo = "";
        if(bluetoothAdapter.isEnabled()){
            UnityPlayer.UnitySendMessage(UNITY_OBJECT_NAME, "ReciveMessage", "블루투스 실행중");
            if(Build.VERSION.SDK_INT >= 23 && unityContext.checkSelfPermission("android.permission.BLUETOOTH_CONNECT") != PackageManager.PERMISSION_GRANTED)
                return null;
            pairDevices = bluetoothAdapter.getBondedDevices();

            if(pairDevices.size() > 0){
                for(BluetoothDevice device : pairDevices){
                    UnityPlayer.UnitySendMessage(UNITY_OBJECT_NAME, "ReciveMessage", device.getName());
                    UnityPlayer.UnitySendMessage(UNITY_OBJECT_NAME, "ReciveMessage", device.getAddress());

                    connetedDeviceInfo += device.getName() + "/";
                    connetedDeviceInfo += device.getAddress() + ",";
                }
            }
        }else {
            UnityPlayer.UnitySendMessage(UNITY_OBJECT_NAME, "ReciveMessage", "블루투스 비활성화 상태");
            connetedDeviceInfo += "Bluetooth Off";
        }
        return connetedDeviceInfo;
    }

    public void ConnectedDevice(String DeviceName){
        UnityPlayer.UnitySendMessage(UNITY_OBJECT_NAME, "ReciveMessage", DeviceName);
        if(Build.VERSION.SDK_INT > 23 && unityContext.checkSelfPermission("android.permission.BLUETOOTH_CONNECT") != PackageManager.PERMISSION_GRANTED)
            return;
        if(pairDevices.size() > 0){
            for(BluetoothDevice device : pairDevices){
                UnityPlayer.UnitySendMessage(UNITY_OBJECT_NAME, "ReciveMessage", device.getName());
                if(device.getName().equals(DeviceName)){
                    selectDevice = device;
                    break;
                }
            }
        }
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        String Data = "";
        UnityPlayer.UnitySendMessage(UNITY_OBJECT_NAME, "ReciveMessage", "커넥션 준비중");
        try{
            BluetoothSocket mSocket = selectDevice.createRfcommSocketToServiceRecord(uuid);
            mSocket.connect();
            outputStream = mSocket.getOutputStream();
            UnityPlayer.UnitySendMessage(UNITY_OBJECT_NAME, "ReciveMessage", "커넥션");

        } catch(Exception e){
            UnityPlayer.UnitySendMessage(UNITY_OBJECT_NAME, "ReciveMessage", e.toString());
            e.printStackTrace();
        }
    }

    public void SendData(String data){
        byte[] sendBytes = data.getBytes();
        try{
            outputStream.write(sendBytes);
            UnityPlayer.UnitySendMessage(UNITY_OBJECT_NAME, "ReciveMessage", "데이터 전송 완료" + sendBytes.toString());
        }catch (Exception e){
            UnityPlayer.UnitySendMessage(UNITY_OBJECT_NAME, "ReciveMessage", e.toString());
            e.printStackTrace();
        }
    }
}
