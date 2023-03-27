

## 自定义协议

### 协议要素

* 魔数，用来在第一时间判定是否是无效数据包
* 版本号，可以支持协议的升级
* 序列化算法，消息正文到底采用哪种序列化反序列化方式，可以由此扩展，例如：json、protobuf、hessian、jdk
* 指令类型，是登录、注册、单聊、群聊... 跟业务相关
* 请求序号，为了双工通信，提供异步能力
* 正文长度
* 消息正文





### 协议父类消息

```java
package mao.message;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Project name(项目名称)：Netty_自定义协议
 * Package(包名): mao.message
 * Class(类名): Message
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2023/3/26
 * Time(创建时间)： 14:05
 * Version(版本): 1.0
 * Description(描述)： 协议父类消息
 */

@Data
public abstract class Message implements Serializable
{
    /**
     * 根据消息类型字节，获得对应的消息 class
     *
     * @param messageType 消息类型字节
     * @return 消息 class
     */
    public static Class<? extends Message> getMessageClass(int messageType)
    {
        return messageClasses.get(messageType);
    }

    /**
     * 序列id
     */
    private int sequenceId;

    /**
     * 消息类型
     */
    private int messageType;

    /**
     * 得到消息类型
     *
     * @return int
     */
    public abstract int getMessageType();

    public static final int PingMessage = 1;
    public static final int PongMessage = 2;
    public static final int HelloRequestMessage = 3;
    public static final int HelloResponseMessage = 4;


    /**
     * 请求类型 byte 值
     */
    public static final int RPC_MESSAGE_TYPE_REQUEST = 101;

    /**
     * 响应类型 byte 值
     */
    public static final int RPC_MESSAGE_TYPE_RESPONSE = 102;

    /**
     * 消息类
     */
    private static final Map<Integer, Class<? extends Message>> messageClasses = new HashMap<>();

    static
    {
        messageClasses.put(PingMessage, PingMessage.class);
        messageClasses.put(PongMessage, PongMessage.class);
        messageClasses.put(HelloRequestMessage, HelloRequestMessage.class);
        messageClasses.put(HelloResponseMessage, HelloResponseMessage.class);
    }
}
```





### 抽象响应消息

```java
package mao.message;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Project name(项目名称)：Netty_自定义协议
 * Package(包名): mao.message
 * Class(类名): AbstractResponseMessage
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2023/3/26
 * Time(创建时间)： 14:12
 * Version(版本): 1.0
 * Description(描述)： 抽象响应消息
 */


@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class AbstractResponseMessage extends Message
{
    /**
     * 是否是成功的消息
     */
    private boolean success;

    /**
     * 如果失败，失败的原因
     */
    private String reason;

    public AbstractResponseMessage()
    {
    }

    public AbstractResponseMessage(boolean success, String reason)
    {
        this.success = success;
        this.reason = reason;
    }
}
```





### ping消息

```java
package mao.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Project name(项目名称)：Netty_自定义协议
 * Package(包名): mao.message
 * Class(类名): PingMessage
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2023/3/26
 * Time(创建时间)： 14:17
 * Version(版本): 1.0
 * Description(描述)： ping消息
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class PingMessage extends Message
{
    /**
     * 时间
     */
    private long time;

    @Override
    public int getMessageType()
    {
        return PingMessage;
    }
}
```





###  pong消息

```java
package mao.message;

import lombok.Data;
import lombok.EqualsAndHashCode;
import mao.protocol.SequenceIdGenerator;

/**
 * Project name(项目名称)：Netty_自定义协议
 * Package(包名): mao.message
 * Class(类名): PongMessage
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2023/3/26
 * Time(创建时间)： 14:18
 * Version(版本): 1.0
 * Description(描述)： pong 消息
 */


@Data
@EqualsAndHashCode(callSuper = true)
public class PongMessage extends Message
{
    /**
     * 请求时间
     */
    private long time;

    public PongMessage(int time)
    {
        this.time = time;
        setSequenceId(SequenceIdGenerator.nextId());
    }

    public PongMessage()
    {
        setSequenceId(SequenceIdGenerator.nextId());
    }

    @Override
    public int getMessageType()
    {
        return PongMessage;
    }
}
```





### 打招呼的请求消息

```java
package mao.message;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Project name(项目名称)：Netty_自定义协议
 * Package(包名): mao.message
 * Class(类名): HelloRequestMessage
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2023/3/26
 * Time(创建时间)： 14:20
 * Version(版本): 1.0
 * Description(描述)： 打招呼的请求消息
 */


@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class HelloRequestMessage extends Message
{
    /**
     * 打招呼的人的姓名
     */
    private String name;

    /**
     * 内容
     */
    private String body;

    @Override
    public int getMessageType()
    {
        return HelloRequestMessage;
    }
}
```





### 打招呼的响应消息

```java
package mao.message;

import lombok.*;
import mao.protocol.SequenceIdGenerator;

/**
 * Project name(项目名称)：Netty_自定义协议
 * Package(包名): mao.message
 * Class(类名): HelloResponseMessage
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2023/3/26
 * Time(创建时间)： 14:23
 * Version(版本): 1.0
 * Description(描述)： 打招呼的响应消息
 */


@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class HelloResponseMessage extends AbstractResponseMessage
{
    /**
     * 要响应的消息
     */
    private String body;

    public HelloResponseMessage(String body)
    {
        this.body = body;
        setSequenceId(SequenceIdGenerator.nextId());
    }

    public HelloResponseMessage(boolean success, String reason, String body)
    {
        super(success, reason);
        this.body = body;
        setSequenceId(SequenceIdGenerator.nextId());
    }

    public HelloResponseMessage()
    {
        setSequenceId(SequenceIdGenerator.nextId());
    }

    @Override
    public int getMessageType()
    {
        return HelloResponseMessage;
    }

    //这里写静态方法简化类的频繁创建

    /**
     * 成功
     *
     * @param body 消息内容
     * @return {@link HelloResponseMessage}
     */
    public static HelloResponseMessage success(String body)
    {
        return new HelloResponseMessage(true, null, body);
    }


    /**
     * 失败
     *
     * @return {@link HelloResponseMessage}
     */
    public static HelloResponseMessage fail()
    {
        return new HelloResponseMessage(false, "未知", null);
    }

    /**
     * 失败
     *
     * @param reason 原因
     * @return {@link HelloResponseMessage}
     */
    public static HelloResponseMessage fail(String reason)
    {
        return new HelloResponseMessage(false, reason, null);
    }

}
```





### config.properties

```properties
server.port=8080
serializer.algorithm=Json
```





### 服务配置类

