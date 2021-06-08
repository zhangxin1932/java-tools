package com.zy.tools.undefined.assembly.controller;

import com.netflix.config.DynamicProperty;
import com.zy.commons.did.core.DidGenerator;
import com.zy.commons.lang.response.ResponseVO;
import com.zy.commons.lang.validator.Validators;
import com.zy.tools.undefined.assembly.bean.Req;
import com.zy.tools.undefined.assembly.bean.Stu;
import com.zy.tools.undefined.assembly.exception.ExceptionEnum;
import com.zy.tools.undefined.assembly.mapper.StuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Validated
public class DynamicController {

    @Autowired
    private DidGenerator didGenerator;

    @Autowired
    private StuMapper stuMapper;

    @RequestMapping("did")
    public ResponseVO<String> did() {
        String did = didGenerator.getDid();
        String parseDid = didGenerator.parseDid(did);
        return ResponseVO.of(parseDid);
    }

    @GetMapping("hello")
    public Object hello() {
        return "hello" + DynamicProperty.getInstance("name").getString();
    }

    @PostMapping("getStu")
    public ResponseVO<List<Stu>> getStu(@Valid @RequestBody Req req) {
        Validators.ifInvalid(req.getId() < 0).thenThrow(ExceptionEnum.ERR_0001);
        return ResponseVO.of(stuMapper.getAllStu());
    }
}
