package com.crazymakercircle.mutithread.basic.create;

import com.crazymakercircle.util.Print;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.crazymakercircle.util.ThreadUtil.getCurThreadName;
import static com.crazymakercircle.util.ThreadUtil.sleepMilliSeconds;
import static com.crazymakercircle.util.ThreadUtil.sleepSeconds;

/**
 * Created by 尼恩@疯狂创客圈.
 */

public class CreateDemo4
{

    //创建一个包含三个线程的线程池
    private static ExecutorService pool = Executors.newFixedThreadPool(3);

    public static void main(String[] args) throws ExecutionException, InterruptedException
    {

        pool.execute(new DemoThread()); //执行线程实例
        //执行Runnable执行目标实例
        pool.execute(new Runnable()
        {
            @Override
            public void run()
            {
                for (int j = 1; j < MAX_TURN; j++)
                {
                    Print.cfo(getCurThreadName() + ", 轮次：" + j);
                    sleepMilliSeconds(10);
                }
            }
        });
        //提交Callable 执行目标实例
        Future future = pool.submit(new ReturnableTask());
        Long result = (Long) future.get();
        Print.cfo("异步任务的执行结果为：" + result);

        sleepSeconds(Integer.MAX_VALUE);

    }


    static class DemoThread implements Runnable
    {

        @Override

        public void run()
        {

            for (int j = 1; j < MAX_TURN; j++)
            {
                Print.cfo(getCurThreadName() + ", 轮次：" + j);
                sleepMilliSeconds(10);
            }
        }
    }

    public static final int MAX_TURN = 5;
    public static final int COMPUTE_TIMES = 100000000;


    static class ReturnableTask implements Callable<Long>
    {
        //返回并发执行的时间
        public Long call() throws Exception
        {
            long startTime = System.currentTimeMillis();
            Print.cfo(getCurThreadName() + " 线程运行开始.");
            for (int j = 1; j < MAX_TURN; j++)
            {
                Print.cfo(getCurThreadName() + ", 轮次：" + j);
                sleepMilliSeconds(10);
            }
            long used = System.currentTimeMillis() - startTime;
            Print.cfo(getCurThreadName() + " 线程运行结束.");
            return used;
        }
    }


}