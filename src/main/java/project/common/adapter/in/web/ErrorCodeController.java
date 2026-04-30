package project.common.adapter.in.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.common.adapter.in.web.response.ErrorCodeResponse;
import project.common.exception.ErrorCode;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/errors")
public class ErrorCodeController {

    @GetMapping
    public ResponseEntity<List<ErrorCodeResponse>> getErrors() {
        List<ErrorCodeResponse> errors = Arrays.stream(ErrorCode.values())
                                               .map(ErrorCodeResponse::from)
                                               .toList();
        return ResponseEntity.ok(errors);
    }
}
