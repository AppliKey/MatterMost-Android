package com.applikey.mattermost.models.channel;

import java.util.Comparator;

public class ChannelWithMetadata {

    public static final Comparator<ChannelWithMetadata> COMPARATOR_BY_DATE =
            new ChannelWithMetadata.ComparatorByDate();

    private Channel channel;
    private Membership membership;

    public ChannelWithMetadata() {

    }

    public ChannelWithMetadata(Channel channel) {
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Membership getMembership() {
        return membership;
    }

    public void setMembership(Membership membership) {
        this.membership = membership;
    }

    public boolean checkIsUnread() {
        long lastViewedAt = getMembership().getLastViewedAt();
        long lastPostAt = getChannel().getLastPostAt();

        return lastPostAt > lastViewedAt;
    }

    private static class ComparatorByDate implements Comparator<ChannelWithMetadata> {
        @Override
        public int compare(ChannelWithMetadata o1, ChannelWithMetadata o2) {
            final Channel o1channel = o1.getChannel();
            final Channel o2channel = o2.getChannel();
            final long o1time = o1channel.getLastPostAt() != 0
                    ? o1channel.getLastPostAt() : o1channel.getCreatedAt();
            final long o2time = o2channel.getLastPostAt() != 0
                    ? o2channel.getLastPostAt() : o2channel.getCreatedAt();

            if (o1time > o2time) {
                return -1;
            }
            if (o1time == o2time) {
                return 0;
            }
            return 1;
        }
    }
}
