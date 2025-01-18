package lavie.skincare_booking.utils;

import jakarta.mail.MessagingException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    public ResponseEntity<Object> handleValidationErrors(BindingResult result) {
        if (!result.hasErrors()) {
            return ResponseEntity.ok("Validation passed");
        }
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach((error) -> errors.put(error.getField(), error.getDefaultMessage()));
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Validation error");
        response.put("fieldErrors", errors);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach((violation) -> errors.put(violation.getPropertyPath().toString(), violation.getMessage()));
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());
        response.put("fieldErrors", errors);
        LOGGER.error("Constraint violation at : {} ",errors);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errorMessages = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();
        String formattedErrorMessage = String.join(", ", errorMessages);
        return createErrorResponse(formattedErrorMessage, "Validation Error", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleResponseStatusException(ResponseStatusException ex) {
        String errorMessage = ex.getReason();
        return createErrorResponse(ex.getMessage(), ex.getReason(), HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
        String errorMessage = ex.getMessage();
        return createErrorResponse(errorMessage, "Bad request", HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(ParseException.class)
    public ResponseEntity<?> handleParseException(ParseException ex) {
        return createErrorResponse(ex.getMessage(), "Parse Error", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MessagingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleMailException(MessagingException ex) {
        String errorMessage = ex.getMessage();
        return createErrorResponse(errorMessage, "Messaging service unavailable", HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<?> handleLockedException(LockedException ex) {
        String errorMessage = ex.getMessage();
        return createErrorResponse(errorMessage, "Locked", HttpStatus.LOCKED);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<?> handleDisabledException(DisabledException ex) {
        String errorMessage = ex.getMessage();
        return createErrorResponse(errorMessage, "Disabled", HttpStatus.PRECONDITION_REQUIRED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException ex) {
        String errorMessage = ex.getMessage();
        return createErrorResponse(errorMessage, "Bad credentials", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        String errorMessage = ex.getMostSpecificCause().getMessage();
        if (errorMessage.contains("duplicate key value violates unique constraint")) {
            String duplicateKey = extractDuplicateKey(errorMessage);
            String message = duplicateKey + " already exists";
            return createErrorResponse(message, "Data Integrity Violation", HttpStatus.CONFLICT);
        } else {
            return createErrorResponse(errorMessage, "Data Integrity Violation", HttpStatus.CONFLICT);
        }
    }

    private String extractDuplicateKey(String errorMessage) {
        int startIndex = errorMessage.indexOf("(");
        int endIndex = errorMessage.indexOf(")");
        if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
            return errorMessage.substring(startIndex + 1, endIndex);
        }
        return "";
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    private ResponseEntity<?> createErrorResponse(String errorMessage, String errorType, HttpStatus status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", errorType);
        errorResponse.put("message", errorMessage);
        errorResponse.put("status", status.value());
        return new ResponseEntity<>(errorResponse, status);
    }
}