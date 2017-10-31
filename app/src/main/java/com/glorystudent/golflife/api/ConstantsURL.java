package com.glorystudent.golflife.api;

/**
 * TODO 请求的URL
 * Created by Gavin.J on 2017/10/24.
 */

public interface ConstantsURL {
    String BASE_URL2 = "http://app.pgagolf.cn";
    String BASE_URL3 = "https://192.168.1.168:4431";
    String BASE_URL4 = "http://192.168.1.199:8000";
    String BASE_URL = BASE_URL2;//请求URL
    //赛事活动默认的图片
    String  imgUrl="http://glorygolflife.oss-cn-shenzhen.aliyuncs.com/XXQ2Lsd0yq3b72wWdapS4a5Rg24MhxF6cxEOYyODBFQ%3D%2Fcompetitions%2Fbanners%2Faf2026710e2859c421fc19f67eca1fc8.png";

    String GetSMSCheck=BASE_URL+"/api/APISMSCheck/GetSMSCheck";//获取虚拟短信验证码接口URL
    String SMSLogin=BASE_URL+ "/api/APISMSLogin/SMSLogin";//登录接口URL
    String SERVICE_TERMS = BASE_URL + "/home/license";//服务条款URL
    String QueryAD = BASE_URL +"/api/APIAD/QueryAD";//获取广告URL
    String QueryNews = BASE_URL +"/api/APINews/QueryNews";//获取新闻URL
    String NEWS_URL = BASE_URL + "/home/newsDetail?id=%d";//新闻详情
    String QueryEventActivity =BASE_URL+ "/Public/APIPublicEventActivity/QueryEventActivity";//活动赛事URL
    String EVENT_SHARE_URL = BASE_URL + "/Activity/eventActivities?eventActivity_id=%d";//分享赛事活动URL
    String EVENT_DETAIL_URL = BASE_URL + "/ActivityAPP/EventActivities?eventActivity_id=%d&userId=%s&isAndroid=Android";//查看赛事活动URL
//    String EVENT_DETAIL_URL ="http://www.pgagolf.cn/ActivityAPP/EventActivities?eventActivity_id=83&userId=sZDlfuhZvDYchAc0GqKrmw==&isIos=Android";//查看赛事活动URL
    String WXPayAPP= BASE_URL + "/Activity/WXPayAPP";//微信统一下单URL
    String BindingWX=BASE_URL+"/api/APIWXLogin/BindingWX";//绑定微信URL
    String QueryUser=BASE_URL+"/Public/APIPublicUser/QueryUser";//获取用户信息URL
    String QuerySignUpByMy=BASE_URL+"/api/APISignUp/QuerySignUpByMy";//获取我参加的赛事活动URL
    String EVENT_PREVIEW_URL = BASE_URL + "/ActivityAPP/EventActivitiesShow?eventActivity_id=%d";//预览赛事活动URL
    String QueryEventActivityByID=BASE_URL+"/Public/APIPublicEventActivity/QueryEventActivityByID";//通过ID获取赛事活动数据URL
    String QuerySignDefinition=BASE_URL+"/api/APISignDefinition/QuerySignDefinition";////新增时请求网络获取定义信息URL
    String QueryCourt=BASE_URL+"/public/APIPublicCourt/QueryCourt";//获取附近球场位置信息URL
    String EditEventActivity=BASE_URL+"/Public/APIPublicEventActivity/EditEventActivity";//修改赛事活动URL
    String AddEventActivity=BASE_URL+"/Public/APIPublicEventActivity/AddEventActivity";//新增活动赛事URL
    String SelectAllByOSS=BASE_URL+ "/api/APISettings/SelectAllByOSS";//AliyunOSS请求URL
    String QueryEventActivityByEventActivityID=BASE_URL+"/Public/APIPublicEventActivity/QueryEventActivityByEventActivityID";//查看活动详情URL
    String CancelEventActivity=BASE_URL+"/Public/APIPublicEventActivity/CancelEventActivity";//取消赛事活动URL
    String QueryEventActivityPicWall=BASE_URL+"/Public/APIPublicEventActivityPic/QueryEventActivityPicWall";//获取照片墙数据URL
    String AddEventActivityPic=BASE_URL+"/Public/APIPublicEventActivityPic/AddEventActivityPic";//赛事活动图片添加URL
    String AgreeSignUp=BASE_URL+"/api/APISignUp/AgreeSignUp";//是否允许报名赛事活动URL
    String WXPayReFundAPP=BASE_URL+"/api/APIWXPay/WXPayReFundAPP";
    String EditSignUp=BASE_URL+"/api/APISignUp/EditSignUp";
    String QuerySignUpByEventID=BASE_URL+"/api/APISignUp/QuerySignUpByEventID";//通过赛事ID获取对应的报名名单URL
}
