package project.accommodation.sync.application.fetcher;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import project.accommodation.sync.adapter.out.api.HttpClientTemplate;
import project.accommodation.sync.adapter.out.api.TourApiClient;
import project.accommodation.sync.application.model.AccommodationIntroPayload;

import java.util.List;
import java.util.Map;

import static org.springframework.util.StringUtils.hasText;

@Component
@RequiredArgsConstructor
public class DetailIntroFetcher {

    private final HttpClientTemplate<TourApiClient> httpClientTemplate;

    public AccommodationIntroPayload fetch(String contentId) {
        List<Map<String, String>> items = httpClientTemplate.fetchItems(client -> client.detailIntro(contentId));

        if (items.isEmpty()) {
            return new AccommodationIntroPayload();
        }

        return toPayload(items.getFirst());
    }

    private AccommodationIntroPayload toPayload(Map<String, String> item) {
        AccommodationIntroPayload payload = new AccommodationIntroPayload();

        String checkIn = item.get("checkintime");
        if (hasText(checkIn)) {
            payload.setCheckIn(checkIn);
        }

        String checkOut = item.get("checkouttime");
        if (hasText(checkOut)) {
            payload.setCheckOut(checkOut);
        }

        String refundRegulation = item.get("refundregulation");
        if (hasText(refundRegulation)) {
            payload.setRefundRegulation(refundRegulation);
        }

        for (IntroAmenity amenity : IntroAmenity.values()) {
            String amenityName = amenity.getKey();
            payload.putAmenity(amenityName, "1".equals(item.get(amenityName)));
        }

        return payload;
    }

    @Getter
    @RequiredArgsConstructor
    private enum IntroAmenity {
        BARBECUE("barbecue"),
        BEAUTY("beauty"),
        BEVERAGE("beverage"),
        BICYCLE("bicycle"),
        CAMPFIRE("campfire"),
        FITNESS("fitness"),
        KARAOKE("karaoke"),
        PUBLIC_BATH("publicbath"),
        PUBLIC_PC("publicpc"),
        SAUNA("sauna"),
        SEMINAR("seminar"),
        SPORTS("sports");

        private final String key;
    }
}
