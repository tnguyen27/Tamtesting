function uploadLocationsCSV(csvPath){
    var resultData = [];
    var url = $("#fileUploadButton").attr("csv-uploader-url");
    var extension = $("#fileUploadButton").attr("csv-uploader-extension");
    var servletURL = url + extension;
    console.log(servletURL);
    jQuery.ajax({
        url:  servletURL,
        data: {filePath : csvPath},
        success: function (data) {
            console.log(data);
            checkResponse(data);
            $.merge(resultData, data);
        },
        async: false
    });

    return resultData;
}

function checkResponse(result){
    $("#resultDiv").removeAttr("style");
    if(result.success) {
        $("#resultDiv").css({"color":"green", "font-size":"large"})
    } else {
        $("#resultDiv").css({"color":"red"})
    }
    $("#resultDiv").html(result.result);

    if (result.success == true)
        reloadPage();
}

function reloadPage(){
    setTimeout(location.reload(true), 4000);
}

function isCSVFile(file){
    var fileSplitted = file.split(".");
    var response = false;
    if (fileSplitted.length > 1){
        if (fileSplitted[1].toLowerCase() == "csv"){
            response = true;
        }
    }
    return response;
}

function isFileSelected(fileInput){
    var response = false;
    if (fileInput !== ""){
        response = true;
    }
    return response;
}

function showError(error){
    $("#errorDiv").html(error);
}

function changeButtonStatus(status){
    $("#fileUploadButton").prop('disabled', status);
}

$( document ).ready(function() {
    var fileInput =  $( "#fileInput" ).val();
    if(typeof fileInput == 'undefined')
        return;
    if(isFileSelected(fileInput)) {
        if (isCSVFile(fileInput)){
            changeButtonStatus(false);
        } else {
            changeButtonStatus(true);
            var errorMessage = "<b>Please select a valid CSV locations file.</b>";
            showError(errorMessage);
        }
    }else {
        var errorMessage = "<b>Please go to the component configurations and choose a CSV locations file.</b>";
        changeButtonStatus(true);
        showError(errorMessage);
    }
    
});