package mao.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import mao.message.HelloRequestMessage;
import mao.message.PingMessage;
import mao.protocol.MessageCodecSharable;
import mao.protocol.ProcotolFrameDecoder;

/**
 * Project name(项目名称)：Netty_自定义协议
 * Package(包名): mao.server
 * Class(类名): Server
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2023/3/26
 * Time(创建时间)： 14:53
 * Version(版本): 1.0
 * Description(描述)： 无
 */
@Slf4j
public class Server
{
    public static void main(String[] args)
    {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try
        {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.group(boss, worker);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>()
            {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception
                {
                    ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                    ch.pipeline().addLast(new ProcotolFrameDecoder());
                    ch.pipeline().addLast(new MessageCodecSharable());
                    ch.pipeline().addLast(new SimpleChannelInboundHandler<PingMessage>()
                    {
                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, PingMessage msg) throws Exception
                        {

                        }
                    });

                    ch.pipeline().addLast(new SimpleChannelInboundHandler<HelloRequestMessage>()
                    {

                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, HelloRequestMessage msg) throws Exception
                        {

                        }
                    });
                }
            });
            Channel channel = serverBootstrap.bind(8080).sync().channel();
            channel.closeFuture().sync();
        }
        catch (InterruptedException e)
        {
            log.error("server error", e);
        }
        finally
        {
            log.debug("关闭服务");
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
