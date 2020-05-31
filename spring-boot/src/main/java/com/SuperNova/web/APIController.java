package com.SuperNova.web;

import com.SuperNova.core.*;
import com.SuperNova.dao.CourseMapper;
import com.SuperNova.model.*;
import com.SuperNova.service.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api")
public class APIController {
    @Resource
    private CourseService courseService;
    @Resource
    private UserService userService;
    @Resource
    private FileService fileService;
    @Resource
    private ProjectService projectService;
    @Resource
    private AssignmentService assignmentService;
    @Resource
    private EvaluationService evaluationService;

    @CrossOrigin(origins = "*")
    @GetMapping("/searchMyCourses")
    public Result searchMyCourses(@RequestParam String pbl_token) {
        String u_id = userService.getUIdByToken(pbl_token);
        String courses = courseService.getMyCourses(u_id);
        JSONObject data = new JSONObject();
        data.put("courses",courses);
        return ResultGenerator.genSuccessResult(data);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/searchMyInformation")
    public Result searchMyInformation(@RequestParam String pbl_token) {
        String u_id = userService.getUIdByToken(pbl_token);
        User user = userService.searchUser(u_id);
        JSONObject data = new JSONObject();
        data.put("content", JSON.toJSONString(user));
        data.put("image",userService.getImageURL(u_id));
        return ResultGenerator.genSuccessResult(data);
    }

    @CrossOrigin(origins = "*")
    @PutMapping("/changeMyImage")
    public Result changeMyImage(@RequestParam String pbl_token,
                                @RequestParam(required = false) MultipartFile image) {


        return ResultGenerator.genSuccessResult();
    }

    @CrossOrigin(origins = "*")
    @PutMapping("/changeMyInformation")
    public Result changeMyInformation(@RequestParam String pbl_token,
                                      @RequestParam String content,
                                      @RequestParam(required = false) MultipartFile image) {


        return ResultGenerator.genSuccessResult();
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/searchOtherCourses")
    public Result searchOtherCourses(@RequestParam String pbl_token,
                                     @RequestParam int pageIndex,
                                     @RequestParam int pageSize) {
        String u_id = userService.getUIdByToken(pbl_token);
        return ResultGenerator.genSuccessResult(courseService.searchOtherCourses(u_id,pageIndex,pageSize));
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/searchAllCourses")
    public Result searchAllCourses(@RequestParam String pbl_token) {
        JSONObject data = new JSONObject();
        data.put("courses",courseService.searchAllCourses());
        return ResultGenerator.genSuccessResult(data);
    }

    @CrossOrigin(origins = "*")
    @PutMapping("/changeCourseStatus")
    public Result changeCourseStatus(@RequestParam String pbl_token,
                                     @RequestParam Integer c_id,
                                     @RequestParam String status) {
        courseService.changeCourseStatus(c_id,status);
        return ResultGenerator.genSuccessResult().setMessage("修改成功");
    }

    @CrossOrigin(origins = "*")
    @PutMapping("/changeCourse")
    public Result changeCourseStatus(@RequestParam String pbl_token,
                                     @RequestParam String course) {
        Course courseObj = JSON.parseObject(course,Course.class);
        courseService.updateCourse(courseObj);
        return ResultGenerator.genSuccessResult().setMessage("修改成功");
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/addCourse")
    public Result addCourse(@RequestParam String pbl_token,
                            @RequestParam String course,
                            @RequestParam MultipartFile image) {
        Course courseObj = JSON.parseObject(course,Course.class);
        int c_id = courseService.addCourse(courseObj);
        String imgURL = fileService.getImageURL(image,""+c_id);
        FileUtil.storageImage(image,imgURL, ProjectConstant.IMG_BASE+c_id+"\\");
        courseObj.setimage_URL(imgURL);
        courseService.updateCourse(courseObj);

        JSONObject data = new JSONObject();
        data.put("c_id",c_id);
        data.put("image_URL",ProjectConstant.WEB_IMG_BASE+c_id+"/"+imgURL);
        return ResultGenerator.genSuccessResult(data).setMessage("课程创建成功");
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/joinCourse")
    public Result joinCourse(@RequestParam String pbl_token,
                             @RequestParam String s_id,
                             @RequestParam Integer c_id) {
        courseService.joinCourse(c_id,s_id);
        return ResultGenerator.genSuccessResult().setMessage("成功加入课程");
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/searchProject")
    public Result searchProject(@RequestParam String pbl_token,
                                @RequestParam Integer c_id) {
        String u_id = userService.getUIdByToken(pbl_token);
        int p_id = projectService.studentCoursePID(u_id,c_id);
        User user = userService.searchUser(u_id);

        JSONObject data = new JSONObject();
        data.put("type",user.getType());
        data.put("project_take",p_id);
        data.put("projects",projectService.searchProject(c_id));

        return ResultGenerator.genSuccessResult(data);
    }


    @CrossOrigin(origins = "*")
    @PostMapping("/createProject")
    public Result createProject(@RequestParam String pbl_token,
                                @RequestParam String project,
                                @RequestParam String grades) {
        String t_id = userService.getUIdByToken(pbl_token);
        User user = userService.searchUser(t_id);

        if(!user.getType().equals("admin")&&!user.getType().equals("teacher")){
            return ResultGenerator.genFailResult("创建失败，权限不够").setCode(ResultCode.DENY);
        }

        Project projectObj = JSON.parseObject(project,Project.class);
        List<GradeSystem> gradeSystems = JSON.parseArray(grades,GradeSystem.class);
        int p_id = projectService.addProject(projectObj,gradeSystems);
        JSONObject data = new JSONObject();
        data.put("p_id",p_id);

        return ResultGenerator.genSuccessResult(data);
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping("/deleteProject")
    public Result deleteProject(@RequestParam String pbl_token,
                                @RequestParam Integer p_id) {
        String t_id = userService.getUIdByToken(pbl_token);
        User user = userService.searchUser(t_id);
        if(!user.getType().equals("admin")&&!user.getType().equals("teacher")){
            return ResultGenerator.genFailResult("删除失败，权限不够").setCode(ResultCode.DENY);
        }
        projectService.deletProject(p_id);
        return ResultGenerator.genSuccessResult("删除成功");
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/changeProject")
    public Result changeProject(@RequestParam String pbl_token,
                                @RequestParam String project,
                                @RequestParam String grades) {
        String t_id = userService.getUIdByToken(pbl_token);
        User user = userService.searchUser(t_id);
        if(!user.getType().equals("admin")){
            return ResultGenerator.genFailResult("修改项目失败，权限不够").setCode(ResultCode.DENY);
        }
        Project projectObj = JSON.parseObject(project,Project.class);
        List<GradeSystem> gradeSystems = JSON.parseArray(grades,GradeSystem.class);
        projectService.changeProject(projectObj,gradeSystems);
        return ResultGenerator.genSuccessResult().setMessage("修改成功");
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/searchProjectGradeSystem")
    public Result searchProjectGradeSystem(@RequestParam String pbl_token,
                                           @RequestParam Integer p_id) {
        JSONObject data = new JSONObject();
        data.put("grades",projectService.searchGradeSystem(p_id));
        return ResultGenerator.genSuccessResult(data);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/searchAssignment")
    public Result searchAssignment(@RequestParam String pbl_token,
                                   @RequestParam Integer p_id) {
        String u_id = userService.getUIdByToken(pbl_token);

        JSONObject data = new JSONObject();
        data.put("assignments",assignmentService.searchAssignment(p_id));
        data.put("studentStatus",assignmentService.searchDoneStatus(p_id,u_id));
        return ResultGenerator.genSuccessResult(data);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/searchAssignmentDone")
    public Result searchAssignmentDone(@RequestParam String pbl_token,
                                       @RequestParam Integer p_id,
                                       @RequestParam Integer a_id) {
        JSONObject data = new JSONObject();
        data.put("assignments",assignmentService.searchAssignmentDoneNum(p_id,a_id));
        data.put("totalNum",projectService.searchTotalNum(p_id));

        return ResultGenerator.genSuccessResult(data);
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/createAssignment")
    public Result createAssignment(@RequestParam String pbl_token,
                                   @RequestParam String assignment) {
        Assignment assignmentObj = JSON.parseObject(assignment,Assignment.class);
        int a_id = assignmentService.createAssignment(assignmentObj);
        JSONObject data = new JSONObject();
        data.put("a_id",a_id);
        return ResultGenerator.genSuccessResult(data).setMessage("创建任务成功");
    }

    @CrossOrigin(origins = "*")
    @PutMapping("/changeAssignment")
    public Result changeAssignment(@RequestParam String pbl_token,
                                   @RequestParam String assignment) {
        Assignment assignmentObj = JSON.parseObject(assignment,Assignment.class);
        assignmentService.changeAssignment(assignmentObj);

        return ResultGenerator.genSuccessResult().setMessage("修改任务成功");
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping("/deleteAssignment")
    public Result deleteAssignment(@RequestParam String pbl_token,
                                   @RequestParam Integer a_id,
                                   @RequestParam Integer p_id) {
        assignmentService.deleteAssignment(p_id,a_id);
        return ResultGenerator.genSuccessResult().setMessage("删除任务成功");
    }

    @CrossOrigin(origins = "*")
    @PutMapping("/urgeAssignment")
    public Result urgeAssignment(@RequestParam String pbl_token,
                                 @RequestParam Integer a_id,
                                 @RequestParam Integer p_id) {
        assignmentService.urgeAssignment(p_id,a_id);
        return ResultGenerator.genSuccessResult().setMessage("催促消息发送成功");
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/countDone")
    public Result countDone(@RequestParam String pbl_token,
                            @RequestParam Integer p_id) {
        int doneNum = projectService.countDone(p_id);
        int totalNum = projectService.searchTotalNum(p_id);
        JSONObject data = new JSONObject();
        data.put("done_num",doneNum);
        data.put("total_num",totalNum);
        return ResultGenerator.genSuccessResult(data).setMessage("成功统计项目完成人数");
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/searchGroupers")
    public Result searchGroupers(@RequestParam String pbl_token,
                                 @RequestParam Integer p_id) {
        JSONObject data = new JSONObject();
        data.put("groupers",projectService.searchGroupers(p_id));
        return ResultGenerator.genSuccessResult(data);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/searchEvaluateBySelf")
    public Result searchEvaluateBySelf(@RequestParam String pbl_token,
                                       @RequestParam Integer p_id) {
        String u_id = userService.getUIdByToken(pbl_token);

        JSONObject data = new JSONObject();
        String grade = evaluationService.searchEvaluateBySelf(p_id,u_id);
        if(grade.equals("-1")){
            return ResultGenerator.genFailResult("暂未评分").setCode(ResultCode.DENY);
        }

        data.put("grade",grade);
        return ResultGenerator.genSuccessResult(data);
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/evaluateSelf")
    public Result evaluateSelf(@RequestParam String pbl_token,
                               @RequestParam Integer p_id,
                               @RequestParam Double grade){


        return ResultGenerator.genSuccessResult();
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/searchEvaluateByOther")
    public Result searchEvaluateByOther(@RequestParam String pbl_token,
                                        @RequestParam Integer p_id) {


        return ResultGenerator.genSuccessResult();
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/evaluateOther")
    public Result evaluateOther(@RequestParam String pbl_token,
                                @RequestParam Integer p_id,
                                @RequestParam Integer u_id,
                                @RequestParam Double grade) {


        return ResultGenerator.genSuccessResult();
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/searchEvaluateByTeacher")
    public Result searchEvaluateByTeacher(@RequestParam String pbl_token,
                                          @RequestParam Integer p_id) {


        return ResultGenerator.genSuccessResult();
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/searchGrade")
    public Result searchGrade(@RequestParam String pbl_token,
                              @RequestParam Integer p_id) {


        return ResultGenerator.genSuccessResult();
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/searchDiscussions")
    public Result searchDiscussions(@RequestParam String pbl_token,
                                    @RequestParam Integer p_id) {


        return ResultGenerator.genSuccessResult();
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/createDiscussion")
    public Result createDiscussion(@RequestParam String pbl_token,
                                   @RequestParam String discussion) {


        return ResultGenerator.genSuccessResult();
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping("/deleteDiscussion")
    public Result deleteDiscussion(@RequestParam String pbl_token, @RequestParam String discussion) {


        return ResultGenerator.genSuccessResult();
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/searchReply")
    public Result searchReply(@RequestParam String pbl_token,
                              @RequestParam Integer p_id) {


        return ResultGenerator.genSuccessResult();
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/createReply")
    public Result createReply(@RequestParam String pbl_token,
                              @RequestParam String reply) {


        return ResultGenerator.genSuccessResult();
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping("/deleteReply")
    public Result deleteReply(@RequestParam String pbl_token,
                              @RequestParam Integer r_id) {


        return ResultGenerator.genSuccessResult();
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/searchAllFiles")
    public Result searchAllFiles(@RequestParam String pbl_token,
                                 @RequestParam Integer p_id) {


        return ResultGenerator.genSuccessResult();
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/uploadFile")
    public Result uploadFile(@RequestParam String pbl_token,
                             @RequestParam String f_name,
                             @RequestParam Integer p_id,
                             @RequestParam MultipartFile file,
                             @RequestParam String description) {

        return ResultGenerator.genSuccessResult();
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping("/deleteFile")
    public Result deleteFile(@RequestParam String pbl_token,
                             @RequestParam Integer f_id,
                             @RequestParam Integer p_id) {


        return ResultGenerator.genSuccessResult();
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/countAssignmentDone")
    public Result countAssignmentDone(@RequestParam String pbl_token,
                                      @RequestParam Integer p_id) {


        return ResultGenerator.genSuccessResult();
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/countDiscussion")
    public Result countDiscussion(@RequestParam String pbl_token,
                                  @RequestParam Integer p_id) {


        return ResultGenerator.genSuccessResult();
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/getGradeItems")
    public Result getGradeItems(@RequestParam String pbl_token,
                                @RequestParam Integer p_id) {


        return ResultGenerator.genSuccessResult();
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/evaluateByTeacher")
    public Result evaluateByTeacher(@RequestParam String pbl_token,
                                    @RequestParam Integer p_id,
                                    @RequestParam String s_id,
                                    @RequestParam String grade) {

        return ResultGenerator.genSuccessResult();
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/searchAllUsers")
    public Result searchAllUsers(@RequestParam String pbl_token) {


        return ResultGenerator.genSuccessResult();
    }

    @CrossOrigin(origins = "*")
    @PutMapping("/changeImage")
    public Result changeImage(@RequestParam String pbl_token,
                              @RequestParam String u_id,
                              @RequestParam(required = false) MultipartFile image) {

        return ResultGenerator.genSuccessResult();
    }

    @CrossOrigin(origins = "*")
    @PutMapping("/changeInformation")
    public Result changeInformation(@RequestParam String pbl_token,
                                    @RequestParam String user){

        return ResultGenerator.genSuccessResult();
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/addUser")
    public Result addUser(@RequestParam String pbl_token,
                          @RequestParam String user,
                          @RequestParam String password,
                          @RequestParam(required = false) MultipartFile image) {

        return ResultGenerator.genSuccessResult();
    }

}
