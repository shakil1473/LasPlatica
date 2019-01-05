package com.example.shakil.lasplatica;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageButton btnImageSendMeg;
    private EditText eTxtUserMsg;
    private ScrollView mScrollView;
    private TextView txtDisplayMsg;

    private String currentGroupName;
    private String currentUserID;
    private String currentUserName;
    private String currentDate;
    private String currentTime;
    private String customUsername;

    private String msg;
    private String msgKey;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUserReference;
    private DatabaseReference mDatabaseGroupReference;
    private DatabaseReference mDatabaseGroupMsgKeyReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        currentGroupName = getIntent().getExtras().get("groupName").toString();

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        mDatabaseUserReference = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseGroupReference = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);

        
        initializeFields();

        getUserInfo();

        btnImageSendMeg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!TextUtils.isEmpty(customUsername)) {
                    saveMsgToDatabaseAsAnonymous();
                }
                else {
                    saveMsgToDatabase();
                }
                eTxtUserMsg.setText("");
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        mDatabaseGroupReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()){
                    displayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()){
                    displayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void initializeFields() {
        mToolbar = findViewById(R.id.group_chat_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(currentGroupName);

        btnImageSendMeg = findViewById(R.id.btnImg_send_msg);
        eTxtUserMsg = findViewById(R.id.etxt_user_group_msg);
        txtDisplayMsg = findViewById(R.id.txt_display_group_msg);
        mScrollView = findViewById(R.id.group_char_scroll_view);
    }

    public void getUserInfo() {

        mDatabaseUserReference.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    currentUserName = dataSnapshot.child("username").getValue().toString();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //saving msg using real username
    private void saveMsgToDatabase() {
        msg = eTxtUserMsg.getText().toString();
        msgKey = mDatabaseGroupReference.push().getKey();

        if(TextUtils.isEmpty(msg)){
            Toast.makeText(this,"write something to send",Toast.LENGTH_SHORT).show();
        }
        else{
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd MMM, yyyy");
            currentDate = currentDateFormat.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            currentTime = currentTimeFormat.format(calForTime.getTime());

            HashMap<String, Object> groupMsgKey = new HashMap<>();
            mDatabaseGroupReference.updateChildren(groupMsgKey);

            mDatabaseGroupMsgKeyReference = mDatabaseGroupReference.child(msgKey);

            HashMap<String,Object> msgInfoMap = new HashMap<>();
            msgInfoMap.put("username",currentUserName);
            msgInfoMap.put("message",msg);
            msgInfoMap.put("date",currentDate);
            msgInfoMap.put("time",currentTime);

            mDatabaseGroupMsgKeyReference.updateChildren(msgInfoMap);
        }
    }

    //saving msg as custom name
    private void saveMsgToDatabaseAsAnonymous(){
        Toast.makeText(this,customUsername,Toast.LENGTH_LONG).show();
        msg = eTxtUserMsg.getText().toString();
        msgKey = mDatabaseGroupReference.push().getKey();

        if(TextUtils.isEmpty(msg)){
            Toast.makeText(this,"write something to send",Toast.LENGTH_SHORT).show();
        }
        else{
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd MMM, yyyy");
            currentDate = currentDateFormat.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            currentTime = currentTimeFormat.format(calForTime.getTime());

            HashMap<String, Object> groupMsgKey = new HashMap<>();
            mDatabaseGroupReference.updateChildren(groupMsgKey);

            mDatabaseGroupMsgKeyReference = mDatabaseGroupReference.child(msgKey);

            HashMap<String,Object> msgInfoMap = new HashMap<>();
            msgInfoMap.put("username",customUsername);
            msgInfoMap.put("message",msg);
            msgInfoMap.put("date",currentDate);
            msgInfoMap.put("time",currentTime);

            mDatabaseGroupMsgKeyReference.updateChildren(msgInfoMap);
        }
        customUsername = null;
    }

    //options in the top
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.chat_opetion_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId()==R.id.chat_msg_anonymously_option){
            createCustomUsername();
        }
        return true;
    }

    //creating custom username
    private void createCustomUsername() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GroupChatActivity.this, R.style.AlertDialog);
        builder.setTitle("Custom Username :");

        final EditText eTxtGroupNameField = new EditText(GroupChatActivity.this);
        eTxtGroupNameField.setHint("anonymous");
        builder.setView(eTxtGroupNameField);

        builder.setPositiveButton("create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                customUsername = eTxtGroupNameField.getText().toString();
                Toast.makeText(GroupChatActivity.this,"sender name is 'Anonymous' if no custom name  is given.",Toast.LENGTH_LONG).show();
                if(TextUtils.isEmpty(customUsername)){
                    customUsername = "anonymous";
                }
            }
        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    private void displayMessages(DataSnapshot dataSnapshot) {
        Iterator iterator = dataSnapshot.getChildren().iterator();

        while(iterator.hasNext()){
            String msgDate = (String) ((DataSnapshot)iterator.next()).getValue();
            String msg = (String) ((DataSnapshot)iterator.next()).getValue();
            String msgName = (String) ((DataSnapshot)iterator.next()).getValue();
            String msgTime = (String) ((DataSnapshot)iterator.next()).getValue();

            txtDisplayMsg.append(msgName+" :\n"+msg+"\n"+msgTime+"  "+msgDate+"\n\n");

            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }

}
