package project.accommodation.sync.application.model;

import lombok.Data;
import project.common.domain.DayType;
import project.common.domain.Season;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class AccommodationInfoPayload {

    private Integer maxPeople;
    private Map<Season, Map<DayType, Integer>> prices = new EnumMap<>(Season.class);
    private Map<String, Boolean> amenities = new HashMap<>();
    private List<String> roomImgUrls = new ArrayList<>();

    public AccommodationInfoPayload() {
        this.prices = initPrices();
    }

    public boolean hasAllPrices() {
        for (Season season : Season.values()) {
            for (DayType dayType : DayType.values()) {
                if (this.prices.get(season).get(dayType) == null) {
                    return false;
                }
            }
        }

        return true;
    }

    private Map<Season, Map<DayType, Integer>> initPrices() {
        Map<Season, Map<DayType, Integer>> prices = new EnumMap<>(Season.class);

        for (Season season : Season.values()) {
            prices.put(season, new EnumMap<>(DayType.class));

            for (DayType dayType : DayType.values()) {
                prices.get(season).put(dayType, null);
            }
        }

        return prices;
    }
}
