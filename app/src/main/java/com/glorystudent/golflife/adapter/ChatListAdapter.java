package com.glorystudent.golflife.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.glorystudent.golflibrary.adapter.AbsMoreBaseAdapter;
import com.glorystudent.golflibrary.util.DensityUtil;
import com.glorystudent.golflibrary.util.GlideUtil;
import com.glorystudent.golflibrary.util.SharedUtil;
import com.glorystudent.golflife.R;
import com.glorystudent.golflife.activity.SystemJumpActivity;
import com.glorystudent.golflife.activity.VideoGraffitiActivity;
import com.glorystudent.golflife.api.OkGoRequest;
import com.glorystudent.golflife.api.RequestAPI;
import com.glorystudent.golflife.entity.AliyunRequestEntity;
import com.glorystudent.golflife.entity.ChatEntity;
import com.glorystudent.golflife.entity.ExtEntity;
import com.glorystudent.golflife.entity.GetObjectSamples;
import com.glorystudent.golflife.entity.SubscribeRequestEntity;
import com.glorystudent.golflife.entity.SystemExtMessageEntity;
import com.glorystudent.golflife.util.Constants;
import com.glorystudent.golflife.util.DialogUtil;
import com.glorystudent.golflife.util.FileToMD5Util;
import com.glorystudent.golflife.util.ResuambleUploadSamples;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

/**
 * TODO 聊天适配器
 * Created by Gavin.J on 2017/11/6.
 */

public class ChatListAdapter extends AbsMoreBaseAdapter<ChatEntity> implements View.OnClickListener {
    private final static String TAG = "ChatListAdapter";
    private OSS oss;
    private String customerphoto;
    private AliyunRequestEntity aliyunRequestEntity;
    // 运行sample前需要配置以下字段为有效的值
    private static String endpoint;
    private static String accessKeyId;
    private static String accessKeySecret;
    private static String uploadFilePath;

    private static String testBucket;
    private static String uploadObject;
    private static String downloadObject;
    private LocalBroadcastManager localBroadcastManager;
    private OnUpAliyunListener onUpAliyunListener;
    private MediaPlayer mp;
    private ImageView saveImageView;
    private ImageView iv ;
    private ImageView iv_play ;
    private TextView tv_progress;

    public void setOnUpAliyunListener(OnUpAliyunListener onUpAliyunListener) {
        this.onUpAliyunListener = onUpAliyunListener;
    }

    public void setKeyId(AliyunRequestEntity aliyunRequestEntity) {
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        List<AliyunRequestEntity.ListsettingvalueBean> listsettingvalue = aliyunRequestEntity.getListsettingvalue();
        accessKeyId = listsettingvalue.get(0).getSettingvalue();
        accessKeySecret = listsettingvalue.get(1).getSettingvalue();
        endpoint = "https://" + listsettingvalue.get(2).getSettingvalue() + ".aliyuncs.com";
        testBucket = listsettingvalue.get(3).getSettingvalue();
        setOss();
    }

    public void setOss() {
        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(accessKeyId, accessKeySecret);
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        OSSLog.enableLog();
        oss = new OSSClient(context.getApplicationContext(), endpoint, credentialProvider, conf);
    }

    public ChatListAdapter(Context context) {
        super(context, R.layout.item_chat_my_type1, R.layout.item_chat_my_type2, R.layout.item_chat_my_type3);
    }

    public void setCustomerphoto(String customerphoto) {
        this.customerphoto = customerphoto;
    }


