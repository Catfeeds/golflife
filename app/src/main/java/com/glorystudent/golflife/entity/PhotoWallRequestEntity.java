package com.glorystudent.golflife.entity;

/**
 * TODO 照片墙请求实体类
 * Created by Gavin.J on 2017/10/31.
 */

public class PhotoWallRequestEntity {
    private EventpicBean eventpic;

    public EventpicBean getEventpic() {
        return eventpic;
    }

    public void setEventpic(EventpicBean eventpic) {
        this.eventpic = eventpic;
    }

    public static class EventpicBean {
        private String eventactivity_picpath;
        private Integer eventactivity_id;

        public String getEventactivity_picpath() {
            return eventactivity_picpath;
        }

        public void setEventactivity_picpath(String eventactivity_picpath) {
            this.eventactivity_picpath = eventactivity_picpath;
        }

        public Integer getEventactivity_id() {
            return eventactivity_id;
        }

        public void setEventactivity_id(Integer eventactivity_id) {
            this.eventactivity_id = eventactivity_id;
        }
    }
}
