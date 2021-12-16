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

@CommandInfo(name = "addbreadrole", aliases = {"addrole"}, description = "Add a BreadRole Goal")
public class AddBreadRole extends SubCommand {

    private final DiscordServerManager serverManager;
    public AddBreadRole(DiscordServerManager serverManager) {
        this.serverManager = serverManager;

        setData(data -> data.addOption(OptionType.ROLE, "role", "The role to become a new BreadRole Goal", true)
                .addOption(OptionType.INTEGER, "required", "The required amount of Bread to reach this goal"));
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
        long required = Objects.requireNonNull(event.getOption("required")).getAsLong();

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

        if (alreadyHasRole) {
            event.reply("This Role is already a BreadRole Goal!").setEphemeral(true).queue();
            return;
        }

        discordServer.addBreadRole(role, required);
        event.reply("Successfully added " + role.getAsMention() + " as a BreadRole Goal for **" +
                Utils.decimalFormat(required) + "** Bread!").setEphemeral(true).queue();
    }
}
