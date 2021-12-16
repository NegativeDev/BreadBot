package dev.negativekb.bot.listeners;

import dev.negativekb.bot.api.DiscordServerManager;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class MemberMessageListener extends ListenerAdapter {

    private final DiscordServerManager serverManager;

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        Guild guild = event.getGuild();
        Member member = event.getMember();
        if (member == null || member.getUser().isBot())
            return;

        serverManager.getServer(guild).ifPresent(server ->
                server.addOrUpdate(member, server.getBreadPerMessage()));
    }
}