    @Override
    public void bindDatas(ViewHolder viewHolder, ChatEntity datas, int typeView) {
        if (typeView == 2) {
            viewHolder.bindTextView(R.id.tv_chattime, datas.getChatTime());
        } else {
            ImageView iv_header2 = (ImageView) viewHolder.getView(R.id.iv_header);
            switch (typeView) {
                case 0:
                    GlideUtil.loadCircleImageView(context, customerphoto, iv_header2, R.drawable.icon_chat_golffriend);
                    break;
                case 1:
                    GlideUtil.loadCircleImageView(context, SharedUtil.getString(Constants.HEAD_PORTRAIT), iv_header2, R.drawable.icon_chat_golffriend);
                    break;
            }
            final LinearLayout ll_layout2 = (LinearLayout) viewHolder.getView(R.id.ll_layout);
            String chatType = datas.getChatType();
            switch (chatType) {
                case "TXT":
                    //发送文字
                    switch (typeView) {
                        case 0:
                            ll_layout2.setBackgroundResource(R.drawable.chat_bg_white_n);
                            break;
                        case 1:
                            ll_layout2.setBackgroundResource(R.drawable.chat_bg_orange_n);
                            break;
                    }
                    ll_layout2.removeAllViews();
                    if (datas.getTextType() == null) {
                        TextView textView2 = new TextView(context);
                        textView2.setTextColor(context.getResources().getColor(R.color.primaryColor));
                        textView2.setText(datas.getTxt());
                        ll_layout2.addView(textView2);
                    } else {
                        if (datas.getTextType().equals("2")) {
                            //图文
                            View inflate = LayoutInflater.from(context).inflate(R.layout.item_chat_imgtext_list, null);
                            ImageView iv_img = (ImageView) inflate.findViewById(R.id.iv_img);
                            TextView tv_message = (TextView) inflate.findViewById(R.id.tv_message);
                            if (datas.getSystemExtMessageEntity().getPicPath() != null) {
                                GlideUtil.loadImageView(context, datas.getSystemExtMessageEntity().getPicPath(), iv_img);
                            }
                            tv_message.setText(datas.getTxt());
                            ll_layout2.addView(inflate);
                            String url = datas.getSystemExtMessageEntity().getUrl();
                            ll_layout2.setTag(url);
                            ll_layout2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String url = (String) v.getTag();
                                    Intent intent = new Intent(context, SystemJumpActivity.class);
                                    intent.putExtra("url", url);
                                    context.startActivity(intent);
                                }
                            });
                        }
                    }
                    break;
                case "IMAGE":
                    //图片、视频
                    ll_layout2.setBackgroundColor(Color.TRANSPARENT);
                    ll_layout2.removeAllViews();
                    View inflate = LayoutInflater.from(context).inflate(R.layout.item_chat_img_list, null);
                    RelativeLayout rl_img = (RelativeLayout) inflate.findViewById(R.id.rl_img);
                   iv = (ImageView) inflate.findViewById(R.id.iv_img);
                   iv_play = (ImageView) inflate.findViewById(R.id.iv_play);
                   tv_progress = (TextView) inflate.findViewById(R.id.tv_progress);
                    Log.d(TAG, "bindDatas: --->我真是最了" + SharedUtil.getString(Constants.USER_ID) + " " + datas.getExt());
                    if (datas.getExt() != null) {
                        if (!datas.getExt().getVideoMD5().contains("/")) {
                            //下载
                            iv_play.setVisibility(View.VISIBLE);
                            rl_img.setTag(datas.getExt());
                        } else {
                            //上传
                            if (datas.getUpState() == 1) {
                                datas.setUpState(2);
                                upLoadVideo(iv_play, tv_progress, datas.getExt(), datas.getTxt());
                            }
                        }
                    } else {
                        tv_progress.setVisibility(View.GONE);
                        iv_play.setVisibility(View.GONE);
                    }
                    Glide.with(context).load(datas.getTxt()).asBitmap().into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            try {
                                iv.setImageBitmap(resource);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {

                        }
                    });
                    GlideUtil.loadImageView(context, datas.getTxt(), iv);
                    ll_layout2.addView(inflate);
                    break;
                case "VOICE":
                    //发送语音
                    ll_layout2.removeAllViews();
                    switch (typeView) {
                        case 0:
                            ll_layout2.setBackgroundResource(R.drawable.chat_bg_white_n);
                            break;
                        case 1:
                            ll_layout2.setBackgroundResource(R.drawable.chat_bg_orange_n);
                            break;
                    }
                    View voice = LayoutInflater.from(context).inflate(R.layout.item_chat_voice_layout, null);
                    LinearLayout ll_voice = (LinearLayout) voice.findViewById(R.id.ll_voice);
                    ImageView iv_voice = (ImageView) voice.findViewById(R.id.iv_voice);
                    ll_voice.setTag(typeView);
                    ll_voice.setTag(R.id.play_voice, datas.getTxt());
                    ll_voice.setOnClickListener(this);
                    switch (typeView) {
                        case 0:
                            iv_voice.setImageResource(R.drawable.chat_pic_phone_n);
                            ll_voice.setGravity(Gravity.LEFT);
                            break;
                        case 1:
                            iv_voice.setImageResource(R.drawable.chat_pic_myphone_n);
                            ll_voice.setGravity(Gravity.RIGHT);
                            break;
                    }
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) ll_voice.getLayoutParams();
                    int length = datas.getLength();
                    int len = 0;
                    if (length >= 0 && length <= 30) {
                        len = 4 * length;
                        layoutParams.width = DensityUtil.dip2px(context, 60) + DensityUtil.dip2px(context, len);
                    } else if (length > 30) {
                        layoutParams.width = DensityUtil.dip2px(context, 250);
                    }
                    ll_voice.setLayoutParams(layoutParams);
                    ll_layout2.addView(voice);
                    break;
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_voice:
                //播放语音
                if (mp != null) {
                    mp.stop();
                    int tag = (int) saveImageView.getTag();
                    switch (tag) {
                        case 0:
                            saveImageView.setImageResource(R.drawable.play_voice_left_frame);
                            break;
                        case 1:
                            saveImageView.setImageResource(R.drawable.play_voice_right_frame);
                            break;
                    }
                }
                LinearLayout ll_voice = (LinearLayout) v;
                final ImageView iv = (ImageView) ll_voice.findViewById(R.id.iv_voice);
                saveImageView = iv;
                final int tag = (int) v.getTag();
                saveImageView.setTag(tag);
                String voicePath = (String) v.getTag(R.id.play_voice);
                switch (tag) {
                    case 0:
                        iv.setImageResource(R.drawable.play_voice_left_frame);
                        break;
                    case 1:
                        iv.setImageResource(R.drawable.play_voice_right_frame);
                        break;
                }
                final AnimationDrawable background = (AnimationDrawable) iv.getDrawable();
                background.start();
                try {
                    mp = new MediaPlayer();
                    mp.setDataSource(voicePath);
                    mp.prepare();
                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            background.stop();
                            switch (tag) {
                                case 0:
                                    iv.setImageResource(R.drawable.chat_pic_phone_n);
                                    break;
                                case 1:
                                    iv.setImageResource(R.drawable.chat_pic_myphone_n);
                                    break;
                            }
                        }
                    });
                    mp.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
    }

    /**
     * todo 下载TXT文件
     * @param textMD5
     * @param textFolder
     * @param pathMp4
     * @param tv_progress
     */
    private void downLoadText(String textMD5, String textFolder, final String pathMp4, final TextView tv_progress) {
        if (textMD5 != null) {
            downloadObject = textFolder + "/" + textMD5 + ".zip";
            String fileDirs = Environment.getExternalStorageDirectory().getPath() + "/golf/download/chat/";
            String filename = System.currentTimeMillis() + ".zip";
            GetObjectSamples getObjectSamples = new GetObjectSamples(oss, testBucket, downloadObject, fileDirs, filename, new ProgressBar(context));
            getObjectSamples.setOnProgressListener(new GetObjectSamples.OnProgressListener() {
                @Override
                public void onProgress(long sum, long current) {

                }
            });
            getObjectSamples.setOnDownSuccessListener(new GetObjectSamples.OnDownSuccessListener() {
                @Override
                public void onDownComplete(String path) {
                    try {
                        tv_progress.post(new Runnable() {
                            @Override
                            public void run() {
                                tv_progress.setText("100%");
                            }
                        });
                        Intent intent = new Intent(context, VideoGraffitiActivity.class);
                        intent.putExtra("path", pathMp4);
                        intent.putExtra("type", "2");
                        intent.putExtra("zippath", path);
                        context.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            getObjectSamples.asyncGetObjectSample();
        }
    }

    /**
     * todo 上传视频
     * @param iv_play
     * @param tv_progress
     * @param ext
     * @param imgPath
     */
    private void upLoadVideo(final ImageView iv_play, final TextView tv_progress, final ExtEntity ext, final String imgPath) {
        iv_play.setVisibility(View.GONE);
        tv_progress.setText("0%");
        tv_progress.setVisibility(View.VISIBLE);
        uploadFilePath = ext.getVideoMD5();
        final String zipMD5 = ext.getZipMD5();

        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                final String fileMD5 = FileToMD5Util.getFileMD5(new File(uploadFilePath));
                if (zipMD5 != null && !zipMD5.isEmpty()) {
                    uploadObject = ext.getVideoFolderPath() + "/" + fileMD5 + "_" + FileToMD5Util.getFileMD5(new File(zipMD5)) + ".mp4";
                } else {
                    uploadObject = ext.getVideoFolderPath() + "/" + fileMD5 + ".mp4";
                }

                Log.d(TAG, "onActivityResult: 在上传视频的EXT" + uploadObject);
                ResuambleUploadSamples up = new ResuambleUploadSamples(oss, testBucket, uploadObject, uploadFilePath, new ProgressBar(context))
                        .setOnUpLoadVideoListener(new ResuambleUploadSamples.OnUpLoadVideoListener() {
                            @Override
                            public void onUpSuccess(String url) {
                                if (zipMD5 != null && !zipMD5.isEmpty()) {
                                    upLoadZip(tv_progress, iv_play, fileMD5, ext, imgPath);
                                } else {
                                    tv_progress.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            tv_progress.setVisibility(View.GONE);
                                        }
                                    });

                                    iv_play.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            iv_play.setVisibility(View.VISIBLE);
                                            iv_play.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(context, VideoGraffitiActivity.class);
                                                    intent.putExtra("path", ext.getVideoMD5());
                                                    context.startActivity(intent);
                                                }
                                            });
                                        }
                                    });

                                    ChatEntity chatEntity = new ChatEntity();
                                    chatEntity.setTxt(imgPath);
                                    ExtEntity extEntity = new ExtEntity();
                                    extEntity.setVideoMD5(fileMD5);
                                    extEntity.setVideoFolderPath(ext.getVideoFolderPath());
                                    chatEntity.setExt(extEntity);
                                    EventBus.getDefault().post(chatEntity);
                                }

                            }

                            @Override
                            public void onUpFailure() {

                            }
                        });

                up.resumableUploadWithRecordPathSetting();
                up.setOnProgressListener(new ResuambleUploadSamples.OnProgressListener() {
                    @Override
                    public void onProgress(long sum, long current) {
                        final long ss = sum;
                        final long cu = current;
                        tv_progress.post(new Runnable() {
                            @Override
                            public void run() {
                                if (cu != 0) {
                                    float num = (float) cu / ss;
                                    DecimalFormat df = new DecimalFormat("0.00");//格式化小数，.后跟几个零代表几位小数
                                    String s = df.format(num);//返回的是String类型
                                    Float aFloat = Float.valueOf(s);
                                    float v = aFloat * 100;
                                    int progess = (int) v;
                                    if (progess > 99) {
                                        if (zipMD5 != null && !zipMD5.isEmpty()) {
                                            progess = 99;
                                        }
                                    }
                                    tv_progress.setText(progess + "%");
                                }
                            }
                        });
                    }
                });
            }


        });

        AliyunRequestEntity aliyunOSS = RequestAPI.getAliyunOSS();
        if (aliyunOSS != null) {
            Log.d(TAG, "getOssSucceed: ---->不为空");
            setKeyId(aliyunOSS);
            thread.start();
        } else {
            Log.d(TAG, "getOssSucceed: ---->为空");
            OkGoRequest.getOkGoRequest().setOnGetOssListener(new OkGoRequest.OnGetOssListener() {
                @Override
                public void getOssSucceed(AliyunRequestEntity aliyunRequestEntity) {
                    setKeyId(aliyunRequestEntity);
                    thread.start();
                }
            }).getAliyunOSS(context);
        }
    }

    /**
     *TODO 上传ZIP
     * @param tv_progress
     * @param iv_play
     * @param fileMD5
     * @param ext
     * @param imgPath
     */
    private void upLoadZip(final TextView tv_progress, final ImageView iv_play, final String fileMD5, final ExtEntity ext, final String imgPath) {
        uploadFilePath = ext.getZipMD5();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String zipMD5 = FileToMD5Util.getFileMD5(new File(uploadFilePath));
                uploadObject = ext.getVideoFolderPath() + "/" + zipMD5 + ".zip";
                Log.d(TAG, "onActivityResult: 在上传视频zip" + uploadObject);
                ResuambleUploadSamples up = new ResuambleUploadSamples(oss, testBucket, uploadObject, uploadFilePath, new ProgressBar(context))
                        .setOnUpLoadVideoListener(new ResuambleUploadSamples.OnUpLoadVideoListener() {
                            @Override
                            public void onUpSuccess(String url) {
                                tv_progress.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        tv_progress.setText("100%");
                                        tv_progress.setVisibility(View.GONE);
                                    }
                                });

                                iv_play.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        iv_play.setVisibility(View.VISIBLE);
                                        iv_play.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                System.out.println("ext.getVideoMD5(  "+ext.getVideoMD5());
                                                Intent intent = new Intent(context, VideoGraffitiActivity.class);
                                                intent.putExtra("path", ext.getVideoMD5());
                                                context.startActivity(intent);
                                            }
                                        });
                                    }
                                });

                                ChatEntity chatEntity = new ChatEntity();
                                chatEntity.setTxt(imgPath);
                                ExtEntity extEntity = new ExtEntity();
                                extEntity.setVideoMD5(fileMD5);
                                extEntity.setVideoFolderPath(ext.getVideoFolderPath());
                                extEntity.setZipMD5(zipMD5);
                                extEntity.setZipFolderPath(ext.getZipFolderPath());
                                chatEntity.setExt(extEntity);
                                EventBus.getDefault().post(chatEntity);
                            }

                            @Override
                            public void onUpFailure() {

                            }
                        });

                up.resumableUploadWithRecordPathSetting();
                up.setOnProgressListener(new ResuambleUploadSamples.OnProgressListener() {
                    @Override
                    public void onProgress(long sum, long current) {

                    }
                });
            }


        }).start();
    }


    public interface OnUpAliyunListener {
        void onUpAliyunSuccess(String url, ChatEntity chatEntity);
    }

    public interface OnImageLoadListener {
        void onLoadSuccess();
    }
}
