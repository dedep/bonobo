function addSuccessAlert(content) {
    $("#alert-box")
        .removeClass("alert-danger")
        .addClass("alert-success")
        .css("display", "block")
        .delay(5000).fadeOut('slow');

    $("#alert-content").text(content)
}

function addErrorAlert(content) {
    $("#alert-box")
        .removeClass("alert-success")
        .addClass("alert-danger")
        .css("display", "block")
        .delay(5000).fadeOut('slow');

    $("#alert-content").text(content)
}