package dev.negativekb.bot.api;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

public abstract class API {

    @Getter @Setter
    private static API instance;

    @NotNull
    public abstract DiscordServerManager getDiscordServerManager();

    @NotNull
    public abstract CommandCooldownManager getCooldownManager();

}
