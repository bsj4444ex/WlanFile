package com.bsj4444.listviewtest2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bsj4444.listviewtest2.util.Tool;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/1.
 */
public class FileManager extends Activity {
    private static String StartDec="/mnt";
    private static String isDirectory="Directory";
    private static String isFile="file";
    private int[] pic=new int[]{R.drawable.img_dir,R.drawable.img_file};
    private String currentDec;
    private Button shouYe;
    private Button shangYe;
    private Button send;
    private ListView listView;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filechange);
        init();
        display(currentDec);
    }
    public void init(){
        currentDec=StartDec;
        shouYe=(Button)findViewById(R.id.shouYe);
        shangYe=(Button)findViewById(R.id.shangYe);
        send=(Button)findViewById(R.id.send);
        send.setVisibility(View.INVISIBLE);
        listView=(ListView)findViewById(R.id.listView);
        textView=(TextView)findViewById(R.id.textView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String,String> map=(Map<String, String>) parent.getAdapter().getItem(position);
                if(map.get("imag").equals(String.valueOf(pic[0]))){
                    currentDec=currentDec+"/"+map.get("name");
                    display(currentDec);
                }
                if(map.get("imag").equals(String.valueOf(pic[1]))){
                    currentDec=currentDec+"/"+map.get("name");
                    Tool.sendFileName=map.get("name");
                    send.setVisibility(View.VISIBLE);
                    shangYe.setText("取消");
                }
            }
        });
        shouYe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDec=StartDec;
                send.setVisibility(View.INVISIBLE);
                shangYe.setText("上页");
                display(currentDec);
            }
        });
        shangYe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentDec!=StartDec){
                    currentDec=new File(currentDec).getParent();
                    send.setVisibility(View.INVISIBLE);
                    shangYe.setText("上页");
                    display(currentDec);
                }
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=FileManager.this.getIntent();
                it.putExtra("path",currentDec);
                FileManager.this.setResult(RESULT_OK,it);
                FileManager.this.finish();
            }
        });
    }
    public void display(String current){
        File f=new File(current);
        File[] files=f.listFiles();
        //Log.d("beiledeng",""+files.length);
        List<Map<String,String>> listCurrent=getList(files);
        SimpleAdapter sa=new SimpleAdapter(FileManager.this,listCurrent,R.layout.fileitem,new String[] {"imag","name"},
                new int[] {R.id.image,R.id.text});
        listView.setAdapter(sa);
        textView.setText(current);
    }
    //没加排序方法
    public List<Map<String,String>> getList(File[] fs){
        List<Map<String,String>> list=new ArrayList<>();

        for(int i=0;i<fs.length;i++){
            Map<String,String> map=new HashMap<>();
            if(fs[i].isDirectory()){
                map.put("imag",String.valueOf(pic[0]));
                map.put("name",fs[i].getName());
            }
            else{
                map.put("imag",String.valueOf(pic[1]));
                map.put("name",fs[i].getName());
            }
            list.add(map);
        }
        return list;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

    }
}
