/*
 * Copyright 2000-2026 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.examplefeature.ui;

import com.vaadin.flow.component.ai.chart.ChartAIController;
import com.vaadin.flow.component.ai.orchestrator.AIOrchestrator;
import com.vaadin.flow.component.ai.provider.LangChain4JLLMProvider;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import dev.langchain4j.model.openai.OpenAiStreamingChatModel;


@Route("vaadin-ai/ai-chart-demo")
public class AiChartDemoView extends HorizontalLayout {

    public AiChartDemoView() {
        setSizeFull();

        var chatModel = OpenAiStreamingChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName("gpt-4o-mini").build();

        var chart = new Chart();
        chart.setSizeFull();

        var chartController = new ChartAIController(chart,
                new InMemoryDatabaseProvider());

        var messageList = new MessageList();
        messageList.setSizeFull();

        var messageInput = new MessageInput();
        messageInput.setWidthFull();

        var llmProvider = new LangChain4JLLMProvider(chatModel);

        AIOrchestrator.builder(llmProvider, ChartAIController.getSystemPrompt())
                .withMessageList(messageList).withInput(messageInput)
                .withController(chartController).build();

        var chatLayout = new VerticalLayout(messageList, messageInput);
        chatLayout.setSizeFull();
        chatLayout.setFlexGrow(1, messageList);
        chatLayout.setFlexShrink(0, messageInput);

        add(chart, chatLayout);
        setFlexGrow(1, chart);
        setFlexGrow(1, chatLayout);
    }

}
