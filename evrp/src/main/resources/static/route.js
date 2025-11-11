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

function getHardScore(score) {
   return score.slice(0,score.indexOf("hard"))
}

function getSoftScore(score) {
   return score.slice(score.indexOf("hard/"),score.indexOf("soft"))
}

$(document).ready(function () {
    const urlParams = new URLSearchParams(window.location.search);
    const solutionId = urlParams.get('id');

    $.getJSON("/evrp/score/" + solutionId, function(analysis) {
            var badge = "badge bg-danger";
            if (getHardScore(analysis.score)==0) { badge = "badge bg-success"; }
            $("#score_a").attr({"title":"Score Brakedown","data-bs-content":""
                + getScorePopoverContent(analysis.constraints) + "","data-bs-html":"true"});
            $("#score_text").text(analysis.score);
            $("#score_text").attr({"class":badge});
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
    var indictmentMap = {};
    indictments.forEach((indictment) => {
         indictmentMap[indictment.indictedObjectID] = indictment;
    })
    const vehicle_div = $("#vehicle_container");
    solution.vehicleList.forEach((vehicle) => {
        var v_badge = "badge bg-danger";
        if (indictmentMap[vehicle.regNr]==null || getHardScore(indictmentMap[vehicle.regNr].score)==0) { v_badge = "badge bg-success"; }
        vehicle_div.append($('<a data-toggle="popover" data-bs-html="true" data-bs-content="' +
            'charge=' + vehicle.charge + "/" + vehicle.maxCharge +
            '<hr>' +
            getEntityPopoverContent(vehicle.regNr, indictmentMap) +
            '" data-bs-original-title="'+ vehicle.regNr + '"><span class="'+ v_badge +'">'+
            vehicle.regNr + '</span></a>'));
        var visit_nr = 1;
        vehicle.visits.forEach((visit) => {
            var visit_badge = "badge bg-danger";
            if (indictmentMap[visit.name] == null || getHardScore(indictmentMap[visit.name].score)==0) { visit_badge = "badge bg-success"; }
            vehicle_div.append($('<a data-toggle="popover" data-bs-html="true" data-bs-content="'+
                'works from ' + formatTime(visit.startTime) + " to " + formatTime(visit.endTime) +
                '<br>arrives at ' + formatTime(visit.arrivalTime) + ", departs at " + formatTime(visit.departureTime) +
                '<br>service duration is ' + formatTime(visit.serviceDuration) +
                '<br>charge arriving = ' + visit.vehicleCharge +
                '<br>charge leaving  = ' + visit.vehicleChargeAfterVisit +
                '<hr>' +
                getEntityPopoverContent(visit.name, indictmentMap) +
                '" data-bs-original-title="'+
                '#' + visit_nr + ' ' + visit.name+'"><span class="'+visit_badge+'">'+
                '#' + visit_nr + ' ' + visit.name + '</span></a>'));
            visit_nr = visit_nr + 1;
        })
        vehicle_div.append($('<br><br>'));
    })
}

function formatTime(timeInSeconds) {
        if (timeInSeconds != null) {
            const HH = Math.floor(timeInSeconds / 3600);
            const MM = Math.floor((timeInSeconds % 3600) / 60);
            const SS = Math.floor(timeInSeconds % 60);
            return HH + ":" + MM + ":" + SS;
        } else return "null";
}



