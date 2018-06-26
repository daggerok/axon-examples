package daggerok.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static java.util.Collections.singletonMap;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

  @ExceptionHandler(Throwable.class)
  public ResponseEntity onError(final Exception e) {

    log.error(e.getLocalizedMessage(), e);

    return ResponseEntity.badRequest()
                         .body(singletonMap("error", e.getLocalizedMessage()));
  }
}

