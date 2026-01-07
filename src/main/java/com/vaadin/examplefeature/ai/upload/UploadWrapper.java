package com.vaadin.examplefeature.ai.upload;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.upload.SucceededEvent;
import com.vaadin.flow.component.upload.Upload;

import java.io.InputStream;

public class UploadWrapper extends Upload implements AiFileReceiver {

    /**
     * Adds a listener that is called when a file upload succeeds.
     * This is an adapter method for AI orchestrators.
     * <p>
     * Note: The Upload component streams file content and does not keep it in
     * memory by default. To use this with AI orchestrators, you need to
     * configure an UploadHandler that stores the uploaded data and makes it
     * accessible via an InputStream.
     *
     * @param listener
     *            the listener to add
     */
    @Override
    public void addSucceededListener(FileUploadListener listener) {
        addListener(SucceededEvent.class,
                (ComponentEventListener<SucceededEvent>) event -> {
                    FileUploadEvent aiEvent = new FileUploadEvent() {
                        @Override
                        public String getFileName() {
                            return event.getFileName();
                        }

                        @Override
                        public String getMimeType() {
                            return event.getMIMEType();
                        }

                        @Override
                        public InputStream getInputStream() {
                            // The Upload component streams content and doesn't keep it
                            // in memory. Applications need to handle storage in their
                            // UploadHandler and provide access to the data.
                            // This is a limitation of the adapter pattern.
                            return null;
                        }
                    };
                    listener.onFileUploaded(aiEvent);
                });
    }
}
