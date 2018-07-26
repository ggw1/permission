package com.mmall.service;

import com.mmall.dao.SysDeptMapper;
import com.mmall.dto.DeptLevelDto;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SysTreeService {

    @Resource
    private SysDeptMapper sysDeptMapper;

    public List<DeptLevelDto> deptTree(){

    }
}