```java
package mao.config;

import mao.protocol.SerializerAlgorithm;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Project name(项目名称)：Netty_自定义协议
 * Package(包名): mao.config
 * Class(类名): ServerConfig
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2023/3/26
 * Time(创建时间)： 21:18
 * Version(版本): 1.0
 * Description(描述)： 服务配置类
 */

public class ServerConfig
{

    private static Properties properties;

    static
    {
        try (InputStream inputStream =
                     ServerConfig.class.getClassLoader().getResourceAsStream("config.properties"))
        {
            properties = new Properties();
            properties.load(inputStream);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 得到服务器端口號
     *
     * @return int
     */
    public static int getServerPort()
    {
        String value = properties.getProperty("server.port");
        if (value == null)
        {
            return 8080;
        }
        else
        {
            return Integer.parseInt(value);
        }
    }

    /**
     * 得到序列化器算法
     *
     * @return {@link SerializerAlgorithm}
     */
    public static SerializerAlgorithm getSerializerAlgorithm()
    {
        String value = properties.getProperty("serializer.algorithm");
        if (value == null)
        {
            return SerializerAlgorithm.Java;
        }
        else
        {
            return SerializerAlgorithm.valueOf(value);
        }
    }
}
```







### Serializer

用于扩展序列化、反序列化算法

```java
package mao.protocol;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * Project name(项目名称)：Netty_自定义协议
 * Package(包名): mao.protocol
 * Interface(接口名): Serializer
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2023/3/26
 * Time(创建时间)： 21:10
 * Version(版本): 1.0
 * Description(描述)： 用于扩展序列化、反序列化算法
 */

public interface Serializer
{
    /**
     * 反序列化
     *
     * @param clazz clazz
     * @param bytes 字节数组
     * @return {@link T}
     */
    <T> T deserialize(Class<T> clazz, byte[] bytes);

    /**
     * 序列化
     *
     * @param object 对象
     * @return {@link byte[]}
     */
    <T> byte[] serialize(T object);

}
```





### SerializerAlgorithm

```java
package mao.protocol;

import com.alibaba.fastjson.JSON;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Project name(项目名称)：Netty_自定义协议
 * Package(包名): mao.protocol
 * Enum(枚举名): SerializerAlgorithm
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2023/3/26
 * Time(创建时间)： 21:12
 * Version(版本): 1.0
 * Description(描述)： 序列化算法，json采用fastjson
 */

public enum SerializerAlgorithm implements Serializer
{
    Java
            {
                @Override
                public <T> T deserialize(Class<T> clazz, byte[] bytes)
                {
                    try
                    {
                        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
                        return (T) objectInputStream.readObject();
                    }
                    catch (IOException | ClassNotFoundException e)
                    {
                        throw new RuntimeException("反序列化失败", e);
                    }
                }

                @Override
                public <T> byte[] serialize(T object)
                {
                    try
                    {
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                        objectOutputStream.writeObject(object);
                        return byteArrayOutputStream.toByteArray();
                    }
                    catch (IOException e)
                    {
                        throw new RuntimeException("序列化失败", e);
                    }
                }
            },

    Json
            {
                @Override
                public <T> T deserialize(Class<T> clazz, byte[] bytes)
                {
                    String json = new String(bytes, StandardCharsets.UTF_8);
                    return JSON.parseObject(json, clazz);
                }

                @Override
                public <T> byte[] serialize(T object)
                {
                    String jsonString = JSON.toJSONString(object);
                    return jsonString.getBytes(StandardCharsets.UTF_8);
                }
            }
}
```





### 序列ID生成器

比较简单，非全局唯一

```java
package mao.protocol;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Project name(项目名称)：Netty_自定义协议
 * Package(包名): mao.protocol
 * Class(类名): SequenceIdGenerator
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2023/3/26
 * Time(创建时间)： 21:06
 * Version(版本): 1.0
 * Description(描述)： 序列 ID 生成器，内部采用CAS的方式累加
 */

public class SequenceIdGenerator
{
    /**
     * id
     */
    private static final AtomicInteger id = new AtomicInteger();

    public static int nextId()
    {
        return id.incrementAndGet();
    }
}
```







### 协议解码器

为了解决粘包半包问题，用的第四种方法

```java
package mao.protocol;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * Project name(项目名称)：Netty_自定义协议
 * Package(包名): mao.protocol
 * Class(类名): ProcotolFrameDecoder
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2023/3/26
 * Time(创建时间)： 14:57
 * Version(版本): 1.0
 * Description(描述)： 协议解码器
 */

public class ProcotolFrameDecoder extends LengthFieldBasedFrameDecoder
{
    /**
     * 协议解码器，默认
     */
    public ProcotolFrameDecoder()
    {
        this(4096, 12, 4, 0, 0);
    }

    /**
     * 协议解码器，参数传递
     *
     * @param maxFrameLength      最大值框架长度
     * @param lengthFieldOffset   长度字段偏移量
     * @param lengthFieldLength   长度字段长度
     * @param lengthAdjustment    长度调整
     * @param initialBytesToStrip 最初字节地带
     */
    public ProcotolFrameDecoder(int maxFrameLength, int lengthFieldOffset,
                                int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip)
    {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }
}
```







### 消息编码和解码器

```java
package mao.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;
import mao.config.ServerConfig;
import mao.message.Message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * Project name(项目名称)：Netty_自定义协议
 * Package(包名): mao.protocol
 * Class(类名): MessageCodecSharable
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2023/3/26
 * Time(创建时间)： 15:03
 * Version(版本): 1.0
 * Description(描述)： 消息编码和解码
 * 必须和LengthFieldBasedFrameDecoder一起使用，确保接到的 ByteBuf 消息是完整的
 */

@Slf4j
@ChannelHandler.Sharable
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf, Message>
{
    /**
     * 编码
     *
     * @param ctx     ctx
     * @param msg     Message对象
     * @param outList List<Object>
     * @throws Exception 异常
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> outList) throws Exception
    {
        ByteBuf out = ctx.alloc().buffer();
        //字节的魔数，用来在第一时间判定是否是无效数据包
        out.writeBytes(new byte[]{1, 2, 3, 4});
        //字节的版本，可以支持协议的升级
        out.writeByte(1);
        //字节的序列化方式 jdk 0 , json 1
        out.writeByte(ServerConfig.getSerializerAlgorithm().ordinal());
        //字节的指令类型
        out.writeByte(msg.getMessageType());
        //4个字节，为了双工通信，提供异步能力
        out.writeInt(msg.getSequenceId());
        //无意义，对齐填充
        out.writeByte(0xff);
        //获取内容的字节数组
        byte[] bytes = ServerConfig.getSerializerAlgorithm().serialize(msg);
        //写入长度消息
        out.writeInt(bytes.length);
        //写入内容消息
        out.writeBytes(bytes);
        outList.add(out);
    }


    /**
     * 解码
     *
     * @param ctx ctx
     * @param in  ByteBuf
     * @param out List<Object>
     * @throws Exception 异常
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception
    {
        //字节的魔数，用来在第一时间判定是否是无效数据包
        int magicNum = in.readInt();
        //字节的版本，可以支持协议的升级
        byte version = in.readByte();
        //字节的序列化方式 jdk 0 , json 1
        byte serializerType = in.readByte();
        //字节的指令类型
        byte messageType = in.readByte();
        //4个字节，为了双工通信，提供异步能力
        int sequenceId = in.readInt();
        //无意义，对齐填充
        in.readByte();
        //长度信息
        int length = in.readInt();
        //内容
        byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);
        //得到序列化算法
        SerializerAlgorithm serializerAlgorithm = SerializerAlgorithm.values()[serializerType];
        //得到消息类型
        Class<? extends Message> messageClass = Message.getMessageClass(messageType);
        //转换
        Message message = serializerAlgorithm.deserialize(messageClass, bytes);
        //打印
        log.debug("{}, {}, {}, {}, {}, {}", magicNum, version, serializerType, messageType, sequenceId, length);
        log.debug("{}", message);
        //加入到集合中
        out.add(message);
    }
}
```





