package project.common.adapter.in.web.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class PageResponse<E> {

    private final List<E> contents;
    private final boolean hasPrev, hasNext;
    private final int totalCount, prevPage, nextPage, totalPage, current, size;

    @Builder
    public PageResponse(List<E> contents, int pageSize, int pageNumber, long total) {
        this.contents = contents;
        this.totalCount = (int) total;
        this.size = pageSize;
        this.current = pageNumber;

        this.totalPage = (int) (Math.ceil(totalCount / (double) size));

        this.hasPrev = pageNumber > 0;
        this.hasNext = pageNumber < (totalPage - 1);

        this.prevPage = hasPrev ? current - 1 : -1;
        this.nextPage = hasNext ? current + 1 : -1;
    }

//    public static <E> PageResponse<E> from(Page<E> page) {
//        return new PageResponse<>(page.getContent(), page.getSize(), page.getNumber(), page.getTotalElements());
//    }

    public static <E> PageResponse<E> from(Page<E> page) {
        return PageResponse.<E>builder()
                           .contents(page.getContent())
                           .pageNumber(page.getNumber())
                           .pageSize(page.getSize())
                           .total(page.getTotalElements())
                           .build();
    }
}
