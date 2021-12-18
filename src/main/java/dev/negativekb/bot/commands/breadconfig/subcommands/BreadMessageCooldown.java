package dev.negativekb.bot.commands.breadconfig.subcommands;

import dev.negativekb.bot.api.DiscordServerManager;
import dev.negativekb.bot.api.commands.CommandInfo;
import dev.negativekb.bot.api.commands.SubCommand;
import dev.negativekb.bot.core.structure.DiscordServer;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

@CommandInfo(name = "messagecooldown", aliases = {"msgcooldown"},
        description = "Set the Bread Message Cooldown for the server!")
public class BreadMessageCooldown extends SubCommand {

    private final DiscordServerManager serverManager;
    public BreadMessageCooldown(DiscordServerManager serverManager) {
        this.serverManager = serverManager;

        setData(data -> data.addOption(OptionType.INTEGER, "time",
                "Duration in milliseconds", true));
    }

    @Override
    public void onCommand(@NotNull SlashCommandEvent event) {
        long time = Objects.requireNonNull(event.getOption("time")).getAsLong();

        Guild guild = event.getGuild();
        assert guild != null;

        User user = event.getUser();
        Member member = guild.getMember(user);
        if (member == null) {
            event.reply("Something went wrong while attempting to check " +
                    "your permission level.").setEphemeral(true).queue();
            return;
        }
        boolean hasAdmin = member.getPermissions()
                .stream().anyMatch(permission -> permission.equals(Permission.ADMINISTRATOR));

        if (!hasAdmin) {
            event.reply("You do not have permission to use this command!").setEphemeral(true).queue();
            return;
        }

        Optional<DiscordServer> server = serverManager.getServer(guild);
        if (server.isEmpty()) {
            event.reply("Something went wrong while attempting to get" +
                    " server information.").setEphemeral(true).queue();
            return;
        }

        DiscordServer discordServer = server.get();
        discordServer.setMessageCooldown(time);

        event.reply("You have set the message cooldown duration to **" +
                time + "** milliseconds!").setEphemeral(true).queue();
    }
}
