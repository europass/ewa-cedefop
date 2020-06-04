<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>EWA-923: Cloud providers load/store</title>

        <style type="text/css" rel="stylesheet">

            button:hover{
                cursor:pointer;
                background-color:#fff;
            }

        </style>

        <!-- Google Drive -->
        <script type="text/javascript" 
                src="${applicationScope['context.resourcesBase']}/static/ewa/libraries/google/api.js"
        id="googleapijs"></script>
        <script type="text/javascript" 
                src="${applicationScope['context.resourcesBase']}/static/ewa/libraries/google/client.js"
        id="googleclientapijs"></script>
        <script type="text/javascript" 
                src="${applicationScope['context.resourcesBase']}/static/ewa/libraries/google/platform.js"
        id="googleplatformjs"></script>

        <!-- Dropbox -->
        <script type="text/javascript" 
                src="${applicationScope['context.resourcesBase']}/static/ewa/libraries/dropbox/dropins.js" 
                id="dropboxjs" 
        data-app-key="1emyu6ztcaly8j2"></script>
        <!--script src="//cdnjs.cloudflare.com/ajax/libs/dropbox.js/0.10.2/dropbox.min.js"></script  -->
        <!-- OneDrive -->
        <script type="text/javascript"
                src="${applicationScope['context.resourcesBase']}/static/ewa/libraries/onedrive/oneDriveAPI.js"
        id="onedrivejs"></script>
        <script src="${applicationScope['context.resourcesBase']}/static/ewa/libraries/jquery/jquery-2.1.0-min.js"></script>

        <script type="text/javascript">
            console.log(window.location.host);
            console.log("${applicationScope['context.ewa.editors.onedrive.rootdomain']}");
            console.log("${applicationScope['context.ewa.editors.onedrive.appkey']}");

            window.googledriveDevKey = {
                "${applicationScope['context.ewa.editors.googledrive.url']}": "${applicationScope['context.ewa.editors.googledrive.devkey']}"
            };
            window.googledriveAppId = {
                "${applicationScope['context.ewa.editors.googledrive.url']}": "${applicationScope['context.ewa.editors.googledrive.appid']}"
            };
            window.googledriveClientId = {
                "${applicationScope['context.ewa.editors.googledrive.url']}": "${applicationScope['context.ewa.editors.googledrive.clientid']}"
            };
            window.dropboxKey = {
                "${applicationScope['context.ewa.editors.dropbox.url']}": "${applicationScope['context.ewa.editors.dropbox.appkey']}"
            };
            window.onedriveKey = {
                "${applicationScope['context.ewa.editors.onedrive.rootdomain']}": "${applicationScope['context.ewa.editors.onedrive.appkey']}"
            };

            window.dropboxCallback = "${applicationScope['context.ewa.editors.dropbox.callbackurl']}";
            //======================= O N E   D R I V E ===========================================/
            function prepareOneDrive() {
                WL.init({
                    client_id: "000000004C11BC33", //window.onedriveKey[window.location.host]
                    redirect_uri: window.location.href
                });
                window.jsonESP = {
                    SkillsPassport: {
                        Locale: "el",
                        LearnerInfo: {
                            Identification: {
                                PersonName: {
                                    FirstName: "Αλέξια",
                                    Surname: "Αντωνίου"
                                }
                            }
                        }
                    }
                };


                prepareOneDrivePicker(
                        "onedrive-picker-downloader",
                        "open",
                        downloadFromOneDrive,
                        function () {
                            console.log("OneDrive:Picker - failed to select a file/folder.")
                        });


                prepareOneDrivePicker(
                        "onedrive-picker-uploader",
                        "save",
                        uploadToDrive,
                        function () {
                            console.log("OneDrive:Picker - failed to select a file/folder location to store the document.")
                        });

                registerOnClickHandlers();
            }

            function registerOnClickHandlers(loginResponse) {
                var uploadFileButton =
                        document.getElementById('onedrive-picker-uploader');
//		    uploadFileButton.disabled = true;
                uploadFileButton.onclick = function () {

                    WL.login({scope: "wl.skydrive_update"}).then(
                            function (loginResponse) {

                                WL.fileDialog({mode: 'save'}).then(
                                        function (response) {
                                            uploadToDriveResponse(loginResponse, folder, loginResponse.session.access_token);
                                        }
                                , function (response) {
                                    log("WL.fileDialog errorResponse = " + JSON.stringify(response));
                                }
                                );

                            },
                            function (loginResponse) {
                                log("Failed to authenticate: " + JSON.stringify(loginResponse));
                            }
                    );

                };


                var downloadFileButton =
                        document.getElementById('onedrive-picker-downloader');
//		    downloadFileButton.disabled = true;
                downloadFileButton.onclick = function () {

                    WL.login({scope: "wl.skydrive wl.signin"}).then(
                            function (loginResponse) {

                                WL.fileDialog({mode: 'open', select: 'single'}).then(
                                        function (response) {
                                            alert(response);
                                            downloadFromOneDrive(response);
                                        }
                                , function (response) {
                                    log("WL.fileDialog errorResponse = " + JSON.stringify(response));
                                }
                                );

                            },
                            function (loginResponse) {
                                log("Failed to authenticate: " + JSON.stringify(loginResponse));
                            }
                    );

                };

            }

            function prepareOneDrivePicker(elementId, mode, _SuccessCallback, _FailureCallback) {
                WL.ui({
                    name: "skydrivepicker",
                    mode: mode, //save or open
                    select: "single", //single or multi
                    element: elementId,
                    onselected: _SuccessCallback,
                    onerror: _FailureCallback
                });
            }
            function downloadFromOneDrive(response) {
                console.log("OneDrive:Picker - About to download file...");
                var files = response.data.files;

                if (files.length > 0) {
                    var file = files[0];
                    var msg = "OneDrive:Picker - Selected file:\n" +
                            "\tName: " + file.name + "\n" +
                            "\tURL: " + file.source + "\n";
                    console.log(msg);
                    alert(msg);
                }
            }
            function uploadToDrive(response) {
                console.log("OneDrive:Picker - About to upload Europass document...");
                var folders = response.data.folders;
                if (folders.length > 0) {
                    var folder = folders[0];
                    WL.login({
                        scope: "wl.skydrive_update"
                    }).then(
                            function (response) {
                                uploadToDriveResponse(response, folder, response.session.access_token);
                            }

                    );
                }
            }

            function uploadToDriveResponse(response, folder, accessToken) {
                var path = folder.id;
//			var accessToken = response.session.access_token;
                var params = [
                    {name: "json", value: JSON.stringify(window.jsonESP)},
                    {name: "folderId", value: folder.id},
                    {name: "accessToken", value: accessToken}];
                var inputs = "";
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    inputs += "<input type='hidden' name='" + param.name + "' value='" + param.value + "' />";
                }
