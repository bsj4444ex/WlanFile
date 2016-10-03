package com.bsj4444.listviewtest2.util;

import android.app.Service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Administrator on 2016/10/3.
 */
public class FileTCPSever {

    public FileTCPSever(){}

    public void start(){
        server s=new server();
        s.start();
    }

    class server extends Thread{
        public void run(){
            try{
                createServer();
            }catch (Exception e){e.printStackTrace();}
        }
    }

    public void createServer() throws Exception{
        Tool.log("创建服务器");
        ServerSocket ss=new ServerSocket(2222);
        Socket s=new Socket();
        s=ss.accept();
        File file=new File(Tool.newsavepath+"/"+Tool.newFileName);
        if(!file.exists()){
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        BufferedInputStream is=new BufferedInputStream(s.getInputStream());
        BufferedOutputStream os=new BufferedOutputStream(new FileOutputStream(file));
        Thread.sleep(1000);
        byte[] data=new byte[Tool.byteSize];
        int len=-1;
        Tool.log("准备接收");
        while((len=is.read(data))!=-1){
            os.write(data,0,len);
            Tool.log("len is "+len);
            //进度条
        }
        Tool.log("接收完毕");

        is.close();
        os.flush();
        os.close();
        s.close();
    }
}
