package project.auth.application.out.command;

public interface ManageBlacklistedTokenPort {

    boolean contains(String accessToken);

    void save(String accessToken, long ttlMillis);
}
