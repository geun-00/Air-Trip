package project.member.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.common.adapter.out.persistence.BaseEntity;
import project.member.domain.support.AdminMemberCreateSpec;
import project.member.domain.support.RestMemberCreateSpec;
import project.member.domain.support.SocialMemberCreateSpec;

import java.time.LocalDate;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "members")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private MemberName name;

    @Column(name = "email", nullable = false)
    private Email email;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "social_type")
    private SocialType socialType;

    @Column(name = "is_email_verified", nullable = false)
    private Boolean isEmailVerified = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role = Role.GUEST;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, optional = false)
    private MemberDetail detail;

    public static Member createAdmin(AdminMemberCreateSpec spec) {
        return new Member("ADMIN-USER", null, null, spec.email(), spec.password(), SocialType.NONE, Role.ADMIN, true);
    }

    public static Member createForRest(RestMemberCreateSpec spec) {
        return new Member(spec.name(), spec.birthDate(), spec.number(), spec.email(), spec.password(), SocialType.NONE, Role.GUEST, false);
    }

    public static Member createForSocial(SocialMemberCreateSpec spec) {
        return new Member(spec.name(), spec.birthDate(), spec.number(), spec.email(), spec.password(), spec.socialType(), Role.GUEST, false);
    }

    private Member(
            String name,
            LocalDate birthDate,
            String number,
            String email,
            String password,
            SocialType socialType,
            Role role,
            boolean isEmailVerified
    ) {
        this.name = new MemberName(name);
        this.email = new Email(email);
        this.password = password;
        this.socialType = socialType;
        this.role = role;
        this.isEmailVerified = isEmailVerified;
        this.detail = new MemberDetail(this, birthDate, number);
    }

    public void updateProfile(String name, String aboutMe) {
        this.name = new MemberName(name);
        this.detail.updateProfile(aboutMe);
    }

    public void verifyEmail() {
        this.isEmailVerified = true;
    }

    public void updateProfileUrl(String profileUrl) {
        this.detail.updateProfileUrl(profileUrl);
    }

    public String getName() {
        return name.value();
    }

    public String getEmail() {
        return email.address();
    }

    public String getProfileUrl() {
        return detail.getProfileUrl();
    }

    public String getAboutMe() {
        return detail.getAboutMe();
    }
}
