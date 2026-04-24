package project.auth.adapter.in.web.request;

public record LoginRequest(
        String email,
        String password) {
}
