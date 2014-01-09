/**
 * Loads all the applications for the applications page
 */
function appendHtmlContent(json) {
    $.ajax({url: json.url,
        type: "GET",
        dataType: "html",
        success: function(html) {
            // append html after id
            $("#" + json.elem).append(html);
            // call success function
            if (json.funct && jQuery.isFunction(json.funct)) {
                json.funct();
            }
        }
    });
}


/**
 * Executes a URL and puts the response in the id element
 * @param json
 */
function replaceHtmlContent(json) {
    $.ajax({url: json.url,
        type: "GET",
        dataType: "html",
        success: function(html) {
            // replace html for id
            $("#" + json.id).after(html).remove();
            // call success function
            if (json.funct && jQuery.isFunction(json.funct)) {
                json.funct();
            }
        }
    });
}

/**
 * Loads a url conditionally based on that the id doesn't already exist
 */
function loadPageModuleById(json) {
    $("#" + json.id).remove();
    appendHtmlContent(json);
}

/**
 * Loads a modal window. Appends new id after contentWrapper
 * @param json
 */
function loadAjaxModal(json) {
    var divId = '#' + json.id + "_modal";
    $(divId).remove();

    var div = document.createElement('div');
    $(div).attr("id", divId);

    if (json.cssClass) {
        $(div).attr("class", json.cssClass);
    } else {
        $(div).attr("class", "jqmWindow");
    }
    $(div).attr("visibility", "hidden");

    $('#contentWrapper').after(div);

    var options = {
        modal: true,
        ajax: json.url
    };
    $(div).jqm(options).jqmShow();
}

/**
 * First closes and then removes a modal div.
 * @param id
 */
function closeModal() {
    $("#form_modal").jqm().jqmHide();
    $("#form_modal").remove();
}

/**
 * Method for loading up the application form
 * Expected parameters are:
 * json {
 *  id : id of the entity
 *  url: url of modal
 *  cssClass: css class of modal window
 *  onCallbackForm: callback function for form validation
 *
 */
function loadAjaxModalForm(json) {

    var divId = "form_modal";
    $('#' + divId).remove();

    var div = document.createElement('div');
    $(div).attr("id", divId);

    if (json.cssClass) {
        $(div).attr("class", json.cssClass);
    } else {
        $(div).attr("class", 'jqmWindow');
    }
    $(div).attr("visibility", "hidden");

    $("body").after(div);

    var options = {
        modal: true,
        ajax: json.url
    };

    $(div).jqm(options).jqmShow();
}

/**
 * Binds a link to the form edit process
 * Expected parameters are:
 * url
 * id
 * cssClass
 * @param json
 */
function bindFormLink(json) {
    var options = {
        url: json.url,
        id: json.id,
        cssClass: json.cssClass,
        onCallbackForm: onCallBackForm(json)
    };

    loadAjaxModalForm(options);
}

function submitForm(json) {
    var options = {
        success: onCallBackForm(json)
    };

    $("#form").ajaxSubmit(options);
}

function onCallBackForm(json) {
    return function(response, status, form) {
        if (response.indexOf("formPage") > -1) {
            // error here - refresh form page
            $("#formPage").after(response).remove();
        } else {
            // ok, delete existing application application (if any)
            $("#" + json.id).after(response).remove();

            // close modal
            closeModal();
        }
    };
}

/**
 * Adds a new tab to the list of tabs
 * @param json
 */
function addTab(json) {
    var tabs = $("#navbar > ul");
    tabs = $(tabs).tabs("add", json.url, json.label);
}

/**
 * Adds a new tab to the secondary tab bar
 * @param json
 */
function addSecondaryTab(json) {
    var id = "#" + json.id + " > ul";
    var tabs = $(id);
    tabs = $(tabs).tabs("add", json.url, json.label);
}

/**
 * Remove tab
 * @param json
 */
function removeTab(json) {
    var tabs = $("#navbar > ul");
    var children = $(tabs).children().length;

    for (var i = 0; i < children; i++) {
        var tab = $(tabs).children()[i];
        var tabName = tab.firstChild.firstChild.firstChild;
        if (tabName.textContent == json.label) {
            $(tabs).tabs("remove", i);
        }
    }
}

/**
 * Removes a tab from the secondary tab bar
 * @param json
 */
function removeSecondaryTab(json) {
    var id = "#" + json.id + " > ul";
    var tabs = $(id);
    var children = $(tabs).children().length;

    for (var i = 0; i < children; i++) {
        var tab = $(tabs).children()[i];
        var tabName = tab.firstChild.firstChild.firstChild;
        if (tabName.textContent == json.label) {
            $(tabs).tabs("remove", i);
        }
    }
}

/**
 * Opens a new browser window
 * @param json
 */
function openNewWindow(json) {
    var url = json.url;
    var title = json.title;
    var height = json.height;
    var width = json.width;

    window.open(url, title, config = 'height=' + height + ', width=' + width + ', toolbar=no, menubar=no, scrollbars=no, resizable=no, location=no, directories=no, status=no');
}

function makeVisible(id) {
    if ($("#"+id).is(":hidden")) {
        $("#"+id).slideDown("slow");
    } else {
        $("#"+id).hide();
    }
}