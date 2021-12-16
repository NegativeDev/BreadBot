package dev.negativekb.bot.commands.breadconfig;

import dev.negativekb.bot.api.DiscordServerManager;
import dev.negativekb.bot.api.commands.Command;
import dev.negativekb.bot.api.commands.CommandInfo;
import dev.negativekb.bot.commands.breadconfig.subcommands.BreadPerMessage;
import dev.negativekb.bot.commands.breadconfig.subcommands.roles.AddBreadRole;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.NotNull;

@CommandInfo(name = "breadconfig", aliases = {"bconfig"}, description = "Configure the bot for your server!")
public class CommandBreadConfig extends Command {

    public CommandBreadConfig(DiscordServerManager serverManager) {
        addSubCommands(
            new AddBreadRole(serverManager),
            new BreadPerMessage(serverManager)
        );
    }

    @Override
    public void onCommand(@NotNull SlashCommandEvent event) {

    }
}
