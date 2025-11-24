package lv.lu.eztf.dn.combopt.evrp.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.graphhopper.util.shapes.GHPoint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter @Getter @AllArgsConstructor @NoArgsConstructor
@JsonIdentityInfo(scope = Location.class,
    property = "id", generator = ObjectIdGenerators.PropertyGenerator.class)
public class Location {
    static Double SPEED = 50.0;
    Long id;
    Double lat;
    Double lon;

    public Location(Long id, Double lat, Double lon) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
    }

    public Double simpleDistanceTo(Location location) {
        return Math.sqrt(Math.pow(this.lat - location.lat, 2)
                + Math.pow(this.lon - location.lon,2));
    }

    public Long simpleTimeTo(Location location) {
        return Math.round((this.distanceTo(location) / SPEED) * 3600);
    }

    @JsonIgnore
    private Map<Location, Double> distanceMap = new HashMap<>();

    @JsonIgnore
    private Map<Location, Long> timeMap = new HashMap<>();

    @JsonIgnore
    private Map<Location, List<GHPoint>> pathMap = new HashMap<>();

    public Double distanceTo(Location location) {
        return this.distanceMap.get(location);
    }

    public Long timeTo(Location location) {
        return this.timeMap.get(location);
    }

    public List<GHPoint> pathTo(Location location) { return this.pathMap.get(location); }
}
