package com.glorystudent.golflife.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.glorystudent.golflibrary.base.BaseActivity;
import com.glorystudent.golflibrary.dialog.iosdialog.AlertDialog;
import com.glorystudent.golflibrary.widget.oywidget.MyRecycleView;
import com.glorystudent.golflife.R;
import com.glorystudent.golflife.adapter.TeamMemberRecylerAdapter;
import com.glorystudent.golflife.api.ConstantsURL;
import com.glorystudent.golflife.api.OkGoRequest;
import com.glorystudent.golflife.api.RequestAPI;
import com.glorystudent.golflife.customView.PullToRefreshLayout;
import com.glorystudent.golflife.customView.TeamMemberDecoration;
import com.glorystudent.golflife.entity.FriendsProfileEntity;
import com.glorystudent.golflife.entity.LetterEntity;
import com.glorystudent.golflife.entity.TeamMemberEntity;
import com.glorystudent.golflife.util.PinyinComparator;
import com.glorystudent.golflife.util.PinyinUtil;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 *  TODO 球队队员页面
 */
public class TeamMemberActivity extends BaseActivity implements TeamMemberRecylerAdapter.OnRecyclerItemClickListener {

    private static final String TAG = "TeamMemberActivity";
    @Bind(R.id.back)
    ImageView back;
    @Bind(R.id.refresh_view)
    PullToRefreshLayout refreshView;
    @Bind(R.id.search)
    EditText search;
    @Bind(R.id.recycler_view)
    MyRecycleView recyclerView;
    private boolean isRefresh = true;//默认刷新
    private int page = 1;
    private List<LetterEntity> letterList;//字母索引集合
    private int teamId;
    private int captainId;
    private TeamMemberRecylerAdapter teamMemberRecylerAdapter;//球队成员适配器
    private List<TeamMemberEntity.TeamUserListBean> datas;//排序后队长在首位的数据源
    private TeamMemberEntity.TeamUserListBean captainBean;//队长的数据对象
    private boolean isTransfer;
    private List<TeamMemberEntity.TeamUserListBean> teamUserList;//球队成员数据集合
    @Override
    protected int getContentId() {
        return R.layout.activity_team_member;
    }
    @Override
    protected void init() {
        refreshView.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                //下拉刷新回调
                isRefresh = true;
                page = 1;
                loadDatas();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                //上拉加载回调
                isRefresh = false;
                page++;
                loadDatas();
            }
        });
        Intent intent = getIntent();
        teamId = intent.getIntExtra("teamId", -1);
        captainId = intent.getIntExtra("captainId", -1);
        //是否是转让队长
        isTransfer = intent.getBooleanExtra("isTransfer", false);

        showLoading();
        datas = new ArrayList<>();
        teamMemberRecylerAdapter = new TeamMemberRecylerAdapter(this);
        teamMemberRecylerAdapter.setOnRecyclerItemClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new TeamMemberDecoration(letterList, this, new TeamMemberDecoration.DecorationCallback() {
            @Override
            public String getGroupId(int position) {
                return letterList.get(position).getName();
            }
        }));
        recyclerView.setAdapter(teamMemberRecylerAdapter);
    }

    /**
     * TODO 获取球队成员数据
     */
    @Override
    protected void loadDatas() {
        String requestJson = RequestAPI.QueryTeamUser(this,teamId+"",page+"");
        Log.i(TAG, "loadDatas: " + requestJson);
        OkGoRequest.getOkGoRequest().setOnOkGoUtilListener(new OkGoRequest.OnOkGoUtilListener() {
            @Override
            public void parseDatas(String json) {
                try {
                    JSONObject jo = new JSONObject(json);
                    String statuscode = jo.getString("statuscode");
                    String statusmessage = jo.getString("statusmessage");
                    if (statuscode.equals("1")) {
                        refreshView.setRefreshState(isRefresh, PullToRefreshLayout.SUCCEED);
                        TeamMemberEntity teamMemberEntity = new Gson().fromJson(jo.toString(), TeamMemberEntity.class);
                        if (teamMemberEntity != null) {
                            teamUserList = teamMemberEntity.getTeamUserList();
                            if (teamUserList != null && teamUserList.size() > 0) {
                                addDecoration(teamUserList);
                            }
                        }
                    } else if (statuscode.equals("2")) {
                        refreshView.setRefreshState(isRefresh, PullToRefreshLayout.SUCCEED);
                        Log.i(TAG, "parseDatas: " + statusmessage);
                    } else {
                        refreshView.setRefreshState(isRefresh, PullToRefreshLayout.FAIL);
                        Log.i(TAG, "parseDatas: " + statusmessage);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dismissLoading();
            }

            @Override
            public void requestFailed() {
                dismissLoading();
                Toast.makeText(TeamMemberActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
                refreshView.setRefreshState(isRefresh, PullToRefreshLayout.FAIL);
                //访问失败则当前页需要重新加载
                page--;
            }
        }).getEntityData(this, ConstantsURL.QueryTeamUser, requestJson);
    }

    /**
     * TODO 点击事件监听
     * @param view
     */
    @OnClick({R.id.back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }
    /**
     * TODO 球队成员recyclerview的点击事件
     * @param position
     */
    @Override
    public void onItemClick(final int position) {
        if (isTransfer) {//转让队长时点击队员返回队员id
            new AlertDialog(this)
                    .builder()
                    .setTitle("确认转让队长")
                    .setCancelable(true)
                    .setNegativeButton("取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    })
                    .setPositiveButton("确认", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int userid = datas.get(position).getUserid();
                            Intent intent = new Intent();
                            intent.putExtra("userid", userid);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }).show();
        } else {
            if (datas != null) {
                String telphone = datas.get(position).getTelphone();
                requestFriendDetail(telphone);
            }
        }
    }
    /**
     *TODO  给recycleview增加字母索引
     */
    private void addDecoration(List<TeamMemberEntity.TeamUserListBean> teamUserList) {
        if (isRefresh) {//刷新
            handleData(teamUserList);
        } else {//加载更多
            datas.addAll(teamUserList);
            List<TeamMemberEntity.TeamUserListBean> tempList = new ArrayList<>();
            tempList.addAll(datas);
            handleData(tempList);
        }
        teamMemberRecylerAdapter.setDatas(datas);
    }
    /**
     * TODO 处理数据为队长在首位，队员已排好序
     *
     * @param list 需要处理的数据源
     */
    private void handleData(List<TeamMemberEntity.TeamUserListBean> list) {
        datas.clear();
        //判断队长 单独提出队长对象，对所有队员进行排序后再把队长对象插在首位
        for (TeamMemberEntity.TeamUserListBean teamUserListBean : list) {
            if (teamUserListBean.getUserid() == captainId) {
                captainBean = teamUserListBean;
            } else {
                datas.add(teamUserListBean);
            }
        }
        //排序
        Collections.sort(datas, new PinyinComparator());
        //设置首字母索引
        setIndexAction(datas);
        //将队长插在首位
        if (!isTransfer) {//转让队长时不显示队长
            LetterEntity duiZhang = new LetterEntity();
            duiZhang.setName("队长");
            letterList.add(0, duiZhang);
            datas.add(0, captainBean);
        }
    }

    /**
     * TODO 设置字母索引
     * @param list
     */
    public void setIndexAction(List<TeamMemberEntity.TeamUserListBean> list) {
        letterList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            LetterEntity letterEntity = new LetterEntity();
            String letter = PinyinUtil.getPinYinHeadChar(list.get(i).getName());
            letterEntity.setName(letter);
            letterList.add(letterEntity);
        }
    }
    /**
     * TODO 根据队员手机号搜索此人
     * @param phoneNumber
     */
    private void requestFriendDetail(String phoneNumber) {
        showLoading();
        String requestJson = RequestAPI.QueryFriend(this, phoneNumber);
        Log.i(TAG, "getGolfFriends: " + requestJson);
        OkGoRequest.getOkGoRequest().setOnOkGoUtilListener(new OkGoRequest.OnOkGoUtilListener() {
            @Override
            public void parseDatas(String json) {
                dismissLoading();
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
                            Intent intent = new Intent(TeamMemberActivity.this, FriendProfileActivity.class);
                            intent.putExtra("applyType", "3");//3队内查找
                            startActivity(intent);
                        } else {
                            Toast.makeText(TeamMemberActivity.this, "搜索不到此用户", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(TeamMemberActivity.this, "搜索不到此用户", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestFailed() {
                dismissLoading();
                Toast.makeText(TeamMemberActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
            }
        }).getEntityData(this,ConstantsURL.QueryFriend, requestJson);
    }
}
