package project.accommodation.sync.application.fetcher;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import project.accommodation.sync.adapter.out.api.HttpClientTemplate;
import project.accommodation.sync.adapter.out.api.TourApiClient;
import project.accommodation.sync.application.model.AccommodationInfoPayload;
import project.common.domain.DayType;
import project.common.domain.Season;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.util.StringUtils.hasText;
import static project.common.domain.DayType.WEEKDAY;
import static project.common.domain.DayType.WEEKEND;
import static project.common.domain.Season.OFF;
import static project.common.domain.Season.PEAK;

@Slf4j
@Component
@RequiredArgsConstructor
public class DetailInfoFetcher {

    private final HttpClientTemplate<TourApiClient> httpClientTemplate;

    @Getter
    @RequiredArgsConstructor
    private enum InfoAmenity {
        ROOM_BATH_FACILITY("roombathfacility"),
        ROOM_BATH("roombath"),
        ROOM_HOME_THEATER("roomhometheater"),
        ROOM_AIR_CONDITION("roomaircondition"),
        ROOM_TV("roomtv"), ROOM_PC("roompc"),
        ROOM_CABLE("roomcable"),
        ROOM_INTERNET("roominternet"),
        ROOM_REFRIGERATOR("roomrefrigerator"),
        ROOM_TOILETRIES("roomtoiletries"),
        ROOM_SOFA("roomsofa"),
        ROOM_COOK("roomcook"),
        ROOM_TABLE("roomtable"),
        ROOM_HAIRDRYER("roomhairdryer");

        private final String key;
    }

    @Getter
    @AllArgsConstructor
    private enum PriceKey {
        OFF_WEEKDAY("roomoffseasonminfee1", OFF, WEEKDAY),
        OFF_WEEKEND("roomoffseasonminfee2", OFF, WEEKEND),
        PEAK_WEEKDAY("roompeakseasonminfee1", PEAK, WEEKDAY),
        PEAK_WEEKEND("roompeakseasonminfee2", PEAK, WEEKEND);

        private final String key;
        private final Season season;
        private final DayType dayType;
    }

    @Getter
    @RequiredArgsConstructor
    private enum InfoRoomImage {
        ROOM_IMAGE_1("roomimg1"),
        ROOM_IMAGE_2("roomimg2"),
        ROOM_IMAGE_3("roomimg3"),
        ROOM_IMAGE_4("roomimg4"),
        ROOM_IMAGE_5("roomimg5");

        private final String key;
    }

    public AccommodationInfoPayload fetch(String contentId) {
        List<Map<String, String>> items = httpClientTemplate.fetchItems(
                client -> client.detailInfo(contentId),
                itemList -> {
                    if (itemList.size() > 10) {
                        log.info("detailInfo 10개 이상, contentId: {}", contentId);
                    }
                });

        if (items.isEmpty()) {
            return new AccommodationInfoPayload();
        }

        return toPayload(items);
    }

    private AccommodationInfoPayload toPayload(List<Map<String, String>> items) {
        AccommodationInfoPayload payload = new AccommodationInfoPayload();

        payload.setAmenities(extractAmenities(items));
        payload.setPrices(extractPrices(items));
        payload.setRoomImgUrls(extractRoomImageUrls(items));

        Integer maxPeople = extractMaxPeople(items);
        if (maxPeople != null) {
            payload.setMaxPeople(maxPeople);
        }

        return payload;
    }

    private Map<String, Boolean> extractAmenities(List<Map<String, String>> items) {
        Map<String, Boolean> amenities = new HashMap<>();

        for (Map<String, String> item : items) {
            for (InfoAmenity amenity : InfoAmenity.values()) {
                String amenityName = amenity.getKey();

                if ("Y".equals(item.get(amenityName))) {
                    amenities.put(amenityName, true);
                } else {
                    amenities.putIfAbsent(amenityName, false);
                }
            }
        }

        return amenities;
    }

    private Map<Season, Map<DayType, Integer>> extractPrices(List<Map<String, String>> items) {
        Map<Season, Map<DayType, Integer>> maxPrices = new EnumMap<>(Season.class);
        for (Season season : Season.values()) {
            maxPrices.put(season, new EnumMap<>(DayType.class));
        }

        for (Map<String, String> item : items) {
            for (PriceKey priceKey : PriceKey.values()) {
                Integer price = parseNumber(item.get(priceKey.getKey()));
                if (price != null) {
                    updateMax(maxPrices.get(priceKey.getSeason()), priceKey.getDayType(), price);
                }
            }
        }

        return maxPrices;
    }

    private void updateMax(Map<DayType, Integer> map, DayType dayType, int newPrice) {
        if (newPrice == 0) {
            return;
        }
        map.merge(dayType, newPrice, Integer::max);
    }

    private List<String> extractRoomImageUrls(List<Map<String, String>> items) {
        Set<String> roomImageUrls = new LinkedHashSet<>();

        for (Map<String, String> item : items) {
            for (InfoRoomImage roomImage : InfoRoomImage.values()) {
                String url = item.get(roomImage.getKey());
                if (hasText(url)) {
                    roomImageUrls.add(url);
                }
            }
        }

        return roomImageUrls.stream().toList();
    }

    private Integer extractMaxPeople(List<Map<String, String>> items) {
        Integer max = null;

        for (Map<String, String> item : items) {
            Integer value = parseNumber(item.get("roommaxcount"));
            if (value != null && (max == null || max < value)) {
                    max = value;
                }
        }

        return max;
    }

    private Integer parseNumber(String value) {
        if (!hasText(value)) {
            return null;
        }

        String digits = value.replaceAll("\\D", "").trim();
        if (!hasText(digits)) {
            return null;
        }

        return Integer.parseInt(digits);
    }
}
