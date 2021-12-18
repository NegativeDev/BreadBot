package dev.negativekb.bot;

import dev.negativekb.bot.api.API;
import dev.negativekb.bot.api.CommandMap;
import dev.negativekb.bot.api.DiscordServerManager;
import dev.negativekb.bot.api.commands.Command;
import dev.negativekb.bot.commands.bread.CommandBread;
import dev.negativekb.bot.commands.breadconfig.CommandBreadConfig;
import dev.negativekb.bot.core.properties.PropertiesConfiguration;
import dev.negativekb.bot.core.properties.PropertiesFile;
import dev.negativekb.bot.core.provider.BreadBotAPIProvider;
import dev.negativekb.bot.core.provider.CommandMapProvider;
import dev.negativekb.bot.core.structure.DiscordServer;
import dev.negativekb.bot.listeners.MemberMessageListener;
import dev.negativekb.bot.listeners.PersistentGuildListener;
import dev.negativekb.bot.listeners.SlashCommandListener;
import lombok.Getter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

@Getter
public class BreadBot {
    @Getter
    private static BreadBot instance;
    private final PropertiesConfiguration botConfig;
    private final CommandMap commandMap;
    private final JDA jda;

    @SneakyThrows
    public BreadBot() {
        instance = this;

        PropertiesFile propertiesFile = new PropertiesFile(null, "bot.properties");
        propertiesFile.createFile(true);

        botConfig = new PropertiesConfiguration(propertiesFile.asFile());

        String token = botConfig.getString("token");

        JDABuilder builder = JDABuilder.createDefault(token);
        builder.setActivity(Activity.watching("bread"));
        builder.setStatus(OnlineStatus.DO_NOT_DISTURB);

        commandMap = new CommandMapProvider();

        // API Initialization
        new BreadBotAPIProvider();

        API api = API.getInstance();
        DiscordServerManager serverManager = api.getDiscordServerManager();

        // Commands
        String dev = botConfig.getString("dev.discord.id");

        registerGlobalCommand(new CommandBread(serverManager));
        registerGlobalCommand(new CommandBreadConfig(serverManager));

        // Listeners
        builder.addEventListeners(new SlashCommandListener(commandMap));
        builder.addEventListeners(new PersistentGuildListener(serverManager));
        builder.addEventListeners(new MemberMessageListener(serverManager));

        // Finalization
        jda = builder.build().awaitReady();
        initializeCommands(jda);
        serverManager.getServers().forEach(DiscordServer::onEnable);
    }

    /**
     * Register a {@link Command} as a global command
     * @param command {@link Command} instance
     * @apiNote This may take up to an hour for Discord to register it!
     */
    public void registerGlobalCommand(@NotNull Command command) {
        commandMap.registerGlobalCommand(command.getName(), command);
    }

    /**
     * Register a {@link Command} as a server command
     * @param serverID {@link Guild} ID
     * @param command {@link Command} instance
     * @apiNote This should register almost instantly!
     */
    public void registerServerCommand(@NotNull String serverID, @NotNull Command command) {
        commandMap.registerServerCommand(serverID, command.getName(), command);
    }

