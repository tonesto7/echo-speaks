// Get Root Page URL for Callback to Send Settings
console.log('host: ' + window.location.host);
let rootUrl = window.location.host;
$(document).ready(function() {
    let url = 'http://' + rootUrl + '/configData';
    $.getJSON(url, function(data) {
        $.each(data, function(key, val) {
            var elem = document.getElementById(key);
            if ($(elem)) {
                $(elem).attr('value', val);
                $(elem).focus();
            }
        });
    });
});

(function($) {
    $.fn.serializeFormJSON = function() {
        var o = {};
        var a = this.serializeArray();
        $.each(a, function() {
            if (o[this.name]) {
                if (!o[this.name].push) {
                    o[this.name] = [o[this.name]];
                }
                o[this.name].push(this.value || '');
            } else {
                o[this.name] = this.value || '';
            }
        });
        return o;
    };
})(jQuery);

$('form').submit(function(e) {
    e.preventDefault();
    var config = $(this).serializeFormJSON();
    var url = "http://10.0.0.173:8092/configSave";
    if (Object.keys(config).length) {
        console.log(config);
        // $.post(url, {
        //     "clientId": "1",
        //     "sensor": "Temp",
        //     "dateStart": "2016-09-03 00:00:00",
        //     "dateEnd": "2016-09-03 00:59:59"
        // })
        var xmlhttp = new XMLHttpRequest();
        xmlhttp.open("POST", 'http://' + rootUrl + '/configSave');
        xmlhttp.onreadystatechange = function() {
            if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
                // console.log(xmlhttp.responseText);
                // Command: toastr["success"]("Settings Saved", "Success")
                toastr.options = {
                    "closeButton": false,
                    "debug": false,
                    "newestOnTop": false,
                    "progressBar": false,
                    "positionClass": "toast-bottom-center",
                    "preventDuplicates": true,
                    "onclick": null,
                    "showDuration": 300,
                    "hideDuration": 1000,
                    "timeOut": 5000,
                    "extendedTimeOut": 1000,
                    "showEasing": "swing",
                    "hideEasing": "linear",
                    "showMethod": "fadeIn",
                    "hideMethod": "fadeOut"
                };
                toastr.success('Success!', "Setting Saved..");
            }
        };
        xmlhttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
        for (const h in config) {
            xmlhttp.setRequestHeader(h.toString(), config[h].toString());
        }
        xmlhttp.send(JSON.stringify(config));

    }
});