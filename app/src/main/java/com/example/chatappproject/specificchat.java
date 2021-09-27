package com.example.chatappproject;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class specificchat extends AppCompatActivity {

    EditText mgetmessage;
    ImageButton msendmessagebutton,msendimage;

    CardView msendmessagecardview;
    androidx.appcompat.widget.Toolbar mtoolbarofspecificchat;
    ImageView mimageviewofspecificuser;
    TextView mnameofspecificuser;

    private String enteredmessage;
    Intent intent;
    String mrecievername,sendername,mrecieveruid,msenderuid;
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    private DatabaseReference Rootref;
    String senderroom,recieverroom;

    ImageButton mbackbuttonofspecificchat;

    RecyclerView mmessagerecyclerview;
    String checker="",myUrl="";
    StorageTask uploadTask;
    Uri fileUri;
    private ProgressDialog loadingbar;


    String currenttime;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;

    MessagesAdapter messagesAdapter;
    ArrayList<Messages> messagesArrayList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow ().setFlags (WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_specificchat);

        mgetmessage=findViewById(R.id.getmessage);
        msendmessagecardview=findViewById(R.id.carviewofsendmessage);
        msendmessagebutton=findViewById(R.id.imageviewsendmessage);
        mtoolbarofspecificchat=findViewById(R.id.toolbarofspecificchat);
        mnameofspecificuser=findViewById(R.id.Nameofspecificuser);
        mimageviewofspecificuser=findViewById(R.id.specificuserimageinimageview);
        mbackbuttonofspecificchat=findViewById(R.id.backbuttonofspecificchat);
        msendimage=findViewById (R.id.cam_sendmessage);

        loadingbar=new ProgressDialog (this);

        messagesArrayList=new ArrayList<>();
        mmessagerecyclerview=findViewById(R.id.recyclerviewofspecific);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mmessagerecyclerview.setLayoutManager(linearLayoutManager);
        messagesAdapter=new MessagesAdapter(specificchat.this,messagesArrayList);
        mmessagerecyclerview.setAdapter(messagesAdapter);




        intent=getIntent();

        //setSupportActionBar(mtoolbarofspecificchat);
        mtoolbarofspecificchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Toolbar is Clicked",Toast.LENGTH_SHORT).show();


            }
        });

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        calendar=Calendar.getInstance();
        simpleDateFormat=new SimpleDateFormat("hh:mm a");


        msenderuid=firebaseAuth.getUid();
        mrecieveruid=getIntent().getStringExtra("receiveruid");
        mrecievername=getIntent().getStringExtra("name");



        senderroom=msenderuid+mrecieveruid;
        recieverroom=mrecieveruid+msenderuid;



        DatabaseReference databaseReference=firebaseDatabase.getReference().child("chats").child(senderroom).child("messages");
        messagesAdapter=new MessagesAdapter(specificchat.this,messagesArrayList);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren())
                {
                    Messages messages=snapshot1.getValue(Messages.class);
                    messagesArrayList.add(messages);
                }
                messagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        mbackbuttonofspecificchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        mnameofspecificuser.setText(mrecievername);
        String uri=intent.getStringExtra("imageuri");
        if(uri.isEmpty())
        {
            Toast.makeText(getApplicationContext(),"null is recieved",Toast.LENGTH_SHORT).show();
        }
        else
        {
            Picasso.get().load(uri).into(mimageviewofspecificuser);
        }


        msendmessagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                enteredmessage=mgetmessage.getText().toString();
                if(enteredmessage.isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"Enter message first",Toast.LENGTH_SHORT).show();
                }

                else

                {
                    Date date=new Date();
                    currenttime=simpleDateFormat.format(calendar.getTime());
                    Messages messages=new Messages(enteredmessage,firebaseAuth.getUid(),date.getTime(),currenttime);
                    firebaseDatabase=FirebaseDatabase.getInstance();
                    firebaseDatabase.getReference().child("chats")
                            .child(senderroom)
                            .child("messages")
                            .push().setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            firebaseDatabase.getReference()
                                    .child("chats")
                                    .child(recieverroom)
                                    .child("messages")
                                    .push()
                                    .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });
                        }
                    });

                    mgetmessage.setText(null);




                }




            }
        });
      msendimage.setOnClickListener (new View.OnClickListener () {
          @Override
          public void onClick(View v) {
              CharSequence options[]=new CharSequence[]
                      {
                              "Images",
                              "PDF Files",
                              "MS Word Files"
                      };
              AlertDialog.Builder builder=new AlertDialog.Builder (specificchat.this);
              builder.setTitle ("Select Files");
              builder.setItems (options, new DialogInterface.OnClickListener () {
                  @Override
                  public void onClick(DialogInterface dialog, int i) {
                      if ( i == 0 ) {
                          checker="image";
                          Intent intent=new Intent ();
                          intent.setAction (Intent.ACTION_GET_CONTENT);
                          intent.setType ("image/*");
                         startActivityForResult (intent.createChooser(intent,"Select image"),438);

                      }
                      if ( i == 1 ) {
                          checker="pdf";
                          Intent intent=new Intent ();
                          intent.setAction (Intent.ACTION_GET_CONTENT);
                          intent.setType ("application/pdf");
                          startActivityForResult (intent.createChooser(intent,"Select PDF File"),438);


                      }
                      if ( i == 2 ) {
                          checker="docx";
                          Intent intent=new Intent ();
                          intent.setAction (Intent.ACTION_GET_CONTENT);
                          intent.setType ("application/msword");
                          startActivityForResult (intent.createChooser(intent,"Select MSWord File"),438);

                      }
                  }


              });
              builder.show ();

          }
      });




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
        if(requestCode==438 && resultCode==RESULT_OK && data!=null && data.getData ()!=null)
        {
          loadingbar.setTitle ("Sending File");
          loadingbar.setMessage ("Please wait....");
          loadingbar.setCanceledOnTouchOutside (false);
          loadingbar.show ();

         fileUri=data.getData ();
         if(!checker.equals ("image"))
         {
             StorageReference storageReference= FirebaseStorage.getInstance ().getReference ().child ("Document Files");
             String messagesSenderRef="messages/"+msenderuid+"/"+mrecieveruid;
             String messageReceiverRef="messages/"+mrecieveruid+"/"+msenderuid;
             firebaseDatabase.getReference().child("chats")
                     .child(senderroom)
                     .child("messages")
                     .push();
             int messagepushId=mgetmessage.getId();
             StorageReference filepath=storageReference.child (messagepushId+"."+checker);

             filepath.putFile (fileUri).addOnCompleteListener (new OnCompleteListener<UploadTask.TaskSnapshot> () {
                 @Override
                 public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {

                     if(task.isSuccessful ())
                     {
                         Map messageTextBody=new HashMap ();
                         messageTextBody.put ("message",task.getResult ().getStorage ().getDownloadUrl ().toString ());
                         messageTextBody.put ("name",fileUri.getLastPathSegment ());
                       messageTextBody.put ("type",checker);
                         messageTextBody.put ("from",msenderuid);
                         messageTextBody.put ("to",mrecieveruid);
                         messageTextBody.put ("messageId",messagepushId);

                         Map messageBodyDetails=new HashMap ();
                         messageBodyDetails.put (messagesSenderRef+"/"+messagepushId,messageTextBody);
                         messageBodyDetails.put (messageReceiverRef+"/"+messagepushId,messageTextBody);

                         Rootref.updateChildren (messageBodyDetails);
                         loadingbar.dismiss ();

                     }

                 }
             }).addOnFailureListener (new OnFailureListener () {
                 @Override
                 public void onFailure(@NonNull @NotNull Exception e)
                 {
                 loadingbar.dismiss ();
                 Toast.makeText (specificchat.this,e.getMessage (),Toast.LENGTH_SHORT).show ();
                 }
             }).addOnProgressListener (new OnProgressListener<UploadTask.TaskSnapshot> () {
                 @Override
                 public void onProgress(@NonNull @NotNull UploadTask.TaskSnapshot snapshot)
                 {
                     double p=(100.0*snapshot.getBytesTransferred ())/snapshot.getTotalByteCount ();
                     loadingbar.setMessage ((int) p+"% Uploading...");
                 }
             });

         }
         else if(checker.equals ("image"))
         {
             StorageReference storageReference= FirebaseStorage.getInstance ().getReference ().child ("Image Files");
             String messagesSenderRef="messages/"+msenderuid+"/"+mrecieveruid;
             String messageReceiverRef="messages/"+mrecieveruid+"/"+msenderuid;
             firebaseDatabase.getReference().child("chats")
                     .child(senderroom)
                     .child("messages")
                     .push();
             int messagepushId=mgetmessage.getId();
             StorageReference filepath=storageReference.child (messagepushId+"."+"jpg");
             uploadTask = filepath.putFile (fileUri);
             uploadTask.continueWithTask (new Continuation () {
                 @Override
                 public Object then(@NonNull @NotNull Task task) throws Exception {
                     if(!task.isSuccessful ())
                     {
                         throw task.getException ();
                     }
                     return filepath.getDownloadUrl ();
                 }
             }).addOnCompleteListener (new OnCompleteListener <Uri>() {
                 @Override
                 public void onComplete(@NonNull @NotNull Task<Uri> task) {
                     if(task.isSuccessful ())
                     {
                         Uri downloadUrl =task.getResult ();
                         myUrl=downloadUrl.toString ();

                         Map messageTextBody=new HashMap ();
                         messageTextBody.put ("message",myUrl);
                         messageTextBody.put ("name",fileUri.getLastPathSegment ());
                         messageTextBody.put ("type",checker);
                         messageTextBody.put ("from",msenderuid);
                         messageTextBody.put ("to",mrecieveruid);
                         messageTextBody.put ("messageId",messagepushId);

                         Map messageBodyDetails=new HashMap ();
                         messageBodyDetails.put (messagesSenderRef+"/"+messagepushId,messageTextBody);
                         messageBodyDetails.put (messageReceiverRef+"/"+messagepushId,messageTextBody);


                        Rootref.updateChildren (messageBodyDetails).addOnCompleteListener((task1) -> {
                            if(task1.isSuccessful ())
                            {
                                loadingbar.dismiss ();
                                Toast.makeText (specificchat.this,"Message Send Succesfully",Toast.LENGTH_SHORT).show ();
                            }
                            else
                            {
                                loadingbar.dismiss ();
                                Toast.makeText (specificchat.this,"Error",Toast.LENGTH_SHORT).show ();
                            }
                            mgetmessage.setText ("");

                        });
                     }
                 }
             });




             }
         else
         {
             loadingbar.dismiss ();
             Toast.makeText (this,"Nothing is Selected",Toast.LENGTH_SHORT).show ();
         }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        messagesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(messagesAdapter!=null)
        {
            messagesAdapter.notifyDataSetChanged();
        }
    }



}