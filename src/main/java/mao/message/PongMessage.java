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
