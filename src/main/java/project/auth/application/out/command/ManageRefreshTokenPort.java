package project.auth.application.out.command;

public interface ManageRefreshTokenPort {

    boolean exists(String refreshToken);

    void save(String refreshToken, Long memberId, long ttlSeconds);

    void delete(String refreshToken);
}
