package project.common.adapter.in.web.response;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

// TODO : 애플리케이션 계층 공용 모델 분리 or 패키지 구조 변경
@Getter
public class PageResponse<E> {

    private final List<E> contents;
    private final boolean hasPrev, hasNext;
    private final int totalCount, prevPage, nextPage, totalPage, current, size;

    private PageResponse(List<E> contents, int pageSize, int pageNumber, long total) {
        this.contents = List.copyOf(contents);
        this.totalCount = (int) total;
        this.size = pageSize;
        this.current = pageNumber;

        this.totalPage = (int) (Math.ceil(totalCount / (double) size));

        this.hasPrev = pageNumber > 0;
        this.hasNext = pageNumber < (totalPage - 1);

        this.prevPage = hasPrev ? current - 1 : -1;
        this.nextPage = hasNext ? current + 1 : -1;
    }

    public static <E> PageResponse<E> from(Page<E> page) {
        return new PageResponse<>(page.getContent(), page.getSize(), page.getNumber(), page.getTotalElements());
    }

    public <R> PageResponse<R> map(Function<E, R> mapper) {
        List<R> mapped = contents.stream()
                                 .map(mapper)
                                 .toList();

        return new PageResponse<>(mapped, size, current, totalCount);
    }
}
