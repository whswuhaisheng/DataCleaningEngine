package com.binggou.cloud.persistence.annotation;

import com.binggou.cloud.model.Province;

import java.util.List;

@MyBatisDao
public interface ProvinceDao {
    int deleteByPrimaryKey(Integer id);

    int insert(Province record);

    int insertSelective(Province record);

    Province selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Province record);

    int updateByPrimaryKey(Province record);
    
    List<Province> selectAllProvince();
}