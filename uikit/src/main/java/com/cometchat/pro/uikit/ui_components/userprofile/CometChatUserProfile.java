package com.cometchat.pro.uikit.ui_components.userprofile;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.cometchat.pro.uikit.R;
import com.cometchat.pro.uikit.databinding.FragmentCometchatUserProfileBinding;
import com.cometchat.pro.uikit.ui_components.shared.cometchatAvatar.CometChatAvatar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import com.cometchat.pro.uikit.ui_resources.constants.UIKitConstants;
import com.cometchat.pro.uikit.ui_resources.utils.FontUtils;
import com.cometchat.pro.uikit.ui_settings.UISettings;
import com.cometchat.pro.uikit.ui_resources.utils.Utils;
import com.cometchat.pro.uikit.ui_components.userprofile.privacy_security.CometChatMorePrivacyActivity;

public class CometChatUserProfile extends Fragment {

    private CometChatAvatar notificationIv;
    private AlertDialog.Builder dialog;
    FragmentCometchatUserProfileBinding moreInfoScreenBinding;
    private static final String TAG = "Action Logout";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        moreInfoScreenBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_cometchat_user_profile, container, false);
        moreInfoScreenBinding.setUser(CometChat.getLoggedInUser());
        moreInfoScreenBinding.ivUser.setAvatar(CometChat.getLoggedInUser());

//        moreInfoScreenBinding.mbtnLogout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                CometChat.logout(new CometChat.CallbackListener<String>() {
//                    @SuppressLint("WrongConstant")
//                    @Override
//                    public void onSuccess(String successMessage) {
//                        Log.d(TAG, "Logout completed successfully");
//                        Toast.makeText(getContext(), "Logout Cometchat", Toast.LENGTH_SHORT).show();
//
//                        try {
//                            // to clear the activity stack
//                            Intent myIntent = new Intent(getActivity(),Class.forName("com.example.arteria.LoginActivity2"));
//                            myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
//                                myIntent.addFlags(0x8000); // equal to Intent.FLAG_ACTIVITY_CLEAR_TASK which is only available from API level 11
//                            startActivity(myIntent);
//                        } catch (ClassNotFoundException e) {
//                            e.printStackTrace();
//                        }
//                        getActivity().finish();
//                    }
//                    @Override
//                    public void onError(CometChatException e) {
//                        Log.d(TAG, "Logout failed with exception: " + e.getMessage());
//                    }
//                });
//            }
//        });

        moreInfoScreenBinding.tvTitle.setTypeface(FontUtils.getInstance(getActivity()).getTypeFace(FontUtils.robotoMedium));
        Log.e("onCreateView: ", CometChat.getLoggedInUser().toString());
        moreInfoScreenBinding.privacyAndSecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), CometChatMorePrivacyActivity.class));
            }
        });

        if (UISettings.getColor()!=null) {
            int widgetColor = Color.parseColor(UISettings.getColor());
            Drawable wrappedDrawable = DrawableCompat.wrap(getResources().
                    getDrawable(R.drawable.ic_security_24dp));
            wrappedDrawable.setTint(widgetColor);
            DrawableCompat.setTint(wrappedDrawable, widgetColor);
            moreInfoScreenBinding.ivSecurity.setImageDrawable(wrappedDrawable);
        }
        if(Utils.isDarkMode(getContext())) {
            moreInfoScreenBinding.tvTitle.setTextColor(getResources().getColor(R.color.textColorWhiteuikit));
            moreInfoScreenBinding.tvSeperator.setBackgroundColor(getResources().getColor(R.color.greyUikit));
            moreInfoScreenBinding.tvSeperator1.setBackgroundColor(getResources().getColor(R.color.greyUikit));
        } else {
            moreInfoScreenBinding.tvTitle.setTextColor(getResources().getColor(R.color.primaryTextColoruikit));
            moreInfoScreenBinding.tvSeperator.setBackgroundColor(getResources().getColor(R.color.light_greyuikit));
            moreInfoScreenBinding.tvSeperator1.setBackgroundColor(getResources().getColor(R.color.light_greyuikit));
        }

        // not required for client
//        moreInfoScreenBinding.userContainer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                updateUserDialog();
//            }
//        });
        return moreInfoScreenBinding.getRoot();
    }

    private void updateUserDialog() {
        dialog = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.cometchat_update_user_dialog,null);
        CometChatAvatar avatar = view.findViewById(R.id.user_avatar);
        avatar.setAvatar(CometChat.getLoggedInUser());
        TextInputEditText avatar_url = view.findViewById(R.id.avatar_url_edt);
        avatar_url.setText(CometChat.getLoggedInUser().getAvatar());
        TextInputEditText username = view.findViewById(R.id.username_edt);
        username.setText(CometChat.getLoggedInUser().getName());
        MaterialButton updateUserBtn = view.findViewById(R.id.updateUserBtn);
        MaterialButton cancelBtn = view.findViewById(R.id.cancelBtn);

        if(CometChat.getLoggedInUser().getAvatar()==null) {
            avatar.setVisibility(View.GONE);
            avatar_url.setVisibility(View.GONE);
        }
        else {
            avatar.setVisibility(View.VISIBLE);
            avatar_url.setVisibility(View.GONE);
        }
        avatar_url.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().isEmpty())
                {
                    avatar.setVisibility(View.VISIBLE);
                    Glide.with(getContext()).load(s.toString()).into(avatar);
                } else
                    avatar.setVisibility(View.GONE);
            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.setView(view);
        updateUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User();
                if (username.getText().toString().isEmpty())
                    username.setError(getString(R.string.fill_this_field));
                else {
                    user.setName(username.getText().toString());
                    user.setUid(CometChat.getLoggedInUser().getUid());
                    user.setAvatar(avatar_url.getText().toString());
                    updateUser(user);
                    alertDialog.dismiss();
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void updateUser(User user) {
        CometChat.updateUser(user, UIKitConstants.AppInfo.AUTH_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                if (getContext()!=null)
                    Toast.makeText(getContext(),"Updated User Successfull",Toast.LENGTH_LONG).show();
                moreInfoScreenBinding.setUser(user);
            }

            @Override
            public void onError(CometChatException e) {
                if (getContext()!=null)
                    Utils.showCometChatDialog(getContext(),moreInfoScreenBinding.tvTitle,e.getMessage(),true);
            }
        });
    }
}
