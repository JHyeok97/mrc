package org.techtown.mrc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.EditText;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Button btn_send;
    private EditText et_msg;
    private ListView lv_chating;

    private ArrayAdapter<String> arrayAdapter;

    private String str_name;
    private String str_msg;
    private String chat_user;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("message");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv_chating = (ListView) findViewById((R.id.lv_chating));
        btn_send = (Button) findViewById(R.id.btn_send);
        et_msg = (EditText) findViewById((R.id.et_msg));

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        lv_chating.setAdapter(arrayAdapter);

        //listview가 갱신될 때 하단으로 자동 스크롤
        lv_chating.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        str_name = "Giest " + new Random().nextInt(1000);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // map을 사용해 name과 메시지를 가져오고, key에 값을 요청한다.
                Map<String, Object> map = new HashMap<String, Object>();

                //key로 데이터베이스 오픈
                String key = reference.push().getKey();
                reference.updateChildren(map);

                DatabaseReference dbRef = reference.child(key);

                Map<String,Object> objectMap = new HashMap<String, Object>();

                objectMap.put("str_name", str_name);
                objectMap.put("text", et_msg.getText().toString());

                dbRef.updateChildren(objectMap);
                et_msg.setText("");
            }
        });
        /*
        add ChildEventListener는 Child에서 일어나는 변화를 감지
        - onChildAdded()    : 리스트의 아이템을 검색하거나 추가가 있을 때 수신
        - onChildChanged()  : 리스트의 아이템의 변화가 있을 때 수신
        - onChildRemoved()  : 리스트의 아이템이 삭제되었을 때 수신
        - onChildMoved()    : 리스트의 순서가 변경되었을 때 수신
        */

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                chatListener(dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                chatListener(dataSnapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void chatListener(DataSnapshot dataSnapshot){
        //dataSnapshot 밸류값을 가져온다.
        Iterator i = dataSnapshot.getChildren().iterator();

        while (i.hasNext()){
            chat_user = (String) ((DataSnapshot) i.next()).getValue();
            str_msg = (String) ((DataSnapshot) i.next()).getValue();

            //유저 이름, 메시지를 가져와서 array에 추가한다.
            arrayAdapter.add(chat_user + " : " + str_msg);
        }

        //변경된 값으로 리스트 뷰 갱신
        arrayAdapter.notifyDataSetChanged();
    }

}