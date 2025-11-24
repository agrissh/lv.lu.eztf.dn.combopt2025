var map = L.map('map').setView([56.9337, 24.1258], 11);
var color_idx = 0;
const colors = ["#f44336","#e81e63","#9c27b0","#673ab7","#3f51b5","#2196f3","#03a9f4","#00bcd4","#009688",
                                                "#4caf50","#8bc34a","#cddc39","#ffeb3b","#ffc107","#ff9800","#ff5722"];
                       ;
const defaultIcon = new L.Icon.Default();
const vehicleIcon = L.divIcon({
    html: '<i class="fas fa-truck"></i>'
});
const chargingIcon = L.divIcon({
    html: '<i class="fas fa-battery-full"></i>'
});
const customerIcon = L.divIcon({
    html: '<i class="fas fa-warehouse"></i>'
});
const vehicleIcon_red = L.divIcon({
    html: '<i class="fas fa-truck" style="color: #ff0000"></i>'
});
const chargingIcon_red = L.divIcon({
    html: '<i class="fas fa-battery-full" style="color: #ff0000"></i>'
});
const customerIcon_red = L.divIcon({
    html: '<i class="fas fa-warehouse" style="color: #ff0000"></i>'
});

$(document).ready(function () {
    const urlParams = new URLSearchParams(window.location.search);
    const solutionId = urlParams.get('id');

    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
    }).addTo(map);

    $.getJSON("/evrp/score/" + solutionId, function(analysis) {
                    var badge = "badge bg-danger";
                    if (getHardScore(analysis.score)==0) { badge = "badge bg-success"; }
                    $("#score_a").attr({"title":"Score Brakedown","data-bs-content":"" + getScorePopoverContent(analysis.constraints) + "","data-bs-html":"true"});
                    $("#score_text").text(analysis.score);
                    $("#score_text").attr({"class":badge});

                    $(function () {
                       $('[data-toggle="popover"]').popover()
                    })
    });

    $.getJSON("/evrp/" + solutionId, function(solution) {
            $.getJSON("/evrp/indictments/" + solutionId, function(indictments) {
                            renderRoutes(solution, indictments);
                            $(function () {
                              $('[data-toggle="popover"]').popover()
                            })
            })
        });
});

function renderRoutes(solution, indictments) {
    $("#solutionTitle").text("Version 24/Nov/2025" + solution.name + "  " + solution.solverStatus);

    var indictmentMap = {};
    indictments.forEach((indictment) => {
        indictmentMap[indictment.indictedObjectID] = indictment;
    })

    var locationMap = {};
    solution.locationList.forEach((location) => {
            locationMap[location.id] = location;
    })

    solution.vehicleList.forEach((vehicle) => {
        let previous_location = [locationMap[vehicle.depot].lat, locationMap[vehicle.depot].lon];
        let path_to_next = vehicle.pathToFirst;
        let nr = 1;
        const vcolor = getColor();
        const vmarker = L.marker(previous_location).addTo(map);
        vmarker.setIcon(getVehicleIcon(indictmentMap[vehicle.regNr]));
        vmarker.bindPopup("<b>#"+vehicle.regNr+"</b><br>totalDistance=" + vehicle.totalDistance +
                    "<br>maxCharge=" + vehicle.maxCharge +
                    "<hr>" + getEntityPopoverContent(vehicle.regNr, indictmentMap));
        vehicle.visits.forEach((visit) => {
            const location = [locationMap[visit.location].lat, locationMap[visit.location].lon];
            const marker = L.marker(location).addTo(map);
            marker.setIcon(getVisitIcon(visit["@class"], indictmentMap[visit.name]));
            marker.bindPopup("<b>#"+nr+"</b><br>id="+visit.name+"<br>arrival="
            + formatTime(visit.arrivalTime) + "<br>charge=" + visit.vehicleCharge + "<br>after=" + visit.vehicleChargeAfterVisit +
            "<hr>" + getEntityPopoverContent(visit.name, indictmentMap));
            //const line = L.polyline([previous_location, location], {color: vcolor}).addTo(map);
            // Draw a path
            let previous_path_point = previous_location;
            path_to_next.forEach((point => {
                const line = L.polyline([previous_path_point, [point.lat, point.lon]], {color: vcolor}).addTo(map);
                previous_path_point = [point.lat, point.lon]
            }))
            const last_line = L.polyline([previous_path_point, location], {color: vcolor}).addTo(map);
            previous_location = location;
            path_to_next = visit.pathToNext;
            nr = nr + 1;
        });

        let last_previous_path_point = previous_location;
        path_to_next.forEach((point => {
             const line = L.polyline([last_previous_path_point, [point.lat, point.lon]], {color: vcolor}).addTo(map);
             last_previous_path_point = [point.lat, point.lon]
        }))
        const last_last_line = L.polyline([last_previous_path_point, [locationMap[vehicle.depot].lat, locationMap[vehicle.depot].lon]], {color: vcolor}).addTo(map);
        //const line_back = L.polyline([previous_location, [vehicle.depot.lat, vehicle.depot.lon]],{color: vcolor}).addTo(map);
    });
}

function getEntityPopoverContent(entityId, indictmentMap) {
    var popover_content = "";
    const indictment = indictmentMap[entityId];
    if (indictment != null) {
        popover_content = popover_content + "Total score: <b>" + indictment.score + "</b> (" + indictment.matchCount + ")<br>";
        indictment.constraintMatches.forEach((match) => {
                  if (getHardScore(match.score) == 0) {
                     popover_content = popover_content + match.constraintName + " : " + match.score + "<br>";
                  } else {
                     popover_content = popover_content + "<b>" + match.constraintName + " : " + match.score + "</b><br>";
                  }
            })
    }
    return popover_content;
}

function getVisitIcon(v_type, indictment) {
    if (indictment==undefined || getHardScore(indictment.score) == 0) {
        return v_type == "lv.lu.eztf.dn.combopt.evrp.domain.Customer" ? customerIcon : chargingIcon;
    } else {
        return v_type == "lv.lu.eztf.dn.combopt.evrp.domain.Customer" ? customerIcon_red : chargingIcon_red;
    }

}

function getVehicleIcon(indictment) {
    if (indictment==undefined || getHardScore(indictment.score) == 0) {
        return vehicleIcon;
    } else {
        return vehicleIcon_red;
    }

}

function getColor() {
   color_idx = (color_idx + 4) % colors.length;
   return colors[color_idx];
}

function getScorePopoverContent(constraint_list) {
    var popover_content = "";
    constraint_list.forEach((constraint) => {
          if (getHardScore(constraint.score) == 0) {
             popover_content = popover_content + constraint.name + " : " + constraint.score + "<br>";
          } else {
             popover_content = popover_content + "<b>" + constraint.name + " : " + constraint.score + "</b><br>";
          }
    })
    return popover_content;
}

function getHardScore(score) {
   return score.slice(0,score.indexOf("hard"))
}

function getSoftScore(score) {
   return score.slice(score.indexOf("hard/"),score.indexOf("soft"))
}

function formatTime(timeInSeconds) {
        if (timeInSeconds != null) {
            const HH = Math.floor(timeInSeconds / 3600);
            const MM = Math.floor((timeInSeconds % 3600) / 60);
            const SS = Math.floor(timeInSeconds % 60);
            return HH + ":" + MM + ":" + SS;
        } else return "null";
}