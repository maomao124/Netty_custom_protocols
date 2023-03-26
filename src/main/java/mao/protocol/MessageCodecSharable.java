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
