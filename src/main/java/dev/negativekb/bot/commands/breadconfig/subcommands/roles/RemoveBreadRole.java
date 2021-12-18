package dev.negativekb.bot.commands.breadconfig.subcommands.roles;

import dev.negativekb.bot.api.DiscordServerManager;
import dev.negativekb.bot.api.commands.CommandInfo;
import dev.negativekb.bot.api.commands.SubCommand;
import dev.negativekb.bot.core.structure.DiscordServer;
import dev.negativekb.bot.core.util.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

@CommandInfo(name = "removebreadrole", aliases = {"removerole"}, description = "Remove a BreadRole Goal")
public class RemoveBreadRole extends SubCommand {

    private final DiscordServerManager serverManager;
    public RemoveBreadRole(DiscordServerManager serverManager) {
        this.serverManager = serverManager;
        setData(data -> data.addOption(OptionType.ROLE, "role",
                "The role to be removed from the BreadRole Roles", true));
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

        Role role = Objects.requireNonNull(event.getOption("role")).getAsRole();

        Optional<DiscordServer> server = serverManager.getServer(guild);
        if (server.isEmpty()) {
            event.reply("Something went wrong while attempting to get" +
                    " server information.").setEphemeral(true).queue();
            return;
        }

        DiscordServer discordServer = server.get();
        boolean alreadyHasRole = discordServer.getBreadRoles().stream()
                .anyMatch(discordBreadRole -> discordBreadRole.getRole(discordServer).isPresent() &&
                        discordBreadRole.getRole(discordServer).get().getId().equalsIgnoreCase(role.getId()));

        if (!alreadyHasRole) {
            event.reply("This Role is not a BreadRole Goal!").setEphemeral(true).queue();
            return;
        }

        discordServer.removeBreadRole(role);
        event.reply("Successfully removed " + role.getAsMention() + " from the BreadRole Goals!")
                .setEphemeral(true).queue();
    }
}
