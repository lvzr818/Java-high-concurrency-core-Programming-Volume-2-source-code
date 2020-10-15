package com.crazymakercircle.mutithread.basic.threadlocal;

import com.crazymakercircle.util.Print;
import com.crazymakercircle.util.ThreadUtil;
import lombok.Data;
import org.junit.Test;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import static com.crazymakercircle.util.ThreadUtil.sleepMilliSeconds;

public class ThreadLocalTest
{
    @Data
    static class Foo
    {
        //实例总数
        static final AtomicInteger AMOUNT = new AtomicInteger(0);
        //对象的编号
        int index = 0;
        //对象的内容
        int bar = 10;

        //构造器
        public Foo()
        {
            index = AMOUNT.incrementAndGet(); //总数增加，并且给对象的编号
        }

        @Override
        public String toString()
        {
            return index + "@Foo{bar=" + bar + '}';
        }
    }

    //定义线程本地变量
    private static final ThreadLocal<Foo> LOCAL_FOO = new ThreadLocal<Foo>();

    public static void main(String[] args) throws InterruptedException
    {
//        ThreadLocal<Foo> localFoo = ThreadLocal.withInitial(() -> new Foo());

        //获取自定义的混合线程池
        ThreadPoolExecutor threadPool = ThreadUtil.getMixedTargetThreadPool();

        //共5个线程
        for (int i = 0; i < 5; i++)
        {
            threadPool.execute(new Runnable()
            {
                @Override
                public void run()
                {
                    //获取“线程本地变量”中当前线程所绑定的值
                    if (LOCAL_FOO.get() == null)
                    {
                        //设置“线程本地变量”中当前线程所绑定的值
                        LOCAL_FOO.set(new Foo());
                    }

                    Print.tco("初始的本地值：" + LOCAL_FOO.get());
                    //每个线程执行10次
                    for (int i = 0; i < 10; i++)
                    {
                        Foo foo = LOCAL_FOO.get();
                        foo.setBar(foo.getBar() + 1);
                        sleepMilliSeconds(10);

                    }
                    Print.tco("累加10次之后的本地值：" + LOCAL_FOO.get());

                    //删除“线程本地变量”中当前线程所绑定的值，对于线程池中的线程尤其重要
                    LOCAL_FOO.remove();
                }
            });
        }
    }

    static class LeakFoo extends Foo
    {

        private final Byte[] toLeak;

        public LeakFoo()
        {
            super();
            toLeak = new Byte[1024 * 1024];
        }

        @Override
        protected void finalize()
        {
            Print.tco(super.toString());

        }
    }

    @Test
    public void testLeak() throws InterruptedException
    {
        //获取自定义的混合线程池
        ThreadPoolExecutor threadPool = ThreadUtil.getMixedTargetThreadPool();
        //共1000个任务
        for (int i = 0; i < 1000; i++)
        {
            threadPool.submit(new Runnable()
            {

                @Override
                public void run()
                {

                    //每个任务执行1000次
                    for (int i = 0; i < 1000; i++)
                    {
                        ThreadLocal<LeakFoo> localFoo = new ThreadLocal<LeakFoo>();
                        if (null == localFoo.get())
                        {
                            localFoo.set(new LeakFoo());
                        }
                        LeakFoo foo = localFoo.get();
                    }
                }
            });
        }
    }


}