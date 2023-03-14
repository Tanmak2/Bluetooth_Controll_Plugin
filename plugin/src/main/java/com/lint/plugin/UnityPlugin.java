//package com.lint.plugin;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.ProgressDialog;
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothSocket;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.os.Build;
//import android.widget.Toast;
//
//import com.unity3d.player.UnityPlayer;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Set;
//import java.util.UUID;
//
//public class UnityPlugin{
//    public static final String UNITY_OBJECT_NAME= "Bluetooth";
//    private static final int REQUEST_ENABLE_BT = 1;
//    public Context unityContext;
//    public BluetoothAdapter mBluetoothAdapter;
//    public Set<BluetoothDevice> mDevices;
//    public Set<BluetoothDevice> mPairedDevices;
//    private BluetoothSocket bSocket;
//    private OutputStream mOutputStream;
//    private InputStream mInputStream;
//    private BluetoothDevice mRemoteDevice;
//    public boolean onBT = false;
//    public byte[] sendByte = new byte[4];
//    public ProgressDialog asyncDialog;
//
//    public UnityPlugin(){
//        unityContext = UnityPlayer.currentActivity.getApplicationContext();
//        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        UnityPlayer.UnitySendMessage(UNITY_OBJECT_NAME, "ReciveMessage", "init complete!");
//    }
//
//    public void showToast(String msg){
//        UnityPlayer.currentActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(unityContext, msg, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    public void bluetoothOn(){
//        if(mBluetoothAdapter == null){
//            showToast("블루투스 미지원");
//            UnityPlayer.UnitySendMessage(UNITY_OBJECT_NAME, "ReciveMessage", "블루투스 미지원");
//        }else if(mBluetoothAdapter.isEnabled()){
//            UnityPlayer.UnitySendMessage(UNITY_OBJECT_NAME, "ReciveMessage", "블루투스가 이미 활성화 되어 있음");
//            showToast("블루투스가 이미 활성화 되어 있음");
//        }else{
//            UnityPlayer.UnitySendMessage(UNITY_OBJECT_NAME, "ReciveMessage", "블루투스 활성화 중");
//            showToast("블루투스 활성화 중");
//            if(Build.VERSION.SDK_INT >= 23 && unityContext.checkSelfPermission("android.permission.BLUETOOTH_CONNECT") != PackageManager.PERMISSION_GRANTED)
//                return;
//            mBluetoothAdapter.enable();
//            UnityPlayer.UnitySendMessage(UNITY_OBJECT_NAME, "ReciveMessage", "블루투스 활성화");
//            showToast("블루투스 활성화");
//        }
//    }
//
//    public void bluetoothOff(){
//        if(mBluetoothAdapter == null){
//            showToast("블루투스 미지원");
//            UnityPlayer.UnitySendMessage(UNITY_OBJECT_NAME, "ReciveMessage", "블루투스 미지원");
//        }else if(!mBluetoothAdapter.isEnabled()){
//            UnityPlayer.UnitySendMessage(UNITY_OBJECT_NAME, "ReciveMessage", "블루투스가 이미 비활성화 되어 있음");
//            showToast("블루투스가 이미 비활성화 되어 있음");
//        }else{
//            UnityPlayer.UnitySendMessage(UNITY_OBJECT_NAME, "ReciveMessage", "블루투스 비활성화 중");
//            showToast("블루투스 비활성화 중");
//            if(Build.VERSION.SDK_INT >= 23 && unityContext.checkSelfPermission("android.permission.BLUETOOTH_CONNECT") != PackageManager.PERMISSION_GRANTED)
//                return;
//            mBluetoothAdapter.disable();
//            UnityPlayer.UnitySendMessage(UNITY_OBJECT_NAME, "ReciveMessage", "블루투스 비활성화");
//            showToast("블루투스 비활성화");
//        }
//    }
//
//    public void listPairedDevices(){
//        if(mBluetoothAdapter.isEnabled()){
//            if(Build.VERSION.SDK_INT >= 23 && unityContext.checkSelfPermission("android.permission.BLUETOOTH_CONNECT") != PackageManager.PERMISSION_GRANTED)
//                return;
//            mPairedDevices = mBluetoothAdapter.getBondedDevices();
//            if(mPairedDevices.size() > 0){
//                List<String> mListPairedDevices = new ArrayList<>();
//                for(BluetoothDevice device : mPairedDevices){
//                    mListPairedDevices.add(device.getName());
//                    UnityPlayer.UnitySendMessage(UNITY_OBJECT_NAME, "ReciveMessage", "페어링된 장치 이름 : " + device.getName());
//                }
//            }else{
//                showToast("페어링된 장치가 없음");
//            }
//        }else {
//            showToast("블루투스 비활성화 상태");
//        }
//    }
//
//    public void bluetoothOnOff(){
//        UnityPlayer.UnitySendMessage(UNITY_OBJECT_NAME, "ReciveMessage", "bluetoothOnOff start");
//        if(!onBT){
//            UnityPlayer.UnitySendMessage(UNITY_OBJECT_NAME, "ReciveMessage", "onBT in");
//            if(mBluetoothAdapter == null){
//                showToast("Bluetooth 지원을 하지 않는 기기입니다.");
//            }else {
//                if(!mBluetoothAdapter.isEnabled()){
//                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//                }else {
//                    if(this.unityContext.checkSelfPermission("android.permission.BLUETOOTH_CONNECT") != PackageManager.PERMISSION_GRANTED){
//                        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
//                        if(pairedDevices.size() > 0){
//                            selectDevice();
//                        }else{
//                            showToast("먼저 Bluetooth 설정에 들어가 페어링을 진행해주세요");
//                        }
//                    }
//                }
//            }
//        }else{
//            try{
//                BTSend.interrupt();
//                mInputStream.close();
//                mOutputStream.close();
//                bSocket.close();
//                onBT = false;
//
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }
//        UnityPlayer.UnitySendMessage(UNITY_OBJECT_NAME, "ReciveMessage", "bluetoothOnOff end");
//    }
//
//    public void selectDevice(){
//        UnityPlayer.UnitySendMessage(UNITY_OBJECT_NAME, "ReciveMessage", "권한 확인 중");
//        if(Build.VERSION.SDK_INT >= 23 && unityContext.checkSelfPermission("android.permission.BLUETOOTH_CONNECT") != PackageManager.PERMISSION_GRANTED)
//            return;
//        UnityPlayer.UnitySendMessage(UNITY_OBJECT_NAME, "ReciveMessage", "권한 확인 완료");
//        mDevices = mBluetoothAdapter.getBondedDevices();
//        UnityPlayer.UnitySendMessage(UNITY_OBJECT_NAME, "ReciveMessage", mDevices.toString());
//        final int mPairedDeviceCount = mDevices.size();
//        if(mPairedDeviceCount == 0) {
//            showToast("장치를 페어링 해주세요!");
//        }
//        if(!isFinishing()) {
//            UnityPlayer.UnitySendMessage(UNITY_OBJECT_NAME, "ReciveMessage", "isFinishing() 내부");
//            AlertDialog.Builder builder = new AlertDialog.Builder(unityContext);
//            builder.setTitle("블루투스 장치 선택");
//            UnityPlayer.UnitySendMessage(UNITY_OBJECT_NAME, "ReciveMessage", "권한 확인 완료");
//            List<String> listItems = new ArrayList<>();
//            for (BluetoothDevice device : mDevices) {
//                listItems.add(device.getName());
//            }
//            listItems.add("취소");
//            final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);
//
//            builder.setItems(items, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int item) {
//                    if (item == mPairedDeviceCount) {
//
//                    } else {
//                        connectToSelectedDevice(items[item].toString());
//                    }
//                }
//            });
//            builder.setCancelable(false);
//            UnityPlayer.UnitySendMessage(UNITY_OBJECT_NAME, "ReciveMessage", "취소버튼 설정");
//            builder.show();
//        }
//    }
//
//    public void dialogView(){
//        AlertDialog.Builder builder = new AlertDialog.Builder(unityContext);
//        builder.setTitle("테스트").setMessage("테스트용 다이얼로그");
//        AlertDialog alertDialog = builder.create();
//        alertDialog.show();
//    }
//
//    public void connectToSelectedDevice(final String selectedDeviceName){
//        mRemoteDevice = getDeviceFromBondedList(selectedDeviceName);
//
//        asyncDialog = new ProgressDialog(this);
//        asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        asyncDialog.setMessage("블루투스 연결중..");
//        asyncDialog.show();
//        asyncDialog.setCancelable(false);
//
//        Thread BTConnect = new Thread(new Runnable() {
//
//            private Context unityContext;
//
//            @Override
//            public void run() {
//                try{
//                    UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
//                    if(this.unityContext.checkSelfPermission("android.permission.BLUETOOTH_CONNECT") != PackageManager.PERMISSION_GRANTED) {
//                        bSocket = mRemoteDevice.createRfcommSocketToServiceRecord(uuid);
//                        bSocket.connect();
//                        mOutputStream = bSocket.getOutputStream();
//                        mInputStream = bSocket.getInputStream();
//
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                showToast("연결 완료");
//                                asyncDialog.dismiss();
//                            }
//                        });
//                    }
//                    onBT = true;
//                }catch(Exception e){
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            showToast("연결 오류");
//                            asyncDialog.dismiss();
//                        }
//                    });
//                }
//            }
//        });
//        BTConnect.start();
//    }
//
//    public BluetoothDevice getDeviceFromBondedList(String name){
//        BluetoothDevice selectedDevice = null;
//
//        for(BluetoothDevice device : mDevices) {
//            if(this.unityContext.checkSelfPermission("android.permission.BLUETOOTH_CONNECT") != PackageManager.PERMISSION_GRANTED){
//                if(name.equals(device.getName())){
//                    selectedDevice = device;
//                    break;
//                }
//            }
//        }
//        return selectedDevice;
//    }
//
//    Thread BTSend = new Thread(new Runnable() {
//        @Override
//        public void run() {
//            try{
//                mOutputStream.write(sendByte);
//            } catch (Exception e){
//                e.printStackTrace();
//            }
//        }
//    });
//
//    public void sendbtData(String data) throws IOException {
//        sendByte = data.getBytes();
//        BTSend.run();
//    }
//}
