package com.binggou.cloud.persistence.annotation;

import com.binggou.cloud.model.Category;

@MyBatisDao
public interface CategoryDao {
    int deleteByPrimaryKey(Integer id);

    int insert(Category record);

    int insertSelective(Category record);

    Category selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Category record);

    int updateByPrimaryKey(Category record);
    
    Category selectByCategoryName(String categoryname);
    
    Category selectFirstByCategoryName(String categoryname);
}