**@Sharable注解**

* 当 handler 不保存状态时，就可以安全地在多线程下被共享
* 但要注意对于编解码器类，不能继承 ByteToMessageCodec 或 CombinedChannelDuplexHandler 父类，他们的构造方法对 @Sharable 有限制
* 如果能确保编解码器不会保存状态，可以继承 MessageToMessageCodec 父类







### 服务端

```java
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
import mao.message.HelloResponseMessage;
import mao.message.PingMessage;
import mao.message.PongMessage;
import mao.protocol.MessageCodecSharable;
import mao.protocol.ProcotolFrameDecoder;
import mao.protocol.SequenceIdGenerator;

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
 * Description(描述)： 服务端
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
                        protected void channelRead0(ChannelHandlerContext ctx, PingMessage pingMessage) throws Exception
                        {
                            log.debug("ping消息:" + ctx.channel());
                            PongMessage pongMessage = new PongMessage();
                            pongMessage.setTime(pingMessage.getTime());
                            ctx.writeAndFlush(pongMessage);
                        }
                    });

                    ch.pipeline().addLast(new SimpleChannelInboundHandler<HelloRequestMessage>()
                    {

                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, HelloRequestMessage helloRequestMessage)
                                throws Exception
                        {
                            try
                            {
                                log.debug("打招呼消息:" + ctx.channel());
                                //得到姓名
                                String name = helloRequestMessage.getName();
                                //得到内容
                                String body = helloRequestMessage.getBody();
                                log.info("姓名 " + name + " 和服务器打招呼：" + body);
                                //HelloResponseMessage helloResponseMessage = new HelloResponseMessage();
                                //helloResponseMessage.setSuccess(true);
                                String respBody = "你好，" + name + ",非常荣幸您能和我打招呼！";
                                //helloResponseMessage.setBody(respBody);
                                HelloResponseMessage helloResponseMessage = HelloResponseMessage.success(respBody);
                                //响应
                                ctx.writeAndFlush(helloResponseMessage);
                            }
                            catch (Exception e)
                            {
                                //HelloResponseMessage helloResponseMessage = new HelloResponseMessage();
                                //helloResponseMessage.setSuccess(false);
                                //helloResponseMessage.setReason();
                                HelloResponseMessage helloResponseMessage =
                                        HelloResponseMessage.fail("服务器异常：" + e.getMessage());
                                //响应
                                ctx.writeAndFlush(helloResponseMessage);
                            }

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
```





### 客户端

```java
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
                    System.out.println("3.退出");
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
                    else if ("3".equals(num))
                    {
                        channel.close();
                        return;
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
                thread.interrupt();
            }
        });
    }
}
```







### 测试

客户端

