package lv.lu.eztf.dn.combopt.evrp.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter @AllArgsConstructor @NoArgsConstructor
public class Location {
    Long id;
    Double lat;
    Double lon;

    public Double distanceTo(Location location) {
        return Math.sqrt(Math.pow(this.lat - location.lat, 2)
                + Math.pow(this.lon - location.lon,2));
    }

    public Long timeTo(Location location) {
        return 0l;
    }
}
