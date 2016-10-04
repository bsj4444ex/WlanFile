package com.bsj4444.listviewtest2.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Message;
import android.util.Log;

import com.bsj4444.listviewtest2.MainActivity;
import com.bsj4444.listviewtest2.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.Objects;

/**
 * Created by Administrator on 2016/9/27.
 */
public class Tool {
    public static Context context=null;
    public static MainActivity mainActivity=null;
    public static String hostIp=null; //自己的ip地址
    public static String hostName=null;
    public static  boolean receiveFlag=true;//接收广播标志
    public static boolean receiveMsgFlag=true; //接收消息标志
    public static String sendFileName=null; //要发送的文件名
    public static long sendFileLength=0;//要发送的或接收的文件长度
    public static String receiverUserIp=null; //接收人ip
    public static long progressLong=0;//已接收的长度
    public static final int progressFlush=100;//刷新进度条

    public static final int broadcast=101; //广播消息
    public static final int broadcastResult=102;//回应广播
    public static final int requestSendFile=103;//请求发送文件
    public static final int confirmReceiverFile=104;//确认接收文件
    public static final int refuseReceiverFile=105;//拒绝接收文件
    public static String newsavepath="/mnt/sdcard/myMes";//文件存储路径
    public static String newFileName="name";//存储的文件名称
    public static long newFileLength=0;//存储的文件长度
    public static int byteSize = 1024*5;
    public static String ChooseFilePath=null;//选择的文件路径

    public static final int broadcastPort=6186;  //端口
    public static final int receiverPort=6015;
    public static final int sendPort=6016;
    public static final String broadcastIp="239.255.9.0";  //广播ip


    public Tool(){}
    public static void log(String s){
        Log.d("beiledeng",s);
    }
    //获取本机ip
    public String getHostIp(){
        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = intToIp(ipAddress);
        return ip;
    }
    private String intToIp(int i) {

        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ( i >> 24 & 0xFF) ;
    }
    //发送广播
    public void sendMsg(String IP,Msg msg){
        (new UdpSend(msg)).start();
    }
    class UdpSend extends Thread {
        Msg msg=null;
        UdpSend(Msg msg) {
            this.msg=msg;
        }

        public void run() {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                byte[] data = Tool.getByte(msg);
                MulticastSocket ds = new MulticastSocket(Tool.broadcastPort);
                ds.setTimeToLive(1);
                ds.setLoopbackMode(true);
                InetAddress group=InetAddress.getByName(Tool.broadcastIp);
                ds.joinGroup(group);
                DatagramPacket packet = new DatagramPacket(data, data.length,
                        group, Tool.broadcastPort);
                //packet.setData(data);
                ds.send(packet);
                ds.close();
            } catch (Exception e) {}
        }
    }
    //将msg转为byte
    public static byte[] getByte(Object b){
        byte[] bytes=null;
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        try{
            ObjectOutputStream oos=new ObjectOutputStream(bos);
            oos.writeObject(b);
            oos.flush();
            bytes =bos.toByteArray();
            oos.close();
            bos.close();
        }catch (IOException e){e.printStackTrace();}
        return bytes;
    }
    // 将byte转为object消息解析成对象
    public static Msg getMsg(byte[] bytes) {
        Object obj = null;
        Msg msg=null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            obj = ois.readObject();
            ois.close();
            bis.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        msg=(Msg)obj;
        return msg;
    }

    //接收广播消息
    public void receviemsg(){
        new receiver().start();
    }
    public class receiver extends Thread{
        public void run(){
            byte[] data=new byte[1024*8];
            DatagramPacket packet=null;
            while(Tool.receiveFlag){
                try{
                    MulticastSocket socket=new MulticastSocket(Tool.broadcastPort);
                    socket.joinGroup(InetAddress.getByName(Tool.broadcastIp));
                    packet=new DatagramPacket(data,data.length);
                    socket.receive(packet);
                    byte[] data2=new byte[packet.getLength()];
                    System.arraycopy(data,0,data2,0,data2.length);
                    Msg msg=Tool.getMsg(data2);
                    msgAnalysis(msg);
                    socket.close();
                }catch (Exception e){e.printStackTrace();}
                try{
                    Thread.sleep(500);
                }catch (Exception e){}
            }
        }
    }
    //uI操作
    public void msgAnalysis(Msg msg){
        Message message=new Message();
        message.what=msg.type;
        message.obj=msg;
        Tool.mainActivity.handler.sendMessage(message);
    }
    //发送消息
    public void sendmsg(String ip,Msg msg){
        (new send(ip,msg)).start();
    }
    class send extends Thread{
        Msg msg=null;
        String ip=null;
        public send(String ip,Msg msg){
            this.msg=msg;
            this.ip=ip;
        }
        public void run(){
            try{
                byte[] data=getByte(msg);
                DatagramSocket socket=new DatagramSocket(sendPort);
                DatagramPacket packet=new DatagramPacket(data,data.length,
                        InetAddress.getByName(ip),receiverPort);
                socket.send(packet);
                socket.close();
            }catch (Exception e){e.printStackTrace();}
        }
    }
    //接收消息
    public void receivemsg(){
        (new receiveThread()).start();
    }
    class receiveThread extends Thread{
        public receiveThread(){
        }
        public void run(){
            while(receiveFlag){
                try{
                    byte[] data=new byte[1024*8];
                    DatagramSocket socket=new DatagramSocket(receiverPort);
                    DatagramPacket packet=new DatagramPacket(data,data.length);
                    socket.receive(packet);
                    byte[] data2=new byte[packet.getLength()];
                    System.arraycopy(data,0,data2,0,data2.length);
                    Msg msg=Tool.getMsg(data2);
                    msgAnalysis(msg);
                    socket.close();
                }catch (Exception e){}
            }
        }
    }

}
