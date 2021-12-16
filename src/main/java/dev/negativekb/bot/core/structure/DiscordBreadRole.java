package dev.negativekb.bot.core.structure;

import lombok.Data;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

import java.util.Optional;

@Data
public class DiscordBreadRole {

    private final String id;
    private final long required;

    public Optional<Role> getRole(DiscordServer server) {
        Optional<Guild> serverGuild = server.getGuild();
        if (serverGuild.isEmpty())
            return Optional.empty();

        Guild guild = serverGuild.get();
        return Optional.ofNullable(guild.getRoleById(id));
    }
}
