package dev.negativekb.bot.commands.breadconfig.subcommands;

import dev.negativekb.bot.api.DiscordServerManager;
import dev.negativekb.bot.api.commands.CommandInfo;
import dev.negativekb.bot.api.commands.SubCommand;
import dev.negativekb.bot.core.structure.DiscordServer;
import dev.negativekb.bot.core.util.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

@CommandInfo(name = "setbreadpermessage", aliases = {"setbpm"},
        description = "Set the amount of Bread Per Message for the server")
public class BreadPerMessage extends SubCommand {

    private final DiscordServerManager serverManager;
    public BreadPerMessage(DiscordServerManager serverManager) {
        this.serverManager = serverManager;

        setData(data ->
                data.addOption(OptionType.INTEGER, "amount",
                        "Set the amount of Bread Per Message for the server", true));
    }

    @Override
    public void onCommand(@NotNull SlashCommandEvent event) {
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

        long amount = Objects.requireNonNull(event.getOption("amount")).getAsLong();

        Optional<DiscordServer> server = serverManager.getServer(guild);
        if (server.isEmpty()) {
            event.reply("Something went wrong while attempting to get" +
                    " server information.").setEphemeral(true).queue();
            return;
        }

        DiscordServer discordServer = server.get();
        discordServer.setBreadPerMessage(amount);

        event.reply("You have successfully set the Bread Per Message " +
                "amount to **" + Utils.decimalFormat(amount) + "** for the server!")
                .setEphemeral(true).queue();
    }
}
