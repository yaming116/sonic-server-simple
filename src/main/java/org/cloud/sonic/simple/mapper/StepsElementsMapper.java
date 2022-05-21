package org.cloud.sonic.simple.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.cloud.sonic.simple.models.domain.StepsElements;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 *  Mapper 接口
 * @author JayWenStar
 * @since 2021-12-17
 */
@Mapper
public interface StepsElementsMapper extends BaseMapper<StepsElements> {
    @Select("select steps_id FROM steps_elements WHERE elements_id = #{eleId} ORDER BY steps_id")
    List<Integer> selectStepsIdByEleId(@Param("eleId")Integer eleId);

}
