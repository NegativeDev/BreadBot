package dev.negativekb.bot.api;

import dev.negativekb.bot.core.structure.DiscordServer;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

public interface DiscordServerManager {

    void addServer(@NotNull String id);

    default void addServer(@NotNull Guild guild) {
        addServer(guild.getId());
    }

    void removeServer(@NotNull String id);

    default void removeServer(@NotNull Guild guild) {
        removeServer(guild.getId());
    }

    Optional<DiscordServer> getServer(@NotNull String id);

    default Optional<DiscordServer> getServer(@NotNull Guild guild) {
        return getServer(guild.getId());
    }

    Collection<DiscordServer> getServers();

}
