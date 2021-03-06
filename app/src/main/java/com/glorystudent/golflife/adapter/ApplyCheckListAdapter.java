package com.glorystudent.golflife.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.glorystudent.golflibrary.adapter.AbsBaseAdapter;
import com.glorystudent.golflibrary.base.BaseActivity;
import com.glorystudent.golflibrary.util.GlideUtil;
import com.glorystudent.golflife.R;
import com.glorystudent.golflife.api.ConstantsURL;
import com.glorystudent.golflife.api.OkGoRequest;
import com.glorystudent.golflife.api.RequestAPI;
import com.glorystudent.golflife.entity.ApplyCheckEntity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * TODO 申请入队列表适配器
 * Created by Gavin.J on 2017/11/2.
 */

public class ApplyCheckListAdapter extends AbsBaseAdapter<ApplyCheckEntity.ApplyTeamUserListBean> {

    private static final String TAG = "ApplyCheckListAdapter";

    public ApplyCheckListAdapter(Context context) {
        super(context, R.layout.item_apply_check_list);
    }

    @Override
    public void bindView(ViewHolder viewHolder, final ApplyCheckEntity.ApplyTeamUserListBean data) {
        viewHolder.setTextView(R.id.tv_apply_name, data.getName());
        viewHolder.setTextView(R.id.tv_apply_remarks, data.getRemarks());
        GlideUtil.loadCircleImageView(context, data.getCustomerphoto(), (ImageView) viewHolder.getView(R.id.iv_apply_head), R.drawable.pic_default_avatar);
        switch (data.getStatus()) {
            case 0://待同意
                viewHolder.getView(R.id.tv_apply_agree).setVisibility(View.VISIBLE);
                viewHolder.getView(R.id.tv_apply_agreed).setVisibility(View.GONE);
                viewHolder.getView(R.id.tv_apply_refused).setVisibility(View.GONE);
                break;
            case 1://已同意
                viewHolder.getView(R.id.tv_apply_agreed).setVisibility(View.VISIBLE);
                viewHolder.getView(R.id.tv_apply_agree).setVisibility(View.GONE);
                viewHolder.getView(R.id.tv_apply_refused).setVisibility(View.GONE);
                break;
            case 2://已拒绝
                viewHolder.getView(R.id.tv_apply_refused).setVisibility(View.VISIBLE);
                viewHolder.getView(R.id.tv_apply_agree).setVisibility(View.GONE);
                viewHolder.getView(R.id.tv_apply_agreed).setVisibility(View.GONE);
                break;
        }
        viewHolder.getView(R.id.tv_apply_agree).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestAgree(data);
            }
        });
    }

    /**
     * TODO 请求网络同意入队
     */
    private void requestAgree(final ApplyCheckEntity.ApplyTeamUserListBean data) {
        ((BaseActivity) context).showLoading();
        String requestJson = RequestAPI.EditTeamUserApply(context, data.getId()+"",1+"");
        Log.i(TAG, "requestAgree: " + requestJson);
        OkGoRequest.getOkGoRequest().setOnOkGoUtilListener(new OkGoRequest.OnOkGoUtilListener() {
            @Override
            public void parseDatas(String json) {
                try {
                    JSONObject jo = new JSONObject(json);
                    String statuscode = jo.getString("statuscode");
                    String statusmessage = jo.getString("statusmessage");
                    if (statuscode.equals("1")) {//正常
                        Toast.makeText(context, "操作成功", Toast.LENGTH_SHORT).show();
                        data.setStatus(1);
                        notifyDataSetChanged();
                    } else if (statuscode.equals("2")) {//暂无数据
                        Log.i(TAG, "parseDatas: " + statusmessage);
                    } else {
                        Log.i(TAG, "parseDatas: " + statusmessage);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ((BaseActivity) context).dismissLoading();
            }

            @Override
            public void requestFailed() {
                ((BaseActivity) context).dismissLoading();
                Toast.makeText(context, "网络请求失败", Toast.LENGTH_SHORT).show();
            }
        }).getEntityData(context, ConstantsURL.EditTeamUserApply, requestJson);
    }
}