```sh
2023-03-26  23:21:37.601  [nioEventLoopGroup-2-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x60cc5bd3] REGISTERED
2023-03-26  23:21:37.601  [nioEventLoopGroup-2-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x60cc5bd3] CONNECT: /127.0.0.1:8080
2023-03-26  23:21:37.603  [nioEventLoopGroup-2-1] DEBUG mao.client.Client:  客户端连接成功
2023-03-26  23:21:37.604  [nioEventLoopGroup-2-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x60cc5bd3, L:/127.0.0.1:64882 - R:/127.0.0.1:8080] ACTIVE
---------------
1.ping
2.和服务器打招呼
3.退出
---------------
请输入序号:1
2023-03-26  23:21:41.255  [input] DEBUG mao.client.Client:  发送ping消息
2023-03-26  23:21:41.256  [input] DEBUG io.netty.util.Recycler:  -Dio.netty.recycler.maxCapacityPerThread: 4096
2023-03-26  23:21:41.256  [input] DEBUG io.netty.util.Recycler:  -Dio.netty.recycler.maxSharedCapacityFactor: 2
2023-03-26  23:21:41.256  [input] DEBUG io.netty.util.Recycler:  -Dio.netty.recycler.linkCapacity: 16
2023-03-26  23:21:41.256  [input] DEBUG io.netty.util.Recycler:  -Dio.netty.recycler.ratio: 8
2023-03-26  23:21:41.261  [nioEventLoopGroup-2-1] DEBUG io.netty.buffer.AbstractByteBuf:  -Dio.netty.buffer.checkAccessible: true
2023-03-26  23:21:41.261  [nioEventLoopGroup-2-1] DEBUG io.netty.buffer.AbstractByteBuf:  -Dio.netty.buffer.checkBounds: true
2023-03-26  23:21:41.262  [nioEventLoopGroup-2-1] DEBUG io.netty.util.ResourceLeakDetectorFactory:  Loaded default ResourceLeakDetector: io.netty.util.ResourceLeakDetector@56657d7e
2023-03-26  23:21:41.302  [nioEventLoopGroup-2-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x60cc5bd3, L:/127.0.0.1:64882 - R:/127.0.0.1:8080] WRITE: 69B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 01 02 03 04 01 01 01 00 00 00 00 ff 00 00 00 35 |...............5|
|00000010| 7b 22 6d 65 73 73 61 67 65 54 79 70 65 22 3a 31 |{"messageType":1|
|00000020| 2c 22 73 65 71 75 65 6e 63 65 49 64 22 3a 30 2c |,"sequenceId":0,|
|00000030| 22 74 69 6d 65 22 3a 31 36 37 39 38 34 34 31 30 |"time":167984410|
|00000040| 31 32 35 35 7d                                  |1255}           |
+--------+-------------------------------------------------+----------------+
2023-03-26  23:21:41.302  [nioEventLoopGroup-2-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x60cc5bd3, L:/127.0.0.1:64882 - R:/127.0.0.1:8080] FLUSH
2023-03-26  23:21:41.361  [nioEventLoopGroup-2-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x60cc5bd3, L:/127.0.0.1:64882 - R:/127.0.0.1:8080] READ: 69B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 01 02 03 04 01 01 02 00 00 00 01 ff 00 00 00 35 |...............5|
|00000010| 7b 22 6d 65 73 73 61 67 65 54 79 70 65 22 3a 32 |{"messageType":2|
|00000020| 2c 22 73 65 71 75 65 6e 63 65 49 64 22 3a 31 2c |,"sequenceId":1,|
|00000030| 22 74 69 6d 65 22 3a 31 36 37 39 38 34 34 31 30 |"time":167984410|
|00000040| 31 32 35 35 7d                                  |1255}           |
+--------+-------------------------------------------------+----------------+
2023-03-26  23:21:41.370  [nioEventLoopGroup-2-1] DEBUG mao.protocol.MessageCodecSharable:  16909060, 1, 1, 2, 1, 53
2023-03-26  23:21:41.372  [nioEventLoopGroup-2-1] DEBUG mao.protocol.MessageCodecSharable:  PongMessage(time=1679844101255)
2023-03-26  23:21:41.372  [nioEventLoopGroup-2-1] INFO  mao.client.Client:  得到服务器ping响应
2023-03-26  23:21:41.372  [nioEventLoopGroup-2-1] DEBUG mao.client.Client:  PongMessage(time=1679844101255)
2023-03-26  23:21:41.373  [nioEventLoopGroup-2-1] INFO  mao.client.Client:  延时：117毫秒
2023-03-26  23:21:41.373  [nioEventLoopGroup-2-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x60cc5bd3, L:/127.0.0.1:64882 - R:/127.0.0.1:8080] READ COMPLETE
---------------
1.ping
2.和服务器打招呼
3.退出
---------------
请输入序号:1
2023-03-26  23:21:42.805  [input] DEBUG mao.client.Client:  发送ping消息
2023-03-26  23:21:42.805  [nioEventLoopGroup-2-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x60cc5bd3, L:/127.0.0.1:64882 - R:/127.0.0.1:8080] WRITE: 69B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 01 02 03 04 01 01 01 00 00 00 00 ff 00 00 00 35 |...............5|
|00000010| 7b 22 6d 65 73 73 61 67 65 54 79 70 65 22 3a 31 |{"messageType":1|
|00000020| 2c 22 73 65 71 75 65 6e 63 65 49 64 22 3a 30 2c |,"sequenceId":0,|
|00000030| 22 74 69 6d 65 22 3a 31 36 37 39 38 34 34 31 30 |"time":167984410|
|00000040| 32 38 30 35 7d                                  |2805}           |
+--------+-------------------------------------------------+----------------+
2023-03-26  23:21:42.805  [nioEventLoopGroup-2-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x60cc5bd3, L:/127.0.0.1:64882 - R:/127.0.0.1:8080] FLUSH
2023-03-26  23:21:42.807  [nioEventLoopGroup-2-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x60cc5bd3, L:/127.0.0.1:64882 - R:/127.0.0.1:8080] READ: 69B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 01 02 03 04 01 01 02 00 00 00 02 ff 00 00 00 35 |...............5|
|00000010| 7b 22 6d 65 73 73 61 67 65 54 79 70 65 22 3a 32 |{"messageType":2|
|00000020| 2c 22 73 65 71 75 65 6e 63 65 49 64 22 3a 32 2c |,"sequenceId":2,|
|00000030| 22 74 69 6d 65 22 3a 31 36 37 39 38 34 34 31 30 |"time":167984410|
|00000040| 32 38 30 35 7d                                  |2805}           |
+--------+-------------------------------------------------+----------------+
2023-03-26  23:21:42.807  [nioEventLoopGroup-2-1] DEBUG mao.protocol.MessageCodecSharable:  16909060, 1, 1, 2, 2, 53
2023-03-26  23:21:42.807  [nioEventLoopGroup-2-1] DEBUG mao.protocol.MessageCodecSharable:  PongMessage(time=1679844102805)
2023-03-26  23:21:42.807  [nioEventLoopGroup-2-1] INFO  mao.client.Client:  得到服务器ping响应
2023-03-26  23:21:42.807  [nioEventLoopGroup-2-1] DEBUG mao.client.Client:  PongMessage(time=1679844102805)
2023-03-26  23:21:42.807  [nioEventLoopGroup-2-1] INFO  mao.client.Client:  延时：2毫秒
2023-03-26  23:21:42.807  [nioEventLoopGroup-2-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x60cc5bd3, L:/127.0.0.1:64882 - R:/127.0.0.1:8080] READ COMPLETE
---------------
1.ping
2.和服务器打招呼
3.退出
---------------
请输入序号:1
2023-03-26  23:21:44.290  [input] DEBUG mao.client.Client:  发送ping消息
2023-03-26  23:21:44.290  [nioEventLoopGroup-2-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x60cc5bd3, L:/127.0.0.1:64882 - R:/127.0.0.1:8080] WRITE: 69B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 01 02 03 04 01 01 01 00 00 00 00 ff 00 00 00 35 |...............5|
|00000010| 7b 22 6d 65 73 73 61 67 65 54 79 70 65 22 3a 31 |{"messageType":1|
|00000020| 2c 22 73 65 71 75 65 6e 63 65 49 64 22 3a 30 2c |,"sequenceId":0,|
|00000030| 22 74 69 6d 65 22 3a 31 36 37 39 38 34 34 31 30 |"time":167984410|
|00000040| 34 32 39 30 7d                                  |4290}           |
+--------+-------------------------------------------------+----------------+
2023-03-26  23:21:44.290  [nioEventLoopGroup-2-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x60cc5bd3, L:/127.0.0.1:64882 - R:/127.0.0.1:8080] FLUSH
2023-03-26  23:21:44.292  [nioEventLoopGroup-2-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x60cc5bd3, L:/127.0.0.1:64882 - R:/127.0.0.1:8080] READ: 69B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 01 02 03 04 01 01 02 00 00 00 03 ff 00 00 00 35 |...............5|
|00000010| 7b 22 6d 65 73 73 61 67 65 54 79 70 65 22 3a 32 |{"messageType":2|
|00000020| 2c 22 73 65 71 75 65 6e 63 65 49 64 22 3a 33 2c |,"sequenceId":3,|
|00000030| 22 74 69 6d 65 22 3a 31 36 37 39 38 34 34 31 30 |"time":167984410|
|00000040| 34 32 39 30 7d                                  |4290}           |
+--------+-------------------------------------------------+----------------+
2023-03-26  23:21:44.292  [nioEventLoopGroup-2-1] DEBUG mao.protocol.MessageCodecSharable:  16909060, 1, 1, 2, 3, 53
2023-03-26  23:21:44.292  [nioEventLoopGroup-2-1] DEBUG mao.protocol.MessageCodecSharable:  PongMessage(time=1679844104290)
2023-03-26  23:21:44.292  [nioEventLoopGroup-2-1] INFO  mao.client.Client:  得到服务器ping响应
2023-03-26  23:21:44.292  [nioEventLoopGroup-2-1] DEBUG mao.client.Client:  PongMessage(time=1679844104290)
2023-03-26  23:21:44.292  [nioEventLoopGroup-2-1] INFO  mao.client.Client:  延时：2毫秒
2023-03-26  23:21:44.292  [nioEventLoopGroup-2-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x60cc5bd3, L:/127.0.0.1:64882 - R:/127.0.0.1:8080] READ COMPLETE
---------------
1.ping
2.和服务器打招呼
3.退出
---------------
请输入序号:2
请输入您的姓名：张三
请输入内容：
你好
2023-03-26  23:21:53.079  [input] DEBUG mao.client.Client:  发送打招呼消息
2023-03-26  23:21:53.081  [nioEventLoopGroup-2-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x60cc5bd3, L:/127.0.0.1:64882 - R:/127.0.0.1:8080] WRITE: 80B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 01 02 03 04 01 01 03 00 00 00 00 ff 00 00 00 40 |...............@|
|00000010| 7b 22 62 6f 64 79 22 3a 22 e4 bd a0 e5 a5 bd 22 |{"body":"......"|
|00000020| 2c 22 6d 65 73 73 61 67 65 54 79 70 65 22 3a 33 |,"messageType":3|
|00000030| 2c 22 6e 61 6d 65 22 3a 22 e5 bc a0 e4 b8 89 22 |,"name":"......"|
|00000040| 2c 22 73 65 71 75 65 6e 63 65 49 64 22 3a 30 7d |,"sequenceId":0}|
+--------+-------------------------------------------------+----------------+
2023-03-26  23:21:53.082  [nioEventLoopGroup-2-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x60cc5bd3, L:/127.0.0.1:64882 - R:/127.0.0.1:8080] FLUSH
2023-03-26  23:21:53.088  [nioEventLoopGroup-2-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x60cc5bd3, L:/127.0.0.1:64882 - R:/127.0.0.1:8080] READ: 125B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 01 02 03 04 01 01 04 00 00 00 04 ff 00 00 00 6d |...............m|
|00000010| 7b 22 62 6f 64 79 22 3a 22 e4 bd a0 e5 a5 bd ef |{"body":".......|
|00000020| bc 8c e5 bc a0 e4 b8 89 2c e9 9d 9e e5 b8 b8 e8 |........,.......|
|00000030| 8d a3 e5 b9 b8 e6 82 a8 e8 83 bd e5 92 8c e6 88 |................|
|00000040| 91 e6 89 93 e6 8b 9b e5 91 bc ef bc 81 22 2c 22 |.............","|
|00000050| 6d 65 73 73 61 67 65 54 79 70 65 22 3a 34 2c 22 |messageType":4,"|
|00000060| 73 65 71 75 65 6e 63 65 49 64 22 3a 34 2c 22 73 |sequenceId":4,"s|
|00000070| 75 63 63 65 73 73 22 3a 74 72 75 65 7d          |uccess":true}   |
+--------+-------------------------------------------------+----------------+
2023-03-26  23:21:53.089  [nioEventLoopGroup-2-1] DEBUG mao.protocol.MessageCodecSharable:  16909060, 1, 1, 4, 4, 109
2023-03-26  23:21:53.092  [nioEventLoopGroup-2-1] DEBUG mao.protocol.MessageCodecSharable:  HelloResponseMessage(super=AbstractResponseMessage(super=Message(sequenceId=4, messageType=4), success=true, reason=null), body=你好，张三,非常荣幸您能和我打招呼！)
2023-03-26  23:21:53.093  [nioEventLoopGroup-2-1] INFO  mao.client.Client:  得到服务器打招呼响应
2023-03-26  23:21:53.093  [nioEventLoopGroup-2-1] INFO  mao.client.Client:  HelloResponseMessage(super=AbstractResponseMessage(super=Message(sequenceId=4, messageType=4), success=true, reason=null), body=你好，张三,非常荣幸您能和我打招呼！)
2023-03-26  23:21:53.093  [nioEventLoopGroup-2-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x60cc5bd3, L:/127.0.0.1:64882 - R:/127.0.0.1:8080] READ COMPLETE
---------------
1.ping
2.和服务器打招呼
3.退出
---------------
请输入序号:2
请输入您的姓名：lisi
请输入内容：
hello
2023-03-26  23:22:03.110  [input] DEBUG mao.client.Client:  发送打招呼消息
2023-03-26  23:22:03.110  [nioEventLoopGroup-2-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x60cc5bd3, L:/127.0.0.1:64882 - R:/127.0.0.1:8080] WRITE: 77B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 01 02 03 04 01 01 03 00 00 00 00 ff 00 00 00 3d |...............=|
|00000010| 7b 22 62 6f 64 79 22 3a 22 68 65 6c 6c 6f 22 2c |{"body":"hello",|
|00000020| 22 6d 65 73 73 61 67 65 54 79 70 65 22 3a 33 2c |"messageType":3,|
|00000030| 22 6e 61 6d 65 22 3a 22 6c 69 73 69 22 2c 22 73 |"name":"lisi","s|
|00000040| 65 71 75 65 6e 63 65 49 64 22 3a 30 7d          |equenceId":0}   |
+--------+-------------------------------------------------+----------------+
2023-03-26  23:22:03.110  [nioEventLoopGroup-2-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x60cc5bd3, L:/127.0.0.1:64882 - R:/127.0.0.1:8080] FLUSH
2023-03-26  23:22:03.112  [nioEventLoopGroup-2-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x60cc5bd3, L:/127.0.0.1:64882 - R:/127.0.0.1:8080] READ: 123B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 01 02 03 04 01 01 04 00 00 00 05 ff 00 00 00 6b |...............k|
|00000010| 7b 22 62 6f 64 79 22 3a 22 e4 bd a0 e5 a5 bd ef |{"body":".......|
|00000020| bc 8c 6c 69 73 69 2c e9 9d 9e e5 b8 b8 e8 8d a3 |..lisi,.........|
|00000030| e5 b9 b8 e6 82 a8 e8 83 bd e5 92 8c e6 88 91 e6 |................|
|00000040| 89 93 e6 8b 9b e5 91 bc ef bc 81 22 2c 22 6d 65 |...........","me|
|00000050| 73 73 61 67 65 54 79 70 65 22 3a 34 2c 22 73 65 |ssageType":4,"se|
|00000060| 71 75 65 6e 63 65 49 64 22 3a 35 2c 22 73 75 63 |quenceId":5,"suc|
|00000070| 63 65 73 73 22 3a 74 72 75 65 7d                |cess":true}     |
+--------+-------------------------------------------------+----------------+
2023-03-26  23:22:03.112  [nioEventLoopGroup-2-1] DEBUG mao.protocol.MessageCodecSharable:  16909060, 1, 1, 4, 5, 107
2023-03-26  23:22:03.112  [nioEventLoopGroup-2-1] DEBUG mao.protocol.MessageCodecSharable:  HelloResponseMessage(super=AbstractResponseMessage(super=Message(sequenceId=5, messageType=4), success=true, reason=null), body=你好，lisi,非常荣幸您能和我打招呼！)
2023-03-26  23:22:03.112  [nioEventLoopGroup-2-1] INFO  mao.client.Client:  得到服务器打招呼响应
2023-03-26  23:22:03.112  [nioEventLoopGroup-2-1] INFO  mao.client.Client:  HelloResponseMessage(super=AbstractResponseMessage(super=Message(sequenceId=5, messageType=4), success=true, reason=null), body=你好，lisi,非常荣幸您能和我打招呼！)
2023-03-26  23:22:03.112  [nioEventLoopGroup-2-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x60cc5bd3, L:/127.0.0.1:64882 - R:/127.0.0.1:8080] READ COMPLETE
---------------
1.ping
2.和服务器打招呼
3.退出
---------------
请输入序号:1
2023-03-26  23:23:03.989  [input] DEBUG mao.client.Client:  发送ping消息
2023-03-26  23:23:03.990  [nioEventLoopGroup-2-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x60cc5bd3, L:/127.0.0.1:64882 - R:/127.0.0.1:8080] WRITE: 69B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 01 02 03 04 01 01 01 00 00 00 00 ff 00 00 00 35 |...............5|
|00000010| 7b 22 6d 65 73 73 61 67 65 54 79 70 65 22 3a 31 |{"messageType":1|
|00000020| 2c 22 73 65 71 75 65 6e 63 65 49 64 22 3a 30 2c |,"sequenceId":0,|
|00000030| 22 74 69 6d 65 22 3a 31 36 37 39 38 34 34 31 38 |"time":167984418|
|00000040| 33 39 38 39 7d                                  |3989}           |
+--------+-------------------------------------------------+----------------+
2023-03-26  23:23:03.990  [nioEventLoopGroup-2-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x60cc5bd3, L:/127.0.0.1:64882 - R:/127.0.0.1:8080] FLUSH
2023-03-26  23:23:03.991  [nioEventLoopGroup-2-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x60cc5bd3, L:/127.0.0.1:64882 - R:/127.0.0.1:8080] READ: 69B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 01 02 03 04 01 01 02 00 00 00 06 ff 00 00 00 35 |...............5|
|00000010| 7b 22 6d 65 73 73 61 67 65 54 79 70 65 22 3a 32 |{"messageType":2|
|00000020| 2c 22 73 65 71 75 65 6e 63 65 49 64 22 3a 36 2c |,"sequenceId":6,|
|00000030| 22 74 69 6d 65 22 3a 31 36 37 39 38 34 34 31 38 |"time":167984418|
|00000040| 33 39 38 39 7d                                  |3989}           |
+--------+-------------------------------------------------+----------------+
2023-03-26  23:23:03.991  [nioEventLoopGroup-2-1] DEBUG mao.protocol.MessageCodecSharable:  16909060, 1, 1, 2, 6, 53
2023-03-26  23:23:03.991  [nioEventLoopGroup-2-1] DEBUG mao.protocol.MessageCodecSharable:  PongMessage(time=1679844183989)
2023-03-26  23:23:03.991  [nioEventLoopGroup-2-1] INFO  mao.client.Client:  得到服务器ping响应
2023-03-26  23:23:03.991  [nioEventLoopGroup-2-1] DEBUG mao.client.Client:  PongMessage(time=1679844183989)
2023-03-26  23:23:03.992  [nioEventLoopGroup-2-1] INFO  mao.client.Client:  延时：3毫秒
2023-03-26  23:23:03.992  [nioEventLoopGroup-2-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x60cc5bd3, L:/127.0.0.1:64882 - R:/127.0.0.1:8080] READ COMPLETE
---------------
1.ping
2.和服务器打招呼
3.退出
---------------
请输入序号:3
2023-03-26  23:23:06.533  [nioEventLoopGroup-2-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x60cc5bd3, L:/127.0.0.1:64882 - R:/127.0.0.1:8080] CLOSE
2023-03-26  23:23:06.534  [nioEventLoopGroup-2-1] INFO  mao.client.Client:  关闭客户端
2023-03-26  23:23:06.534  [nioEventLoopGroup-2-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x60cc5bd3, L:/127.0.0.1:64882 ! R:/127.0.0.1:8080] INACTIVE
2023-03-26  23:23:06.534  [nioEventLoopGroup-2-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x60cc5bd3, L:/127.0.0.1:64882 ! R:/127.0.0.1:8080] UNREGISTERED
2023-03-26  23:23:08.783  [nioEventLoopGroup-2-1] DEBUG io.netty.buffer.PoolThreadCache:  Freed 4 thread-local buffer(s) from thread: nioEventLoopGroup-2-1
```





