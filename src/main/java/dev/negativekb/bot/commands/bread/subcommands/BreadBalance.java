package dev.negativekb.bot.commands.bread.subcommands;

import dev.negativekb.bot.api.DiscordServerManager;
import dev.negativekb.bot.api.commands.CommandInfo;
import dev.negativekb.bot.api.commands.SubCommand;
import dev.negativekb.bot.core.structure.DiscordMember;
import dev.negativekb.bot.core.structure.DiscordServer;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.Optional;

@CommandInfo(name = "balance", description = "Check your current bread balance!")
public class BreadBalance extends SubCommand {

    private final DiscordServerManager serverManager;
    private final DecimalFormat df = new DecimalFormat("###,###,###,###,###.##");

    public BreadBalance(DiscordServerManager serverManager) {
        this.serverManager = serverManager;

        setCooldownInSeconds(5);

        setData(data -> data.addOption(OptionType.USER, "user", "Check a user's bread balance"));
    }

    @Override
    public void onCommand(@NotNull SlashCommandEvent event) {
        OptionMapping userOption = event.getOption("user");
        User user = event.getUser();
        if (userOption != null)
            user = userOption.getAsUser();

        Guild guild = event.getGuild();
        assert guild != null;

        Optional<DiscordServer> server = serverManager.getServer(guild);
        if (server.isEmpty()) {
            event.reply("Something went wrong while attempting to retrieve your stats.")
                    .setEphemeral(true).queue();
            return;
        }

        DiscordServer discordServer = server.get();
        Optional<DiscordMember> member = discordServer.getMember(user.getId());

        long result = 0;
        if (member.isPresent())
            result = member.get().getBread();

        event.reply("Bread Balance of **" + user.getName() + "** is **"
                + df.format(result) + "**").setEphemeral(true).queue();
    }
}
