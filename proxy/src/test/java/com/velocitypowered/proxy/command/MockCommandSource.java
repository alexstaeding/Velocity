package com.velocitypowered.proxy.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.permission.Tristate;
import net.kyori.text.Component;

import java.util.function.Function;

public class MockCommandSource implements CommandSource {

  public static final CommandSource INSTANCE = new MockCommandSource();

  private static final Function<String, Tristate> ALWAYS_UNDEFINED = permission -> Tristate.UNDEFINED;
  private Function<String, Tristate> permissionFunction = ALWAYS_UNDEFINED;

  @Override
  public void sendMessage(final Component component) {

  }

  public void setPermissionFunction(final Function<String, Tristate> permissionFunction) {
    this.permissionFunction = permissionFunction;
  }

  public void resetPermissionFunction() {
    this.permissionFunction = ALWAYS_UNDEFINED;
  }

  @Override
  public Tristate getPermissionValue(final String permission) {
    return Tristate.UNDEFINED;
  }
}
