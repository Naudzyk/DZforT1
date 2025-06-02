package com.example.DZforT1.aop.CachedAOP;


import com.example.DZforT1.models.CacheEntry;
import com.example.DZforT1.repository.CacheEntryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Optional;

@Aspect
@Component
@RequiredArgsConstructor
public class CachedAspect {

    private final CacheEntryRepository cacheEntryRepository;
    private final ObjectMapper objectMapper;

    @Value("${app.cache.expiration-seconds}")
    private long expirationSeconds;

    @Around("@annotation(com.example.DZforT1.aop.CachedAOP.Cached)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();

        if (args.length == 0 || !(args[0] instanceof Long id)) {
            return joinPoint.proceed();
        }

        String cachekey = method.getName() + "_" + id;

        Optional<CacheEntry> cached = cacheEntryRepository.findById(cachekey);

        if (cached.isPresent() && !isExpired(cached.get())) {
            return objectMapper.readValue(cached.get().getCacheValue(), getReturnType(joinPoint));
        }
        Object result = joinPoint.proceed();
        String json = objectMapper.writeValueAsString(result);

        CacheEntry entry = new CacheEntry();
        entry.setId(cachekey);
        entry.setCacheValue(json);
        entry.setTimeAt(LocalDateTime.now().plusSeconds(expirationSeconds));

        cacheEntryRepository.save(entry);

        return result;
    }

    private boolean isExpired(CacheEntry entry) {
        return entry.getTimeAt().isBefore(LocalDateTime.now());
    }

    private Class<?> getReturnType(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod().getReturnType();
    }
}
