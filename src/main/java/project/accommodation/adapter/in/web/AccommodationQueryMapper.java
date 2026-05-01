package project.accommodation.adapter.in.web;

import org.springframework.data.domain.Pageable;
import project.accommodation.adapter.in.web.request.AccommodationSearchRequest;
import project.accommodation.adapter.in.web.response.AccommodationPriceResponse;
import project.accommodation.adapter.in.web.response.DetailAccommodationResponse;
import project.accommodation.adapter.in.web.response.FilteredAccommodationResponse;
import project.accommodation.adapter.in.web.response.MainAccommodationResponse;
import project.accommodation.adapter.in.web.response.MainAccommodationsResponse;
import project.accommodation.application.in.query.model.AccommodationDetailView;
import project.accommodation.application.in.query.model.AccommodationPriceView;
import project.accommodation.application.in.query.model.AccommodationSearchQuery;
import project.accommodation.application.in.query.model.DetailImageView;
import project.accommodation.application.in.query.model.DetailReviewView;
import project.accommodation.application.in.query.model.FilteredAccommodationView;
import project.accommodation.application.in.query.model.MainAccommodationItemView;
import project.accommodation.application.in.query.model.MainAccommodationView;

import static project.accommodation.adapter.in.web.response.DetailAccommodationResponse.DetailImageResponse;
import static project.accommodation.adapter.in.web.response.DetailAccommodationResponse.DetailReviewResponse;
import static project.accommodation.adapter.in.web.response.DetailAccommodationResponse.ReservedDateResponse;
import static project.accommodation.application.in.query.model.AccommodationDetailView.ReservedDateView;

final class AccommodationQueryMapper {

    private AccommodationQueryMapper() {
    }

    static AccommodationSearchQuery toQuery(AccommodationSearchRequest request, Pageable pageable) {
        return new AccommodationSearchQuery(
                request.areaCode(),
                request.amenities(),
                request.priceGoe(),
                request.priceLoe(),
                pageable
        );
    }

    static MainAccommodationResponse toResponse(MainAccommodationView view) {
        return new MainAccommodationResponse(
                view.areaName(),
                view.areaCode(),
                view.accommodations()
                    .stream()
                    .map(AccommodationQueryMapper::toResponse)
                    .toList()
        );
    }

    private static MainAccommodationsResponse toResponse(MainAccommodationItemView view) {
        return new MainAccommodationsResponse(
                view.accommodationId(),
                view.title(),
                view.price(),
                view.avgRate(),
                view.thumbnailUrl(),
                view.isInWishlist(),
                view.wishlistName(),
                view.wishlistId()
        );
    }

    static FilteredAccommodationResponse toResponse(FilteredAccommodationView view) {
        return new FilteredAccommodationResponse(
                view.accommodationId(),
                view.title(),
                view.price(),
                view.avgRate(),
                view.reviewCount(),
                view.imageUrls(),
                view.isInWishlist(),
                view.wishlistId(),
                view.wishlistName()
        );
    }

    static DetailAccommodationResponse toResponse(AccommodationDetailView view) {
        return new DetailAccommodationResponse(
                view.accommodationId(),
                view.title(),
                view.maxPeople(),
                view.address(),
                view.mapX(),
                view.mapY(),
                view.checkIn(),
                view.checkOut(),
                view.description(),
                view.number(),
                view.refundRegulation(),
                view.price(),
                view.isInWishlist(),
                view.wishlistId(),
                view.wishlistName(),
                view.avgRate(),
                toResponse(view.images()),
                view.amenities(),
                view.reviews().stream().map(AccommodationQueryMapper::toResponse).toList(),
                view.reservedDates().stream().map(AccommodationQueryMapper::toResponse).toList()
        );
    }

    private static DetailImageResponse toResponse(DetailImageView view) {
        return new DetailImageResponse(view.thumbnail(), view.others());
    }

    private static DetailReviewResponse toResponse(DetailReviewView view) {
        return new DetailReviewResponse(
                view.memberId(),
                view.memberName(),
                view.profileUrl(),
                view.memberCreatedDate(),
                view.reviewCreatedDate(),
                view.rating(),
                view.content()
        );
    }

    private static ReservedDateResponse toResponse(ReservedDateView view) {
        return new ReservedDateResponse(view.start(), view.end());
    }

    static AccommodationPriceResponse toResponse(AccommodationPriceView view) {
        return new AccommodationPriceResponse(view.accommodationId(), view.date(), view.price());
    }
}
