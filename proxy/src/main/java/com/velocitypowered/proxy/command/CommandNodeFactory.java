package com.velocitypowered.proxy.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandInvocation;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.InvocableCommand;
import com.velocitypowered.api.command.RawCommand;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.proxy.util.BrigadierUtils;

@FunctionalInterface
public interface CommandNodeFactory<T extends Command> {

  InvocableCommandNodeFactory<SimpleCommand.Invocation> SIMPLE =
      new InvocableCommandNodeFactory<>(VelocitySimpleCommandInvocation.FACTORY);

  InvocableCommandNodeFactory<RawCommand.Invocation> RAW =
      new InvocableCommandNodeFactory<>(VelocityRawCommandInvocation.FACTORY);

  CommandNodeFactory<Command> FALLBACK = (alias, command) ->
      BrigadierUtils.buildRawArgumentsLiteral(alias,
        source -> command.hasPermission(source, new String[0]),
        context -> {
          CommandSource source = context.getSource();
          String[] args = BrigadierUtils.getSplitArguments(context);

          if (!command.hasPermission(source, args)) {
            return BrigadierCommand.FORWARD;
          }
          command.execute(source, args);
          return 1;
        },
        (context, builder) -> {
          String[] args = BrigadierUtils.getSplitArguments(context);
          if (!command.hasPermission(context.getSource(), args)) {
              return builder.buildFuture();
          }

          return command.suggestAsync(context.getSource(), args).thenApply(values -> {
            for (String value : values) {
              builder.suggest(value);
            }

            return builder.build();
          });
        });

  /**
   * Returns a Brigadier node for the execution of the given command.
   *
   * @param alias the command alias
   * @param command the command to execute
   * @return the command node
   */
  LiteralCommandNode<CommandSource> create(String alias, T command);

  class InvocableCommandNodeFactory<I extends CommandInvocation<?>>
          implements CommandNodeFactory<InvocableCommand<I>> {

    private final CommandInvocationFactory<I> invocationFactory;

    protected InvocableCommandNodeFactory(CommandInvocationFactory<I> invocationFactory) {
      this.invocationFactory = invocationFactory;
    }

    @Override
    public LiteralCommandNode<CommandSource> create(
            final String alias, final InvocableCommand<I> command) {
      return BrigadierUtils.buildRawArgumentsLiteral(alias,
          source -> command.hasPermission(invocationFactory.create(source)),
          context -> {
            I invocation = invocationFactory.create(context);
            if (!command.hasPermission(invocation)) {
              return BrigadierCommand.FORWARD;
            }
            command.execute(invocation);
            return 1;
          },
          (context, builder) -> {
            I invocation = invocationFactory.create(context);

            if (!command.hasPermission(invocation)) {
                return builder.buildFuture();
            }
            return command.suggestAsync(invocation).thenApply(values -> {
              for (String value : values) {
                builder.suggest(value);
              }

              return builder.build();
            });
          });
    }
  }
}
