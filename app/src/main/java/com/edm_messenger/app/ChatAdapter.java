/*
 * Name: Zuhao Zhang
 *       Adrian Wong
 *       Yuxiang Liu
 *       Zhe Xu
 *       Vu Nguyen
 *       Boram Wong
 *       Srinivas Venkatraman
 *       Apurva Goenka
 *       Steven Kan
 *       Edgar Matias
 *
 * Project: EDM-Messenger
 * Date: 9/23/2019
 * Description: UCSD Fall 2019 Gary's CSE 110 Team EDM's messenger project
 */
package com.edm_messenger.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.edm_messenger.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 *  Class: ChatAdapter.java
 *  Description: Adapter class for chat object
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.Holder> {

    /**
     * Holder class to display Chat view differently from different user's perspective
     */
    public class Holder extends RecyclerView.ViewHolder{
        private  CircleImageView receiver_profile_image;
        private ImageView sender_image_view;
        private ImageView sender_file_image_view;
        private ImageView receiver_image_view;
        private ImageView receiver_file_image_view;
        private TextView receiver_text_view;
        private TextView receiver_name_text_view;
        private TextView receiver_time_view;
        private TextView sender_text_view;
        private TextView sender_name_text_view;
        private TextView sender_time_text_view;

        private Holder(@NonNull View itemView) {

            super(itemView);

            receiver_profile_image = (CircleImageView) itemView.findViewById(R.id.receiver_profile_image);

            receiver_image_view = (ImageView) itemView.findViewById(R.id.receiver_image_view);

            receiver_file_image_view = (ImageView) itemView.findViewById(R.id.receiver_file_image_view);

            receiver_text_view = (TextView) itemView.findViewById(R.id.receiver_text_view);

            receiver_time_view = (TextView) itemView.findViewById(R.id.receiver_time_text_view);

            receiver_name_text_view = (TextView) itemView.findViewById(R.id.receiver_name_text_view);

            sender_image_view = (ImageView) itemView.findViewById(R.id.sender_image_view);

            sender_file_image_view = (ImageView) itemView.findViewById(R.id.sender_file_image_view);

            sender_text_view = (TextView) itemView.findViewById(R.id.sender_text_view);

            sender_name_text_view = (TextView) itemView.findViewById(R.id.sender_name_text_view);

            sender_time_text_view = (TextView) itemView.findViewById(R.id.sender_time_text_view);
        }

        /**
         * Hide all components
         */
        public void  hideAll(){

            this.receiver_text_view.setVisibility(View.GONE);

            this.receiver_image_view.setVisibility(View.GONE);

            this.receiver_profile_image.setVisibility(View.GONE);

            this.receiver_name_text_view.setVisibility(View.GONE);

            this.receiver_time_view.setVisibility(View.GONE);

            this.receiver_file_image_view.setVisibility(View.GONE);

            this.sender_text_view.setVisibility(View.GONE);

            this.sender_image_view.setVisibility(View.GONE);

            this.sender_time_text_view.setVisibility(View.GONE);

            this.sender_name_text_view.setVisibility(View.GONE);

            this.sender_file_image_view.setVisibility(View.GONE);
        }
    }

    /*********************************************************************************************/

    private final static String IMAGE_TYPE = "image";
    private final static String PDF_TYPE = "pdf";
    private final static String LOCATION_TYPE = "location";
    private final static String PROFILE_PIC_KEY = "profilePicDir";
    private final static String NAME_KEY = "name";
    private final static String PDF_QUERY="application/pdf";
    private final static String MAP_PACKAGE = "com.google.android.apps.maps";
    private final static String MAP_QUERY = "?q=";

    private ArrayList<Chat> chats;
    private String myId;
    private String senderId;
    private String content;
    private String type;
    private String time;
    private Context context;

    public ChatAdapter(ArrayList<Chat> chats, Context context) {

        this.chats = chats;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_chat_activity, parent, false);

        return new Holder(view);
    }

    /**
     *
     * @param holder
     * @param pos
     *
     * display different type of chat content
     */
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int pos) {

        getData(pos);

        getUserData(holder);

        holder.hideAll();

        if( type.equals(PDF_TYPE)) {

            displayPdf(holder, pos);

        } else if ( type.equals(IMAGE_TYPE)) {

            displayImage(holder);

        } else if ( type.equals(LOCATION_TYPE) ) {

            displayMap(holder, pos);

        } else{

            displayText(holder);

        }
    }

    /**
     *
     * @param holder
     * given sender ID, access sender's data(name, profile Pictures) and use this information to modify holder
     */
    private void getUserData(Holder holder) {

        Database.getUsersRef()
                .child(senderId)
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){

                            String name = "\t" + dataSnapshot.child(NAME_KEY).getValue().toString();

                            String link = dataSnapshot.child(PROFILE_PIC_KEY).getValue().toString();

                            Picasso.get().load(link).placeholder(R.drawable.profile).into(holder.receiver_profile_image);

                            holder.receiver_name_text_view.setText(name + ": ");

                            holder.sender_name_text_view.setText(CurrentUser.getName() + ": ");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}

                });

    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    /**
     *
     * @param pos: position of item seen on chat adapter
     * get data from chat adapter
     */
    private void getData(int pos){

        myId = CurrentUser.getId();

        senderId = chats.get(pos).getSender();

        content = chats.get(pos).getContent();

        type = chats.get(pos).getType();

        time = Utility.getTime(chats.get(pos).getTimeStamp());

    }

    /**
     *
     * @param holder
     * display text message for current user and other user
     */
    private void displayText(Holder holder){

        if(myId.equals(senderId)){

            holder.sender_text_view.setVisibility(View.VISIBLE);

            holder.sender_name_text_view.setVisibility(View.VISIBLE);

            holder.sender_time_text_view.setVisibility(View.VISIBLE);

            holder.sender_text_view.setBackgroundResource(R.drawable.sender_layout);

            holder.sender_text_view.setText(content);

            holder.sender_time_text_view.setText(time);

        } else{

            holder.receiver_name_text_view.setVisibility(View.VISIBLE);

            holder.receiver_text_view.setVisibility(View.VISIBLE);

            holder.receiver_profile_image.setVisibility(View.VISIBLE);

            holder.receiver_time_view.setVisibility(View.VISIBLE);

            holder.receiver_text_view.setBackgroundResource(R.drawable.receiver_layout);

            holder.receiver_text_view.setText(content);

            holder.receiver_time_view.setText(time);

        }

    }

    /**
     *
     * @param holder
     * display image message for current user and other user.
     */
    private void displayImage(Holder holder){

        if(myId.equals(senderId)){

            holder.sender_name_text_view.setVisibility(View.VISIBLE);

            holder.sender_time_text_view.setVisibility(View.VISIBLE);

            holder.sender_image_view.setVisibility(View.VISIBLE);

            holder.sender_time_text_view.setText(time);

            Picasso.get().load(content).placeholder(R.drawable.default_image).into(holder.sender_image_view);

        } else{

            holder.receiver_name_text_view.setVisibility(View.VISIBLE);

            holder.receiver_profile_image.setVisibility(View.VISIBLE);

            holder.receiver_time_view.setVisibility(View.VISIBLE);

            holder.receiver_image_view.setVisibility(View.VISIBLE);

            holder.receiver_time_view.setText(time);

            Picasso.get().load(content).placeholder(R.drawable.default_image).into(holder.receiver_image_view);
        }

    }

    /**
     *
     * @param holder
     * @param pos
     * display pdf message for current user and other user.
     */
    private void displayPdf(Holder holder,int pos){

        getData(pos);

        if(myId.equals(senderId)){

            holder.sender_name_text_view.setVisibility(View.VISIBLE);

            holder.sender_time_text_view.setVisibility(View.VISIBLE);

            holder.sender_file_image_view.setVisibility(View.VISIBLE);

            holder.sender_time_text_view.setText(time);

            holder.sender_file_image_view.setImageResource(R.drawable.pdf);

            holder.sender_file_image_view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    toPDFIntent(holder,pos);
                }

            });

        } else{

            holder.receiver_name_text_view.setVisibility(View.VISIBLE);

            holder.receiver_profile_image.setVisibility(View.VISIBLE);

            holder.receiver_time_view.setVisibility(View.VISIBLE);

            holder.receiver_file_image_view.setVisibility(View.VISIBLE);

            holder.receiver_time_view.setText(time);

            holder.receiver_file_image_view.setImageResource(R.drawable.pdf);

            holder.receiver_file_image_view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    toPDFIntent(holder,pos);
                }

            });
        }
    }

    /**
     *
     * @param holder
     * @param pos
     * open a new intent to watch pdf file
     */
    private void toPDFIntent(Holder holder,int pos){

        getData(pos);

        Intent pdfIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(content));

        pdfIntent.setDataAndType(Uri.parse(content),PDF_QUERY);

        holder.itemView.getContext().startActivity(pdfIntent);

    }

    /**
     *
     * @param holder
     * @param pos
     * display map message
     */
    private void displayMap(Holder holder, int pos) {

        if(myId.equals(senderId)){

            holder.sender_name_text_view.setVisibility(View.VISIBLE);

            holder.sender_time_text_view.setVisibility(View.VISIBLE);

            holder.sender_file_image_view.setVisibility(View.VISIBLE);

            holder.sender_time_text_view.setText(time);

            holder.sender_file_image_view.setImageResource(R.drawable.google_maps_icon);

            holder.sender_file_image_view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    toMapIntent(holder,pos);
                }

            });

        }else{

            holder.receiver_name_text_view.setVisibility(View.VISIBLE);

            holder.receiver_profile_image.setVisibility(View.VISIBLE);

            holder.receiver_time_view.setVisibility(View.VISIBLE);

            holder.receiver_file_image_view.setVisibility(View.VISIBLE);

            holder.receiver_time_view.setText(time);

            holder.receiver_file_image_view.setImageResource(R.drawable.google_maps_icon);

            holder.receiver_file_image_view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    toMapIntent(holder,pos);
                }

            });
        }
    }

    /**
     *
     * @param holder
     * @param pos
     * open a new intent(Google Map web view) to display current location
     */
    private void toMapIntent(Holder holder, int pos) {

        getData(pos);

        String query = content + MAP_QUERY + content.substring(4);

        Uri gmmIntentUri = Uri.parse(query);

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

        mapIntent.setPackage(MAP_PACKAGE);

        if (mapIntent.resolveActivity(holder.itemView.getContext().getPackageManager()) != null) {
            holder.itemView.getContext().startActivity(mapIntent);
        }
    }

}