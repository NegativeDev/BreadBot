package dev.negativekb.bot.core.provider;

import dev.negativekb.bot.api.DiscordServerManager;
import dev.negativekb.bot.core.cache.ObjectCache;
import dev.negativekb.bot.core.structure.DiscordServer;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BreadBotDiscordServerManagerProvider extends ObjectCache<DiscordServer> implements DiscordServerManager {

    private ArrayList<DiscordServer> cachedServers;
    @SneakyThrows
    public BreadBotDiscordServerManagerProvider() {
        super("data/servers.json", DiscordServer[].class);

        cachedServers = load();

        new DataSaveTaskThread().start();
        System.out.println("[Tasks] Started servers.json save task!");
    }

    @Override
    public void addServer(@NotNull String id) {
        if (getServer(id).isPresent())
            return;

        DiscordServer server = new DiscordServer(id);
        cachedServers.add(server);
    }

    @Override
    public void removeServer(@NotNull String id) {
        getServer(id).ifPresent(server -> cachedServers.remove(server));
    }

    @Override
    public Optional<DiscordServer> getServer(@NotNull String id) {
        return cachedServers.stream().filter(server -> server.getId().equalsIgnoreCase(id)).findAny();
    }

    @Override
    public Collection<DiscordServer> getServers() {
        return new ArrayList<>(cachedServers);
    }


    private class DataSaveTaskThread extends Thread {
        @Override
        public void run() {
            int seconds = 30;
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new Task(), 0, (1000L * seconds));
        }
    }

    private class Task extends TimerTask {

        @SneakyThrows
        @Override
        public void run() {
            save(cachedServers);
            cachedServers = load();
        }
    }
}
