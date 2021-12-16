package dev.negativekb.bot.api.commands;

import dev.negativekb.bot.api.API;
import dev.negativekb.bot.api.CommandCooldownManager;
import dev.negativekb.bot.api.commands.exception.InvalidCommandInfoException;
import dev.negativekb.bot.core.util.TimeUtil;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

@Getter
public abstract class SubCommand {

    private final String name;
    private final String description;
    @Getter
    private List<String> aliases = new ArrayList<>();
    @Getter
    @Setter
    private Consumer<SubcommandData> data;
    @Setter
    private int cooldownInSeconds = 0;
    private final CommandCooldownManager cooldownManager;

    public SubCommand() {
        if (!getClass().isAnnotationPresent(CommandInfo.class))
            throw new InvalidCommandInfoException();

        CommandInfo info = getClass().getAnnotation(CommandInfo.class);
        this.name = info.name();
        this.description = info.description();

        List<String> aliasList = new ArrayList<>(Arrays.asList(info.aliases()));
        if (!aliasList.get(0).isEmpty()) {
            this.aliases = aliasList;
        }

        cooldownManager = API.getInstance().getCooldownManager();
    }

    public void runCommand(@NotNull SlashCommandEvent event) {
        if (cooldownInSeconds != 0) {
            User user = event.getUser();
            String id = user.getId();

            if (cooldownManager.checkCooldown(id, this)) {
                // Reply with cooldown message
                long cooldown = cooldownManager.getCooldown(id, this);
                String format = TimeUtil.format(cooldown, System.currentTimeMillis());
                event.reply("You cannot use this command for another " +
                        "**" + format + "**!").setEphemeral(true).queue();
                return;
            }

            long cooldownInMills = cooldownInSeconds * 1000L;
            cooldownManager.addCooldown(id, this, cooldownInMills);
        }

        onCommand(event);
    }

    public abstract void onCommand(@NotNull SlashCommandEvent event);
}