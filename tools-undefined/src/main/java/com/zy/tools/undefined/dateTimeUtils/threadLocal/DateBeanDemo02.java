package com.zy.tools.undefined.dateTimeUtils.threadLocal;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class DateBeanDemo02 {


    private String name;

    @JSONField(format = "yyyy-MM-dd'T'HH:mm:ssZ")
    private Date date;

    public String getDate() {
        return DateFormatTools.convertDateToString(this.date);
    }

    public void setDate(Date date) {
        this.date = (date == null ? null : (Date) date.clone());
    }
}
