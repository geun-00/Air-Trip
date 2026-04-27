package project.accommodation.sync.application.fetcher;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import project.accommodation.sync.adapter.out.api.HttpClientTemplate;
import project.accommodation.sync.adapter.out.api.TourApiClient;
import project.accommodation.sync.application.model.ChildAreaCodeSyncPayload;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.springframework.util.StringUtils.hasText;

@Component
@RequiredArgsConstructor
public class ChildAreaCodeFetcher {

    private final HttpClientTemplate<TourApiClient> httpClientTemplate;

    public List<ChildAreaCodeSyncPayload> fetch(String areaCode) {
        return httpClientTemplate.fetchItems(client -> client.areaCode(1, 31, areaCode))
                                 .stream()
                                 .map(this::toPayloadOrNull)
                                 .filter(Objects::nonNull)
                                 .toList();
    }

    private ChildAreaCodeSyncPayload toPayloadOrNull(Map<String, String> item) {
        String code = item.get("code");
        String codeName = item.get("name");

        if (!hasText(code) || !hasText(codeName)) {
            return null;
        }

        return new ChildAreaCodeSyncPayload(code, codeName);
    }
}
