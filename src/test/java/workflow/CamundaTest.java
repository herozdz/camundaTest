package workflow;

import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

/**
 * Created with IntelliJ IDEA.
 * User: zoudezhu
 * Date: 2018/8/24
 * Time: 15:00
 * To change this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring/spring-config.xml"})
@ActiveProfiles(profiles = "test")
public class CamundaTest {

    @Autowired
    private RuntimeService runtimeService;

    @Resource
    private TaskService taskService;

    @Resource
    private RepositoryService repositoryService;

    @Resource
    private ProcessEngine  processEngine;

    @Resource
    private HistoryService  historyService;


    @Test
    //step.1 启动工作流
    public void  testCamundaWork(){
        //Gson gson =
        org.camunda.bpm.engine.runtime.ProcessInstance processInstance =
                runtimeService.startProcessInstanceByKey("test_process001");

        /**实例id*/
       String processInstanceId =  processInstance.getId();

        //ServiceTask_1

        //taskService.complete(task.getId());

       // long count = runtimeService.createProcessInstanceQuery().count();
        /*List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("").list();
        for (Task task : tasks) {
            System.out.println("Task available: " + task.getName());
        }*/

    }

    @Test
    //任务列表
    //step.2 查看任务列表
    public void testListTask(){
        List<Task> tasks = taskService.createTaskQuery().taskCandidateUser("zoudezhu").list();
        //List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("accountancy").list();
        for(Task task:tasks){
            System.out.println("task:"+task.toString());
            //task:Task[2104]
        }
    }

    @Test
    //step.3 认领任务
    public void testClaimTask(){
        taskService.claim("2902","zdz");
        //领完任务 list() 方法就查不到 taskId - 2104 的任务了
    }

    @Test
    //step.4 认领列表
    public void testClaimList(){
        List<Task> tasks = taskService.createTaskQuery().taskAssignee("zdz").list();
        for(Task task:tasks){
            System.out.println("Claim task:"+task.toString());
            //Claim task:Task[2104]
        }
    }
    @Test
    //step.5 结束任务
    public void testCompleteTask(){
       taskService.complete("2902");
    }

    @Test
    //step.6 查看历史任务
    public void testListCompletedTask(){
        HistoricProcessInstance historicProcessInstance =  historyService.createHistoricProcessInstanceQuery()
                .processInstanceId("2101").singleResult();
        System.out.println("Process instance end time: " + historicProcessInstance.getEndTime());
    }

    @Test
    // test all process
    public void testAllProcess(){
        String processId = runtimeService.startProcessInstanceByKey("test_process001").getId();

        /**第一个任务节点处理**/
        List<Task> tasks = taskService.createTaskQuery().taskCandidateUser("zoudezhu").list();
        for(Task task:tasks){
            System.out.println("Following task is available for user zoudezhu: " + task.getName());
            taskService.claim(task.getId(),"zoudezhu");
        }

        tasks = taskService.createTaskQuery().taskAssignee("zoudezhu").list();
        /**完成第一个节点任务*/
        for (Task task: tasks){
            taskService.complete(task.getId());
        }

        System.out.println("Number of tasks for zoudezhu: "
                + taskService.createTaskQuery().taskAssignee("zoudezhu").count());

        /**第二个任务节点处理**/
        tasks = taskService.createTaskQuery().taskCandidateUser("zdz").list();
        for(Task task:tasks){
            System.out.println("Following task is available for user zdz: " + task.getName());
            taskService.claim(task.getId(),"zdz");
        }
        /**完成第二个任务*/
        for(Task task:tasks){
            taskService.complete(task.getId());
        }

        /**
         * 查看整个流程
         * */
        // verify that the process is actually finished
        HistoryService historyService = processEngine.getHistoryService();
        HistoricProcessInstance historicProcessInstance =
                historyService.createHistoricProcessInstanceQuery().processInstanceId(processId).singleResult();
        System.out.println("Process instance end time: " + historicProcessInstance.getEndTime());

    }




    @Test
    public void  testDeploy(){
        String barFileName = "D:\\\\program\\\\loanapprovalspring\\\\src\\\\main\\\\resources\\\\diagram_5.zip";
        ZipInputStream inputStream = null;
        try {
            inputStream = new ZipInputStream(new FileInputStream(barFileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String deployId = repositoryService.createDeployment()
                .name("diagram_5")
                .addZipInputStream(inputStream)
                .deploy().getId();

        System.out.println("deployId--->:"+deployId);

        /**获取图片*/
        /*ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("loanApproval")
                .singleResult();

        String diagramResourceName = processDefinition.getDiagramResourceName();
        InputStream imageStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), diagramResourceName);*/
    }
      /*  String deployId = repositoryService.createDeployment()
                .addClasspathResource("D:\\program\\loanapprovalspring\\src\\main\\resources\\diagram_3.bpmn")
                .deploy().getId();

        System.out.println("Number of process definitions: "
                + repositoryService.createProcessDefinitionQuery().count());

    }*/
}
