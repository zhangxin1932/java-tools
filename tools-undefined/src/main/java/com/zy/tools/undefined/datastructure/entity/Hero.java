package com.zy.tools.undefined.datastructure.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Hero implements Serializable {
    private static final long serialVersionUID = -4583653657934061193L;
    private int no;
    private String name;
}
