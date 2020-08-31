package com.xuecheng.order.task;

import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.order.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@Component
public class ChooseCourseTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChooseCourseTask.class);

    @Autowired
    private TaskService taskService;


    /**
     * cron 表达式
     * 0/3 * * * * * 每隔3秒执行
     * 0 0/5 * * * * 每隔5分钟执行
     * 0 0 0 * * * 表示每天0点执行
     * 0 0 12 ? * WEN 每周三12点执行
     * 0 15 10 ? * MON-FRI
     * 每月的周一到周五10点 15分执行
     * 0 15 10 ? * MON,FRI
     * 每月的周一和周五10点 15分执行
     */

    @Scheduled(fixedRate = 5000) //上次执行开始时间后5秒执行
    @Scheduled(fixedRate = 50000) // 上次执行完毕后5秒执行
    @Scheduled(initialDelay = 3000,fixedRate = 5000) //第一次执行延迟三秒，以后每隔5秒执行一次
    @Scheduled(cron = "0/3 * * * * *")
    public void task1(){
        LOGGER.info("测试任务开始----------");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.info("测试任务结束..........");
    }

    //定时发送加选课任务
    @Scheduled(cron="0/3 * * * * *")
    public void sendChoosecourseTask(){
        //得到1分钟之前的时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.set(GregorianCalendar.MINUTE,-1);
        Date time = calendar.getTime();
        List<XcTask> xcTaskList = taskService.findTaskList(time, 100);
        System.out.println(xcTaskList);
        //调用service发布消息，将添加选课的任务发送给mq
        for(XcTask xcTask:xcTaskList){
            //取任务
            if(taskService.getTask(xcTask.getId(),xcTask.getVersion())>0){
                String ex = xcTask.getMqExchange();//要发送的交换机
                String routingKey = xcTask.getMqRoutingkey();//发送消息要带routingKey
                taskService.publish(xcTask,ex,routingKey);
            }

        }
    }

}
