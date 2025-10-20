package de.upteams.tasktracker.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Возвращает JSON при недостатке прав (403 Forbidden)
 */
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                HttpServletResponse.SC_FORBIDDEN,
                "Forbidden",
                accessDeniedException.getMessage(),
                request.getRequestURI()
        );
        ErrorResponseWriter.write(response, error);
    }
}
