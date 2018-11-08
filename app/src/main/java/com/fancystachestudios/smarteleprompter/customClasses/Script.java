package com.fancystachestudios.smarteleprompter.customClasses;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 *
 * Room annotation added following this guide https://codelabs.developers.google.com/codelabs/android-room-with-a-view/
 *
 */

@Entity(tableName = "script_table")
public class Script implements Parcelable{

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private Long id;

    @NonNull
    @ColumnInfo(name = "title")
    private String title;

    @NonNull
    @ColumnInfo(name = "original_date")
    private Long originalDate;

    @NonNull
    @ColumnInfo(name = "date")
    private Long date;

    @ColumnInfo(name = "body")
    private String body;

    @ColumnInfo(name = "scroll_speed")
    private Long scrollSpeed;

    @ColumnInfo(name = "font_size")
    private Long fontSize;

    @ColumnInfo(name = "enable_wait_tags")
    private Boolean enableWaitTags;

    @ColumnInfo(name = "enable_smart_scroll")
    private Boolean enableSmartScroll;

    @ColumnInfo(name = "smart_scroll_interval")
    private Long smartScrollInterval;

    @ColumnInfo(name = "smart_scroll_keyframes")
    private Long smartScrollKeyframes;

    public Script(@NonNull Long id, @NonNull String title, @NonNull Long originalDate, @NonNull Long date) {
        this.id = id;
        this.title = title;
        this.originalDate = originalDate;
        this.date = date;
    }

    protected Script(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        title = in.readString();
        if (in.readByte() == 0) {
            originalDate = null;
        } else {
            originalDate = in.readLong();
        }
        if (in.readByte() == 0) {
            date = null;
        } else {
            date = in.readLong();
        }
        body = in.readString();
        if (in.readByte() == 0) {
            scrollSpeed = null;
        } else {
            scrollSpeed = in.readLong();
        }
        if (in.readByte() == 0) {
            fontSize = null;
        } else {
            fontSize = in.readLong();
        }
        byte tmpEnableWaitTags = in.readByte();
        enableWaitTags = tmpEnableWaitTags == 0 ? null : tmpEnableWaitTags == 1;
        byte tmpEnableSmartScroll = in.readByte();
        enableSmartScroll = tmpEnableSmartScroll == 0 ? null : tmpEnableSmartScroll == 1;
        if (in.readByte() == 0) {
            smartScrollInterval = null;
        } else {
            smartScrollInterval = in.readLong();
        }
        if (in.readByte() == 0) {
            smartScrollKeyframes = null;
        } else {
            smartScrollKeyframes = in.readLong();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(title);
        if (originalDate == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(originalDate);
        }
        if (date == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(date);
        }
        dest.writeString(body);
        if (scrollSpeed == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(scrollSpeed);
        }
        if (fontSize == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(fontSize);
        }
        dest.writeByte((byte) (enableWaitTags == null ? 0 : enableWaitTags ? 1 : 2));
        dest.writeByte((byte) (enableSmartScroll == null ? 0 : enableSmartScroll ? 1 : 2));
        if (smartScrollInterval == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(smartScrollInterval);
        }
        if (smartScrollKeyframes == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(smartScrollKeyframes);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Script> CREATOR = new Creator<Script>() {
        @Override
        public Script createFromParcel(Parcel in) {
            return new Script(in);
        }

        @Override
        public Script[] newArray(int size) {
            return new Script[size];
        }
    };

    @NonNull
    public Long getId() {
        return id;
    }

    public void setId(@NonNull Long id) {
        this.id = id;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @NonNull
    public Long getOriginalDate() {
        return originalDate;
    }

    public void setOriginalDate(@NonNull Long originalDate) {
        this.date = originalDate;
    }

    @NonNull
    public Long getDate() {
        return date;
    }

    public void setDate(@NonNull Long date) {
        this.date = date;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Long getScrollSpeed() {
        return scrollSpeed;
    }

    public void setScrollSpeed(Long scrollSpeed) {
        this.scrollSpeed = scrollSpeed;
    }

    public Long getFontSize() {
        return fontSize;
    }

    public void setFontSize(Long fontSize) {
        this.fontSize = fontSize;
    }

    public Boolean getEnableWaitTags() {
        return enableWaitTags;
    }

    public void setEnableWaitTags(Boolean enableWaitTags) {
        this.enableWaitTags = enableWaitTags;
    }

    public Boolean getEnableSmartScroll() {
        return enableSmartScroll;
    }

    public void setEnableSmartScroll(Boolean enableSmartScroll) {
        this.enableSmartScroll = enableSmartScroll;
    }

    public Long getSmartScrollInterval() {
        return smartScrollInterval;
    }

    public void setSmartScrollInterval(Long smartScrollInterval) {
        this.smartScrollInterval = smartScrollInterval;
    }

    public Long getSmartScrollKeyframes() {
        return smartScrollKeyframes;
    }

    public void setSmartScrollKeyframes(Long smartScrollKeyframes) {
        this.smartScrollKeyframes = smartScrollKeyframes;
    }

}
