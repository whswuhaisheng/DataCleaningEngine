package com.binggou.cloud.persistence.annotation;

import com.binggou.cloud.model.ProjectClean;

@MyBatisDao
public interface ProjectCleanDao {
	int deleteByPrimaryKey(Integer id);

    int insert(ProjectClean record);

    int insertSelective(ProjectClean record);

    ProjectClean selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ProjectClean record);

    int updateByPrimaryKeyWithBLOBs(ProjectClean record);

    int updateByPrimaryKey(ProjectClean record);
    
    ProjectClean selectByProjectNumber(String projectnumber);
}