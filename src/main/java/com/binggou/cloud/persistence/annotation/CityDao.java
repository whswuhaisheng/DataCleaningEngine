package com.binggou.cloud.persistence.annotation;

import com.binggou.cloud.model.City;

import java.util.List;

@MyBatisDao
public interface CityDao {
    int deleteByPrimaryKey(Integer id);

    int insert(City record);

    int insertSelective(City record);

    City selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(City record);

    int updateByPrimaryKey(City record);
    
    List<City> selectAllCity();
}