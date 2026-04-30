package project.auth.application.out.command;

public interface ManageBlacklistedTokenPort {

    void save(String accessToken, long ttlMillis);
}
