package dev.negativekb.bot.core.structure;

import dev.negativekb.bot.BreadBot;
import lombok.Data;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
public class DiscordServer {

    private final String id;
    private long messageCooldown;
    private long breadPerMessage;
    private ArrayList<DiscordBreadRole> breadRoles;
    private ArrayList<DiscordMember> cachedMembers;

    public DiscordServer(String id) {
        this.id = id;
        this.messageCooldown = 3000L; // 3 second message cooldown to receive bread for messages
        this.breadPerMessage = 1L;
        this.breadRoles = new ArrayList<>();
        this.cachedMembers = new ArrayList<>();
    }

    public void onEnable() {
        List<DiscordBreadRole> toRemove = breadRoles.stream()
                .filter(discordBreadRole -> discordBreadRole.getRole(this).isEmpty())
                .collect(Collectors.toList());

        toRemove.forEach(discordBreadRole -> breadRoles.remove(discordBreadRole));
    }

    public Optional<Guild> getGuild() {
        return Optional.ofNullable(BreadBot.getInstance().getJda().getGuildById(id));
    }

    public Optional<DiscordMember> getMember(String id) {
        return cachedMembers.stream().filter(discordMember ->
                discordMember.getId().equalsIgnoreCase(id)).findAny();
    }

    public DiscordMember createMember(String id) {
        DiscordMember member = new DiscordMember(id);
        cachedMembers.add(member);

        return member;
    }

    public void addOrUpdate(Member member, long amount) {
        String id = member.getId();
        Optional<DiscordMember> stats = getMember(id);
        if (stats.isEmpty())
            stats = Optional.of(createMember(id));

        DiscordMember discordMember = stats.get();
        long lastMessage = discordMember.getLastMessage();

        long sysMills = System.currentTimeMillis();
        if (messageCooldown == 0)
            discordMember.addBread(amount);
        else if (sysMills >= (lastMessage + messageCooldown)) {
            discordMember.addBread(amount);
            discordMember.setLastMessage(sysMills);
        }

        discordMember.setLastRecordedName(Optional.ofNullable(member.getNickname())
                .orElse(member.getEffectiveName()));

        long bread = discordMember.getBread();
        Collection<Role> breadRolesRoles = new ArrayList<>();
        breadRoles.stream().filter(discordBreadRole -> discordBreadRole.getRole(this).isPresent()).forEach(discordBreadRole -> {
            assert discordBreadRole.getRole(this).isPresent();
            breadRolesRoles.add(discordBreadRole.getRole(this).get());
        });

        breadRolesRoles.stream().filter(role -> !member.getRoles().contains(role)).forEach(role -> {
            DiscordBreadRole breadRole = getBreadRole(role);
            long required = breadRole.getRequired();
            if (bread >= required) {
                getGuild().ifPresent(guild ->
                        guild.addRoleToMember(member, role).reason("Levelled Up (Bread)").queue());
            }
        });
    }

    public void addBreadRole(Role role, long required) {
        DiscordBreadRole breadRole = new DiscordBreadRole(role.getId(), required);
        breadRoles.add(breadRole);
    }

    public DiscordBreadRole getBreadRole(Role role) {
        return breadRoles.stream()
                .filter(discordBreadRole -> discordBreadRole.getRole(this).isPresent()
                        && discordBreadRole.getRole(this).get().getId().equalsIgnoreCase(role.getId()))
                .findFirst().orElse(null);
    }

    public void removeBreadRole(Role role) {
        breadRoles.stream().filter(discordBreadRole -> discordBreadRole.getRole(this).isPresent() &&
                discordBreadRole.getRole(this).get().getId().equalsIgnoreCase(role.getId()))
                .findFirst().ifPresent(discordBreadRole -> breadRoles.remove(discordBreadRole));
    }
}
