package dev.negativekb.bot.core.structure;

import lombok.Data;

@Data
public class DiscordMember {

    private final String id;
    private String lastRecordedName;
    private long bread;
    private long lastMessage;

    public void addBread(long amount) {
        setBread(getBread() + amount);
    }

    public void removeBread(long amount) {
        setBread(getBread() - amount);
        if (getBread() < 0)
            setBread(0);
    }
}
