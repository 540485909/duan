package com.zheng.futureclass.dao.mapper;

import com.zheng.futureclass.dao.model.CmsMenu;
import com.zheng.futureclass.dao.model.CmsMenuExample;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface CmsMenuMapper {
    long countByExample(CmsMenuExample example);

    int deleteByExample(CmsMenuExample example);

    int deleteByPrimaryKey(Integer menuId);

    int insert(CmsMenu record);

    int insertSelective(CmsMenu record);

    List<CmsMenu> selectByExample(CmsMenuExample example);

    CmsMenu selectByPrimaryKey(Integer menuId);

    int updateByExampleSelective(@Param("record") CmsMenu record, @Param("example") CmsMenuExample example);

    int updateByExample(@Param("record") CmsMenu record, @Param("example") CmsMenuExample example);

    int updateByPrimaryKeySelective(CmsMenu record);

    int updateByPrimaryKey(CmsMenu record);
}