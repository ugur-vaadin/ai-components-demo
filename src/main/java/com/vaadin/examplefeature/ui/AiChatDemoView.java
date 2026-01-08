package com.vaadin.examplefeature.ui;

import com.vaadin.examplefeature.ai.orchestrator.AiOrchestrator;
import com.vaadin.examplefeature.ai.provider.langchain4j.LangChain4JLLMProvider;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.Route;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;

@Route("vaadin-ai/ai-chat-demo")
@CssImport("@vaadin/vaadin-lumo-styles/lumo.css")
@Menu(order = 0, icon = "vaadin:clipboard-check", title = "AI Chat")
public class AiChatDemoView extends VerticalLayout {

    public AiChatDemoView() {
        setSizeFull();

        // Create UI components
        var messageList = new MessageList();
        messageList.setSizeFull();
        var messageInput = new MessageInput();

        // Upload Component for attachments
        var upload = new Upload();
        upload.setWidthFull();
        upload.setMaxFiles(5);
        upload.setMaxFileSize(5 * 1024 * 1024); // 5 MB
        upload.setAcceptedFileTypes("image/*", "application/pdf",
                "text/plain");
        upload.getElement().appendChild(messageInput.getElement());

        add(messageList, upload);
        setFlexGrow(1, messageList);
        setFlexShrink(0, upload);

        // Create LLM provider
        var model = OpenAiStreamingChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName("gpt-4o-mini").build();
        var provider = new LangChain4JLLMProvider(model);

        var systemPrompt = "You are a helpful product return assistant for an e-commerce store. "
                + "Help customers with return and refund questions. "
                + "Our return policy allows returns within 30 days of purchase for most items. "
                + "If customers upload product photos or receipts, use them to better assist with their return request. "
                + "Be friendly and professional. Use the checkReturnEligibility tool to verify if an order is eligible for return.";

        // Create return tools
        var returnTools = new ReturnTools();

        // Create and configure orchestrator with input validation
        AiOrchestrator.builder(provider, systemPrompt)
                .withMessageList(messageList)
                .withInput(messageInput)
                .withFileReceiver(upload)
                .withVendorToolObjects(returnTools)
                .build();
    }

    /**
     * Tools for handling product return operations.
     */
    public static class ReturnTools {

        @Tool("Check if an order is eligible for return based on the order ID and days since purchase")
        public String checkReturnEligibility(String orderId, int daysSincePurchase) {
            if (orderId == null || orderId.trim().isEmpty()) {
                return "Error: Order ID is required";
            }

            if (daysSincePurchase < 0) {
                return "Error: Days since purchase cannot be negative";
            }

            // Mock return policy: 30 days for most items
            boolean eligible = daysSincePurchase <= 30;

            if (eligible) {
                return String.format(
                        "Order %s is ELIGIBLE for return. The order was placed %d days ago, which is within our 30-day return window. "
                                + "The customer can proceed with the return process.",
                        orderId, daysSincePurchase);
            } else {
                return String.format(
                        "Order %s is NOT ELIGIBLE for return. The order was placed %d days ago, which exceeds our 30-day return window. "
                                + "The return period ended %d days ago.",
                        orderId, daysSincePurchase, daysSincePurchase - 30);
            }
        }
    }
}
