package org.cloud.sonic.simple.services;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.cloud.sonic.simple.models.base.CommentPage;
import org.cloud.sonic.simple.models.domain.Steps;
import org.cloud.sonic.simple.models.dto.StepsDTO;
import org.cloud.sonic.simple.models.http.StepSort;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author ZhouYiXun
 * @des 测试步骤逻辑层
 * @date 2021/8/20 17:51
 */
public interface StepsService extends IService<Steps> {
    List<StepsDTO> findByCaseIdOrderBySort(int caseId);

    @Transactional
    List<StepsDTO> handleSteps(List<StepsDTO> stepsDTOS);

    /**
     * 如果步骤是条件步骤，且子条件也可能是条件步骤，则递归填充条件步骤的子步骤，且所有步骤都会填充 {@link StepsDTO#elements} 属性
     *
     * @param stepsDTO 步骤对象（不需要填充）
     */
    @Transactional
    StepsDTO handleStep(StepsDTO stepsDTO);

    boolean resetCaseId(int id);

    boolean delete(int id);

    void saveStep(StepsDTO operations);

    StepsDTO findById(int id);

    void sortSteps(StepSort stepSort);

    CommentPage<StepsDTO> findByProjectIdAndPlatform(int projectId, int platform, Page<Steps> pageable);

    /**
     * 按照元素名称或者步骤文本搜索步骤
     * @param projectId
     * @param platform
     * @param pageable
     * @param searchContent   stpes表中content字段， element表中 ele_name字段
     * @return
     */
    CommentPage<StepsDTO> searchFindByProjectIdAndPlatform(int projectId, int platform, Page<Steps> pageable,
                                                           String searchContent);

    List<Steps> listStepsByElementsId(int elementsId);

    boolean deleteByProjectId(int projectId);

    /**
     * 获取公共步骤里面的步骤
     */
    List<StepsDTO> listByPublicStepsId(int publicStepsId);
}
