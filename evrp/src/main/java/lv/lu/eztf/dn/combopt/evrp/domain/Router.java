package lv.lu.eztf.dn.combopt.evrp.domain;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.ResponsePath;
import com.graphhopper.config.CHProfile;
import com.graphhopper.config.Profile;
import com.graphhopper.util.CustomModel;
import com.graphhopper.util.shapes.GHPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.graphhopper.json.Statement.If;
import static com.graphhopper.json.Statement.Op.LIMIT;
import static com.graphhopper.json.Statement.Op.MULTIPLY;

public class Router {
    static public Router getDefaultRouterInstance() {
        return new Router("data/latvia-251123.osm.pbf", ".ghtemp");
    }

    public GraphHopper router;

    public Router(String osmFile, String ghLocation) {
        router = new GraphHopper();
        router.setOSMFile(osmFile);
        router.setGraphHopperLocation(ghLocation);
        router.setEncodedValuesString("car_access, road_access, car_average_speed");
        router.setProfiles(new Profile("car").
                setCustomModel(new CustomModel().
                        addToSpeed(If("true", LIMIT, "car_average_speed")).
                        addToPriority(If("!car_access", MULTIPLY, "0"))));
        router.getCHPreparationHandler().setCHProfiles(new CHProfile("car"));
        router.importOrLoad();
    }

    public void setDistanceTimeMap(List<Location> locationList) {
        for (Location location: locationList) {
            for (Location toLocation: locationList) {
                GHRequest req = new GHRequest(location.getLat(), location.getLon(), toLocation.getLat(), toLocation.getLon()).
                                setProfile("car").
                                setLocale(Locale.US);
                GHResponse rsp = router.route(req);
                if (rsp.hasErrors())
                    throw new RuntimeException(rsp.getErrors().toString());
                ResponsePath path = rsp.getBest();

                List<GHPoint> pointList = new ArrayList<>();
                for (GHPoint point : path.getPoints()) {
                    pointList.add(point);
                }

                location.getPathMap().put(toLocation, pointList);
                // distance in meters and time in millis in the response path
                location.getDistanceMap().put(toLocation, path.getDistance() / 1000);
                location.getTimeMap().put(toLocation,path.getTime() / 1000);
            }
        }
    }
}