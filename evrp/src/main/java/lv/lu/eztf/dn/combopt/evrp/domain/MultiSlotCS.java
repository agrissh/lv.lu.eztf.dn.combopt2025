package lv.lu.eztf.dn.combopt.evrp.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonIdentityInfo(scope = MultiSlotCS.class, property = "id",
        generator = ObjectIdGenerators.PropertyGenerator.class)
public class MultiSlotCS {
    Long id;
    @JsonIdentityReference(alwaysAsId = true)
    Location location;
    Double chargingPower; // kWh / hour
    Integer numberOfSlots;
    Double priceEnergy;

}
