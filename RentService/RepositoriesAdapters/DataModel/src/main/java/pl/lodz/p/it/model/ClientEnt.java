package pl.lodz.p.it.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="clients")
public class ClientEnt {

    @Id
    private UUID id;

    @OneToMany(cascade = CascadeType.REMOVE ,fetch = FetchType.EAGER, mappedBy = "client")
    private List<ReservationEnt> reservations = new ArrayList<>();

    private boolean active = false;
    public ClientEnt(UUID id) {
        this.id = id;
    }
}
