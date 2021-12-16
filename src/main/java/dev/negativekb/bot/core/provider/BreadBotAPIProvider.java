package dev.negativekb.bot.core.provider;

import dev.negativekb.bot.api.API;
import dev.negativekb.bot.api.CommandCooldownManager;
import dev.negativekb.bot.api.DiscordServerManager;
import org.jetbrains.annotations.NotNull;

public class BreadBotAPIProvider extends API {

    private final DiscordServerManager serverManager;
    private final CommandCooldownManager cooldownManager;
    public BreadBotAPIProvider() {
        setInstance(this);

        serverManager = new BreadBotDiscordServerManagerProvider();
        cooldownManager = new BreadBotCooldownManagerProvider();
    }

    @Override
    public @NotNull DiscordServerManager getDiscordServerManager() {
        return serverManager;
    }

    @Override
    public @NotNull CommandCooldownManager getCooldownManager() {
        return cooldownManager;
    }
}
