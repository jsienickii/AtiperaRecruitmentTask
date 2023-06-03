package pl.sienicki.atipera.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import pl.sienicki.atipera.dto.CustomExceptionResponse;

import java.io.OutputStream;

public class AcceptHeaderInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String acceptHeader = request.getHeader("Accept");
        if (acceptHeader == null || !acceptHeader.equals("application/xml")) {
            return true;
        } else {
            response.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
            response.setContentType("application/json");
            CustomExceptionResponse errorResponse = new CustomExceptionResponse(HttpStatus.NOT_ACCEPTABLE.value(),
                    "The requested header: Accept:'application/xml' is not acceptable.");

            OutputStream outputStream = response.getOutputStream();
            new ObjectMapper().writeValue(outputStream, errorResponse);
            outputStream.flush();
            return false;
        }
    }
}
