package com.zy.tools.undefined.undefined.dateTimeUtils.apache;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

@Data
@NoArgsConstructor
public class DateBeanDemo01 {


    private String name;

    @JSONField(format = "yyyy-MM-dd'T'HH:mm:ssZ")
    private Date date;

    public String getDate() {
        // 方案: apache的DateFormatUtils
        return DateFormatUtils.format(this.date, "yyyy-MM-dd'T'HH:mm:ssZ");
    }

    public void setDate(Date date) {
        this.date = (date == null ? null : (Date) date.clone());
    }
}
