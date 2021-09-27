package com.example.chatappproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;


public class MessagesAdapter extends RecyclerView.Adapter {


    Context context;
    ArrayList<Messages> messagesArrayList;

    int ITEM_SEND=1;
    int ITEM_RECIEVE=2;


    public MessagesAdapter(Object context, ArrayList<Messages> messagesArrayList) {
        this.context = (Context) context;
        this.messagesArrayList = messagesArrayList;
    }

    public MessagesAdapter(Object messages,chatActivity chatActivity) {
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==ITEM_SEND)
        {
            View view= LayoutInflater.from(context).inflate(R.layout.senderchatlayout,parent,false);
            return new SenderViewHolder(view);
        }
        else
        {
            View view= LayoutInflater.from(context).inflate(R.layout.recieverchatlayout,parent,false);
            return new RecieverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Messages messages=messagesArrayList.get(position);
        int reactions[]=new int[]{
                R.drawable.ic_fb_like,
                R.drawable.ic_fb_love,
                R.drawable.ic_fb_laugh,
                R.drawable.ic_fb_wow,
                R.drawable.ic_fb_sad,
                R.drawable.ic_fb_angry
        };



        ReactionsConfig config = new ReactionsConfigBuilder (context)
                .withReactions(reactions)
                .build();

        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {
            if ( holder.getClass () == SenderViewHolder.class ) {
                SenderViewHolder viewHolder = (SenderViewHolder) holder;

                //viewHolder.textViewmessaage.feelings.setImageResource(reactions[pos]);

            }
            return true; // true is closing popup, false is requesting a new selection
        });
        if(holder.getClass()==SenderViewHolder.class)
        {
            SenderViewHolder viewHolder=(SenderViewHolder)holder;
            if(messages.getMessage ().equals ("image"))
            {
               viewHolder.senderimage.setVisibility (View.VISIBLE);
               viewHolder.textViewmessaage.setVisibility (View.GONE);
                Glide.with (context).load (messages.getImageurl ()).into (viewHolder.senderimage);
            }
           viewHolder.textViewmessaage.setText(messages.getMessage());
            viewHolder.timeofmessage.setText(messages.getCurrenttime());





            viewHolder.textViewmessaage.setOnTouchListener (new View.OnTouchListener () {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v,event);
                    return false;
                }
            });





        }
        else
        {
            RecieverViewHolder viewHolder=(RecieverViewHolder)holder;
            viewHolder.textViewmessaage.setText(messages.getMessage());
            viewHolder.timeofmessage.setText(messages.getCurrenttime());
            viewHolder.feelings.setVisibility (View.VISIBLE);


            viewHolder.textViewmessaage.setOnTouchListener (new View.OnTouchListener () {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v,event);
                    return false;
                }
            });



        }










    }


    @Override
    public int getItemViewType(int position) {
        Messages messages=messagesArrayList.get(position);

        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messages.getSenderId()))

        {
            return  ITEM_SEND;
        }
        else
        {
            return ITEM_RECIEVE;
        }


    }

    @Override
    public int getItemCount() {
        return messagesArrayList.size();
    }







    class SenderViewHolder extends RecyclerView.ViewHolder{



        //public Messages binding;
        TextView textViewmessaage;
        TextView timeofmessage;
        ImageView feeling,senderimage;




        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewmessaage=itemView.findViewById(R.id.sendermessage);
            timeofmessage=itemView.findViewById(R.id.timeofmessage);
            feeling=itemView.findViewById (R.id.feeling);
            senderimage=itemView.findViewById (R.id.iv_sender);



        }
    }

    public class RecieverViewHolder extends RecyclerView.ViewHolder
    {


        TextView textViewmessaage;
        TextView timeofmessage;
        ImageView feelings;

        public RecieverViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewmessaage=itemView.findViewById(R.id.sendermessage);
            timeofmessage=itemView.findViewById(R.id.timeofmessage);
            feelings=itemView.findViewById (R.id.feelings);


        }
    }



}

