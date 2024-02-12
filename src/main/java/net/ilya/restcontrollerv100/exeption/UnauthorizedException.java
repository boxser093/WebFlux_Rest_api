package net.ilya.restcontrollerv100.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends ApiException{

    public UnauthorizedException(String message) {
        super(message, "ILYAP_UNAUTHORIZED");
    }
}
