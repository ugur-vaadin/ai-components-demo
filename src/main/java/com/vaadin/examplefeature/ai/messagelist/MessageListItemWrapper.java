package com.vaadin.examplefeature.ai.messagelist;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.messages.MessageListItem;

import java.time.Instant;

public class MessageListItemWrapper extends MessageListItem implements AiMessage {

    private Component prefix;

    public MessageListItemWrapper(String text, Instant time, String userName) {
        super(text, time, userName);
    }

    /**
     * Gets the prefix component that will be rendered before the message
     * content.
     *
     * @return the prefix component, or {@code null} if none is set
     */
    @JsonIgnore
    public Component getPrefix() {
        return prefix;
    }

    /**
     * Sets a component to be rendered as a prefix before the message content.
     * <p>
     * The prefix component will be rendered using a custom renderer in the
     * message list. This allows adding custom UI elements like icons, badges,
     * or other components to messages.
     * </p>
     *
     * @param prefix
     *            the component to render as prefix, or {@code null} to remove
     *            the prefix
     */
    public void setPrefix(Component prefix) {
        this.prefix = prefix;
        try {
            var m = getClass().getSuperclass().getDeclaredMethod("propsChanged");
            m.setAccessible(true);
            m.invoke(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