    /**
     * Initalize all the commands in the {@link CommandMap} to Discord
     * @apiNote This should be called after {@link JDABuilder#build()#awaitReady()}
     * @param jda {@link JDA} instance
     */
    @SuppressWarnings("all")
    public void initializeCommands(@NotNull JDA jda) {
        // Global Commands
        Collection<Command> globalCommands = commandMap.getGlobalCommands();
        CommandListUpdateAction commands = jda.updateCommands();

        globalCommands.forEach(command -> {
            if (!command.getAliases().isEmpty()) {
                command.getAliases().forEach(name -> {
                    CommandData commandData = new CommandData(name, command.getDescription());
                    Optional.ofNullable(command.getData()).ifPresent(data -> data.accept(commandData));
                    if (!command.getSubCommands().isEmpty()) {
                        command.getSubCommands().forEach(subCommand -> {
                            if (!subCommand.getAliases().isEmpty()) {
                                subCommand.getAliases().forEach(subName -> {
                                    SubcommandData subcommandData = new SubcommandData(subName, subCommand.getDescription());
                                    Optional.ofNullable(subCommand.getData()).ifPresent(data -> data.accept(subcommandData));
                                    commandData.addSubcommands(subcommandData);
                                });
                            }

                            SubcommandData subcommandData = new SubcommandData(subCommand.getName(), subCommand.getDescription());
                            Optional.ofNullable(subCommand.getData()).ifPresent(data -> data.accept(subcommandData));
                            commandData.addSubcommands(subcommandData);
                        });
                    }
                    System.out.println("[Command Registry] Registered Global Command `" + commandData.getName() +"`");
                    commands.addCommands(commandData);
                });
            }

            CommandData commandData = new CommandData(command.getName(), command.getDescription());
            Optional.ofNullable(command.getData()).ifPresent(data -> data.accept(commandData));
            if (!command.getSubCommands().isEmpty()) {
                command.getSubCommands().forEach(subCommand -> {
                    if (!subCommand.getAliases().isEmpty()) {
                        subCommand.getAliases().forEach(name -> {
                            SubcommandData subcommandData = new SubcommandData(name, subCommand.getDescription());
                            Optional.ofNullable(subCommand.getData()).ifPresent(data -> data.accept(subcommandData));
                            commandData.addSubcommands(subcommandData);
                        });
                    }

                    SubcommandData subcommandData = new SubcommandData(subCommand.getName(), subCommand.getDescription());
                    Optional.ofNullable(subCommand.getData()).ifPresent(data -> data.accept(subcommandData));
                    commandData.addSubcommands(subcommandData);
                });
            }
            System.out.println("[Command Registry] Registered Global Command `" + commandData.getName() +"`");
            commands.addCommands(commandData);
        });

        commands.queue();

        // Server Bound Commands
        commandMap.getAllServerCommands().entrySet().stream().filter(serverEntry -> jda.getGuildById(serverEntry.getKey()) != null).forEach(serverEntry -> {
            Guild guild = jda.getGuildById(serverEntry.getKey());
            assert guild != null;
            CommandListUpdateAction guildCommands = guild.updateCommands();

            Collection<Command> serverCommands = serverEntry.getValue();
            serverCommands.forEach(command -> {
                if (!command.getAliases().isEmpty()) {
                    command.getAliases().forEach(name -> {
                        CommandData commandData = new CommandData(name, command.getDescription());
                        Optional.ofNullable(command.getData()).ifPresent(data -> data.accept(commandData));
                        if (!command.getSubCommands().isEmpty()) {
                            command.getSubCommands().forEach(subCommand -> {
                                if (!subCommand.getAliases().isEmpty()) {
                                    subCommand.getAliases().forEach(subName -> {
                                        SubcommandData subcommandData = new SubcommandData(subName, subCommand.getDescription());
                                        Optional.ofNullable(subCommand.getData()).ifPresent(data -> data.accept(subcommandData));
                                        commandData.addSubcommands(subcommandData);
                                    });
                                }

                                SubcommandData subcommandData = new SubcommandData(subCommand.getName(), subCommand.getDescription());
                                Optional.ofNullable(subCommand.getData()).ifPresent(data -> data.accept(subcommandData));
                                commandData.addSubcommands(subcommandData);
                            });
                        }
                        System.out.println("[Command Registry] Registered Server Command `" + commandData.getName()
                                + "` to Guild `" + guild.getName() + "`");
                        guildCommands.addCommands(commandData);
                    });
                }

                CommandData commandData = new CommandData(command.getName(), command.getDescription());
                Optional.ofNullable(command.getData()).ifPresent(data -> data.accept(commandData));
                if (!command.getSubCommands().isEmpty()) {
                    command.getSubCommands().forEach(subCommand -> {
                        if (!subCommand.getAliases().isEmpty()) {
                            subCommand.getAliases().forEach(name -> {
                                SubcommandData subcommandData = new SubcommandData(name, subCommand.getDescription());
                                Optional.ofNullable(subCommand.getData()).ifPresent(data -> data.accept(subcommandData));
                                commandData.addSubcommands(subcommandData);
                            });
                        }

                        SubcommandData subcommandData = new SubcommandData(subCommand.getName(), subCommand.getDescription());
                        Optional.ofNullable(subCommand.getData()).ifPresent(data -> data.accept(subcommandData));
                        commandData.addSubcommands(subcommandData);
                    });
                }
                System.out.println("[Command Registry] Registered Server Command `" + commandData.getName()
                        + "` to Guild `" + guild.getName() + "`");
                guildCommands.addCommands(commandData);
            });

            guildCommands.queue();

        });

    }
}
