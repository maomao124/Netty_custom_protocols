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
import java.util.concurrent.locks.LockSupport;

/**
 * Project name(项目名称)：Netty_自定义协议
 * Package(包名): mao.client
 * Class(类名): Client
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2023/3/26
 * Time(创建时间)： 21:55
 * Version(版本): 1.0
 * Description(描述)： 客户端
 */

@Slf4j
public class Client
{

    private static Thread thread;

    public static void main(String[] args)
    {
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup(2);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(nioEventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>()
                {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception
                    {

                        ch.pipeline()
                                .addLast(new LoggingHandler(LogLevel.DEBUG))
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
                                            LockSupport.unpark(thread);
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
                                            LockSupport.unpark(thread);
                                        }
                                    }
                                });
                    }
                });
        ChannelFuture channelFuture = bootstrap.connect(new
                InetSocketAddress("127.0.0.1", ServerConfig.getServerPort()));
        Channel channel = channelFuture.channel();

        thread = new Thread(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                Scanner input = new Scanner(System.in);
                while (true)
                {
                    System.out.println("---------------");
                    System.out.println("1.ping");
                    System.out.println("2.和服务器打招呼");
                    System.out.println("---------------");
                    System.out.print("请输入序号:");
                    String num = input.next();
                    if ("1".equals(num))
                    {
                        log.debug("发送ping消息");
                        PingMessage pingMessage = new PingMessage();
                        pingMessage.setTime(System.currentTimeMillis());
                        channel.writeAndFlush(pingMessage);
                    }
                    else if ("2".equals(num))
                    {
                        System.out.print("请输入您的姓名：");
                        String name = input.next();
                        System.out.println("请输入内容：");
                        String body = input.next();
                        log.debug("发送打招呼消息");
                        HelloRequestMessage helloRequestMessage = new HelloRequestMessage();
                        helloRequestMessage.setName(name);
                        helloRequestMessage.setBody(body);
                        //发送
                        channel.writeAndFlush(helloRequestMessage);
                    }
                    else
                    {
                        continue;
                    }
                    LockSupport.park();
                    Thread.sleep(100);
                }
            }
        }, "input");

        channelFuture.addListener(new GenericFutureListener<Future<? super Void>>()
        {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception
            {
                if (future.isSuccess())
                {
                    log.debug("客户端连接成功");
                    thread.start();
                }
                else
                {
                    log.error("启动失败：" + future.cause().getMessage());
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
}
