package com.bsj4444.listviewtest2.util;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/28.
 */
public class Msg  implements Serializable {
    //public String userName;
    public String receiverIp;
    public String receiverName;
    public String sendIp;
    public String sendName;
    public int type;
    public Object body;

    public Msg(){}

    public Msg(String sendIp,String sendName,String receiverIp,String receiverName,int type,Object body){
        super();
        this.sendIp=sendIp;
        this.sendName=sendName;
        this.receiverIp=receiverIp;
        this.receiverName=receiverName;
        this.type=type;
        this.body=body;
    }

    public String getSendIp(){
        return sendIp;
    }
    public Object getBody(){return body;}

}
