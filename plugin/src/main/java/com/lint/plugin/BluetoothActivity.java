package com.lint.plugin;

import android.os.Bundle;

import com.unity3d.player.UnityPlayer;

import kotlin.Unit;

public class BluetoothActivity extends UnityPlayerActivity{
    private static BluetoothService bluetoothService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void BluetoothInit(){
        if(bluetoothService == null){
            bluetoothService = new BluetoothService();
            if(!bluetoothService.CheckBluetoothAvailable()){
                UnityPlayer.UnitySendMessage("Bluetooth", "ErrorMessage", "블루투스 미지원 기기");
            }else {
                UnityPlayer.UnitySendMessage("Bluetooth", "ReciveMessage", bluetoothService.SearchDevice());
            }
        }
    }

    public void ConnectedDevice(String deviceName){
        bluetoothService.ConnectedDevice(deviceName);
    }

    public void SendData(String data){
        bluetoothService.SendData(data);
    }
}
