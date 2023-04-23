package pl.lodz.p.it.model.users;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name= "u")
public abstract class UserEnt {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID userID;

    @Column(unique = true)
    private String username;

    @Column
    private String password;

    @Version
    private Integer version;

    @Column
    @Enumerated(EnumType.ORDINAL)
    private CustomerTypeEnt type;

    public UserEnt(String username, String password, CustomerTypeEnt type) {
        this.username = username;
        this.password = password;
        this.type = type;
    }

    public UserEnt(UUID userID, String username, String password, CustomerTypeEnt type) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        UserEnt userEnt = (UserEnt) o;

        return new org.apache.commons.lang3.builder.EqualsBuilder().append(userID, userEnt.userID).isEquals();
    }

    @Override
    public int hashCode() {
        return new org.apache.commons.lang3.builder.HashCodeBuilder(17, 37).append(userID).toHashCode();
    }
}
