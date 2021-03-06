package com.glorystudent.golflife.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.glorystudent.golflibrary.base.BaseActivity;
import com.glorystudent.golflibrary.util.PhoneFormatCheckUtils;
import com.glorystudent.golflife.R;
import com.glorystudent.golflife.api.ConstantsURL;
import com.glorystudent.golflife.api.OkGoRequest;
import com.glorystudent.golflife.api.RequestAPI;
import com.glorystudent.golflife.entity.FriendsProfileEntity;
import com.glorystudent.golflife.util.Constants;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.OnClick;

/**
 *TODO 手机号搜索
 */
public class PhoneContactActivity extends BaseActivity implements TextWatcher {


    private static final String TAG = "PhoneContactActivity";
    @Bind(R.id.search_phone_number)
    public EditText search_phone_number;

    @Bind(R.id.tv_phone_number)
    public TextView tv_phone_number;

    @Bind(R.id.rl_search_result)
    public RelativeLayout rl_search_result;
    private String phoneNumber;
    @Override
    protected int getContentId() {
        return R.layout.activity_phone_contact;
    }
    @Override
    protected void init() {
        search_phone_number.addTextChangedListener(this);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(search_phone_number, 0);
                }
            }
        }, 200);
    }

    /**
     * TODO 输入框变化监听
     * @param s
     * @param start
     * @param count
     * @param after
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String phoneStr = s.toString().trim();
        if (phoneStr != null && !phoneStr.isEmpty()) {
            rl_search_result.setVisibility(View.VISIBLE);
            tv_phone_number.setText(phoneStr);
        } else {
            rl_search_result.setVisibility(View.GONE);
            tv_phone_number.setText("");
        }
    }

    /**
     * TODO 点击事件监听
     * @param view
     */
    @OnClick({R.id.back, R.id.search, R.id.rl_search_result})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.back:
                //返回
                finish();
                break;
            case R.id.search:
                //搜索
            case R.id.rl_search_result:
                //搜索
                phoneNumber = search_phone_number.getText().toString();
                if (!phoneNumber.isEmpty()) {
                    if (PhoneFormatCheckUtils.isChinaPhoneLegal(phoneNumber)) {
                        getGolfFriends();
                    } else {
                        Toast.makeText(PhoneContactActivity.this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PhoneContactActivity.this, "手机号码不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * TODO 查询好友
     */
    private void getGolfFriends() {
        showLoading();
        String requestJson = RequestAPI.QueryFriend(this,phoneNumber);
        System.out.println("手机号查询好友参数："+requestJson);
        OkGoRequest.getOkGoRequest().setOnOkGoUtilListener(new OkGoRequest.OnOkGoUtilListener() {
            @Override
            public void parseDatas(String json) {
                dismissLoading();
                System.out.println("手机号查询好友："+json);
                try {
                    JSONObject jo = new JSONObject(json);
                    String statuscode = jo.getString("statuscode");
                    String statusmessage = jo.getString("statusmessage");
                    if (statuscode.equals("1")) {
                        FriendsProfileEntity friendsProfileEntity = new Gson().fromJson(jo.toString(), FriendsProfileEntity.class);
                        FriendsProfileEntity.UserBean user = friendsProfileEntity.getUser();
                        if (user != null) {
                            Map<String, FriendsProfileEntity.UserBean> map = new HashMap<>();
                            map.put("FriendProfileActivity", user);
                            EventBus.getDefault().postSticky(map);
                            Intent intent = new Intent(PhoneContactActivity.this, FriendProfileActivity.class);
                            intent.putExtra("applyType", "1");//1电话簿查找
                            startActivity(intent);
                        } else {
                            Toast.makeText(PhoneContactActivity.this, "搜索不到此用户", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(PhoneContactActivity.this, "搜索不到此用户", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestFailed() {
                dismissLoading();
                Toast.makeText(PhoneContactActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
            }
        }).getEntityData(this,ConstantsURL.QueryFriend, requestJson);
    }
}
