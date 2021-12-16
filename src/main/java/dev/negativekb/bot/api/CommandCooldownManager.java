package dev.negativekb.bot.api;

import dev.negativekb.bot.api.commands.Command;
import dev.negativekb.bot.api.commands.SubCommand;
import org.jetbrains.annotations.NotNull;

public interface CommandCooldownManager {

    void addCooldown(@NotNull String id, @NotNull Command command, long duration);

    void addCooldown(@NotNull String id, @NotNull SubCommand subCommand, long duration);

    boolean checkCooldown(@NotNull String id, @NotNull Command command);

    boolean checkCooldown(@NotNull String id, @NotNull SubCommand subCommand);

    long getCooldown(@NotNull String id, @NotNull Command command);

    long getCooldown(@NotNull String id, @NotNull SubCommand command);

}
