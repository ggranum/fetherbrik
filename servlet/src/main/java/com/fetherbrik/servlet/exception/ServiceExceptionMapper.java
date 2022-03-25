/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */
package com.fetherbrik.servlet.exception;

import com.fetherbrik.core.exception.service.ServiceException;
import com.fetherbrik.core.log.Level;
import com.fetherbrik.core.log.Log;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.apache.http.HttpStatus;

/**
 * @author Geoff M. Granum
 */
@Provider
public class ServiceExceptionMapper implements ExceptionMapper<ServiceException> {

  @Override
  public Response toResponse(ServiceException e) {
    e.log();
    int status = e.statusCode();
    if(!e.hasStatusCode()) {
      status = HttpStatus.SC_INTERNAL_SERVER_ERROR;
      this.tellDevsAboutUnhandledServiceError(e);
    }
    return Response.status(status)
               .entity(e.getHttpResponseMessage())
               .header("error", e.getHttpResponseMessage())
               .build();
  }

  private void tellDevsAboutUnhandledServiceError(ServiceException e) {
    String className = e.getThrowingClassName();

    String msg = "No HTTP Status code provided on error that bubbled all the way up to the response: "
                 + "Service Exceptions MUST be handled within request boundaries.";
    Log.log(Level.ERROR, className, e, msg);
  }
}
 
