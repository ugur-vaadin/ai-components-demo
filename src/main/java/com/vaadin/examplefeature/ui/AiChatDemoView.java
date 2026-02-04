package com.vaadin.examplefeature.ui;

import com.vaadin.flow.component.ai.orchestrator.AiOrchestrator;
import com.vaadin.flow.component.ai.provider.LangChain4JLLMProvider;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.upload.UploadButton;
import com.vaadin.flow.component.upload.UploadDropZone;
import com.vaadin.flow.component.upload.UploadFileList;
import com.vaadin.flow.component.upload.UploadFileListVariant;
import com.vaadin.flow.component.upload.UploadManager;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.Route;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;

@Route("vaadin-ai/ai-chat-demo")
@CssImport("@vaadin/vaadin-lumo-styles/lumo.css")
@Menu(order = 0, icon = "vaadin:clipboard-check", title = "AI Chat")
public class AiChatDemoView extends UploadDropZone {

    public AiChatDemoView() {
        setSizeFull();

        var layout = new VerticalLayout();
        layout.setSizeFull();
        add(layout);

        // Create UI components
        var messageList = new MessageList();
        messageList.setSizeFull();
        var messageInput = new MessageInput();
        messageInput.setWidthFull();

        // Upload for attachments
        var uploadManager = new UploadManager(this);
        uploadManager.setMaxFiles(5);
        uploadManager.setMaxFileSize(5 * 1024 * 1024); // 5 MB
        uploadManager.setAcceptedFileTypes("image/*", "application/pdf",
                "text/plain");

        setUploadManager(uploadManager);

        var uploadButton = new UploadButton(uploadManager);
        uploadButton.setIcon(VaadinIcon.UPLOAD.create());
        var inputLayout = new HorizontalLayout(uploadButton, messageInput);
        inputLayout.setWidthFull();
        inputLayout.setAlignItems(Alignment.BASELINE);
        inputLayout.setSpacing(false);

        var uploadFileList = new UploadFileList(uploadManager);
        uploadFileList.getElement().getStyle().setWidth("100%");
        uploadFileList.addThemeName(UploadFileListVariant.LUMO_THUMBNAILS.getVariantName());

        var bottomLayout  = new VerticalLayout(uploadFileList, inputLayout);
        
        layout.add(messageList, bottomLayout);
        layout.setFlexGrow(1, messageList);
        layout.setFlexShrink(0, bottomLayout);

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
                .withFileReceiver(uploadManager)
                .withTools(returnTools)
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
