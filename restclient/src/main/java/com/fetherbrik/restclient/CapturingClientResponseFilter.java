/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */
package com.fetherbrik.restclient;

import com.fetherbrik.core.log.Log;
import org.apache.commons.io.IOUtils;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author Geoff M. Granum
 */
public class CapturingClientResponseFilter extends LoggingClientResponseFilter {

    @Override
    public void filter(
            ClientRequestContext requestContext,
            ClientResponseContext responseContext) throws IOException {
        if (Log.debugEnabled(getClass())) {
            if (responseContext.getLength() > 10E3) {
                Log.debug(getClass(), "Not logging response: contentLength of '%s' exceeds 10Kb", responseContext.getLength());
            } else {
                InputStream stream = responseContext.getEntityStream();
                // Stream can be null if response is empty.
                if (stream != null) {
                    byte[] bytes = IOUtils.toByteArray(stream);
                    String responseBody = new String(bytes, StandardCharsets.UTF_8);
                    responseContext.setEntityStream(new ByteArrayInputStream(bytes));
                    Log.debug(getClass(), responseBody);
                }
            }
        }
    }
}
 
