package com.example.chatappproject;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SendImage extends AppCompatActivity {
    String url, mrecievername, mrecieveruid, msenderuid;

    ImageView imageView;
    Uri imageurl;
    ProgressBar progressBar;
    Button button;
    UploadTask uploadTask;
    FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    StorageReference storageReference;
    FirebaseStorage firebaseStorage;
    DatabaseReference rootRef1, rootRef2;
    FirebaseDatabase database = FirebaseDatabase.getInstance ();
    TextView textView;
    String senderroom,recieverroom;
    EditText mgetmessage;
    private String enteredmessage;


    String currenttime;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;

    private Uri uri;
    MessagesAdapter messagesAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_send_image);


        storageReference = firebaseStorage.getInstance ().getReference ("Message Images");

        imageView = findViewById (R.id.iv_sendimage);
        button = findViewById (R.id.btn_sendimage);
        progressBar = findViewById (R.id.pb_sendimage);
        textView = findViewById (R.id.tv_dont);

        Bundle bundle = getIntent ().getExtras ();
        if ( bundle != null ) {
            url = bundle.getString ("u");
            mrecievername = bundle.getString ("n");
            mrecieveruid = bundle.getString ("ruid");
            msenderuid = bundle.getString ("suid");
        } else {
            Toast.makeText (this, "error", Toast.LENGTH_SHORT).show ();
        }
        Picasso.get ().load (url).into (imageView);
        uri = Uri.parse (url);

        rootRef1 = database.getReference ("Message").child (msenderuid).child (mrecieveruid);
        rootRef2 = database.getReference ("Message").child (mrecieveruid).child (msenderuid);


        button.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                sendImage ();
                textView.setVisibility (View.VISIBLE);
            }
        });
    }

    private String getFileExt(Uri uri) {
        ContentResolver contentResolver = getContentResolver ();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton ();
        return mimeTypeMap.getExtensionFromMimeType (contentResolver.getType (uri));
    }

    private void sendImage() {

        if ( imageurl != null ) {

            progressBar.setVisibility (View.VISIBLE);
            final StorageReference reference = storageReference.child (System.currentTimeMillis () + "." + getFileExt (imageurl));
            uploadTask = reference.putFile (imageurl);

            Task<Uri> uriTask = uploadTask.continueWithTask (new Continuation<UploadTask.TaskSnapshot, Task<Uri>> () {
                @Override
                public Task<Uri> then(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if ( !task.isSuccessful () ) {
                        throw task.getException ();
                    }
                    return reference.getDownloadUrl ();
                }
            }).addOnCompleteListener (new OnCompleteListener<Uri> () {
                @Override
                public void onComplete(@NonNull @NotNull Task<Uri> task) {
                    if ( task.isSuccessful () ) {
                        Uri downloadUri = task.getResult ();

                       /** Calendar cdate=Calendar.getInstance ();
                        SimpleDateFormat currentdate=new SimpleDateFormat ("dd-MMMM-yyyy");
                        final String savedate=currentdate.format (cdate.getTime ());

                        Calendar ctime=Calendar.getInstance ();
                        SimpleDateFormat currenttime=new SimpleDateFormat ("HH:mm:ss");
                        final String savetime=currenttime.format (ctime.getTime ());

                        String time=savedate+":"+savetime;**/

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
                }
            });







        } else {
            Toast.makeText (this,"Please Select Something",Toast.LENGTH_SHORT).show ();

        }
    }
}