服务端

```sh
2023-03-26  23:21:37.613  [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x8b7a3a09, L:/127.0.0.1:8080 - R:/127.0.0.1:64882] REGISTERED
2023-03-26  23:21:37.614  [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x8b7a3a09, L:/127.0.0.1:8080 - R:/127.0.0.1:64882] ACTIVE
2023-03-26  23:21:41.304  [nioEventLoopGroup-3-1] DEBUG io.netty.util.Recycler:  -Dio.netty.recycler.maxCapacityPerThread: 4096
2023-03-26  23:21:41.304  [nioEventLoopGroup-3-1] DEBUG io.netty.util.Recycler:  -Dio.netty.recycler.maxSharedCapacityFactor: 2
2023-03-26  23:21:41.304  [nioEventLoopGroup-3-1] DEBUG io.netty.util.Recycler:  -Dio.netty.recycler.linkCapacity: 16
2023-03-26  23:21:41.304  [nioEventLoopGroup-3-1] DEBUG io.netty.util.Recycler:  -Dio.netty.recycler.ratio: 8
2023-03-26  23:21:41.307  [nioEventLoopGroup-3-1] DEBUG io.netty.buffer.AbstractByteBuf:  -Dio.netty.buffer.checkAccessible: true
2023-03-26  23:21:41.307  [nioEventLoopGroup-3-1] DEBUG io.netty.buffer.AbstractByteBuf:  -Dio.netty.buffer.checkBounds: true
2023-03-26  23:21:41.308  [nioEventLoopGroup-3-1] DEBUG io.netty.util.ResourceLeakDetectorFactory:  Loaded default ResourceLeakDetector: io.netty.util.ResourceLeakDetector@586a6b4e
2023-03-26  23:21:41.312  [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x8b7a3a09, L:/127.0.0.1:8080 - R:/127.0.0.1:64882] READ: 69B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 01 02 03 04 01 01 01 00 00 00 00 ff 00 00 00 35 |...............5|
|00000010| 7b 22 6d 65 73 73 61 67 65 54 79 70 65 22 3a 31 |{"messageType":1|
|00000020| 2c 22 73 65 71 75 65 6e 63 65 49 64 22 3a 30 2c |,"sequenceId":0,|
|00000030| 22 74 69 6d 65 22 3a 31 36 37 39 38 34 34 31 30 |"time":167984410|
|00000040| 31 32 35 35 7d                                  |1255}           |
+--------+-------------------------------------------------+----------------+
2023-03-26  23:21:41.349  [nioEventLoopGroup-3-1] DEBUG mao.protocol.MessageCodecSharable:  16909060, 1, 1, 1, 0, 53
2023-03-26  23:21:41.350  [nioEventLoopGroup-3-1] DEBUG mao.protocol.MessageCodecSharable:  PingMessage(time=1679844101255)
2023-03-26  23:21:41.351  [nioEventLoopGroup-3-1] DEBUG mao.server.Server:  ping消息:[id: 0x8b7a3a09, L:/127.0.0.1:8080 - R:/127.0.0.1:64882]
2023-03-26  23:21:41.360  [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x8b7a3a09, L:/127.0.0.1:8080 - R:/127.0.0.1:64882] WRITE: 69B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 01 02 03 04 01 01 02 00 00 00 01 ff 00 00 00 35 |...............5|
|00000010| 7b 22 6d 65 73 73 61 67 65 54 79 70 65 22 3a 32 |{"messageType":2|
|00000020| 2c 22 73 65 71 75 65 6e 63 65 49 64 22 3a 31 2c |,"sequenceId":1,|
|00000030| 22 74 69 6d 65 22 3a 31 36 37 39 38 34 34 31 30 |"time":167984410|
|00000040| 31 32 35 35 7d                                  |1255}           |
+--------+-------------------------------------------------+----------------+
2023-03-26  23:21:41.360  [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x8b7a3a09, L:/127.0.0.1:8080 - R:/127.0.0.1:64882] FLUSH
2023-03-26  23:21:41.360  [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x8b7a3a09, L:/127.0.0.1:8080 - R:/127.0.0.1:64882] READ COMPLETE
2023-03-26  23:21:42.806  [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x8b7a3a09, L:/127.0.0.1:8080 - R:/127.0.0.1:64882] READ: 69B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 01 02 03 04 01 01 01 00 00 00 00 ff 00 00 00 35 |...............5|
|00000010| 7b 22 6d 65 73 73 61 67 65 54 79 70 65 22 3a 31 |{"messageType":1|
|00000020| 2c 22 73 65 71 75 65 6e 63 65 49 64 22 3a 30 2c |,"sequenceId":0,|
|00000030| 22 74 69 6d 65 22 3a 31 36 37 39 38 34 34 31 30 |"time":167984410|
|00000040| 32 38 30 35 7d                                  |2805}           |
+--------+-------------------------------------------------+----------------+
2023-03-26  23:21:42.806  [nioEventLoopGroup-3-1] DEBUG mao.protocol.MessageCodecSharable:  16909060, 1, 1, 1, 0, 53
2023-03-26  23:21:42.806  [nioEventLoopGroup-3-1] DEBUG mao.protocol.MessageCodecSharable:  PingMessage(time=1679844102805)
2023-03-26  23:21:42.806  [nioEventLoopGroup-3-1] DEBUG mao.server.Server:  ping消息:[id: 0x8b7a3a09, L:/127.0.0.1:8080 - R:/127.0.0.1:64882]
2023-03-26  23:21:42.806  [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x8b7a3a09, L:/127.0.0.1:8080 - R:/127.0.0.1:64882] WRITE: 69B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 01 02 03 04 01 01 02 00 00 00 02 ff 00 00 00 35 |...............5|
|00000010| 7b 22 6d 65 73 73 61 67 65 54 79 70 65 22 3a 32 |{"messageType":2|
|00000020| 2c 22 73 65 71 75 65 6e 63 65 49 64 22 3a 32 2c |,"sequenceId":2,|
|00000030| 22 74 69 6d 65 22 3a 31 36 37 39 38 34 34 31 30 |"time":167984410|
|00000040| 32 38 30 35 7d                                  |2805}           |
+--------+-------------------------------------------------+----------------+
2023-03-26  23:21:42.807  [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x8b7a3a09, L:/127.0.0.1:8080 - R:/127.0.0.1:64882] FLUSH
2023-03-26  23:21:42.807  [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x8b7a3a09, L:/127.0.0.1:8080 - R:/127.0.0.1:64882] READ COMPLETE
2023-03-26  23:21:44.291  [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x8b7a3a09, L:/127.0.0.1:8080 - R:/127.0.0.1:64882] READ: 69B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 01 02 03 04 01 01 01 00 00 00 00 ff 00 00 00 35 |...............5|
|00000010| 7b 22 6d 65 73 73 61 67 65 54 79 70 65 22 3a 31 |{"messageType":1|
|00000020| 2c 22 73 65 71 75 65 6e 63 65 49 64 22 3a 30 2c |,"sequenceId":0,|
|00000030| 22 74 69 6d 65 22 3a 31 36 37 39 38 34 34 31 30 |"time":167984410|
|00000040| 34 32 39 30 7d                                  |4290}           |
+--------+-------------------------------------------------+----------------+
2023-03-26  23:21:44.291  [nioEventLoopGroup-3-1] DEBUG mao.protocol.MessageCodecSharable:  16909060, 1, 1, 1, 0, 53
2023-03-26  23:21:44.291  [nioEventLoopGroup-3-1] DEBUG mao.protocol.MessageCodecSharable:  PingMessage(time=1679844104290)
2023-03-26  23:21:44.291  [nioEventLoopGroup-3-1] DEBUG mao.server.Server:  ping消息:[id: 0x8b7a3a09, L:/127.0.0.1:8080 - R:/127.0.0.1:64882]
2023-03-26  23:21:44.291  [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x8b7a3a09, L:/127.0.0.1:8080 - R:/127.0.0.1:64882] WRITE: 69B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 01 02 03 04 01 01 02 00 00 00 03 ff 00 00 00 35 |...............5|
|00000010| 7b 22 6d 65 73 73 61 67 65 54 79 70 65 22 3a 32 |{"messageType":2|
|00000020| 2c 22 73 65 71 75 65 6e 63 65 49 64 22 3a 33 2c |,"sequenceId":3,|
|00000030| 22 74 69 6d 65 22 3a 31 36 37 39 38 34 34 31 30 |"time":167984410|
|00000040| 34 32 39 30 7d                                  |4290}           |
+--------+-------------------------------------------------+----------------+
2023-03-26  23:21:44.291  [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x8b7a3a09, L:/127.0.0.1:8080 - R:/127.0.0.1:64882] FLUSH
2023-03-26  23:21:44.292  [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x8b7a3a09, L:/127.0.0.1:8080 - R:/127.0.0.1:64882] READ COMPLETE
2023-03-26  23:21:53.082  [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x8b7a3a09, L:/127.0.0.1:8080 - R:/127.0.0.1:64882] READ: 80B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 01 02 03 04 01 01 03 00 00 00 00 ff 00 00 00 40 |...............@|
|00000010| 7b 22 62 6f 64 79 22 3a 22 e4 bd a0 e5 a5 bd 22 |{"body":"......"|
|00000020| 2c 22 6d 65 73 73 61 67 65 54 79 70 65 22 3a 33 |,"messageType":3|
|00000030| 2c 22 6e 61 6d 65 22 3a 22 e5 bc a0 e4 b8 89 22 |,"name":"......"|
|00000040| 2c 22 73 65 71 75 65 6e 63 65 49 64 22 3a 30 7d |,"sequenceId":0}|
+--------+-------------------------------------------------+----------------+
2023-03-26  23:21:53.083  [nioEventLoopGroup-3-1] DEBUG mao.protocol.MessageCodecSharable:  16909060, 1, 1, 3, 0, 64
2023-03-26  23:21:53.084  [nioEventLoopGroup-3-1] DEBUG mao.protocol.MessageCodecSharable:  HelloRequestMessage(super=Message(sequenceId=0, messageType=3), name=张三, body=你好)
2023-03-26  23:21:53.085  [nioEventLoopGroup-3-1] DEBUG mao.server.Server:  打招呼消息:[id: 0x8b7a3a09, L:/127.0.0.1:8080 - R:/127.0.0.1:64882]
2023-03-26  23:21:53.085  [nioEventLoopGroup-3-1] INFO  mao.server.Server:  姓名 张三 和服务器打招呼：你好
2023-03-26  23:21:53.087  [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x8b7a3a09, L:/127.0.0.1:8080 - R:/127.0.0.1:64882] WRITE: 125B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 01 02 03 04 01 01 04 00 00 00 04 ff 00 00 00 6d |...............m|
|00000010| 7b 22 62 6f 64 79 22 3a 22 e4 bd a0 e5 a5 bd ef |{"body":".......|
|00000020| bc 8c e5 bc a0 e4 b8 89 2c e9 9d 9e e5 b8 b8 e8 |........,.......|
|00000030| 8d a3 e5 b9 b8 e6 82 a8 e8 83 bd e5 92 8c e6 88 |................|
|00000040| 91 e6 89 93 e6 8b 9b e5 91 bc ef bc 81 22 2c 22 |.............","|
|00000050| 6d 65 73 73 61 67 65 54 79 70 65 22 3a 34 2c 22 |messageType":4,"|
|00000060| 73 65 71 75 65 6e 63 65 49 64 22 3a 34 2c 22 73 |sequenceId":4,"s|
|00000070| 75 63 63 65 73 73 22 3a 74 72 75 65 7d          |uccess":true}   |
+--------+-------------------------------------------------+----------------+
2023-03-26  23:21:53.087  [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x8b7a3a09, L:/127.0.0.1:8080 - R:/127.0.0.1:64882] FLUSH
2023-03-26  23:21:53.087  [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x8b7a3a09, L:/127.0.0.1:8080 - R:/127.0.0.1:64882] READ COMPLETE
2023-03-26  23:22:03.111  [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x8b7a3a09, L:/127.0.0.1:8080 - R:/127.0.0.1:64882] READ: 77B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 01 02 03 04 01 01 03 00 00 00 00 ff 00 00 00 3d |...............=|
|00000010| 7b 22 62 6f 64 79 22 3a 22 68 65 6c 6c 6f 22 2c |{"body":"hello",|
|00000020| 22 6d 65 73 73 61 67 65 54 79 70 65 22 3a 33 2c |"messageType":3,|
|00000030| 22 6e 61 6d 65 22 3a 22 6c 69 73 69 22 2c 22 73 |"name":"lisi","s|
|00000040| 65 71 75 65 6e 63 65 49 64 22 3a 30 7d          |equenceId":0}   |
+--------+-------------------------------------------------+----------------+
2023-03-26  23:22:03.111  [nioEventLoopGroup-3-1] DEBUG mao.protocol.MessageCodecSharable:  16909060, 1, 1, 3, 0, 61
2023-03-26  23:22:03.111  [nioEventLoopGroup-3-1] DEBUG mao.protocol.MessageCodecSharable:  HelloRequestMessage(super=Message(sequenceId=0, messageType=3), name=lisi, body=hello)
2023-03-26  23:22:03.111  [nioEventLoopGroup-3-1] DEBUG mao.server.Server:  打招呼消息:[id: 0x8b7a3a09, L:/127.0.0.1:8080 - R:/127.0.0.1:64882]
2023-03-26  23:22:03.111  [nioEventLoopGroup-3-1] INFO  mao.server.Server:  姓名 lisi 和服务器打招呼：hello
2023-03-26  23:22:03.111  [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x8b7a3a09, L:/127.0.0.1:8080 - R:/127.0.0.1:64882] WRITE: 123B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 01 02 03 04 01 01 04 00 00 00 05 ff 00 00 00 6b |...............k|
|00000010| 7b 22 62 6f 64 79 22 3a 22 e4 bd a0 e5 a5 bd ef |{"body":".......|
|00000020| bc 8c 6c 69 73 69 2c e9 9d 9e e5 b8 b8 e8 8d a3 |..lisi,.........|
|00000030| e5 b9 b8 e6 82 a8 e8 83 bd e5 92 8c e6 88 91 e6 |................|
|00000040| 89 93 e6 8b 9b e5 91 bc ef bc 81 22 2c 22 6d 65 |...........","me|
|00000050| 73 73 61 67 65 54 79 70 65 22 3a 34 2c 22 73 65 |ssageType":4,"se|
|00000060| 71 75 65 6e 63 65 49 64 22 3a 35 2c 22 73 75 63 |quenceId":5,"suc|
|00000070| 63 65 73 73 22 3a 74 72 75 65 7d                |cess":true}     |
+--------+-------------------------------------------------+----------------+
2023-03-26  23:22:03.111  [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x8b7a3a09, L:/127.0.0.1:8080 - R:/127.0.0.1:64882] FLUSH
2023-03-26  23:22:03.111  [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x8b7a3a09, L:/127.0.0.1:8080 - R:/127.0.0.1:64882] READ COMPLETE
2023-03-26  23:23:03.990  [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x8b7a3a09, L:/127.0.0.1:8080 - R:/127.0.0.1:64882] READ: 69B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 01 02 03 04 01 01 01 00 00 00 00 ff 00 00 00 35 |...............5|
|00000010| 7b 22 6d 65 73 73 61 67 65 54 79 70 65 22 3a 31 |{"messageType":1|
|00000020| 2c 22 73 65 71 75 65 6e 63 65 49 64 22 3a 30 2c |,"sequenceId":0,|
|00000030| 22 74 69 6d 65 22 3a 31 36 37 39 38 34 34 31 38 |"time":167984418|
|00000040| 33 39 38 39 7d                                  |3989}           |
+--------+-------------------------------------------------+----------------+
2023-03-26  23:23:03.990  [nioEventLoopGroup-3-1] DEBUG mao.protocol.MessageCodecSharable:  16909060, 1, 1, 1, 0, 53
2023-03-26  23:23:03.991  [nioEventLoopGroup-3-1] DEBUG mao.protocol.MessageCodecSharable:  PingMessage(time=1679844183989)
2023-03-26  23:23:03.991  [nioEventLoopGroup-3-1] DEBUG mao.server.Server:  ping消息:[id: 0x8b7a3a09, L:/127.0.0.1:8080 - R:/127.0.0.1:64882]
2023-03-26  23:23:03.991  [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x8b7a3a09, L:/127.0.0.1:8080 - R:/127.0.0.1:64882] WRITE: 69B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 01 02 03 04 01 01 02 00 00 00 06 ff 00 00 00 35 |...............5|
|00000010| 7b 22 6d 65 73 73 61 67 65 54 79 70 65 22 3a 32 |{"messageType":2|
|00000020| 2c 22 73 65 71 75 65 6e 63 65 49 64 22 3a 36 2c |,"sequenceId":6,|
|00000030| 22 74 69 6d 65 22 3a 31 36 37 39 38 34 34 31 38 |"time":167984418|
|00000040| 33 39 38 39 7d                                  |3989}           |
+--------+-------------------------------------------------+----------------+
2023-03-26  23:23:03.991  [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x8b7a3a09, L:/127.0.0.1:8080 - R:/127.0.0.1:64882] FLUSH
2023-03-26  23:23:03.991  [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x8b7a3a09, L:/127.0.0.1:8080 - R:/127.0.0.1:64882] READ COMPLETE
2023-03-26  23:23:06.534  [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x8b7a3a09, L:/127.0.0.1:8080 - R:/127.0.0.1:64882] READ COMPLETE
2023-03-26  23:23:06.535  [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x8b7a3a09, L:/127.0.0.1:8080 ! R:/127.0.0.1:64882] INACTIVE
2023-03-26  23:23:06.537  [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler:  [id: 0x8b7a3a09, L:/127.0.0.1:8080 ! R:/127.0.0.1:64882] UNREGISTERED
```













