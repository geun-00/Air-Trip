package project.member.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.auth.adapter.in.web.support.CurrentMemberId;
import project.member.application.in.command.ManageEmailVerificationUseCase;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class EmailVerificationController {

    private final ManageEmailVerificationUseCase manageEmailVerificationUseCase;

    @PostMapping("/me/email-verification")
    public ResponseEntity<Void> sendEmail(@CurrentMemberId Long memberId) {
        manageEmailVerificationUseCase.sendEmail(memberId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/email-verification")
    public ResponseEntity<Void> verifyEmail(@RequestParam("token") String token) {
        String redirectUrl = manageEmailVerificationUseCase.verifyToken(token);

        return ResponseEntity.status(HttpStatus.FOUND)
                             .location(URI.create(redirectUrl))
                             .build();
    }
}
