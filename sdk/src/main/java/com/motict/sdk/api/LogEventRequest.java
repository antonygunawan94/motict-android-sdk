package com.motict.sdk.api;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LogEventRequest {
    private String sid;
    @SerializedName("os_version")
    private String osVersion;
    @SerializedName("app_version")
    private String appVersion;
    @SerializedName("app_package_name")
    private String appPackageName;
    @SerializedName("event_name")
    private String eventName;
    @SerializedName("event_timestamp")
    private String eventTimestamp;
    @SerializedName("latitude")
    private String latitude;
    @SerializedName("longitude")
    private String longitude;

    public LogEventRequest() {
    }

    public LogEventRequest(String sid, String OSVersion, String appVersion, String appPackageName, String eventName, Date eventTimestamp, String latitude, String longitude) {
        this.sid = sid;
        this.osVersion = OSVersion;
        this.appVersion = appVersion;
        this.appPackageName = appPackageName;
        this.eventName = eventName;
        this.eventTimestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ", Locale.getDefault())
                .format(eventTimestamp);
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public String getSid() {
        return sid;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public String getAppPackageName() {
        return appPackageName;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventTimestamp() {
        return eventTimestamp;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }
}
