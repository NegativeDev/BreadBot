package dev.negativekb.bot.listeners;

import dev.negativekb.bot.api.DiscordServerManager;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class PersistentGuildListener extends ListenerAdapter {

    private final DiscordServerManager serverManager;

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        Guild guild = event.getGuild();

        System.out.println("[Server Adder] Adding " + guild.getName() + " to the servers.json file!");
        serverManager.addServer(guild);
    }

    @Override
    public void onGuildLeave(@NotNull GuildLeaveEvent event) {
        Guild guild = event.getGuild();
        System.out.println("[Server Remover] Removing " + guild.getName() + " from the servers.json file!");
        serverManager.removeServer(guild);
    }
}
