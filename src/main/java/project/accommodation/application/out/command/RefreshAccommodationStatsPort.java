package project.accommodation.application.out.command;

public interface RefreshAccommodationStatsPort {

    void refreshTopStats();

    void refreshRecentStats();

    void refreshAllStats();
}
