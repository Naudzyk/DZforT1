//package com.example.DZforT1.service2;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//import org.springframework.web.util.ContentCachingRequestWrapper;
//
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.util.Collections;
//import java.util.stream.Collectors;
//
//@Component
//@RequiredArgsConstructor
//public class RequestLoggingFilter extends OncePerRequestFilter {
//
//    private final ObjectMapper objectMapper;
//    private final Logger log = LoggerFactory.getLogger(getClass());
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//        try {
//            // Логируем запрос
//            if (log.isInfoEnabled()) {
//                String body = readRequestBody(request);
//                log.info("Получен запрос: {} {} | Заголовки: {} | Тело: {}",
//                    request.getMethod(),
//                    request.getRequestURI(),
//                    Collections.list(request.getHeaderNames()).stream()
//                        .collect(Collectors.toMap(
//                            h -> h,
//                            h -> Collections.list(request.getHeaders(h))),
//                    body);
//            }
//
//            // Продолжаем цепочку фильтров
//            filterChain.doFilter(request, response);
//
//        } catch (IOException ex) {
//            log.error("Ошибка чтения тела запроса", ex);
//            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ошибка чтения тела запроса");
//        }
//    }
//
//    // Читаем тело запроса
//    private String readRequestBody(HttpServletRequest request) throws IOException {
//        if (request instanceof ContentCachingRequestWrapper cachingRequest) {
//            byte[] body = cachingRequest.getContentAsByteArray();
//            return new String(body, 0, body.length, StandardCharsets.UTF_8);
//        }
//        return "";
//    }
//
//    // Обертка для повторного чтения тела запроса
//    @Override
//    protected boolean shouldNotFilterAsyncDispatches() {
//        return false;
//    }
//
//    @Override
//    protected boolean requiresContentCachingForRequest() {
//        return true;
//    }
//}
