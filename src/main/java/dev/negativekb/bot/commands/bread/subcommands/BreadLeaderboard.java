package dev.negativekb.bot.commands.bread.subcommands;

import dev.negativekb.bot.BreadBot;
import dev.negativekb.bot.api.DiscordServerManager;
import dev.negativekb.bot.api.commands.CommandInfo;
import dev.negativekb.bot.api.commands.SubCommand;
import dev.negativekb.bot.core.structure.DiscordMember;
import dev.negativekb.bot.core.structure.DiscordServer;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@CommandInfo(name = "leaderboard", aliases = {"lb", "breadmakers"}, description = "Check the top bread makers!")
public class BreadLeaderboard extends SubCommand {

    private final DiscordServerManager serverManager;
    private final DecimalFormat df = new DecimalFormat("###,###,###,###.##");

    public BreadLeaderboard(DiscordServerManager serverManager) {
        this.serverManager = serverManager;

        setCooldownInSeconds(10);
    }

    @Override
    public void onCommand(@NotNull SlashCommandEvent event) {
        Guild guild = event.getGuild();
        assert guild != null;

        User user = event.getUser();

        Optional<DiscordServer> server = serverManager.getServer(guild);
        if (server.isEmpty()) {
            event.reply("There was an issue while attempting to" +
                    " retrieve the data for this Guild.").setEphemeral(true).queue();
            return;
        }

        DiscordServer discordServer = server.get();
        ArrayList<DiscordMember> cachedMembers = discordServer.getCachedMembers();

        List<DiscordMember> topBread = cachedMembers.stream()
                .sorted(Comparator.comparing(DiscordMember::getBread).reversed())
                .collect(Collectors.toList());

        StringBuilder leaderboardBuilder = new StringBuilder();
        leaderboardBuilder.append("__Top Bread Makers__").append("\n\n");

        JDA jda = BreadBot.getInstance().getJda();

        AtomicInteger iteration = new AtomicInteger(0);
        topBread.stream().limit(10).forEach(member -> {
            String userName = member.getLastRecordedName();
            Member memberById = guild.getMemberById(member.getId());
            if (memberById != null)
                userName = memberById.getAsMention();

            leaderboardBuilder.append("**#").append(iteration.incrementAndGet())
                    .append("** ").append(userName).append(": **")
                    .append(df.format(member.getBread())).append("**").append("\n");
        });

        int index = -1;
        long bread = 0;
        Optional<DiscordMember> member = discordServer.getMember(user.getId());
        if (member.isPresent()) {
            DiscordMember discordMember = member.get();
            index = getIndex(discordMember, topBread);
            bread = discordMember.getBread();
        }

        leaderboardBuilder.append("\n").append("Your Position: **#").append(index == -1 ? "N/A" : df.format(index))
                .append("** (").append(df.format(bread)).append(")");

        event.reply(leaderboardBuilder.toString()).setEphemeral(true).queue();
    }

    private int getIndex(DiscordMember member, List<DiscordMember> list) {
        int position = 1;
        for (DiscordMember discordMember : list) {
            if (member.getId().equalsIgnoreCase(discordMember.getId()))
                return position;

            position++;
        }

        return -1;
    }

}
