package org.cloud.sonic.simple.services;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.cloud.sonic.simple.models.domain.Elements;
import org.cloud.sonic.simple.models.domain.Steps;
import org.cloud.sonic.simple.models.dto.StepsDTO;
import org.cloud.sonic.simple.models.http.RespModel;

import java.util.List;

public interface ElementsService extends IService<Elements> {
    Page<Elements> findAll(int projectId, String type, List<String> eleTypes, String name, Page<Elements> pageable);

    List<StepsDTO> findAllStepsByElementsId(int elementsId);

    RespModel delete(int id);

    Elements findById(int id);

    boolean deleteByProjectId(int projectId);

    /**
     * 按照元素名称查找元素Id
     * @param eleName
     */
    List<Integer> selectByEleName(String eleName);
}
