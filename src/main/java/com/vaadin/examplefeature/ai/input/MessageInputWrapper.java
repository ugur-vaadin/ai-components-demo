package com.vaadin.examplefeature.ai.input;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.messages.MessageInput;

public class MessageInputWrapper extends MessageInput implements AiInput {

    /**
     * Adds a listener that is called when the user submits input.
     * This is an adapter method for AI orchestrators.
     *
     * @param listener
     *            the listener to add
     */
    @Override
    public void addSubmitListener(InputSubmitListener listener) {
        addSubmitListener((ComponentEventListener<MessageInput.SubmitEvent>) event -> {
            InputSubmitEvent aiEvent = new InputSubmitEvent() {
                @Override
                public String getValue() {
                    return event.getValue();
                }
            };
            listener.onSubmit(aiEvent);
        });
    }
}
