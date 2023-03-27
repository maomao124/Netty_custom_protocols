package mao.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mao.config.ServerConfig;
import mao.message.HelloRequestMessage;
import mao.message.HelloResponseMessage;
import mao.message.PingMessage;
import mao.message.PongMessage;
import mao.protocol.MessageCodecSharable;

import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.LockSupport;

/**
 * Project name(项目名称)：Netty_自定义协议
 * Package(包名): mao.client
 * Class(类名): StressTestClient
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2023/3/26
 * Time(创建时间)： 22:39
 * Version(版本): 1.0
 * Description(描述)： 压力测试，线程多了会触发full GC ，失败品
 */

@Slf4j
public class StressTestClient
{
    private static Thread[] threads;
    private static Channel[] channels;

    public static void main(String[] args)
    {
        Scanner input = new Scanner(System.in);
        System.out.print("线程数：");
        int threadNumber = input.nextInt();
        if (threadNumber <= 0)
        {
            threadNumber = 1;
        }
        threads = new Thread[threadNumber];
        channels = new Channel[threadNumber];
        start(threadNumber);
    }

    @SneakyThrows
    private static void start(int threadNumber)
    {
        CountDownLatch countDownLatch = new CountDownLatch(threadNumber);
        for (int i = 0; i < threadNumber; i++)
        {
            NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup(threadNumber);
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(nioEventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>()
                    {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception
                        {

                            ch.pipeline()
                                    .addLast(new LoggingHandler(LogLevel.WARN))
                                    .addLast(new MessageCodecSharable())
                                    .addLast(new SimpleChannelInboundHandler<PongMessage>()
                                    {
                                        @Override
                                        protected void channelRead0(ChannelHandlerContext ctx,
                                                                    PongMessage pongMessage)
                                                throws Exception
                                        {
                                            try
                                            {
                                                log.info("得到服务器ping响应");
                                                log.debug(pongMessage.toString());
                                                long start = pongMessage.getTime();
                                                long end = System.currentTimeMillis();
                                                log.info("延时：" + (end - start) + "毫秒");
                                            }
                                            finally
                                            {
                                                //LockSupport.unpark(thread);
                                            }
                                        }
                                    })
                                    .addLast(new SimpleChannelInboundHandler<HelloResponseMessage>()
                                    {
                                        @Override
                                        protected void channelRead0(ChannelHandlerContext ctx,
                                                                    HelloResponseMessage helloResponseMessage)
                                                throws Exception
                                        {
                                            try
                                            {
                                                log.info("得到服务器打招呼响应");
                                                log.info(helloResponseMessage.toString());
                                            }
                                            finally
                                            {
                                                //LockSupport.unpark(thread);
                                            }
                                        }
                                    });
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect(new
                    InetSocketAddress("127.0.0.1", ServerConfig.getServerPort()));
            Channel channel = channelFuture.channel();
            channels[i] = channelFuture.channel();


            int finalI = i;
            channelFuture.addListener(new GenericFutureListener<Future<? super Void>>()
            {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception
                {
                    if (future.isSuccess())
                    {
                        log.debug("客户端连接成功:" + finalI);
                        countDownLatch.countDown();
                    }
                    else
                    {
                        log.error("启动失败：" + future.cause().getMessage());
                        countDownLatch.countDown();
                    }
                }
            });

            channel.closeFuture().addListener(new GenericFutureListener<Future<? super Void>>()
            {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception
                {
                    log.info("关闭客户端");
                    nioEventLoopGroup.shutdownGracefully();
                }
            });

        }

        for (int i = 0; i < threadNumber; i++)
        {
            int finalI = i;
            threads[i] = new Thread(new Runnable()
            {
                @SneakyThrows
                @Override
                public void run()
                {
                    while (true)
                    {
                        log.debug("发送ping消息");
                        PingMessage pingMessage = new PingMessage();
                        pingMessage.setTime(System.currentTimeMillis());
                        channels[finalI].writeAndFlush(pingMessage);
                    }
                }
            }, "input-" + (i + 1));

        }

        countDownLatch.await();

        for (Thread thread : threads)
        {
            thread.start();
        }
    }
}
