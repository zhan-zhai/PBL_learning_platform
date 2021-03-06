package com.SuperNova.service.impl;

import com.SuperNova.core.FileUtil;
import com.SuperNova.core.ProjectConstant;
import com.SuperNova.dao.*;
import com.SuperNova.model.*;
import com.SuperNova.service.ProjectService;
import com.SuperNova.core.AbstractService;
import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by CodeGenerator on 2020/05/14.
 */
@Service
@Transactional
public class ProjectServiceImpl extends AbstractService<Project> implements ProjectService {
    @Resource
    private ProjectMapper projectMapper;
    @Resource
    private GradeSystemMapper gradeSystemMapper;
    @Resource
    private StudentProjectMapper studentProjectMapper;
    @Resource
    private AssignmentMapper assignmentMapper;
    @Resource
    private StudentAssignmentMapper studentAssignmentMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private EvaluationMapper evaluationMapper;

    @Override
    public ArrayList<Map<String, Object>> getSelfAndMutualGradeByPid(int p_id) {
        StudentProject studentProject = new StudentProject();
        studentProject.setP_id(p_id);
        ArrayList<Map<String, Object>> ret = new ArrayList<>();
        List<StudentProject> studentProjects = studentProjectMapper.select(studentProject);
        for (StudentProject s : studentProjects) {
            Map<String, Object> tmp = new HashMap<>();
            User user = userMapper.selectByPrimaryKey(s.getU_id());
            Double mutual_Grade = evaluationMapper.searchEvaluateByOther(p_id,user.getU_id());
            if (mutual_Grade == null)
                mutual_Grade = 0.0;
            Double selfGrade = evaluationMapper.getMyEvaluate(p_id,user.getU_id(),user.getU_id());
            if (selfGrade == null)
                selfGrade = 0.0;
            tmp.put("s_id",s.getU_id());
            tmp.put("s_name",user.getU_name());
            tmp.put("selfScore",selfGrade);
            tmp.put("mutualScore",mutual_Grade);
            ret.add(tmp);
        }
        return ret;
    }

    @Override
    public int studentCoursePID(String u_id, int c_id) {
          Integer res = studentProjectMapper.studentCoursePID(c_id,u_id);
          return res==null?-1:res;
    }

    @Override
    public Object searchProject(int c_id) {
        Project tmp = new Project();
        tmp.setC_id(c_id);
        return JSON.toJSON(projectMapper.select(tmp));
    }

    @Override
    public void deletProject(int p_id) {
        //????????????????????????????????????
        FileUtil.deleteStorageDir(ProjectConstant.File_BASE+p_id);

        projectMapper.deleteByPrimaryKey(p_id);

        GradeSystem tmp = new GradeSystem();
        tmp.setP_id(p_id);
        gradeSystemMapper.delete(tmp);
    }

    @Override
    public int addProject(Project project, List<GradeSystem> grades) {
        project.setGrading_status(false);
        projectMapper.addProject(project);
        int p_id = project.getP_id();
        //?????????????????????????????????id???1????????????
        int item_id = 1;
        for (GradeSystem grade:grades) {
            grade.setP_id(p_id);
            //???????????????????????????item_id???????????????????????????
            grade.setItem_id(item_id++);
            gradeSystemMapper.insert(grade);
        }
        return p_id;
    }

    @Override
    public void changeProject(Project project, List<GradeSystem> grades) {
        projectMapper.updateByPrimaryKey(project);
        GradeSystem gradeSystem = new GradeSystem();
        gradeSystem.setP_id(project.getP_id());
        List<GradeSystem> preGradeSystems = gradeSystemMapper.select(gradeSystem);

        int index = gradeSystemMapper.getMaxItemId(project.getP_id());
        int deleteItemId = 0;
        for (GradeSystem preGrade: preGradeSystems) {
            deleteItemId = preGrade.getItem_id();
            boolean flag = true;
            for (GradeSystem grade : grades) {
                if (preGrade.getItem_id().equals(grade.getItem_id())) {
                    flag = false;
                    break;
                }
            }
            if (flag) {//????????????????????????????????????
                GradeSystem deleteTmp = new GradeSystem();
                deleteTmp.setP_id(project.getP_id());
                deleteTmp.setItem_id(deleteItemId);
                gradeSystemMapper.delete(deleteTmp);
            }else {//???????????????????????????????????????
                for (GradeSystem grade : grades) {
                    if (preGrade.getItem_id().equals(grade.getItem_id())) {
                        gradeSystemMapper.updateByPrimaryKey(grade);
                        break;
                    }
                }
            }
        }
        for (GradeSystem grade:grades) {
            if (grade.getItem_id() == -1){//?????????
                grade.setItem_id(++index);
                gradeSystemMapper.insert(grade);
                break;
            }
        }
    }

