package com.SuperNova.web;

import com.SuperNova.core.*;
import com.SuperNova.model.*;
import com.SuperNova.service.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    @Resource
    private StudentGradeService studentGradeService;
    @Resource
    private DiscussionService discussionService;
    @Resource
    private ReplyService replyService;

    @CrossOrigin(origins = "*")
    @GetMapping("/searchMyCourses")
    public Result searchMyCourses(@RequestParam String pbl_token,
                                  @RequestParam String u_id,
                                  @RequestParam String pageIndex,
                                  @RequestParam String pageSize) {
        return ResultGenerator.genSuccessResult(courseService.getMyCourses(u_id,Integer.parseInt(pageIndex),Integer.parseInt(pageSize)));
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/searchMyInformation")
    public Result searchMyInformation(@RequestParam String pbl_token,
                                      @RequestParam String u_id) {
        User user = userService.searchUser(u_id);
        JSONObject data = new JSONObject();
        data.put("content",user);
        data.put("image",userService.getImageURL(u_id));
        return ResultGenerator.genSuccessResult(data);
    }

    @CrossOrigin(origins = "*")
    @PutMapping("/changeMyImage")
    public Result changeMyImage(@RequestParam String pbl_token,
                                @RequestParam String u_id,
                                @RequestParam(required = false) MultipartFile image) {
        User user = userService.searchUser(u_id);
        userService.deleteImage(user);
        String imgURL = userService.setImage(u_id,image);
        JSONObject data = new JSONObject();
        data.put("image",imgURL);
        return ResultGenerator.genSuccessResult(data).setMessage("??????????????????");
    }

    @CrossOrigin(origins = "*")
    @PutMapping("/changeMyInformation")
    public Result changeMyInformation(@RequestParam String pbl_token,
                                      @RequestParam String content) {
        String u_id = userService.getUIdByToken(pbl_token);
        User user = JSON.parseObject(content,User.class);
        userService.setUser(user);
        JSONObject data = new JSONObject();
        data.put("token",userService.getToken(u_id));
        return ResultGenerator.genSuccessResult(data).setMessage("????????????????????????");
    }

    @CrossOrigin(origins = "*")
    @PutMapping("/changeMyPassword")
    public Result changeMyPassword(@RequestParam String pbl_token,
                                      @RequestParam String oldPassword,
                                      @RequestParam String newPassword) {
        String u_id = userService.getUIdByToken(pbl_token);
        User user = userService.searchUser(u_id);
        if(!user.getPassword().equals(oldPassword)){
            ResultGenerator.genFailResult("???????????????").setCode(ResultCode.FAIL);
        }
        user.setPassword(newPassword);
        userService.setUser(user);
        return ResultGenerator.genSuccessResult().setMessage(userService.getToken(u_id));
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/searchAllTeachers")
    public Result searchAllTeachers(@RequestParam String pbl_token){
        String u_id = userService.getUIdByToken(pbl_token);
        User user = userService.searchUser(u_id);
        if(!user.getType().equals("admin")){
            ResultGenerator.genFailResult("???????????????????????????").setCode(ResultCode.FAIL);
        }

        return ResultGenerator.genSuccessResult(userService.getAllTeachers());
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/searchOtherCourses")
    public Result searchOtherCourses(@RequestParam String pbl_token,
                                     @RequestParam String pageIndex,
                                     @RequestParam String pageSize) {
        String u_id = userService.getUIdByToken(pbl_token);
        return ResultGenerator.genSuccessResult(courseService.searchOtherCourses(u_id,Integer.parseInt(pageIndex),Integer.parseInt(pageSize)));
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/searchAllCourses")
    public Result searchAllCourses(@RequestParam String pbl_token,
                                   @RequestParam String pageIndex,
                                   @RequestParam String pageSize) {
        return ResultGenerator.genSuccessResult(courseService.searchAllCourses(Integer.parseInt(pageIndex),Integer.parseInt(pageSize)));
    }

    @CrossOrigin(origins = "*")
    @PutMapping("/changeCourseStatus")
    public Result changeCourseStatus(@RequestParam String pbl_token,
                                     @RequestParam String c_id,
                                     @RequestParam String status) {
        courseService.changeCourseStatus(Integer.parseInt(c_id),status);
        return ResultGenerator.genSuccessResult().setMessage("????????????");
    }

    @CrossOrigin(origins = "*")
    @PutMapping("/changeCourse")
    public Result changeCourseStatus(@RequestParam String pbl_token,
                                     @RequestParam String course) {
        Course courseObj = JSON.parseObject(course,Course.class);
        courseService.updateCourse(courseObj);
        return ResultGenerator.genSuccessResult().setMessage("????????????");
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/searchCourseByCid")
    public Result searchCourseByCid(@RequestParam String pbl_token,
                                     @RequestParam String c_id) {
        Course course = courseService.searchCourseByCid(Integer.parseInt(c_id));
        JSONObject data = new JSONObject();
        data.put("course",course);
        return ResultGenerator.genSuccessResult(data);
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/addCourse")
    public Result addCourse(@RequestParam String pbl_token,
                            @RequestParam String course,
                            @RequestParam MultipartFile image) {
        Course courseObj = JSON.parseObject(course,Course.class);
        //????????????????????????img URL?????????????????????course id??????img URL
        courseObj.setImage_URL(ProjectConstant.DEAFULT_IMAGE);
        int c_id = courseService.addCourse(courseObj);
        String imgURL = fileService.getImageURL(image,""+c_id);
        FileUtil.storageImage(image,imgURL, ProjectConstant.IMG_BASE+c_id+"/");
        courseObj.setImage_URL(ProjectConstant.WEB_IMG_BASE+c_id+"/"+imgURL);
        courseService.updateCourse(courseObj);

        JSONObject data = new JSONObject();
        data.put("c_id",c_id);
        data.put("image_URL",courseObj.getImage_URL());
        return ResultGenerator.genSuccessResult(data).setMessage("??????????????????");
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/changeCourseWithImg")
    public Result changeCourseWithImg(@RequestParam String pbl_token,
                            @RequestParam String course,
                            @RequestParam MultipartFile image) {
        Course courseObj = JSON.parseObject(course,Course.class);
        //????????????????????????img URL?????????????????????course id??????img URL
        courseObj.setImage_URL(ProjectConstant.DEAFULT_IMAGE);
        int c_id = courseObj.getC_id();
        String imgURL = fileService.getImageURL(image,""+c_id);
        FileUtil.storageImage(image,imgURL, ProjectConstant.IMG_BASE+c_id+"/");
        courseObj.setImage_URL(ProjectConstant.WEB_IMG_BASE+c_id+"/"+imgURL);
        courseService.updateCourse(courseObj);

        JSONObject data = new JSONObject();
        data.put("c_id",c_id);
        data.put("image_URL",courseObj.getImage_URL());
        return ResultGenerator.genSuccessResult(data).setMessage("??????????????????");
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/joinCourse")
    public Result joinCourse(@RequestParam String pbl_token,
                             @RequestParam String s_id,
                             @RequestParam String c_id) {
        courseService.joinCourse(Integer.parseInt(c_id),s_id);
        return ResultGenerator.genSuccessResult().setMessage("??????????????????");
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/searchAllMyCourses")
    public Result searchAllMyCourses(@RequestParam String pbl_token) {
        String u_id = userService.getUIdByToken(pbl_token);
//        String u_id = "S001";
        return ResultGenerator.genSuccessResult(courseService.searchAllMyCourses(u_id));
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/searchProject")
    public Result searchProject(@RequestParam String pbl_token,
                                @RequestParam String c_id) {
        String u_id = userService.getUIdByToken(pbl_token);
//        String u_id = "S001";
        int p_id = projectService.studentCoursePID(u_id,Integer.parseInt(c_id));
        User user = userService.searchUser(u_id);
        JSONObject data = new JSONObject();
        data.put("type",user.getType());
        data.put("project_take",p_id);
        data.put("projects",projectService.searchProject(Integer.parseInt(c_id)));

        return ResultGenerator.genSuccessResult(data);
    }
    @CrossOrigin(origins = "*")
    @GetMapping("/getProjectByPid")
    public Result getProjectByPid(@RequestParam String pbl_token,
                                @RequestParam Integer p_id) {
        JSONObject data = new JSONObject();

        data.put("project",projectService.findById(p_id));

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
            return ResultGenerator.genFailResult("???????????????????????????").setCode(ResultCode.DENY);
        }

        Project projectObj = JSON.parseObject(project,Project.class);
        List<GradeSystem> gradeSystems = JSON.parseArray(grades,GradeSystem.class);
        int p_id = projectService.addProject(projectObj,gradeSystems);
        JSONObject data = new JSONObject();
        data.put("p_id",p_id);

        return ResultGenerator.genSuccessResult(data);
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/joinProject")
    public Result joinProject(@RequestParam String pbl_token,
                              @RequestParam String p_id) {
        String s_id = userService.getUIdByToken(pbl_token);
        projectService.joinProject(Integer.parseInt(p_id),s_id);
        return ResultGenerator.genSuccessResult().setMessage("?????????????????????");
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping("/deleteProject")
    public Result deleteProject(@RequestParam String pbl_token,
                                @RequestParam String p_id) {
        String t_id = userService.getUIdByToken(pbl_token);
        User user = userService.searchUser(t_id);
        if(!user.getType().equals("admin")&&!user.getType().equals("teacher")){
            return ResultGenerator.genFailResult("???????????????????????????").setCode(ResultCode.DENY);
        }
        projectService.deletProject(Integer.parseInt(p_id));
        return ResultGenerator.genSuccessResult("????????????");
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/changeProject")
    public Result changeProject(@RequestParam String pbl_token,
                                @RequestParam String project,
                                @RequestParam String grades) {
        String t_id = userService.getUIdByToken(pbl_token);
        User user = userService.searchUser(t_id);
        if(!user.getType().equals("admin")){
            return ResultGenerator.genFailResult("?????????????????????????????????").setCode(ResultCode.DENY);
        }
        Project projectObj = JSON.parseObject(project,Project.class);
        List<GradeSystem> gradeSystems = JSON.parseArray(grades,GradeSystem.class);
        projectService.changeProject(projectObj,gradeSystems);
        return ResultGenerator.genSuccessResult().setMessage("????????????");
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/searchProjectGradeSystem")
    public Result searchProjectGradeSystem(@RequestParam String pbl_token,
                                           @RequestParam String p_id) {
        JSONObject data = new JSONObject();
        data.put("grades",projectService.searchGradeSystem(Integer.parseInt(p_id)));
        return ResultGenerator.genSuccessResult(data);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/searchAssignment")
    public Result searchAssignment(@RequestParam String pbl_token,
                                   @RequestParam String p_id) {
        String u_id = userService.getUIdByToken(pbl_token);
        int p_idInt = Integer.parseInt(p_id);
        JSONObject data = new JSONObject();
        data.put("assignments",assignmentService.searchAssignment(p_idInt));
        data.put("studentStatus",assignmentService.searchDoneStatus(p_idInt,u_id));
        data.put("urgeStatus",assignmentService.searchAssignmentUrge(p_idInt,u_id));
        data.put("doneNum",assignmentService.searchAllAssignmentsDoneNum(p_idInt));
        data.put("totalNum",projectService.searchTotalNum(p_idInt));
        return ResultGenerator.genSuccessResult(data);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/searchGrouperAssignment")
    public Result searchGrouperAssignment(@RequestParam String pbl_token,
                                          @RequestParam String p_id,
                                          @RequestParam String grouper_id) {
        return ResultGenerator.genSuccessResult(assignmentService.searchDoneStatus(Integer.parseInt(p_id),grouper_id)).setMessage("????????????");
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/searchAssignmentDone")
    public Result searchAssignmentDone(@RequestParam String pbl_token,
                                       @RequestParam String p_id,
                                       @RequestParam String a_id) {
        JSONObject data = new JSONObject();
        int p_idInt = Integer.parseInt(p_id);
        int a_idInt = Integer.parseInt(a_id);
        data.put("assignments",assignmentService.searchAssignmentDoneNum(p_idInt,a_idInt));
        data.put("totalNum",projectService.searchTotalNum(p_idInt));

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
        return ResultGenerator.genSuccessResult(data).setMessage("??????????????????");
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/createAssignments")
    public Result createAssignments(@RequestParam String pbl_token,
                                   @RequestParam String assignments) {
        List<Assignment> assignmentObjs = JSON.parseArray(assignments,Assignment.class);
        JSONObject data = new JSONObject();
        data.put("a_idList",assignmentService.createAssignments(assignmentObjs));
        return ResultGenerator.genSuccessResult(data).setMessage("????????????????????????");
    }

    @CrossOrigin(origins = "*")
    @PutMapping("/changeAssignment")
    public Result changeAssignment(@RequestParam String pbl_token,
                                   @RequestParam String assignment) {
        Assignment assignmentObj = JSON.parseObject(assignment,Assignment.class);
        assignmentService.changeAssignment(assignmentObj);
        return ResultGenerator.genSuccessResult().setMessage("??????????????????");
    }

    @CrossOrigin(origins = "*")
    @PutMapping("/doneAssignment")
    public Result doneAssignment(@RequestParam String pbl_token,
                                 @RequestParam String a_id,
                                 @RequestParam String p_id) {
        String u_id = userService.getUIdByToken(pbl_token);
        assignmentService.doneAssignment(Integer.parseInt(a_id),Integer.parseInt(p_id),u_id);
        return ResultGenerator.genSuccessResult().setMessage("??????????????????");
    }

    @CrossOrigin(origins = "*")
    @PutMapping("/changeAssignments")
    public Result changeAssignments(@RequestParam String pbl_token,
                                   @RequestParam String assignments) {
        List<Assignment> assignmentObjs = JSON.parseArray(assignments,Assignment.class);
        assignmentService.changeAssignments(assignmentObjs);
        return ResultGenerator.genSuccessResult().setMessage("????????????????????????");
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping("/deleteAssignment")
    public Result deleteAssignment(@RequestParam String pbl_token,
                                   @RequestParam String a_id,
                                   @RequestParam String p_id) {
        assignmentService.deleteAssignment(Integer.parseInt(p_id),Integer.parseInt(a_id));
        return ResultGenerator.genSuccessResult().setMessage("??????????????????");
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping("/deleteAssignments")
    public Result deleteAssignments(@RequestParam String pbl_token,
                                   @RequestParam String a_idList,
                                   @RequestParam String p_id) {
        List<Integer> a_ids = JSON.parseArray(a_idList,Integer.class);
        assignmentService.deleteAssignments(Integer.parseInt(p_id),a_ids);
        return ResultGenerator.genSuccessResult().setMessage("????????????????????????");
    }

    @CrossOrigin(origins = "*")
    @PutMapping("/modifyAssignments")
    public Result modifyAssignments(@RequestParam String pbl_token,
                                    @RequestParam String assignmentList,
                                    @RequestParam String opList){
        List<Assignment> assignments = JSON.parseArray(assignmentList,Assignment.class);
        List<String> ops = JSON.parseArray(opList,String.class);
        for (int i=0;i<assignments.size();i++) {
            if(ops.get(i).equals("modify")){
                assignmentService.changeAssignment(assignments.get(i));
            }else{
                assignmentService.deleteAssignment(assignments.get(i).getP_id(),assignments.get(i).getA_id());
            }
        }
        return ResultGenerator.genSuccessResult().setMessage("????????????????????????");
    }

    @CrossOrigin(origins = "*")
    @PutMapping("/urgeAssignment")
    public Result urgeAssignment(@RequestParam String pbl_token,
                                 @RequestParam String a_id,
                                 @RequestParam String p_id) {
        assignmentService.urgeAssignment(Integer.parseInt(p_id),Integer.parseInt(a_id));
        return ResultGenerator.genSuccessResult().setMessage("????????????????????????");
    }

    @CrossOrigin(origins = "*")
    @PutMapping("/applyUrge")
    public Result applyUrge(@RequestParam String pbl_token,
                            @RequestParam String a_id,
                            @RequestParam String p_id) {
        assignmentService.applyUrge(Integer.parseInt(p_id),Integer.parseInt(a_id),userService.getUIdByToken(pbl_token));
        return ResultGenerator.genSuccessResult().setMessage("????????????????????????");
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/countDone")
    public Result countDone(@RequestParam String pbl_token,
                            @RequestParam String p_id) {
        int doneNum = projectService.countDone(Integer.parseInt(p_id));
        int totalNum = projectService.searchTotalNum(Integer.parseInt(p_id));
        JSONObject data = new JSONObject();
        data.put("done_num",doneNum);
        data.put("total_num",totalNum);
        return ResultGenerator.genSuccessResult(data).setMessage("??????????????????????????????");
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/searchGroupers")
    public Result searchGroupers(@RequestParam String pbl_token,
                                 @RequestParam String p_id) {
        JSONObject data = new JSONObject();
        data.put("groupers",projectService.searchGroupers(Integer.parseInt(p_id)));
        data.put("leader",projectService.searchLeader(Integer.parseInt(p_id)));

        return ResultGenerator.genSuccessResult(data);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/searchEvaluateBySelf")
    public Result searchEvaluateBySelf(@RequestParam String pbl_token,
                                       @RequestParam String p_id) {
        String u_id = userService.getUIdByToken(pbl_token);

        JSONObject data = new JSONObject();
        String grade = evaluationService.searchEvaluateBySelf(Integer.parseInt(p_id),u_id);
        if(grade.equals("-1")){
            return ResultGenerator.genFailResult("????????????").setCode(ResultCode.DENY);
        }

        data.put("grade",grade);
        return ResultGenerator.genSuccessResult(data);
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/evaluateSelf")
    public Result evaluateSelf(@RequestParam String pbl_token,
                               @RequestParam String p_id,
                               @RequestParam String grade){
        String u_id = userService.getUIdByToken(pbl_token);
        evaluationService.evaluate(Integer.parseInt(p_id),u_id,u_id,Double.parseDouble(grade));
        return ResultGenerator.genSuccessResult().setMessage("????????????");
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/searchEvaluateByOther")
    public Result searchEvaluateByOther(@RequestParam String pbl_token,
                                        @RequestParam String p_id) {
        String u_id = userService.getUIdByToken(pbl_token);
        return ResultGenerator.genSuccessResult(evaluationService.searchEvaluateByOther(Integer.parseInt(p_id),u_id));
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/evaluateOther")
    public Result evaluateOther(@RequestParam String pbl_token,
                                @RequestParam String p_id,
                                @RequestParam String u_id,
                                @RequestParam String grade) {
        String s_id = userService.getUIdByToken(pbl_token);
        evaluationService.evaluate(Integer.parseInt(p_id),s_id,u_id,Double.parseDouble(grade));
        return ResultGenerator.genSuccessResult("????????????");
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/getMyEvaluation")
    public Result getMyEvaluation(@RequestParam String pbl_token,
                                @RequestParam String p_id) {
        String s_id = userService.getUIdByToken(pbl_token);
        ArrayList<Map<String, String>> ret = evaluationService.getMyEvaluate(Integer.parseInt(p_id),s_id);
        JSONObject data = new JSONObject();
        data.put("rateMapping",ret);
        return ResultGenerator.genSuccessResult(data);
    }

    //??????????????????????????????????????????
    @CrossOrigin(origins = "*")
    @GetMapping("/searchEvaluateByTeacher")
    public Result searchEvaluateByTeacher(@RequestParam String pbl_token,
                                          @RequestParam String p_id) {
        String s_id = userService.getUIdByToken(pbl_token);
        List<StudentGrade> res = studentGradeService.searchEvaluateByTeacher(Integer.parseInt(p_id),s_id);
        if(res==null){
            return ResultGenerator.genFailResult("???????????????").setCode(ResultCode.DENY);
        }
        JSONObject data = new JSONObject();
        data.put("grades",res);
        return ResultGenerator.genSuccessResult(data);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/searchGrade")
    public Result searchGrade(@RequestParam String pbl_token,
                              @RequestParam String p_id) {
        String s_id = userService.getUIdByToken(pbl_token);
        String grade = studentGradeService.searchGrade(Integer.parseInt(p_id),s_id);
        if(grade==null){
            return ResultGenerator.genFailResult("?????????????????????").setCode(ResultCode.DENY);
        }
        JSONObject data = new JSONObject();
        data.put("grade",grade);
        return ResultGenerator.genSuccessResult(data);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/searchDiscussions")
    public Result searchDiscussions(@RequestParam String pbl_token,
                                    @RequestParam String p_id) {
        JSONObject data = new JSONObject();
        data.put("discussions",discussionService.searchDiscussions(Integer.parseInt(p_id)));
        return ResultGenerator.genSuccessResult(data);
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/createDiscussion")
    public Result createDiscussion(@RequestParam String pbl_token,
                                   @RequestParam String discussion) {
        Discussion discussionObj = JSON.parseObject(discussion,Discussion.class);
        int d_id = discussionService.createDiscussion(discussionObj);
        JSONObject data = new JSONObject();
        data.put("d_id",d_id);
        return ResultGenerator.genSuccessResult(data).setMessage("????????????");
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping("/deleteDiscussion")
    public Result deleteDiscussion(@RequestParam String pbl_token,
                                   @RequestParam String d_id,
                                   @RequestParam String p_id) {
        discussionService.deleteDiscussion(Integer.parseInt(p_id),Integer.parseInt(d_id));
        return ResultGenerator.genSuccessResult().setMessage("????????????");
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/searchReply")
    public Result searchReply(@RequestParam String pbl_token,
                              @RequestParam String d_id) {
        JSONObject data = new JSONObject();
        data.put("replies",replyService.searchReplise(Integer.parseInt(d_id)));
        return ResultGenerator.genSuccessResult(data);
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/createReply")
    public Result createReply(@RequestParam String pbl_token,
                              @RequestParam String reply) {
        Reply replyObj = JSON.parseObject(reply,Reply.class);
        int r_id = replyService.createReply(replyObj);
        JSONObject data = new JSONObject();
        data.put("r_id",r_id);
        return ResultGenerator.genSuccessResult(data);
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping("/deleteReply")
    public Result deleteReply(@RequestParam String pbl_token,
                              @RequestParam String r_id) {
        replyService.deleteReply(Integer.parseInt(r_id));
        return ResultGenerator.genSuccessResult("????????????");
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/searchAllFiles")
    public Result searchAllFiles(@RequestParam String pbl_token,
                                 @RequestParam String p_id) {
        return ResultGenerator.genSuccessResult(fileService.searchFiles(Integer.parseInt(p_id)));
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/uploadFile")
    public Result uploadFile(@RequestParam String pbl_token,
                             @RequestParam String f_name,
                             @RequestParam String p_id,
                             @RequestParam MultipartFile file,
                             @RequestParam String description) {
        File fileObj = new File();
        fileObj.setF_name(f_name);
        fileObj.setP_id(Integer.parseInt(p_id));
        fileObj.setDescription(description);
        fileObj.setU_id(userService.getUIdByToken(pbl_token));

        //????????????????????????
        fileObj=fileService.addFile(fileObj);
        //?????????????????????
        fileService.saveFile(file,fileObj);
        JSONObject data = new JSONObject();
        data.put("file",fileObj);
        return ResultGenerator.genSuccessResult(data).setMessage("??????????????????");
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping("/deleteFile")
    public Result deleteFile(@RequestParam String pbl_token,
                             @RequestParam String f_id,
                             @RequestParam String p_id) {
        fileService.deleteFile(Integer.parseInt(p_id),Integer.parseInt(f_id));
        return ResultGenerator.genSuccessResult().setMessage("????????????");
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/downloadFile")
    public Result downloadFile(@RequestParam String pbl_token,
                               @RequestParam String p_id,
                               @RequestParam String f_id,
                               HttpServletResponse response) {
        File file = fileService.getFile(Integer.parseInt(p_id),Integer.parseInt(f_id));
        if(file==null){
            return ResultGenerator.genFailResult("??????????????????");
        }

        if(!fileService.downloadFile(file,response)){
            return ResultGenerator.genFailResult("?????????????????????");
        }

        return ResultGenerator.genSuccessResult().setMessage("?????????????????????");
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/countAssignmentDone")
    public Result countAssignmentDone(@RequestParam String pbl_token,
                                      @RequestParam String p_id) {
        int totalAssignmentNum = assignmentService.countAssignment(Integer.parseInt(p_id));
        JSONObject data = new JSONObject();
        data.put("totalAssignmentNum",totalAssignmentNum);
        data.put("doneInformations",assignmentService.countAssignmentDone(Integer.parseInt(p_id)));
        return ResultGenerator.genSuccessResult(data).setMessage("????????????");
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/countDiscussion")
    public Result countDiscussion(@RequestParam String pbl_token,
                                  @RequestParam String p_id) {
        JSONObject data = new JSONObject();
        data.put("maxDiscussNum",discussionService.getMaxDiscussionNum(Integer.parseInt(p_id)));
        data.put("discussInformations",discussionService.countDiscussion(Integer.parseInt(p_id)));
        return ResultGenerator.genSuccessResult(data).setMessage("????????????");
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/getGradeItems")
    public Result getGradeItems(@RequestParam String pbl_token,
                                @RequestParam String p_id) {
        JSONObject data = new JSONObject();
        data.put("grades",projectService.searchGradeSystem(Integer.parseInt(p_id)));
        return ResultGenerator.genSuccessResult(data).setMessage("????????????");
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/evaluateByTeacher")
    public Result evaluateByTeacher(@RequestParam String pbl_token,
                                    @RequestParam String grades) {
        String t_id = userService.getUIdByToken(pbl_token);
        User user = userService.searchUser(t_id);
        if(!user.getType().equals("teacher")){
            return ResultGenerator.genFailResult("????????????");
        }
        List<StudentGrade> studentGrades = JSON.parseArray(grades,StudentGrade.class);
        studentGradeService.evaluateByTeacher(studentGrades);
        return ResultGenerator.genSuccessResult().setMessage("????????????");
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/evaluateDone")
    public Result evaluateDone(@RequestParam String pbl_token,
                               @RequestParam String p_id) {
        return ResultGenerator.genSuccessResult(projectService.evaluationDone(Integer.parseInt(p_id))).setMessage("????????????");
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/searchAllUsers")
    public Result searchAllUsers(@RequestParam String pbl_token) {
        String a_id = userService.getUIdByToken(pbl_token);
        User user = userService.searchUser(a_id);
        if(!user.getType().equals("admin")){
            return ResultGenerator.genFailResult("????????????");
        }
        List<String> imgURL = new ArrayList<>();
        List<User> users = userService.getAllUser();
        for (User u:users) {
            imgURL.add(u.getImage());
        }
        JSONObject data = new JSONObject();
        data.put("users",users);
        data.put("images",imgURL);
        return ResultGenerator.genSuccessResult(data);
    }

    @CrossOrigin(origins = "*")
    @PutMapping("/changeImage")
    public Result changeImage(@RequestParam String pbl_token,
                              @RequestParam String u_id,
                              @RequestParam(required = false) MultipartFile image) {
        String a_id = userService.getUIdByToken(pbl_token);
        User user = userService.searchUser(a_id);
        if(!user.getType().equals("admin")){
            return ResultGenerator.genFailResult("????????????");
        }
        userService.deleteImage(user);
        String imgURL = userService.setImage(u_id,image);
        JSONObject data = new JSONObject();
        data.put("image",imgURL);
        return ResultGenerator.genSuccessResult(data).setMessage("????????????");
    }

    @CrossOrigin(origins = "*")
    @PutMapping("/changeInformation")
    public Result changeInformation(@RequestParam String pbl_token,
                                    @RequestParam String user){
        String a_id = userService.getUIdByToken(pbl_token);
        User admin = userService.searchUser(a_id);
        if(!admin.getType().equals("admin")){
            return ResultGenerator.genFailResult("????????????????????????");
        }

        User userObj = JSON.parseObject(user,User.class);
        userService.setUser(userObj);
        return ResultGenerator.genSuccessResult().setMessage("????????????????????????");
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/addUser")
    public Result addUser(@RequestParam String pbl_token,
                          @RequestParam String user,
                          @RequestParam(required = false) MultipartFile image) {
        String a_id = userService.getUIdByToken(pbl_token);
        User admin = userService.searchUser(a_id);
        if(!admin.getType().equals("admin")){
            return ResultGenerator.genFailResult("??????????????????");
        }
        User userObj = JSON.parseObject(user,User.class);

        //???????????????????????????
        if(userService.idExist(userObj.getU_id())){
            return ResultGenerator.genFailResult("??????????????????");
        }
        userService.register(userObj);
        String imgURL = userService.setImage(userObj.getU_id(),image);
        JSONObject data = new JSONObject();
        data.put("image",imgURL);
        return ResultGenerator.genSuccessResult().setMessage("???????????????");
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/setTeacherGrade")
    public Result setTeacherGrade(@RequestParam String pbl_token,
                                  @RequestParam String student_grade) {
        String s_id = userService.getUIdByToken(pbl_token);
        User user = userService.searchUser(s_id);
        if(user.getType().equals("student")){
            return ResultGenerator.genFailResult("??????????????????????????????").setCode(ResultCode.DENY);
        }
        StudentGrade studentGradeObj = JSON.parseObject(student_grade,StudentGrade.class);
        studentGradeService.update(studentGradeObj);
        return ResultGenerator.genSuccessResult().setMessage("???????????????");
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/getItemsByPid")
    public Result getItemsByPid(@RequestParam String pbl_token,
                                  @RequestParam String p_id) {
        String s_id = userService.getUIdByToken(pbl_token);
        User user = userService.searchUser(s_id);
        if(user.getType().equals("student")){
            return ResultGenerator.genFailResult("??????items????????????").setCode(ResultCode.DENY);
        }
        ArrayList<Map<String, Object>> ret = studentGradeService.searchEvaluateByPid(Integer.parseInt(p_id));
        JSONObject data = new JSONObject();
        data.put("allItems",ret);
        return ResultGenerator.genSuccessResult(data);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/SelfAndMutualevaluateScore")
    public Result getSelfAndMutualGrade(@RequestParam String pbl_token,
                                        @RequestParam String p_id) {
        String s_id = userService.getUIdByToken(pbl_token);
        User user = userService.searchUser(s_id);
        if(user.getType().equals("student")){
            return ResultGenerator.genFailResult("?????????????????????????????????").setCode(ResultCode.DENY);
        }
        ArrayList<Map<String,Object>> ret = projectService.getSelfAndMutualGradeByPid(Integer.parseInt(p_id));
        JSONObject data = new JSONObject();
        data.put("selfAndMutualInformations",ret);
        return ResultGenerator.genSuccessResult(data);
    }

    @CrossOrigin(origins = "*")
    @PutMapping("/updateTeacherGrade")
    public Result updateTeacherGrade(@RequestParam String pbl_token,
                                    @RequestParam String student_project_list){
        String a_id = userService.getUIdByToken(pbl_token);
        User teacher = userService.searchUser(a_id);
        if(!teacher.getType().equals("teacher")){
            return ResultGenerator.genFailResult("???????????????????????????????????????");
        }
        List<StudentProject> studentProjectsObj = JSON.parseArray(student_project_list,StudentProject.class);
        for (StudentProject s : studentProjectsObj){
            projectService.updateTeacherGrade(s);
        }
        projectService.updateProjectGradeStatus(studentProjectsObj.get(0).getP_id());
        return ResultGenerator.genSuccessResult().setMessage("????????????????????????");
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/getStudentDiscussonAndAssignmentCountByPid")
    public Result getStudentDiscussonCountByPid(@RequestParam String pbl_token,
                                @RequestParam String p_id) {
        String s_id = userService.getUIdByToken(pbl_token);
        int discussionCount = discussionService.getDiscussionAndReplyCount(Integer.parseInt(p_id),s_id);
        int assignmentDoneCount = assignmentService.countAssignmentDoneByUidAndPid(Integer.parseInt(p_id),s_id);
        JSONObject data = new JSONObject();
        data.put("discussionCount",discussionCount);
        data.put("assignmentDoneCount",assignmentDoneCount);
        return ResultGenerator.genSuccessResult(data);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/getUserByUid")
    public Result getUserByUid(@RequestParam String pbl_token,
                               @RequestParam String u_id) {
        JSONObject data = new JSONObject();
        data.put("user",userService.findById(u_id));
        return ResultGenerator.genSuccessResult(data);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/getAllProjects")
    public Result getAllProjects(@RequestParam String pbl_token) {
        String a_id = userService.getUIdByToken(pbl_token);
        User admin = userService.searchUser(a_id);
        if(!admin.getType().equals("admin")){
            return ResultGenerator.genFailResult("?????????????????????????????????");
        }
        JSONObject data = new JSONObject();
        data.put("projectList",projectService.findAll());
        return ResultGenerator.genSuccessResult(data);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/getAllCourses")
    public Result getAllCourses(@RequestParam String pbl_token) {
        String a_id = userService.getUIdByToken(pbl_token);
        User admin = userService.searchUser(a_id);
        if(!admin.getType().equals("admin")){
            return ResultGenerator.genFailResult("?????????????????????????????????");
        }
        JSONObject data = new JSONObject();
        data.put("courseList",courseService.findAll());
        return ResultGenerator.genSuccessResult(data);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/getAllGradeItems")
    public Result getAllGradeItems(@RequestParam String pbl_token) {
        String a_id = userService.getUIdByToken(pbl_token);
        User admin = userService.searchUser(a_id);
        if(!admin.getType().equals("admin")){
            return ResultGenerator.genFailResult("???????????????????????????????????????");
        }
        JSONObject data = new JSONObject();
        data.put("itemList",JSON.toJSON(studentGradeService.getAllGradeItems()));
        return ResultGenerator.genSuccessResult(data);
    }
}
