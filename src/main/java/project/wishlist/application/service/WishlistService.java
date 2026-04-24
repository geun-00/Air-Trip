package project.wishlist.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.accommodation.domain.exception.AccommodationExceptions;
import project.member.domain.exception.MemberExceptions;
import project.wishlist.adapter.in.web.request.AddAccToWishlistRequest;
import project.wishlist.adapter.in.web.request.MemoUpdateRequest;
import project.wishlist.adapter.in.web.request.WishlistCreateRequest;
import project.wishlist.adapter.in.web.response.WishlistCreateResponse;
import project.wishlist.adapter.in.web.response.WishlistDetailResponse;
import project.wishlist.adapter.in.web.request.WishlistUpdateRequest;
import project.wishlist.adapter.in.web.response.WishlistsResponse;
import project.wishlist.domain.exception.WishlistExceptions;
import project.accommodation.domain.Accommodation;
import project.member.domain.Member;
import project.wishlist.domain.Wishlist;
import project.wishlist.domain.WishlistAccommodation;
import project.accommodation.adapter.out.persistence.model.AccAllImagesQueryDto;
import project.wishlist.adapter.out.persistence.model.WishlistDetailQueryDto;
import project.accommodation.adapter.out.persistence.AccommodationRepository;
import project.member.adapter.out.persistence.MemberRepository;
import project.wishlist.adapter.out.persistence.WishlistAccommodationRepository;
import project.wishlist.adapter.out.persistence.WishlistRepository;
import project.wishlist.adapter.out.persistence.WishlistQueryRepository;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishlistService {

    private final MemberRepository memberRepository;
    private final WishlistRepository wishlistRepository;
    private final AccommodationRepository accommodationRepository;
    private final WishlistQueryRepository wishlistQueryRepository;
    private final WishlistAccommodationRepository wishlistAccommodationRepository;

    @Transactional
    public WishlistCreateResponse createWishlist(WishlistCreateRequest reqDto, Long memberId) {
        Member member = memberRepository.findById(memberId)
                                        .orElseThrow(() -> MemberExceptions.notFoundById(memberId));

        Wishlist savedWishlist = wishlistRepository.save(Wishlist.create(member, reqDto.wishlistName()));
        return new WishlistCreateResponse(savedWishlist.getId(), savedWishlist.getName());
    }

    @Transactional
    public void addAccommodationToWishlist(Long wishlistId, AddAccToWishlistRequest reqDto, Long memberId) {
        Long accommodationId = reqDto.accommodationId();
        if (wishlistQueryRepository.existsWishlistAccommodation(wishlistId, accommodationId, memberId)) {
            return;
        }
        Wishlist wishlist = getWishlistByIdAndMemberId(wishlistId, memberId);
        Accommodation accommodation = getAccommodationById(accommodationId);

        wishlistAccommodationRepository.save(WishlistAccommodation.create(wishlist, accommodation));
    }

    @Transactional
    public void removeAccommodationFromWishlist(Long wishlistId, Long accommodationId, Long memberId) {
        Wishlist wishlist = getWishlistByIdAndMemberId(wishlistId, memberId);
        Accommodation accommodation = getAccommodationById(accommodationId);

        wishlistAccommodationRepository.deleteByWishlistAndAccommodation(wishlist, accommodation);
    }

    @Transactional
    public void updateWishlistName(Long wishlistId, WishlistUpdateRequest reqDto, Long memberId) {
        Wishlist wishlist = getWishlistByIdAndMemberId(wishlistId, memberId);
        wishlist.updateName(reqDto.wishlistName());
    }

    @Transactional
    public void removeWishlist(Long wishlistId, Long memberId) {
        Wishlist wishlist = getWishlistByIdAndMemberId(wishlistId, memberId);
        wishlistAccommodationRepository.deleteByWishlist(wishlist);
        wishlistRepository.delete(wishlist);
    }

    @Transactional
    public void updateMemo(Long wishlistId, Long accommodationId, Long memberId, MemoUpdateRequest reqDto) {
        WishlistAccommodation wishlistAccommodation = wishlistAccommodationRepository.findByAllIds(wishlistId, accommodationId, memberId)
                                                                                     .orElseThrow(() -> WishlistExceptions.notFoundWishlistAccommodation(wishlistId, accommodationId, memberId));
        wishlistAccommodation.updateMemo(reqDto.memo());
    }

    public List<WishlistDetailResponse> getAccommodationsFromWishlist(Long wishlistId, Long memberId) {
        List<WishlistDetailQueryDto> detailQueryDtos = wishlistQueryRepository.findWishlistDetails(wishlistId, memberId);
        List<Long> accIds = detailQueryDtos.stream()
                                           .map(WishlistDetailQueryDto::accommodationId)
                                           .toList();

        List<AccAllImagesQueryDto> allImagesQueryDtos = wishlistQueryRepository.findAllImages(accIds);
        Map<Long, List<String>> imagesMap = allImagesQueryDtos.stream()
                                                              .collect(groupingBy(
                                                                      AccAllImagesQueryDto::accommodationId,
                                                                      mapping(AccAllImagesQueryDto::imageUrl, toList())
                                                              ));

        return detailQueryDtos.stream()
                              .map(dto -> WishlistDetailResponse.from(dto, imagesMap.getOrDefault(dto.accommodationId(), List.of())))
                              .toList();
    }

    public List<WishlistsResponse> getAllWishlists(Long memberId) {
        return wishlistQueryRepository.getAllWishlists(memberId);
    }

    private Wishlist getWishlistByIdAndMemberId(Long wishlistId, Long memberId) {
        return wishlistRepository.findByIdAndMemberId(wishlistId, memberId).orElseThrow(
                () -> WishlistExceptions.notFoundByIdAndMemberId(wishlistId, memberId));
    }

    private Accommodation getAccommodationById(Long accommodationId) {
        return accommodationRepository.findById(accommodationId).orElseThrow(
                () -> AccommodationExceptions.notFoundById(accommodationId));
    }
}
