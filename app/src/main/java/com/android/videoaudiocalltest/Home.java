package com.android.videoaudiocalltest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.videoaudiocalltest.Adapters.UsersAdapter;
import com.android.videoaudiocalltest.Models.User;
import com.android.videoaudiocalltest.Notification.FCMHelper;
import com.android.videoaudiocalltest.Util.Common;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {

    //widgets
    private TextView userFirstName, greeting;
    private CircleImageView userAvatar;
    private RecyclerView userRecycler;

    //firebase
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    //recycler data
    private LinearLayoutManager layoutManager;
    private List<User> userList;
    private UsersAdapter adapter;

    //local data
    private User paperUser;

    //pagination values
    private DocumentSnapshot lastVisible;
    private boolean isScrolling = false;
    private boolean isLastItemReached = false;
    private int firstVisible;
    private int visibleItemCount;
    private int totalitemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        //local data
        paperUser = Paper.book().read(Common.PAPER_USER);


        //widgets
        userFirstName = findViewById(R.id.userFirstName);
        greeting = findViewById(R.id.greeting);
        userAvatar = findViewById(R.id.userAvatar);
        userRecycler = findViewById(R.id.userRecycler);
        
        
        //name & greeting
        userFirstName.setText(paperUser.getFirst_name());
        greeting.setText(Common.checkTime(Home.this));
        
        
        //user avatar
        userAvatar.setOnClickListener(view -> openMenu());
        
        
        //load users
        loadUsers();
    }

    private void loadUsers() {

        //set layout manager
        userRecycler.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        userRecycler.setLayoutManager(layoutManager);

        //populate recycler
        Query firstQuery = db.collection(Common.USER_NODE)
                .limit(15);
        firstQuery.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()){

                            userList = new ArrayList<>();

                            if (task.getResult().size() >= 1) {

                                for (DocumentSnapshot snapshot : task.getResult()) {

                                    User theUser = snapshot.toObject(User.class);
                                    if (!theUser.getUser_id().equals(paperUser.getUser_id())) {
                                        userList.add(theUser);
                                    }

                                }

                                //set adapter
                                adapter = new UsersAdapter(Home.this, userList, Home.this);
                                userRecycler.setAdapter(adapter);

                                // Get the last visible document
                                lastVisible = task.getResult().getDocuments()
                                        .get(task.getResult().size() - 1);


                                //scroll listener for recycler
                                userRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                    @Override
                                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                        super.onScrollStateChanged(recyclerView, newState);

                                        if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {

                                            isScrolling = true;

                                        }

                                    }

                                    @Override
                                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                        super.onScrolled(recyclerView, dx, dy);

                                        firstVisible = layoutManager.findFirstVisibleItemPosition();
                                        visibleItemCount = layoutManager.getChildCount();
                                        totalitemCount = layoutManager.getItemCount();

                                        if (isScrolling && (firstVisible + visibleItemCount == totalitemCount) && !isLastItemReached) {

                                            isScrolling = false;

                                            //next query
                                            Query nextQuery = db.collection(Common.USER_NODE)
                                                    .startAfter(lastVisible)
                                                    .limit(15);

                                            nextQuery.get()
                                                    .addOnCompleteListener(task1 -> {

                                                        if (task.getResult().size() >= 1) {

                                                            if (task1.isSuccessful()) {

                                                                for (DocumentSnapshot snapshot : task1.getResult()) {

                                                                    User theUser = snapshot.toObject(User.class);
                                                                    if (!theUser.getUser_id().equals(paperUser.getUser_id())) {
                                                                        userList.add(theUser);
                                                                    }

                                                                }

                                                                //notify change
                                                                adapter.notifyDataSetChanged();

                                                                // Get the last visible document
                                                                lastVisible = task1.getResult().getDocuments()
                                                                        .get(task1.getResult().size() - 1);

                                                                //prevent infinite loading
                                                                if (task1.getResult().size() < 15) {

                                                                    isLastItemReached = true;

                                                                }

                                                            }

                                                        }

                                                    });

                                        }

                                    }
                                });

                            }


                        } else {

                            //error
                            Toast.makeText(Home.this, "Error occurred", Toast.LENGTH_SHORT).show();

                        }

                    }
                });

    }

    private void openMenu() {

        //build popup menu
        PopupMenu popup = new PopupMenu(Home.this, userAvatar);
        popup.inflate(R.menu.user_menu);
        popup.setOnMenuItemClickListener(item -> {

            switch (item.getItemId()) {

                case R.id.action_profile:

                    Toast.makeText(this, "Ehl Oh Ehl", Toast.LENGTH_SHORT).show();
                    return true;

                case R.id.action_logout:

                    logout();
                    return true;

                default:
                    return false;
            }
        });

        //show menu
        popup.show();

    }

    private void logout() {

        //destroy local data
        Paper.book().destroy();

        //disable fcm id
        FCMHelper fcmHelper = new FCMHelper();
        fcmHelper.disableFCM();

        //sign out
        mAuth.signOut();

        //update UI
        Intent signOutIntent = new Intent(Home.this, Login.class);
        signOutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(signOutIntent);
        overridePendingTransition(R.anim.slide_right, R.anim.fade_out);
        finish();

    }
}
