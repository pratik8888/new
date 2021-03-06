package com.cometchat.pro.uikit.ui_components.groups.create_group;

import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.uikit.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.cometchat.pro.uikit.ui_components.messages.message_list.CometChatMessageListActivity;
import com.cometchat.pro.uikit.ui_resources.constants.UIKitConstants;
import com.cometchat.pro.uikit.ui_resources.utils.Utils;

import static com.cometchat.pro.uikit.ui_resources.utils.Utils.generateRandomString;

/**
 * Purpose - CometChatCreateGroup class is a fragment used to create a group. User just need to enter
 * group name. All other information like guid, groupIcon are set by this class.
 *
 * @see CometChat#createGroup(Group, CometChat.CallbackListener)
 *
 *
 */


public class CometChatCreateGroup extends Fragment {

    private TextInputEditText etGroupName,etGroupDesc,etGroupPassword,etGroupCnfPassword;

    private TextView des1;

    private TextView des2;

    private TextInputLayout groupNameLayout,groupDescLayout,groupPasswordLayout,groupCnfPasswordLayout;

    private MaterialButton createGroupBtn;

    private Spinner groupTypeSpinner;

    private String groupType;

    String TAG = "CometChatCreateGroup";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_cometchat_create_group, container, false);

        etGroupName = view.findViewById(R.id.group_name);
        etGroupDesc = view.findViewById(R.id.group_desc);
        etGroupPassword = view.findViewById(R.id.group_pwd);
        etGroupCnfPassword = view.findViewById(R.id.group_cnf_pwd);
        etGroupCnfPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!etGroupPassword.getText().toString().isEmpty() && s.toString().equals(etGroupPassword.getText().toString())) {
                    groupCnfPasswordLayout.setEndIconDrawable(getResources().getDrawable(R.drawable.ic_check_black_24dp));
                    groupCnfPasswordLayout.setEndIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.green_600)));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        des1 = view.findViewById(R.id.tvDes1);

        des2 = view.findViewById(R.id.tvDes2);

        groupNameLayout = view.findViewById(R.id.input_group_name);
        groupDescLayout = view.findViewById(R.id.input_group_desc);
        groupPasswordLayout = view.findViewById(R.id.input_group_pwd);
        groupCnfPasswordLayout = view.findViewById(R.id.input_group_cnf_pwd);
        groupTypeSpinner = view.findViewById(R.id.grouptype_spinner);
        groupTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position==0) {
                    groupType = CometChatConstants.GROUP_TYPE_PUBLIC;
                    groupPasswordLayout.setVisibility(View.GONE);
                    groupCnfPasswordLayout.setVisibility(View.GONE);
                } else if (position==1) {
                    groupType = CometChatConstants.GROUP_TYPE_PRIVATE;
                    groupPasswordLayout.setVisibility(View.GONE);
                    groupCnfPasswordLayout.setVisibility(View.GONE);
                } else if (position==2) {
                    groupType = CometChatConstants.GROUP_TYPE_PASSWORD;
                    groupPasswordLayout.setVisibility(View.VISIBLE);
                    groupCnfPasswordLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        createGroupBtn = view.findViewById(R.id.btn_create_group);

        createGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGroup();
            }
        });
        checkDarkMode();
        return view;
    }

    private void checkDarkMode() {
        if (Utils.isDarkMode(getContext())) {
            des1.setTextColor(getResources().getColor(R.color.textColorWhiteuikit));
            des2.setTextColor(getResources().getColor(R.color.textColorWhiteuikit));
            groupNameLayout.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.textColorWhiteuikit)));
            groupNameLayout.setBoxStrokeColor(getResources().getColor(R.color.textColorWhiteuikit));
            groupNameLayout.setDefaultHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.textColorWhiteuikit)));
            etGroupName.setTextColor(getResources().getColor(R.color.textColorWhiteuikit));

            groupDescLayout.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.textColorWhiteuikit)));
            groupDescLayout.setBoxStrokeColor(getResources().getColor(R.color.textColorWhiteuikit));
            groupDescLayout.setDefaultHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.textColorWhiteuikit)));
            etGroupDesc.setTextColor(getResources().getColor(R.color.textColorWhiteuikit));

            groupPasswordLayout.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.textColorWhiteuikit)));
            groupPasswordLayout.setBoxStrokeColor(getResources().getColor(R.color.textColorWhiteuikit));
            groupPasswordLayout.setDefaultHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.textColorWhiteuikit)));
            etGroupPassword.setTextColor(getResources().getColor(R.color.textColorWhiteuikit));

            groupCnfPasswordLayout.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.textColorWhiteuikit)));
            groupCnfPasswordLayout.setBoxStrokeColor(getResources().getColor(R.color.textColorWhiteuikit));
            groupCnfPasswordLayout.setDefaultHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.textColorWhiteuikit)));
            etGroupCnfPassword.setTextColor(getResources().getColor(R.color.textColorWhiteuikit));

        } else {
            des1.setTextColor(getResources().getColor(R.color.primaryTextColoruikit));
            des2.setTextColor(getResources().getColor(R.color.primaryTextColoruikit));
            groupNameLayout.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.primaryTextColoruikit)));
            groupNameLayout.setBoxStrokeColor(getResources().getColor(R.color.primaryTextColoruikit));
            etGroupName.setTextColor(getResources().getColor(R.color.primaryTextColoruikit));

            groupDescLayout.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.primaryTextColoruikit)));
            groupDescLayout.setBoxStrokeColor(getResources().getColor(R.color.primaryTextColoruikit));
            etGroupDesc.setTextColor(getResources().getColor(R.color.primaryTextColoruikit));

            groupPasswordLayout.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.primaryTextColoruikit)));
            groupPasswordLayout.setBoxStrokeColor(getResources().getColor(R.color.primaryTextColoruikit));
            etGroupPassword.setTextColor(getResources().getColor(R.color.primaryTextColoruikit));

            groupCnfPasswordLayout.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.primaryTextColoruikit)));
            groupCnfPasswordLayout.setBoxStrokeColor(getResources().getColor(R.color.primaryTextColoruikit));
            etGroupCnfPassword.setTextColor(getResources().getColor(R.color.primaryTextColoruikit));
        }
    }
    private void createGroup() {
        if (!etGroupName.getText().toString().isEmpty()) {
            if(groupType.equals(CometChatConstants.GROUP_TYPE_PUBLIC) || groupType.equals(CometChatConstants.GROUP_TYPE_PRIVATE)) {
                Group group = new Group(generateRandomString(25), etGroupName.getText().toString(), groupType, "");
                createGroup(group);
            }
            else if (groupType.equals(CometChatConstants.GROUP_TYPE_PASSWORD)) {
                if(etGroupPassword.getText().toString().isEmpty())
                    etGroupPassword.setError(getResources().getString(R.string.fill_this_field));
                else if (etGroupCnfPassword.getText().toString().isEmpty())
                    etGroupCnfPassword.setError(getResources().getString(R.string.fill_this_field));
                else if(etGroupPassword.getText().toString().equals(etGroupCnfPassword.getText().toString())) {
                    Group group = new Group(generateRandomString(25), etGroupName.getText().toString(), groupType, etGroupPassword.getText().toString());
                    createGroup(group);
                }
                else
                    if (etGroupPassword!=null)
                        Utils.showCometChatDialog(getContext(),etGroupCnfPassword.getRootView(),
                                getResources().getString(R.string.password_not_matched),true);
            }
        }
        else {
            etGroupName.setError(getResources().getString(R.string.fill_this_field));
        }
    }

    private void createGroup(Group group) {
        createGroupBtn.setClickable(false);
        CometChat.createGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group group) {
                Intent intent = new Intent(getActivity(), CometChatMessageListActivity.class);
                intent.putExtra(UIKitConstants.IntentStrings.NAME,group.getName());
                intent.putExtra(UIKitConstants.IntentStrings.GROUP_OWNER,group.getOwner());
                intent.putExtra(UIKitConstants.IntentStrings.GUID,group.getGuid());
                intent.putExtra(UIKitConstants.IntentStrings.AVATAR,group.getIcon());
                intent.putExtra(UIKitConstants.IntentStrings.GROUP_TYPE,group.getGroupType());
                intent.putExtra(UIKitConstants.IntentStrings.TYPE, CometChatConstants.RECEIVER_TYPE_GROUP);
                intent.putExtra(UIKitConstants.IntentStrings.MEMBER_COUNT,group.getMembersCount());
                intent.putExtra(UIKitConstants.IntentStrings.GROUP_DESC,group.getDescription());
                intent.putExtra(UIKitConstants.IntentStrings.GROUP_PASSWORD,group.getPassword());
                if (getActivity()!=null)
                    getActivity().finish();

                createGroupBtn.setClickable(true);
                startActivity(intent);
            }

            @Override
            public void onError(CometChatException e) {
                Utils.showCometChatDialog(getContext(),etGroupName.getRootView(),getResources().getString(R.string.create_group_error),true);
                Log.e(TAG, "onError: "+e.getMessage() );
            }
        });
    }
}
