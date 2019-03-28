package com.enterprise.pc.applicationlocationfree.db.converter;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Created by PC on 2018-03-30.
 */

public class DateConverter {

    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

}
