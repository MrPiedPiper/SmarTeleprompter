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
    @ColumnInfo(name = "date")
    private Long date;

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

    public Script(@NonNull Long id, @NonNull String title, @NonNull Long date) {
        this.id = id;
        this.title = title;
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
            date = null;
        } else {
            date = in.readLong();
        }
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
    public Long getDate() {
        return date;
    }

    public void setDate(@NonNull Long date) {
        this.date = date;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(id);
        }
        parcel.writeString(title);
        if (date == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(date);
        }
        if (scrollSpeed == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(scrollSpeed);
        }
        if (fontSize == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(fontSize);
        }
        parcel.writeByte((byte) (enableWaitTags == null ? 0 : enableWaitTags ? 1 : 2));
        parcel.writeByte((byte) (enableSmartScroll == null ? 0 : enableSmartScroll ? 1 : 2));
        if (smartScrollInterval == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(smartScrollInterval);
        }
        if (smartScrollKeyframes == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(smartScrollKeyframes);
        }
    }
}
