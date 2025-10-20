package lv.lu.eztf.dn.combopt.evrp.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter @AllArgsConstructor @NoArgsConstructor
public class Location {
    static Double SPEED = 50.0;
    Long id;
    Double lat;
    Double lon;

    public Double distanceTo(Location location) {
        return Math.sqrt(Math.pow(this.lat - location.lat, 2)
                + Math.pow(this.lon - location.lon,2));
    }

    public Long timeTo(Location location) {
        return Math.round((this.distanceTo(location) / SPEED) * 3600);
    }
}
