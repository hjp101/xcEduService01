package com.xuecheng.manage_media_process;

import com.xuecheng.framework.utils.Mp4VideoUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.Temporal;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 * @version 1.0
 * @create 2018-07-12 9:11
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestProcessBuilder {

    @Test
    public void testProcessBuilder() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("ping","127.0.0.1");
        //将标准输入流和错误输入流合并，并通过标准输入流读取信息
        processBuilder.redirectErrorStream(true);
        //启动进程
        Process start = processBuilder.start();
        //获取输入流
        InputStream inputStream = start.getInputStream();
        //转换成字符输入流
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "gbk");
        int len = -1;
        char[] c = new char[1024];
        StringBuffer outputString = new StringBuffer();
        //读取进程输入流中的内容
        while ((len = inputStreamReader.read(c)) != -1){
            String s =  new String(c,0,len);
            outputString.append(s);
            System.out.println(s);
        }
        inputStreamReader.close();

    }

    @Test
    public void testFFmpeg(){
        ProcessBuilder processBuilder = new ProcessBuilder();
        //定义命令内容
        List<String> command = new ArrayList<>();
        command.add("D:\\Program Files\\ffmpeg-20180227-fa0c9d6-win64-static\\bin\\ffmpeg.exe");
        command.add("-i");
        command.add("E:\\19 微服务项目【学成在线】·\\day14 媒资管理\\资料\\solr.avi");
        command.add("-y");//覆盖输出文件
        command.add("-c:v");
        command.add("libx264");
        command.add("-s");
        command.add("1280x720");
        command.add("-pix_fmt");
        command.add("yuv420p");
        command.add("-b:a");
        command.add("63k");
        command.add("-b:v");
        command.add("753k");
        command.add("-r");
        command.add("18");
        command.add("E:\\19 微服务项目【学成在线】·\\day14 媒资管理\\资料\\test\\1.mp4");
        processBuilder.command(command);
        //将标准输入流和错误输入流合并
        processBuilder.redirectErrorStream(true);
        try {
            //启动进程
            Process start = processBuilder.start();
            //获取输入流
            InputStream inputStream = start.getInputStream();
            //转成字符输入流
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream,"gbk");
            int len = -1;
            char[] c = new char[1024];
            StringBuffer outStringBuffer = new StringBuffer();
            //读取进程输入流中的内容
            while ((len = inputStreamReader.read(c))!= -1){
                String s = new String(c,0,len);
                outStringBuffer.append(s);
                System.out.println(s);
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void  testUtils(){
        //ffmpeg路径
        String ffmpeg_path = "D:\\Program Files\\ffmpeg-20180227-fa0c9d6-win64-static\\bin\\ffmpeg.exe";
        //源路径
        String video_path = "E:\\testVedio\\solr.avi";
        //转换成mp4之后的文件名
        String mp4_name = "2.mp4";
        //转换之后的路径
        String mp4_path = "E:\\testVedio\\";
        //创建工具类对象
        Mp4VideoUtil videoUtil = new Mp4VideoUtil(ffmpeg_path,video_path,mp4_name,mp4_path);
        //开始视频转换
        String s = videoUtil.generateMp4();
        System.out.println(s);

    }


}
