package project.payment.adapter.in.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.auth.adapter.in.web.support.CurrentMemberId;
import project.payment.adapter.in.web.request.PaymentConfirmRequest;
import project.payment.adapter.in.web.request.SavePaymentRequest;
import project.payment.adapter.in.web.response.PaymentResponse;
import project.payment.application.service.PaymentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/save")
    public ResponseEntity<?> savePayment(@Valid @RequestBody SavePaymentRequest savePaymentRequest) {
        paymentService.savePayment(savePaymentRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/confirm")
    public ResponseEntity<PaymentResponse> confirmPayment(@CurrentMemberId Long memberId,
                                                          @Valid @RequestBody PaymentConfirmRequest paymentConfirmRequest
    ) {
        PaymentResponse response = paymentService.confirmPayment(paymentConfirmRequest, memberId);
        return ResponseEntity.ok(response);
    }
}
