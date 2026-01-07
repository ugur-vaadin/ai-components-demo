package com.vaadin.examplefeature.ai.messagelist;

import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class MessageListWrapper extends MessageList implements AiMessageList {

    /**
     * Adds a message to the list.
     * This is an adapter method for AI orchestrators.
     *
     * @param message
     *            the message to add
     */
    @Override
    public void addMessage(AiMessage message) {
        if (message instanceof MessageListItem) {
            // Direct use if it's already a MessageListItem
            List<MessageListItem> newItems = new ArrayList<>(getItems());
            newItems.add((MessageListItem) message);
            setItems(newItems);
        } else {
            // Convert from generic AiMessage
            MessageListItem item = new MessageListItemWrapper(
                    message.getText(),
                    message.getTime(),
                    message.getUserName()
            );
            List<MessageListItem> newItems = new ArrayList<>(getItems());
            newItems.add(item);
            setItems(newItems);
        }
    }

    /**
     * Creates a new message with the given parameters.
     *
     * @param text
     *            the message text
     * @param userName
     *            the user name
     * @return the created message
     */
    @Override
    public AiMessage createMessage(String text, String userName) {
        return new MessageListItemWrapper(text, Instant.now(), userName);
    }
}
