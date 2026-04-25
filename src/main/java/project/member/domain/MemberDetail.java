package project.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member_details")
class MemberDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_detail_id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "number", length = 11)
    private String number;

    @Column(name = "profile_url")
    private String profileUrl;

    @Column(name = "about_me")
    private String aboutMe;

    MemberDetail(Member member, LocalDate birthDate, String number) {
        this.member = member;
        this.birthDate = birthDate;
        this.number = number;
    }

    void updateProfile(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    void updateProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}
