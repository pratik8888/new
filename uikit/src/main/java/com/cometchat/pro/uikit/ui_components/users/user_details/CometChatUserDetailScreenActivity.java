package com.cometchat.pro.uikit.ui_components.users.user_details;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.Call;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.MessagesRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Action;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.GroupMember;
import com.cometchat.pro.models.User;

import com.cometchat.pro.uikit.ui_components.messages.message_list.CometChatMessageListActivity;
import com.cometchat.pro.uikit.ui_components.shared.cometchatAvatar.CometChatAvatar;
import com.cometchat.pro.uikit.R;
import com.cometchat.pro.uikit.ui_components.shared.cometchatSharedMedia.CometChatSharedMedia;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.cometchat.pro.uikit.ui_components.users.user_details.callHistroy.CallHistoryAdapter;
import com.cometchat.pro.uikit.ui_resources.constants.UIKitConstants;
import com.cometchat.pro.uikit.ui_resources.utils.CallUtils;
import com.cometchat.pro.uikit.ui_resources.utils.FontUtils;
import com.cometchat.pro.uikit.ui_settings.UISettings;
import com.cometchat.pro.uikit.ui_resources.utils.Utils;

public class CometChatUserDetailScreenActivity extends AppCompatActivity {
    private CometChatAvatar userAvatar;

    private TextView userStatus, userName, addBtn;

    private View progressbarBG;

    private String name;

    private String TAG = "CometChatUserDetailScreenActivity";

    private String avatar;

    private String uid;

    private String guid;

    private String groupName;

    private boolean isAddMember;

    private boolean isAlreadyAdded;

    private TextView tvSendMessage;

    private LinearLayout blockUserLayout;

    private TextView tvBlockUser;

    private MaterialToolbar toolbar;

    private boolean isBlocked;

    private FontUtils fontUtils;

    private ImageView callBtn;

    private ImageView videoCallBtn;

    private LinearLayout historyView;

    private RecyclerView historyRv;

    private CallHistoryAdapter callHistoryAdapter;

    private MessagesRequest messageRequest;

    private CometChatSharedMedia sharedMediaView;

    private LinearLayout sharedMediaLayout;

    private boolean inProgress;

    private boolean fromCallList;

    private View divider1,divider2,divider3;

