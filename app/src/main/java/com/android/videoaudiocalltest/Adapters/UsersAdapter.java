package com.android.videoaudiocalltest.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.videoaudiocalltest.Calls.VideoCalls;
import com.android.videoaudiocalltest.Models.User;
import com.android.videoaudiocalltest.R;
import com.android.videoaudiocalltest.Util.Common;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder> {

    //data
    List<User> userList;
    Context context;
    Activity activity;


    //constructor
    public UsersAdapter(Context context, List<User> theList, Activity activity) {
        this.userList = theList;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);

        return new UsersViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final UsersViewHolder holder, final int position) {

        //value
        final String userId = userList.get(position).getUser_id();
        final String firstName = userList.get(position).getFirst_name();
        final String lastName = userList.get(position).getLast_name();

        //set Name
        holder.userName.setText(firstName + " " + lastName);


        //voice call
        holder.voiceCall.setOnClickListener(view -> {

            Toast.makeText(context, "Developing", Toast.LENGTH_SHORT).show();

        });

        //video call
        holder.videoCall.setOnClickListener(view -> {

            Intent callIntent = new Intent(context, VideoCalls.class);
            callIntent.putExtra(Common.CALL_USER_INTENT, userId);
            context.startActivity(callIntent);
            activity.overridePendingTransition(R.anim.slide_left, R.anim.fade_out);

        });


    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder {

        //widgets
        CircleImageView userAvatar;
        TextView userName;
        ImageView voiceCall, videoCall;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);

            userAvatar = itemView.findViewById(R.id.userAvatar);
            userName = itemView.findViewById(R.id.userName);
            voiceCall = itemView.findViewById(R.id.voiceCall);
            videoCall = itemView.findViewById(R.id.videoCall);

        }

    }
}
