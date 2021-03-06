package com.velocitypowered.proxy.protocol.packet;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.connection.MinecraftSessionHandler;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import io.netty.buffer.ByteBuf;

public class StatusPing implements MinecraftPacket {

  private long randomId;

  @Override
  public void decode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion version) {
    randomId = buf.readLong();
  }

  @Override
  public void encode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion version) {
    buf.writeLong(randomId);
  }

  @Override
  public boolean handle(MinecraftSessionHandler handler) {
    return handler.handle(this);
  }
}
