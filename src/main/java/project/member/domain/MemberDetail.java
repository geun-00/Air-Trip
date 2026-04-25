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
    private BirthDate birthDate;

    @Column(name = "number", length = 11)
    private PhoneNumber number;

    @Column(name = "profile_url")
    private String profileUrl;

    @Column(name = "about_me", length = 500)
    private AboutMe aboutMe;

    MemberDetail(Member member, LocalDate birthDate, String number) {
        this.member = member;
        this.birthDate = birthDate == null ? null : new BirthDate(birthDate);
        this.number = number == null ? null : new PhoneNumber(number);
    }

    void updateProfile(String aboutMe) {
        this.aboutMe = aboutMe == null ? null : new AboutMe(aboutMe);
    }

    String getAboutMe() {
        return aboutMe == null ? null : aboutMe.value();
    }

    void updateProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}
