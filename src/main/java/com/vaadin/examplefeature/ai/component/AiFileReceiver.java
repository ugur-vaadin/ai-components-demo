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
package com.vaadin.examplefeature.ai.component;

import java.util.function.Consumer;

/**
 * Interface for file upload components that are used in an AI conversation.
 * <p>
 * Note: This is a temporary simplified version for the demo project that works
 * with Vaadin versions before 25.1.0 alpha4.
 *
 * @author Vaadin Ltd
 */
public interface AiFileReceiver {

    /**
     * Adds a listener that is called when a file upload succeeds.
     *
     * @param listener
     *            the listener to add, receives file metadata and data
     */
    void addFileUploadListener(Consumer<FileUploadEvent> listener);

    /**
     * Adds a listener for file removed events.
     *
     * @param listener
     *            the listener to add, receives the removed file name
     */
    void addFileRemovedListener(Consumer<String> listener);

    /**
     * Clears the list of uploaded files.
     */
    void clearFileList();

    /**
     * Event containing uploaded file information.
     */
    interface FileUploadEvent {
        String fileName();

        String contentType();

        byte[] data();
    }
}
