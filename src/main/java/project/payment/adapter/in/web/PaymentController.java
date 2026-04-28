package project.payment.adapter.in.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.auth.adapter.in.web.support.CurrentMemberId;
import project.payment.adapter.in.web.request.ConfirmPaymentRequest;
import project.payment.adapter.in.web.request.SavePaymentRequest;
import project.payment.adapter.in.web.response.PaymentResponse;
import project.payment.application.in.command.model.ConfirmPaymentCommand;
import project.payment.application.in.command.ConfirmPaymentUseCase;
import project.payment.application.in.command.model.SaveTempPaymentCommand;
import project.payment.application.in.command.SaveTempPaymentUseCase;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final ConfirmPaymentUseCase confirmPaymentUseCase;
    private final SaveTempPaymentUseCase saveTempPaymentUseCase;

    @PostMapping("/save")
    public ResponseEntity<?> savePayment(@Valid @RequestBody SavePaymentRequest request) {
        saveTempPaymentUseCase.saveTempPayment(new SaveTempPaymentCommand(request.orderId(), request.amount()));

        return ResponseEntity.ok().build();
    }

    @PostMapping("/confirm")
    public ResponseEntity<PaymentResponse> confirmPayment(
            @CurrentMemberId Long memberId,
            @Valid @RequestBody ConfirmPaymentRequest request
    ) {
        ConfirmPaymentCommand command = new ConfirmPaymentCommand(
                request.paymentKey(),
                request.orderId(),
                request.amount(),
                request.reservationId()
        );

        String receiptUrl = confirmPaymentUseCase.confirmPayment(command, memberId);
        return ResponseEntity.ok(new PaymentResponse(receiptUrl));
    }
}
