package com.edgeMapper.zigbeeMapper.util;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by rongshuai on 2020/7/15 14:12
 */
public class GateWayUtil {
    private static Map<String, Channel> channelMap = new ConcurrentHashMap<String, Channel>();

    public static Map<String, Channel> getChannelMap() {
        return channelMap;
    }

    public static String getIPString(ChannelHandlerContext ctx) {
        String ipString = "";
        String socketString = ctx.channel().remoteAddress().toString();
        ipString = socketString.substring(1);
        return ipString;
    }

    public static String byte2HexStr(byte[] b) {
        String stmp = "";
        StringBuilder sb = new StringBuilder("");
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            sb.append((stmp.length() == 1) ? "0" + stmp : stmp);
//			sb.append(" ");
        }
        return sb.toString().toUpperCase().trim();
    }

    public static int dataBytesToInt(byte[] src) {
        int value;
        value = (int) ((src[0] & 0xFF)
                | ((src[1] & 0xFF) << 8));
        return value;
    }
}