    @Override
    public void updateProjectGradeStatus(int p_id) {
        Project tmp = new Project();
        tmp.setP_id(p_id);
        tmp = projectMapper.selectByPrimaryKey(tmp);
        tmp.setGrading_status(true);
        projectMapper.updateByPrimaryKey(tmp);
    }

    @Override
    public List<GradeSystem> searchGradeSystem(int p_id) {
        GradeSystem tmp = new GradeSystem();
        tmp.setP_id(p_id);
        return gradeSystemMapper.select(tmp);
    }

    @Override
    public int searchTotalNum(int p_id) {
        StudentProject tmp = new StudentProject();
        tmp.setP_id(p_id);
        return studentProjectMapper.selectCount(tmp);
    }

    @Override
    public int countDone(int p_id) {
        return projectMapper.countDone(p_id);
    }

    @Override
    public Object searchGroupers(int p_id) {
        return JSON.toJSON(projectMapper.searchGroupers(p_id));
    }

    @Override
    public String searchLeader(int p_id) {
        StudentProject tmp = new StudentProject();
        tmp.setIs_group_leader(true);
        tmp.setP_id(p_id);
        List<StudentProject> list = studentProjectMapper.select(tmp);
        if(list.size()==0){
            return "null";
        }
        return list.get(0).getU_id();
    }

    @Override
    public void joinProject(int p_id, String u_id) {
        StudentProject tmp = new StudentProject();
        tmp.setP_id(p_id);
        List<StudentProject> list = studentProjectMapper.select(tmp);
        if(list.size()==0){
            tmp.setIs_group_leader(true);
        }else{
            tmp.setIs_group_leader(false);
        }
        tmp.setU_id(u_id);
        //?????????????????????????????????????????????
        tmp.setMutual_grade(0.0);
        tmp.setSelf_grade(0.0);
        tmp.setTeacher_grade(0.0);
        //?????????????????????
        studentProjectMapper.insert(tmp);

        //??????????????????
        Assignment assignment = new Assignment();
        assignment.setP_id(p_id);
        List<Assignment> aList = assignmentMapper.select(assignment);
        for (Assignment a:aList) {
            StudentAssignment t = new StudentAssignment();
            t.setA_id(a.getA_id());
            t.setP_id(a.getP_id());
            t.setU_id(u_id);
            t.setStatus(false);
            String leaderId = searchLeader(p_id);
            if (leaderId != null){
                StudentAssignment flag = new StudentAssignment();
                flag.setU_id(leaderId);
                flag.setA_id(a.getA_id());
                flag.setP_id(a.getP_id());
                StudentAssignment if_Null_tmp = studentAssignmentMapper.selectOne(flag);
                if (if_Null_tmp != null && if_Null_tmp.getUrge())
                    t.setUrge(true);
                else
                    t.setUrge(false);
            }else
                t.setUrge(false);
            studentAssignmentMapper.insert(t);
        }
    }

    @Override
    public void updateTeacherGrade(StudentProject studentProject) {
        studentProjectMapper.updateByPrimaryKeySelective(studentProject);
    }

    @Override
    public boolean evaluationDone(int p_id) {
        Project tmp = new Project();
        tmp.setP_id(p_id);
        tmp = projectMapper.selectByPrimaryKey(tmp);
        if(tmp.getTeacher_grade_ratio()>0){
            //????????????????????????????????????????????????false
            if(evaluationMapper.searchTeacherNotEvaluateNum(p_id)>0){
                return false;
            }
        }

        if(tmp.getSelf_grade_ratio()>0){
            //??????????????????????????????????????????false
            if(evaluationMapper.searchNotSelfEvaluateNum(p_id)>0){
                return false;
            }

            if(tmp.getMutual_grade_ratio()>0){
                //???????????????????????????/???????????????????????????false
                if(evaluationMapper.searchNotEvaluateNum(p_id)>0){
                    return false;
                }
            }
        }else{
            if(tmp.getMutual_grade_ratio()>0){
                int left = evaluationMapper.searchNotEvaluateNum(p_id)-evaluationMapper.searchNotSelfEvaluateNum(p_id);

                //????????????????????????????????????????????????false
                if(left>0){
                    return false;
                }
            }
        }

        //?????????????????????true
        return true;
    }
}
