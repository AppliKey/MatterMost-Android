package com.applikey.mattermost.utils;

import com.applikey.mattermost.models.SearchItem;
import com.applikey.mattermost.models.post.Message;

import java.util.Comparator;

/**
 * @author Anatoliy Chub
 */

public class MessageDateComparator implements Comparator<SearchItem> {

    @Override
    public int compare(SearchItem o1, SearchItem o2) {
        final Message message1 = (Message) o1;
        final Message message2 = (Message) o2;
        final long lastPost1 = message1.getChannel().getLastPostAt();
        final long lastPost2 = message2.getChannel().getLastPostAt();
        if(lastPost1 == 0 || lastPost2 == 0){
            return (int) (message2.getChannel().getCreatedAt() - message1.getChannel().getCreatedAt());
        }
        return (int) (lastPost2 - lastPost1);
    }
}
