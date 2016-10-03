package com.bsj4444.listviewtest2;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;


import com.bsj4444.listviewtest2.util.FileTCPClient;
import com.bsj4444.listviewtest2.util.FileTCPSever;
import com.bsj4444.listviewtest2.util.Msg;
import com.bsj4444.listviewtest2.util.Tool;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* 滑动列表头菜单自动消失
**/

public class MainActivity extends Activity {

    private String choosePath=null;

    private ListView2 listView;
    private List<String> list;
    private Map<String,String> m;
    ListViewAdapter listViewAdapter;
    private Toolbar mToolbar;
    private int mTouchSlop;//最短距离
    private float mFirstY;     //刚碰Y
    private float mCurrentY;  //实现Y
    private int direction;
    private ObjectAnimator mAnimator;
    private boolean mShow = true;
    public Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            Msg msg1=(Msg)msg.obj;
            String sendIp=msg1.getSendIp();
            switch (msg.what){
                case Tool.broadcast:
                    if(!checkIpInList(sendIp)){
                        list.add(sendIp);
                        listViewAdapter.notifyDataSetChanged(); //刷新动态修改后的listVIew中的内容
                    }
                    broadcastMyself(Tool.broadcastResult);
                    break;
                case Tool.broadcastResult:
                    if(!checkIpInList(sendIp)){
                        list.add(sendIp);
                        listViewAdapter.notifyDataSetChanged(); //刷新动态修改后的listVIew中的内容
                    }
                    break;
                case Tool.requestSendFile:
                    Tool.receiverUserIp=sendIp;
                    String[] ss=((String)msg1.getBody()).split(":");
                    Tool.newFileName=ss[0];
                    AlertDialog.Builder dialog=new AlertDialog.Builder(MainActivity.this);
                    dialog.setTitle("接收文件请求来自");
                    dialog.setMessage(sendIp);
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("接收", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Msg msgResult=new Msg(Tool.hostIp,Tool.hostName,Tool.receiverUserIp,null,
                                    Tool.confirmReceiverFile,null);
                                    tool.sendmsg(Tool.receiverUserIp,msgResult);
                            Tool.log("确认接收文件");
                            FileTCPSever fts=new FileTCPSever();
                            fts.start();

                        }
                    }
            );
                    dialog.setNegativeButton("不接收",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Msg msgResult=new Msg(Tool.hostIp,Tool.hostName,Tool.receiverUserIp,null,
                                Tool.refuseReceiverFile,null);
                        tool.sendmsg(Tool.receiverUserIp,msgResult);
                    }
                });
                    dialog.show();
                    break;
                case Tool.refuseReceiverFile:
                    Toast.makeText(MainActivity.this,"对方拒绝接收文件",Toast.LENGTH_SHORT).show();
                    break;
                case Tool.confirmReceiverFile:
                    Tool.log("收到确认请求");
                    FileTCPClient ft=new FileTCPClient(msg1);
                    ft.start();
                    break;
                default:break;
            }
        }
    };
    Tool tool;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //触发移动事件的最短距离
        mTouchSlop = ViewConfiguration.get(this).getScaledTouchSlop();
        listView=(ListView2)findViewById(R.id.listview);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        tool=new Tool();
        Tool.mainActivity=this;
        Tool.context=Tool.mainActivity;
        Tool.hostIp=tool.getHostIp();
        Tool.hostName= Build.USER;

        //接收消息
        tool.receviemsg();
        tool.receivemsg();

        //广播自己
        broadcastMyself(Tool.broadcast);

        list=new ArrayList<String>();
        list.add(Tool.hostIp);



        //view上toolbar部分
        View header=new View(this);
        header.setLayoutParams(new AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT,
                (int)getResources().getDimension(
                        R.dimen.abc_action_bar_default_height_material
                )
        ));
        listView.addHeaderView(header);
        listViewAdapter=new ListViewAdapter(this,list);
        listView.setAdapter(listViewAdapter);
        listView.setOnTouchListener(myTouchListener);
        listView.setOnItemClickListener(myItemListener);
    }
    private void toolbarAnim(int flag){
        if (mAnimator!=null&&mAnimator.isRunning()){
            mAnimator.cancel();
        }
        if (flag==0){
            mAnimator=ObjectAnimator.ofFloat(
                    mToolbar,
                    "translationY",
                    mToolbar.getTranslationY(),
                    0
            );
        }else{
            mAnimator=ObjectAnimator.ofFloat(
                    mToolbar,
                    "translationY",
                    mToolbar.getTranslationY(),
                    -mToolbar.getHeight()
            );
        }
        mAnimator.start();
    }
    View.OnTouchListener myTouchListener=new View.OnTouchListener(){
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    mFirstY=event.getY();break;
                case MotionEvent.ACTION_MOVE:
                    mCurrentY=event.getY();
                    if(mCurrentY-mFirstY>mTouchSlop){
                        direction=0;
                    }else if(mFirstY-mCurrentY>mTouchSlop){
                        direction=1;
                    }
                    if (direction==1){
                        if(mShow){
                            toolbarAnim(1);
                            mShow=!mShow;
                        }
                    }else if(direction==0){
                        toolbarAnim(0);
                        mShow=true;
                    }
                    break;
                case MotionEvent.ACTION_UP:break;
            }
            return false;
        }
    };

    AdapterView.OnItemClickListener myItemListener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Tool.log("position is "+position);
            Tool.receiverUserIp=list.get(position-1);
            Tool.log("reUip"+Tool.receiverUserIp);
            Intent intent = new Intent(MainActivity.this,FileManager.class);
            MainActivity.this.startActivityForResult(intent,1);
        }
    };
    //检查列表中是否有此人
    public boolean checkIpInList(String Ip){
        int Lsize=list.size();
        for(int i=0;i<Lsize;i++){
            Tool.log(list.get(0));
            if(list.get(i).equals(Ip)){
                return true;
            }
        }
        return false;
    }
    //广播自己
    public void broadcastMyself(int broadcastType){
        Msg msg=new Msg(Tool.hostIp,Tool.hostName,null,null,broadcastType,null);
        tool.sendMsg(Tool.broadcastIp,msg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Tool.receiveFlag=false;
        Tool.receiveMsgFlag=false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (resultCode){
            case RESULT_OK:
                choosePath=data.getStringExtra("path");
                Tool.ChooseFilePath=choosePath;
                Tool.log("文件路径"+choosePath);
                Msg msgRequest=new Msg(Tool.hostIp,Tool.hostName,Tool.receiverUserIp,null,
                        Tool.requestSendFile,(new File(choosePath).getName()+":"+new File(choosePath).length()));
                tool.sendmsg(Tool.receiverUserIp,msgRequest);
                break;
            default:break;
        }
    }
}
