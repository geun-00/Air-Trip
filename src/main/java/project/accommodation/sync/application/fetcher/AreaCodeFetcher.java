package project.accommodation.sync.application.fetcher;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import project.accommodation.sync.adapter.out.api.HttpClientTemplate;
import project.accommodation.sync.adapter.out.api.TourApiClient;
import project.accommodation.sync.application.model.AreaCodeSyncPayload;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.springframework.util.StringUtils.hasText;

@Component
@RequiredArgsConstructor
public class AreaCodeFetcher {

    private final HttpClientTemplate<TourApiClient> httpClientTemplate;

    public List<AreaCodeSyncPayload> fetch() {
        return httpClientTemplate.fetchItems(client -> client.areaCode(1, 17, ""))
                                 .stream()
                                 .map(this::toPayloadOrNull)
                                 .filter(Objects::nonNull)
                                 .toList();
    }

    private AreaCodeSyncPayload toPayloadOrNull(Map<String, String> item) {
        String code = item.get("code");
        String codeName = item.get("name");

        if (!hasText(code) || !hasText(codeName)) {
            return null;
        }

        return new AreaCodeSyncPayload(code, codeName);
    }
}
