package com.liu.mapper;

import com.liu.entity.po.TypePO;
import com.liu.entity.po.TypePOExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TypePOMapper {
    long countByExample(TypePOExample example);

    int deleteByExample(TypePOExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(TypePO record);

    int insertSelective(TypePO record);

    List<TypePO> selectByExample(TypePOExample example);

    TypePO selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") TypePO record, @Param("example") TypePOExample example);

    int updateByExample(@Param("record") TypePO record, @Param("example") TypePOExample example);

    int updateByPrimaryKeySelective(TypePO record);

    int updateByPrimaryKey(TypePO record);

    void insertRelationshipBatch(@Param("projectId")Integer projectId, @Param("typeIdList") List<Integer> typeIdList);
}