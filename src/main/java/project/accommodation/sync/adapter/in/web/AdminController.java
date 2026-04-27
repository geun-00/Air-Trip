package project.accommodation.sync.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.chatbot.application.service.EmbeddingService;
import project.accommodation.sync.application.service.TourService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final TourService tourService;
    private final EmbeddingService embeddingService;

    @PostMapping("/sync-area-codes")
    public ResponseEntity<Void> syncAreaCodes() {
        tourService.syncAreaCodes();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/fetch-acc")
    public ResponseEntity<Void> fetchAccommodations(@RequestParam("pageNo") int pageNo,
                                                    @RequestParam("numOfRows") int numOfRows) {
        tourService.fetchAccommodations(pageNo, numOfRows);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/embed-accommodations")
    public ResponseEntity<Void> embedAccommodations(@PageableDefault(size = 50) Pageable pageable) {
        embeddingService.embedAccommodations(pageable);
        return ResponseEntity.ok().build();
    }
}
