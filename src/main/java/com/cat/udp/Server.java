/*
 * Copyright (C) 2017 Christopher Towner
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.cat.udp;

import com.cat.udp.UDP.Update;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.DatagramPacketDecoder;
import io.netty.handler.codec.protobuf.ProtobufDecoder;

/**
 * Receive an update.
 *
 * @author Christopher Towner
 */
public class Server extends SimpleChannelInboundHandler<Update> {

    public static final int PORT = 9999;

    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new ServerInitializer());
            b.bind(PORT).sync().channel().closeFuture().await();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        } finally {
            group.shutdownGracefully();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, Update update) throws Exception {
        System.out.println(update.getText());
    }
}

class ServerInitializer extends ChannelInitializer<DatagramChannel> {

    @Override
    protected void initChannel(DatagramChannel c) throws Exception {
        ChannelPipeline p = c.pipeline();
        p.addLast(new DatagramPacketDecoder(new ProtobufDecoder(Update.getDefaultInstance())));
        p.addLast(new Server());
    }

}
