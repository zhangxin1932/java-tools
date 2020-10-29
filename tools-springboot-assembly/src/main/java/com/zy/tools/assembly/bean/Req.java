package com.zy.tools.assembly.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Req {
    @Max(value = 100, message = "id 最大值是 100")
    @Min(value = 1, message = "id 最小值是 1")
    private Integer id;
    @NotBlank(message = "reqName 不能为空")
    private String reqName;
}
