package project.area.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.common.adapter.out.persistence.BaseEntity;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "area_codes")
public class AreaCode extends BaseEntity {

    @Id
    @Column(name = "area_code", nullable = false)
    private String code;

    @Column(name = "code_name", nullable = false)
    private String codeName;

    public static AreaCode create(String code, String codeName) {
        return new AreaCode(code, codeName);
    }

    private AreaCode(String code, String codeName) {
        this.code = code;
        this.codeName = codeName;
    }

    public void changeCodeName(String codeName) {
        this.codeName = codeName;
    }
}