    private List<BaseMessage> callList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cometchat_user_detail);
        fontUtils= FontUtils.getInstance(this);
        initComponent();

    }

    private void initComponent() {

        historyView = findViewById(R.id.history_view);
        historyRv = findViewById(R.id.history_rv);
        userAvatar = findViewById(R.id.iv_user);
        userName = findViewById(R.id.tv_name);
        userStatus = findViewById(R.id.tv_status);
        callBtn = findViewById(R.id.callBtn_iv);
        videoCallBtn = findViewById(R.id.video_callBtn_iv);
        addBtn = findViewById(R.id.btn_add);
        progressbarBG = findViewById(R.id.bg_progressbar);
        tvSendMessage = findViewById(R.id.tv_send_message);
        toolbar= findViewById(R.id.user_detail_toolbar);
        divider1 = findViewById(R.id.divider_1);
        divider2 = findViewById(R.id.divider_2);
        divider3 = findViewById(R.id.divider_3);

        setSupportActionBar(toolbar);
         getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addBtn.setTypeface(fontUtils.getTypeFace(FontUtils.robotoRegular));


        blockUserLayout = findViewById(R.id.block_user_layout);

        tvBlockUser = findViewById(R.id.tv_blockUser);

        tvBlockUser.setTypeface(fontUtils.getTypeFace(FontUtils.robotoMedium));

        userName.setTypeface(fontUtils.getTypeFace(FontUtils.robotoMedium));


        handleIntent();

        sharedMediaLayout = findViewById(R.id.shared_media_layout);
        sharedMediaView = findViewById(R.id.shared_media_view);
        sharedMediaView.setRecieverId(uid);
        sharedMediaView.setRecieverType(CometChatConstants.RECEIVER_TYPE_USER);
        sharedMediaView.reload();

        if (!UISettings.isViewSharedMedia())
            sharedMediaLayout.setVisibility(View.GONE);

        checkDarkMode();
        addBtn.setOnClickListener(view -> {

            if (guid != null) {
                if (isAddMember) {
                    if (isAlreadyAdded)
                        kickGroupMember();
                    else
                        addMember();
                }
            }
        });

        tvSendMessage.setOnClickListener(view -> {
              if (isAddMember || fromCallList){
                  Intent intent=new Intent(CometChatUserDetailScreenActivity.this, CometChatMessageListActivity.class);
                  intent.putExtra(UIKitConstants.IntentStrings.TYPE, CometChatConstants.RECEIVER_TYPE_USER);
                  intent.putExtra(UIKitConstants.IntentStrings.UID,uid);
                  intent.putExtra(UIKitConstants.IntentStrings.NAME,name);
                  intent.putExtra(UIKitConstants.IntentStrings.AVATAR,avatar);
                  intent.putExtra(UIKitConstants.IntentStrings.STATUS, CometChatConstants.USER_STATUS_OFFLINE);
                  startActivity(intent);
              }else
                  onBackPressed();
        });

        if (!UISettings.isBlockUser())
            blockUserLayout.setVisibility(View.GONE);

        tvBlockUser.setOnClickListener(view -> {
            if (isBlocked)
               unblockUser();
            else
                blockUser();
        });


        if (UISettings.isEnableVideoCalling())
            videoCallBtn.setVisibility(View.VISIBLE);
        else
            videoCallBtn.setVisibility(View.GONE);

        if (UISettings.isEnableVoiceCalling())
            callBtn.setVisibility(View.VISIBLE);
        else
            callBtn.setVisibility(View.GONE);

        if (UISettings.getColor()!=null) {
            getWindow().setStatusBarColor(Color.parseColor(UISettings.getColor()));
            callBtn.setImageTintList(ColorStateList.valueOf(
                    Color.parseColor(UISettings.getColor())));
            videoCallBtn.setImageTintList(ColorStateList.valueOf(
                    Color.parseColor(UISettings.getColor())));
        }

        callBtn.setOnClickListener(view -> {
            callBtn.setClickable(false);
            checkOnGoingCall(CometChatConstants.CALL_TYPE_AUDIO);
        });
        videoCallBtn.setOnClickListener(view ->{
            videoCallBtn.setClickable(false);
            checkOnGoingCall(CometChatConstants.CALL_TYPE_VIDEO);
        });
    }

    private void checkDarkMode() {
        if (Utils.isDarkMode(this)) {
            userName.setTextColor(getResources().getColor(R.color.textColorWhiteuikit));
            divider1.setBackgroundColor(getResources().getColor(R.color.greyUikit));
            divider2.setBackgroundColor(getResources().getColor(R.color.greyUikit));
            divider3.setBackgroundColor(getResources().getColor(R.color.greyUikit));
        } else {
            userName.setTextColor(getResources().getColor(R.color.primaryTextColoruikit));
            divider1.setBackgroundColor(getResources().getColor(R.color.light_greyuikit));
            divider2.setBackgroundColor(getResources().getColor(R.color.light_greyuikit));
            divider3.setBackgroundColor(getResources().getColor(R.color.light_greyuikit));
        }
    }

    private void checkOnGoingCall(String callType) {
        if(CometChat.getActiveCall()!=null && CometChat.getActiveCall().getCallStatus().equals(CometChatConstants.CALL_STATUS_ONGOING) && CometChat.getActiveCall().getSessionId()!=null) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(getResources().getString(R.string.ongoing_call))
                    .setMessage(getResources().getString(R.string.ongoing_call_message))
                    .setPositiveButton(getResources().getString(R.string.join), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            CallUtils.joinOnGoingCall(CometChatUserDetailScreenActivity.this,CometChat.getActiveCall());
                        }
                    }).setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    callBtn.setClickable(true);
                    videoCallBtn.setClickable(true);
                }
            }).create().show();
        }
        else {
//            CallUtils.initiateCall(CometChatUserDetailScreenActivity.this,uid,CometChatConstants.RECEIVER_TYPE_USER,callType);
            Call call = new Call(uid, CometChatConstants.RECEIVER_TYPE_USER,callType);
            CometChat.initiateCall(call, new CometChat.CallbackListener<Call>() {
                @Override
                public void onSuccess(Call call) {
                    CallUtils.startCallIntent(CometChatUserDetailScreenActivity.this,
                            ((User)call.getCallReceiver()),call.getType(),true,
                            call.getSessionId());
                }

                @Override
                public void onError(CometChatException e) {
                    callBtn.setClickable(true);
                    videoCallBtn.setClickable(true);
                    Log.e(TAG, "onError: "+e.getMessage());
                    Snackbar.make(getWindow().getDecorView().getRootView(),
                            getResources().getString(R.string.call_initiate_error)+":"+e.getMessage(),Snackbar.LENGTH_LONG).show();
                }
            });

        }
    }


    private void handleIntent() {

        if (getIntent().hasExtra(UIKitConstants.IntentStrings.IS_ADD_MEMBER)) {
            isAddMember = getIntent().getBooleanExtra(UIKitConstants.IntentStrings.IS_ADD_MEMBER, false);
        }

        if (getIntent().hasExtra(UIKitConstants.IntentStrings.FROM_CALL_LIST)) {
            fromCallList = getIntent().getBooleanExtra(UIKitConstants.IntentStrings.FROM_CALL_LIST,false);
        }

        if (getIntent().hasExtra(UIKitConstants.IntentStrings.IS_BLOCKED_BY_ME)){
            isBlocked=getIntent().getBooleanExtra(UIKitConstants.IntentStrings.IS_BLOCKED_BY_ME,false);
             setBlockUnblock();
        }

        if (getIntent().hasExtra(UIKitConstants.IntentStrings.GUID)) {
            guid = getIntent().getStringExtra(UIKitConstants.IntentStrings.GUID);
        }

        if (getIntent().hasExtra(UIKitConstants.IntentStrings.UID)) {
            uid = getIntent().getStringExtra(UIKitConstants.IntentStrings.UID);
        }
        if (getIntent().hasExtra(UIKitConstants.IntentStrings.GROUP_NAME)) {
            groupName = getIntent().getStringExtra(UIKitConstants.IntentStrings.GROUP_NAME);
        }
        if (getIntent().hasExtra(UIKitConstants.IntentStrings.NAME)) {
            name = getIntent().getStringExtra(UIKitConstants.IntentStrings.NAME);
            userName.setText(name);
        }

        if (getIntent().hasExtra(UIKitConstants.IntentStrings.AVATAR)) {
            avatar = getIntent().getStringExtra(UIKitConstants.IntentStrings.AVATAR);
        }
        if (getIntent().hasExtra(UIKitConstants.IntentStrings.STATUS)) {
            String status = getIntent().getStringExtra(UIKitConstants.IntentStrings.STATUS);

            if (status != null && status.equals(CometChatConstants.USER_STATUS_ONLINE))
                userStatus.setTextColor(getResources().getColor(R.color.colorPrimaryuikit));

            userStatus.setText(status);
        }

        if (avatar != null && !avatar.isEmpty())
            userAvatar.setAvatar(avatar);
        else {
            if (name != null && !name.isEmpty())
                userAvatar.setInitials(name);
            else
                userAvatar.setInitials("Unknown");
        }

        if (isAddMember) {
            addBtn.setText(String.format(getResources().getString(R.string.add_user_to_group),name,groupName));
            historyView.setVisibility(View.GONE);
        } else {
            fetchCallHistory();
            addBtn.setVisibility(View.GONE);
        }
    }

    private void fetchCallHistory() {
        if (messageRequest==null)
        {
            messageRequest = new MessagesRequest.MessagesRequestBuilder().setUID(uid)
                    .setCategories(Arrays.asList(CometChatConstants.CATEGORY_CALL))
                    .setLimit(30).build();
        }
        messageRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> messageList) {
                if (messageList.size()!=0) {
                    callList.addAll(messageList);
                    setCallHistoryAdapter(messageList);
                }
                if (callList.size()!=0)
                    historyView.setVisibility(View.VISIBLE);
                else
                    historyView.setVisibility(View.GONE);
            }

            @Override
            public void onError(CometChatException e) {

            }
        });
    }

    private void setCallHistoryAdapter(List<BaseMessage> messageList) {
        if (callHistoryAdapter==null)
        {
            callHistoryAdapter = new CallHistoryAdapter(CometChatUserDetailScreenActivity.this,messageList);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,true);
            historyRv.setLayoutManager(linearLayoutManager);
            historyRv.setAdapter(callHistoryAdapter);
        }
        else
            callHistoryAdapter.updateList(messageList);
    }

    private void setBlockUnblock() {
        if (isBlocked) {
            tvBlockUser.setTextColor(getResources().getColor(R.color.online_greenuikit));
            tvBlockUser.setText(getResources().getString(R.string.unblock_user));
        } else{
            tvBlockUser.setText(getResources().getString(R.string.block_user));
            tvBlockUser.setTextColor(getResources().getColor(R.color.redUikit));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

         if (item.getItemId()==android.R.id.home){
             onBackPressed();
         }
        return super.onOptionsItemSelected(item);
    }

    private void setLoaderVisibility(boolean bool){
        if (bool) {
            progressbarBG.setVisibility(View.VISIBLE);
            progressbarBG.setOnClickListener(null);
        } else {
            progressbarBG.setVisibility(View.GONE);
            progressbarBG.setOnClickListener(null);
        }
    }

    private void addMember() {
        List<GroupMember> userList = new ArrayList<>();
        userList.add(new GroupMember(uid, CometChatConstants.SCOPE_PARTICIPANT));
        setLoaderVisibility(true);
        CometChat.addMembersToGroup(guid, userList, null, new CometChat.CallbackListener<HashMap<String, String>>() {
            @Override
            public void onSuccess(HashMap<String, String> stringStringHashMap) {
                Log.e(TAG, "onSuccess: " + uid + "Group" + guid);
                setLoaderVisibility(false);
                if(tvBlockUser!=null)
                    Utils.showCometChatDialog(CometChatUserDetailScreenActivity.this,
                            tvBlockUser,String.format(getResources().getString(R.string.user_added_to_group),userName.getText().toString(), groupName),
                            false);
                addBtn.setText(String.format(getResources().getString(R.string.remove_from_group),groupName));
                isAlreadyAdded = true;
            }

            @Override
            public void onError(CometChatException e) {
                Utils.showCometChatDialog(CometChatUserDetailScreenActivity.this,
                        historyRv,e.getMessage(),true);
                setLoaderVisibility(false);
            }
        });
    }

    private void kickGroupMember() {

        setLoaderVisibility(true);
        CometChat.kickGroupMember(uid, guid, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                setLoaderVisibility(false);
                if (tvBlockUser!=null)
                    Utils.showCometChatDialog(CometChatUserDetailScreenActivity.this,
                            tvBlockUser,
                            String.format(getResources().getString(R.string.user_removed_from_group),userName.getText().toString(),groupName),
                            false);
                addBtn.setText(String.format(getResources().getString(R.string.add_in),groupName));
                addBtn.setVisibility(View.VISIBLE);
                isAlreadyAdded = false;
            }

            @Override
            public void onError(CometChatException e) {
                setLoaderVisibility(false);
                if (tvBlockUser!=null)
                    Utils.showCometChatDialog(CometChatUserDetailScreenActivity.this,
                            tvBlockUser, getResources().getString(R.string.kicked_error),
                            true);
            }
        });
    }


    private void unblockUser() {
        ArrayList<String> uids = new ArrayList<>();
        uids.add(uid);

      CometChat.unblockUsers(uids, new CometChat.CallbackListener<HashMap<String, String>>() {
          @Override
          public void onSuccess(HashMap<String, String> stringStringHashMap) {
              if (tvBlockUser!=null)
                  Utils.showCometChatDialog(CometChatUserDetailScreenActivity.this,
                          tvBlockUser,
                          userName.getText().toString()+" "+getResources().getString(R.string.unblocked_successfully),false);
              isBlocked=false;
              setBlockUnblock();
          }

          @Override
          public void onError(CometChatException e) {
              Log.d(TAG, "onError: "+e.getMessage());
              if (tvBlockUser!=null)
                  Utils.showCometChatDialog(CometChatUserDetailScreenActivity.this,
                          tvBlockUser,getResources().getString(R.string.unblock_user_error),true);
          }
      });
    }


    private void blockUser() {

        ArrayList<String> uids = new ArrayList<>();
        uids.add(uid);
        CometChat.blockUsers(uids, new CometChat.CallbackListener<HashMap<String, String>>() {
            @Override
            public void onSuccess(HashMap<String, String> stringStringHashMap) {
                if (tvBlockUser!=null)
                    Utils.showCometChatDialog(CometChatUserDetailScreenActivity.this,
                            tvBlockUser,
                            String.format(getResources().getString(R.string.user_is_blocked),userName.getText().toString()),
                            false);
                isBlocked=true;
                setBlockUnblock();
            }

            @Override
            public void onError(CometChatException e) {
                if (tvBlockUser!=null)
                    Utils.showCometChatDialog(CometChatUserDetailScreenActivity.this,
                            tvBlockUser,String.format(getResources().getString(R.string.block_user_error),userName.getText().toString()),
                            true);
                Log.d(TAG, "onError: "+e.getMessage());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        groupListener();
        callBtn.setClickable(true);
        videoCallBtn.setClickable(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        CometChat.removeGroupListener(TAG);
    }

    private void groupListener()
    {
        CometChat.addGroupListener(TAG, new CometChat.GroupListener() {
            @Override
            public void onGroupMemberJoined(Action action, User joinedUser, Group joinedGroup) {
                updateBtn(joinedUser,R.string.remove_from_group);
            }

            @Override
            public void onGroupMemberLeft(Action action, User leftUser, Group leftGroup) {
                updateBtn(leftUser,R.string.add_in);
            }

            @Override
            public void onGroupMemberKicked(Action action, User kickedUser, User kickedBy, Group kickedFrom) {
                updateBtn(kickedUser,R.string.add_in);
            }

            @Override
            public void onMemberAddedToGroup(Action action, User addedby, User userAdded, Group addedTo) {
                updateBtn(userAdded,R.string.remove_from_group);
            }
        });
    }

    private void updateBtn(User user, int resource_string) {
        if (user.getUid().equals(uid))
            addBtn.setText(String.format(getResources().getString(resource_string), groupName ));
    }
}
