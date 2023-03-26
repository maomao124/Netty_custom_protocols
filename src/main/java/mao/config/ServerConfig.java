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
 * Description(描述)： 无
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
