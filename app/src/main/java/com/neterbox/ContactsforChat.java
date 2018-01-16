package com.neterbox;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.neterbox.customadapter.ContactsforChatAdapter;

public class ContactsforChat extends AppCompatActivity {
    ListView groupchat;
    Button boneononechat;
    ContactsforChatAdapter adapter;
    public ContactsforChat contactsforChat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactsfor_chat);

        contactsforChat=this;

        Resources res=getResources();
        groupchat=(ListView) findViewById(R.id.groupchat);

       adapter=new ContactsforChatAdapter(contactsforChat);
        groupchat.setAdapter(adapter);
        boneononechat=(Button)findViewById(R.id.boneononechat);
        boneononechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=new Intent(ContactsforChat.this,Contactsforoneononechat.class);
                startActivity(it);
            }
        });
    }
}
