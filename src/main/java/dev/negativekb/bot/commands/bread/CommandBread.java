package dev.negativekb.bot.commands.bread;

import dev.negativekb.bot.api.DiscordServerManager;
import dev.negativekb.bot.api.commands.Command;
import dev.negativekb.bot.api.commands.CommandInfo;
import dev.negativekb.bot.commands.bread.subcommands.BreadBalance;
import dev.negativekb.bot.commands.bread.subcommands.BreadLeaderboard;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.NotNull;

@CommandInfo(name = "bread", aliases = {"b"}, description = "BreadBot's Main Command")
public class CommandBread extends Command {

    public CommandBread(DiscordServerManager serverManager) {
        addSubCommands(
                new BreadBalance(serverManager),
                new BreadLeaderboard(serverManager)
        );
    }

    @Override
    public void onCommand(@NotNull SlashCommandEvent event) {

    }
}
