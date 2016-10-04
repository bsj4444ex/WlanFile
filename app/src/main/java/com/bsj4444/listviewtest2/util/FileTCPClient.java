package com.bsj4444.listviewtest2.util;

import com.bsj4444.listviewtest2.MainActivity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;

/**
 * Created by Administrator on 2016/10/3.
 */
public class FileTCPClient {

    private Msg msg;
    private MainActivity father;
    public FileTCPClient(MainActivity father,Msg msg){
        this.father=father;
        this.msg=msg;
    }

    public void start(){
        Client c=new Client();
        c.start();
    }

    class Client extends Thread{
        public void run(){
            try{
                creatClient();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void creatClient() throws Exception{
        Socket s=new Socket(msg.getSendIp(),2222);
        File file=new File(Tool.ChooseFilePath);
        BufferedInputStream is=new BufferedInputStream(new FileInputStream(file));
        BufferedOutputStream os=new BufferedOutputStream(s.getOutputStream());
        byte[] data=new byte[Tool.byteSize];
        Tool.log("发送文件中。。。。。");
        int len=-1;
        while ((len=is.read(data))!=-1){
            os.write(data,0,len);
            Tool.progressLong+=len;
        }
        Tool.log("发送完毕");
        father.progressDialog.dismiss();
        Tool.progressLong=-1;
        is.close();
        os.flush();
        os.close();
    }
}
