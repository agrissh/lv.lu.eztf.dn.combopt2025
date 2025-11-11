package lv.lu.eztf.dn.combopt.evrp.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class EVRPSolverExceptionHandler {

    @ExceptionHandler({EVRPSolverException.class})
    public ResponseEntity<ErrorInfo> handleEVRPSolverException(EVRPSolverException exception) {
        return new ResponseEntity<>(new ErrorInfo(exception.getJobId(), exception.getMessage()), exception.getStatus());
    }
}
