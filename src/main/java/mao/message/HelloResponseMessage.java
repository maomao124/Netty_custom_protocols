package mao.message;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

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
@Builder
public class HelloResponseMessage extends AbstractResponseMessage
{
    /**
     * 要响应的消息
     */
    private String body;

    @Override
    public int getMessageType()
    {
        return HelloResponseMessage;
    }
}
