package project.accommodation.sync.adapter.in.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;
import project.accommodation.sync.application.fetcher.AreaListFetcher;
import project.accommodation.sync.application.model.AccommodationSyncSeed;

import java.util.Iterator;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AreaBasedSyncListReader implements ItemReader<AccommodationSyncSeed> {

    private final AreaListFetcher areaListFetcher;

    private int pageNo = 1;
    private Iterator<AccommodationSyncSeed> currentIter;

    // TODO : 배치에서 일일 트래픽(1,000건)을 초과했을 때 적절한 처리 필요
    @Override
    public AccommodationSyncSeed read() throws UnexpectedInputException, ParseException, NonTransientResourceException {
        if (currentIter == null || !currentIter.hasNext()) {
            int numOfRows = 100;
            log.debug("남아있는 데이터가 없어 새로 요청");

            List<AccommodationSyncSeed> dtos = areaListFetcher.fetch(pageNo, numOfRows);

            currentIter = dtos.iterator();
            pageNo++;
        }

        return currentIter.hasNext() ? currentIter.next() : null;
    }
}
