$(document).ready(function () {
    $.getJSON("/evrp", function(routes) {
        var listofroutes = $("#listofroutes");
        $.each(routes, function(idx, value) {
              listofroutes.append(
              $('<li><a href="route.html?id='+ value + '">' + value + '</a>'
               ));
        });
    });
});