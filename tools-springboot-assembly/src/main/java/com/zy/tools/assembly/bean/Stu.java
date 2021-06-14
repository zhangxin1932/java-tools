package com.zy.tools.assembly.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Stu implements Serializable {
    private static final long serialVersionUID = 973660625401236630L;
    private Integer id;
    private String name;
    private Integer age;
    private String gender;
}
