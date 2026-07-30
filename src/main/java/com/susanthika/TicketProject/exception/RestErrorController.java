package com.susanthika.TicketProject.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.webmvc.autoconfigure.error.AbstractErrorController;
import org.springframework.boot.webmvc.error.ErrorAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static io.micrometer.common.util.StringUtils.isNotBlank;

@Slf4j
@RestController
@RequestMapping("${server.error.path:${error.path:/error}}")
public class RestErrorController extends AbstractErrorController {

    public RestErrorController(ErrorAttributes errorAttributes) {
        super(errorAttributes, List.of());
    }

    @RequestMapping
    public ResponseEntity<?> error(HttpServletRequest request) {
        HttpStatus status = getStatus(request);
        ErrorType errorType = getErrorType(status);
        Map<String, Object> errorAttributes = getErrorAttributes(request, ErrorAttributeOptions.defaults().including(ErrorAttributeOptions.Include.values()));
        String message = getMessage(status, errorAttributes);
        String path = (String) errorAttributes.get("path");
        QErrorResponse errorResponse = QErrorResponse.fromError(new Error(errorType, status.value(), message, path, null));
        return new ResponseEntity<>(errorResponse, new HttpHeaders(), status);
    }

    private String getMessage(HttpStatus status, Map<String, Object> errorAttributes) {
        if (status == HttpStatus.UNAUTHORIZED) {
            return "Authentication failed.";
        }
        if (status == HttpStatus.FORBIDDEN) {
            return "Access denied.";
        }

        String message = (String) errorAttributes.get("message");
        return isNotBlank(message) ? message : "Error.";
    }

    private ErrorType getErrorType(HttpStatus status) {
        if (status == HttpStatus.BAD_REQUEST) {
            return ErrorType.BAD_REQUEST;
        }
        if (status == HttpStatus.UNAUTHORIZED) {
            return ErrorType.AUTHENTICATION_FAILED;
        }
        if (status == HttpStatus.FORBIDDEN) {
            return ErrorType.ACCESS_DENIED;
        }
        if (status == HttpStatus.NOT_FOUND) {
            return ErrorType.NOT_FOUND;
        }
        if (status == HttpStatus.CONFLICT) {
            return ErrorType.CONFLICT;
        }

        return ErrorType.SYSTEM_ERROR;
    }

}
