package com.SuperNova.dao;

import com.SuperNova.core.Mapper;
import com.SuperNova.model.Assignment;
import com.SuperNova.model.DoneInformation;

import javax.persistence.OrderBy;
import java.util.List;

public interface AssignmentMapper extends Mapper<Assignment> {

    /**
     * 创建一个项目
     * @param assignment
     */
    void addAssignment(Assignment assignment);

    /**
     * 搜索项目中任务的完成情况(按s_id排序)
     * @param p_id
     * @return
     */
    List<DoneInformation> countAssignmentDone(int p_id);

    /**
     * 搜索每个任务的完成人数(按a_id排序)
     * @param p_id
     * @return
     */
    List<Integer> searchAllAssignmentsDoneNum(int p_id);

}