//			$("<form id=\"tempform\" enctype=\"application/x-www-form-urlencoded\" accept-charset=\"utf-8\" method=\"post\">"+inputs+"</form>")
                $("<form id=\"tempform\" enctype=\"application/json\" accept-charset=\"utf-8\" method=\"post\">" + inputs + "</form>")
                        .appendTo('body');
                $("<div id=\"processing\">Processing...</div>").insertAfter($("#skydrivesavepickerbutton"));
                $.post(
//				"/api/cloud-provider/one-drive/save",
                        "/api/document/cloud/onedrive/opendoc",
                        $("#tempform").serialize()

                        ).done(function () {
                    var msg = "OneDrive:Uploader - Document was uploaded successfully.";
                    console.log(msg);
                    alert(msg);
                }).fail(function () {
                    var msg = "OneDrive:Uploader - Document failed to be uploaded.";
                    console.log(msg);
                    alert(msg);
                }).always(function () {
                    $("#tempform").remove();
                    $("#processing").remove();
                    console.log("OneDrive:Uploader - Document uploading completed.");
                });
            }

            //======================= G O O G L E   D R I V E ===========================================/
            // Use the API Loader script to load google.picker and gapi.auth.
            function prepareGDrivePicker() {

                window.scope = ["https://www.googleapis.com/auth/drive.apps.readonly", "https://www.googleapis.com/auth/drive.file"];
                window.pickerApiLoaded = false;
                window.oauthToken;

                var onclick = function () {
                    gapi.load('auth', {'callback': onAuthApiLoad});
                    gapi.client.load('drive', 'v2', onDriveApiLoaded);
                    //ekar: 2014-04-28  gapi.load('picker', {'callback': onPickerApiLoad} );
                };
                document.getElementById("gdrive-picker-upload").onclick = function () {
                    window.uploadClicked = true;
                    onclick();
                };
                document.getElementById("gdrive-picker-store").onclick = function () {
                    window.storeClicked = true;
                    onclick();
                };
            }
            function onAuthApiLoad() {
                window.gapi.auth.authorize(
                        {
                            'client_id': window.clientId,
                            'scope': window.scope,
                            'immediate': false
                        },
                        handleAuthResult);
            }
            function onPickerApiLoad() {
                window.pickerApiLoaded = true;
                createPicker();
            }
            function onDriveApiLoaded() {
                console.log("drive-api-loaded");
            }
            function handleAuthResult(authResult) {
                if (authResult && !authResult.error) {
                    window.oauthToken = authResult.access_token;
                    gapi.load('picker', {'callback': onPickerApiLoad});
                    //ekar: 2014-04-28 createPicker();
                }
            }
            // Create and render a Picker object for picking user Photos.
            function createPicker() {
                if (window.pickerApiLoaded && window.oauthToken) {

                    var toStore = false;
                    if (window.storeClicked === true) {
                        delete window.storeClicked;
                        toStore = true;
                    }
                    if (window.uploadClicked === true) {
                        delete window.uploadClicked;
                        toStore = false;
                    }

                    var callback;
                    var title;
                    var mimeTypes;
                    if (toStore) {
                        callback = storeCallback;
                        title = "Choose the location to store the Europass Document";
                        mimeTypes = "application/vnd.google-apps.folder";
                    } else {
                        callback = uploadCallback;
                        title = "Choose existing Europass Document (PDF+XML or XML) to upload";
                        mimeTypes = "application/pdf,application/xml";
                    }

                    var docsView = new google.picker.DocsView()
                            .setParent('root')
                            .setIncludeFolders(true)
                            .setSelectFolderEnabled(toStore);

                    var pickerBuilder = new google.picker.PickerBuilder()
                            .addView(docsView)
                            .setOrigin(window.location.protocol + "//" + window.location.host)
                            .setAppId(window.gDriveAppId)
                            .setOAuthToken(window.oauthToken)
                            .setDeveloperKey(window.developerKey)
                            .setCallback(callback)
                            .disableFeature(google.picker.Feature.MULTISELECT_ENABLED)
                            .setSelectableMimeTypes(mimeTypes)
                            .setLocale("en")
                            .setTitle(title)
                            .setSize(751, 400);

                    var picker = pickerBuilder.build();
                    picker.setVisible(true);
                }
            }
            function storeCallback(data) {
                var doc = null;
                if (data[google.picker.Response.ACTION] == google.picker.Action.PICKED) {
                    var doc = data[google.picker.Response.DOCUMENTS][0];
                }
                if (doc === null) {
                    console.log("GDrive:Picker - No folder was selected");
                } else {
                    var id = doc[google.picker.Document.ID];

                    var request = gapi.client.drive.files.get({
                        fileId: id
                    });
                    request.execute(function (file) {
                        var name = file.title;
                        var id = file.id;
                        var msg = "GDrive:Picker - Selected folder:\n" +
                                "\tName: " + name + "\n" +
                                "\tId: " + id + "\n"
                        "Use with the Authorization: Bearer '" + window.oauthToken + "'";
                        console.log(msg);
                        alert(msg);

                        var params = [
                            {name: "json", value: JSON.stringify(window.jsonESP)},
                            {name: "folderId", value: id},
                            {name: "accessToken", value: window.oauthToken}];
                        var inputs = "";
                        for (var i = 0; i < params.length; i++) {
                            var param = params[i];
                            inputs += "<input type='hidden' name='" + param.name + "' value='" + param.value + "' />";
                        }
                        $("<form id=\"tempform\" enctype=\"application/x-www-form-urlencoded\" accept-charset=\"utf-8\" method=\"post\">" + inputs + "</form>")
                                .appendTo('body');
                        $("<div id=\"processing\">Processing...</div>").insertAfter($("#gdrive-picker-store"));
                        $.post(
                                "/api/cloud-provider/google-drive/save",
                                $("#tempform").serialize()

                                ).done(function () {
                            var msg = "GoogleDrive - Document was uploaded successfully.";
                            console.log(msg);
                            alert(msg);
                        }).fail(function () {
                            var msg = "GoogleDrive - Document failed to be uploaded.";
                            console.log(msg);
                            alert(msg);
                        }).always(function () {
                            $("#tempform").remove();
                            $("#processing").remove();
                            console.log("GoogleDrive - Document uploading completed.");
                        });
                    });
                }
            }
            function uploadCallback(data) {
                var doc = null;
                if (data[google.picker.Response.ACTION] == google.picker.Action.PICKED) {
                    var doc = data[google.picker.Response.DOCUMENTS][0];
                }
                if (doc === null) {
                    console.log("GDrive:Picker - No file was selected");
                } else {
                    var id = doc[google.picker.Document.ID];

                    var request = gapi.client.drive.files.get({
                        fileId: id
                    });
                    request.execute(function (file) {
                        //TO GET ON THE SERVER SIDE :: https://developers.google.com/drive/web/manage-downloads
                        //GET https://doc-04-c1-docs.googleusercontent.com/docs/securesc/ivearmirmg66&e=download&gd=true
                        //Authorization: Bearer ya29.AHESVbXTUv5mHMo3RYfmS1YJonjzzdTOFZwvyOAUVhrs
                        //Stackoverflow: http://stackoverflow.com/questions/16223806/drive-api-error-download-a-file
                        //http://stackoverflow.com/questions/17508212/how-do-i-use-google-picker-to-access-files-using-the-drive-file-scope
                        var url = file.downloadUrl;
                        var name = file.title;
                        var mimeType = file.mimeType;
                        var size = file.fileSize;
                        var iconUrl = file.iconLink;
                        var msg = "GDrive:Picker - Selected file:\n" +
                                "\tName: " + name + "\n" +
                                "\tURL: " + url + "\n" +
                                "\tMimetype: " + mimeType + "\n" +
                                "\tSize in bytes: " + size + "\n" +
                                "Fetch using the Authorization: Bearer '" + window.oauthToken + "'";
                        console.log(msg);
                        alert(msg);

                        var request = new XMLHttpRequest();
                        request.open('GET', url);
                        request.setRequestHeader('Authorization', 'Bearer ' + window.oauthToken);
                        request.addEventListener('load', function () {
                            var r = request.responseText;
                            var rt = r.substring(0, 400) + "...";
                            console.log(rt);
                            alert(rt);
                        });
                        request.send();
                    });
                }
            }
            function renderSaveToDrive() {
                var url = "https://dl.dropboxusercontent.com/s/deroi5nwm6u7gdf/advice.png";
                var filename = "europassdev-test-image-" + (new Date()).getMilliseconds() + ".png";

                gapi.savetodrive.render('gdrive-save-render', {
                    src: url,
                    filename: filename,
                    sitename: 'EWA offsite TEST'
                });
            }
            function prepareGDriveSaver() {
                var gdriveRender = $('#gdrive-save-render');
                gdriveRender.on('click', renderSaveToDrive);
                gdriveRender.click();
            }
            function prepareGDrive() {
                window.gDriveAppId = window.googledriveAppId[ window.location.host ];
                window.developerKey = window.googledriveDevKey[ window.location.host ];
                window.clientId = window.googledriveClientId[ window.location.host ];
                prepareGDrivePicker();
                prepareGDriveSaver();
            }
            //======================= D R O P B O X ===========================================/
            function prepareDropboxUploadFrom() {
                prepareDropboxChooser("dropbox-upload-from",
                        ['.pdf', '.xml'],
                        function (files) {
                            var file = files[0];
                            var url = file.link
                            var msg = "Dropbox:Chooser - Selected file:\n" +
                                    "\tName: " + file.name + "\n" +
                                    "\tLink: " + url + "\n" +
                                    "\tSize in bytes: " + file.bytes + "\n" +
                                    "\tIcon: " + file.icon + "\n" +
                                    "\tThumbnail: " + file.thumbnailLink;
                            console.log(msg);
                            alert(msg);

                            var request = new XMLHttpRequest();
                            request.open('GET', url);
                            request.addEventListener('load', function () {
                                var r = request.responseText;
                                var rt = r.substring(0, 400) + "...";
                                console.log(rt);
                                alert(rt);
                            });
                            request.send();
                        });
            }
            function prepareDropboxStoreTo(accessToken) {
                $("#dropbox-store-to").click(function () {

                    var control = $(this);

                    var client = new Dropbox.Client({key: window.dropboxKey[window.location.host]});

                    client.authDriver(new Dropbox.AuthDriver.Popup({
                        receiverUrl:
                                window.location.protocol + "//" + window.location.host + window.dropboxCallback
                    }));
                    //var isAuthenticated = client.isAuthenticated();
                    //console.log( "Dropbox:isAuthenticated? " + isAuthenticated );
                    client.authenticate(function (error, client) {
                        if (error) {
                            return dropboxError(error);
                        }
                        // The user authorized your app, and everything went well.
                        // client is a Dropbox.Client instance that you can use to make API calls.
                        var accessToken = client.credentials().token;
                        dropboxStoreTo(control, accessToken, client);
                    });
                });
            }
            function dropboxStoreTo(el, accessToken, client) {
                //Upload...
                var params = [
                    {name: "json", value: JSON.stringify(window.jsonESP)},
                    {name: "folderId", value: null},
                    {name: "accessToken", value: accessToken}];
                var inputs = "";
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    inputs += "<input type='hidden' name='" + param.name + "' value='" + param.value + "' />";
                }
                $("<form id=\"tempform\" enctype=\"application/x-www-form-urlencoded\" accept-charset=\"utf-8\" method=\"post\">" + inputs + "</form>")
                        .appendTo('body');
                $("<div id=\"processing\">Processing...</div>").insertAfter(el);
                $.post(
                        "/api/cloud-provider/dropbox/save",
                        $("#tempform").serialize()

                        ).done(function () {
                    var msg = "Dropbox - Document was uploaded successfully.";
                    console.log(msg);
                    alert(msg);
                }).fail(function () {
                    var msg = "Dropbox - Document failed to be uploaded.";
                    console.log(msg);
                    alert(msg);
                }).always(function () {
                    $("#tempform").remove();
                    $("#processing").remove();

                    //ekar: 2014-04-28: We must delete the local storage so long as we do not find a way to do this through the API 
                    //client.authDriver().BrowserBase.localStorage().forgetCredentials();
                    var key = "dropbox-auth:default:" + (client.appHash());
                    delete window.localStorage [ key ];

                    console.log("Dropbox - Document uploading completed.");
                });
            }
            function prepareDropboxChooser(elId, allowedExtensions, successCallback) {
                var options = {
                    // Required. Called when a user selects an item in the Chooser.
                    success: successCallback,
                    // Optional. Called when the user closes the dialog without selecting a file
                    // and does not include any parameters.
                    cancel: function () {
                        console.log("Dropbox:Chooser - action cancelled.")
                    },
                    // Optional. "preview" (default) is a preview link to the document for sharing,
                    // "direct" is an expiring link to download the contents of the file. For more
                    // information about link types, see Link types below.
                    linkType: "direct", // "preview" or "direct"
                    // Optional. A value of false (default) limits selection to a single file, while
                    // true enables multiple file selection.
                    multiselect: false, // or true
                    // Optional. This is a list of file extensions. If specified, the user will
                    // only be able to select files with these extensions. You may also specify
                    // file types, such as "video" or "images" in the list. For more information,
                    // see File types below. By default, all extensions are allowed.
                    extensions: allowedExtensions,
                };

                var button = Dropbox.createChooseButton(options);
                document.getElementById(elId).appendChild(button);
            }
            function prepareDropbox() {
                console.log("Dropbox - Is browser supported? " + Dropbox.isBrowserSupported());

                prepareDropboxUploadFrom();
                prepareDropboxStoreTo();

            }
            function dropboxError(error) {
                alert(error.status);
                console.log(error.status);
                switch (error.status) {
                    // the user token expired.
                    case Dropbox.ApiError.INVALID_TOKEN:
                        break;
                        // The file or folder you tried to access is not in the user's Dropbox.
                    case Dropbox.ApiError.NOT_FOUND:
                        break;
                        // The user is over their Dropbox quota.
                    case Dropbox.ApiError.OVER_QUOTA:
                        break;
                        // Too many API requests. Tell the user to try again later.
                    case Dropbox.ApiError.RATE_LIMITED:
                        break;
                        // An error occurred at the XMLHttpRequest layer.
                    case Dropbox.ApiError.NETWORK_ERROR:
                        break;
                        // Caused by a bug in dropbox.js, in your application, or in Dropbox.
                        // Tell the user an error occurred, ask them to refresh the page.
                    case Dropbox.ApiError.INVALID_PARAM:
                    case Dropbox.ApiError.OAUTH_ERROR:
                    case Dropbox.ApiError.INVALID_METHOD:
                    default:
                }
            }

            window.onload = function () {
                prepareGDrive();
                prepareDropbox();
                prepareOneDrive();
            };
        </script>
    </head>
    <body>

        <table>
            <thead>
                <tr>
                    <th colspan="3">--- EWA-923 ---</th>
                </tr>
                <tr>
                    <th>Cloud Service</th>
                    <th>Import Europass Document from...</th>
                    <th>Store Europass Document to...</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>Google Drive</td>
                    <td>
                        <button id="gdrive-picker-upload" 
                                type="button"
                                style='padding: 0 0 0 15px;border:1px solid lightgray;border-radius:3px;color: #444444;background:url("//ssl.gstatic.com/docs/doclist/images/save_to_drive-3366057c53bded98c27ddf0789911e35.png") no-repeat scroll 0 -61px / 21px 121px #F5F5F5;'
                                >Upload from...</button>
                    </td>
                    <td>
                        <button id="gdrive-picker-store" 
                                type="button"
                                style='padding: 0 0 0 15px;border:1px solid lightgray;border-radius:3px;color: #444444;background:url("//ssl.gstatic.com/docs/doclist/images/save_to_drive-3366057c53bded98c27ddf0789911e35.png") no-repeat scroll 0 -61px / 21px 121px #F5F5F5;'
                                >Store to...</button>
                    </td>
                </tr>
                <tr>
                    <td>Dropbox</td>
                    <td id="dropbox-upload-from"></td>
                    <td>
                        <a id="dropbox-store-to" class=" dropbox-dropin-btn dropbox-dropin-default">
                            <span class="dropin-btn-status"></span>
                            Store to...
                        </a>
                    </td>
                </tr>
                <tr>
                    <td>One Drive</td>
                    <td>
                        <button id="onedrive-picker-downloader" 
                                type="button"
                                style='padding: 0 0 0 15px;border:1px solid lightgray;border-radius:3px;color: #444444'
                                >Upload from...</button>
                        <!-- <div id="onedrive-picker-downloader"></div> -->
                    </td>
                    <td>
                        <button id="onedrive-picker-uploader" 
                                type="button"
                                style='padding: 0 0 0 15px;border:1px solid lightgray;border-radius:3px;color: #444444'				
                                >Store to...</button>

                        <!-- <div id="onedrive-picker-uploader"></div> -->
                        <!-- <form><input id="onedrive-saver-file" name="file" type="file"></form> -->
                    </td>
                </tr>

            </tbody>
        </table>

    </body>
</html>