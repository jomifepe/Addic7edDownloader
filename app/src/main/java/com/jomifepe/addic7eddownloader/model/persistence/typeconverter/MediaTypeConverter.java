package com.jomifepe.addic7eddownloader.model.persistence.typeconverter;

import android.arch.persistence.room.TypeConverter;

import com.jomifepe.addic7eddownloader.model.MediaType;

public class MediaTypeConverter {
    @TypeConverter
    public static int toInt(MediaType mediaType) {
        return mediaType.ordinal();
    }

    @TypeConverter
    public static MediaType toMediaType(int val) {
        return MediaType.valueOf(val);
    }
}
