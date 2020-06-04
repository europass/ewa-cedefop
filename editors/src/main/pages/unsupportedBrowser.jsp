<!DOCTYPE html>
<html lang="${sessionScope['locale']}">
    <head>
        <meta charset=utf-8 />
        <title>Europass: Curriculum vitae European Skills Passport</title>

        <style type="text/css">

            body {
                font-family: Open Sans,sans-serif;
                font-size: 14px;
                margin: 0;
                padding: 0;
                background: #f4f4f4;
            }

            .image-area {
                border: none;
                position: relative;
                margin-top: 1.5rem;
                margin-bottom: 0.5rem;
                background: none no-repeat 50% 50% #2c4969;
                height: 135px;
            }

            .image-area .img-logo{
                width:201px;
                height: 57px;
                /* margin: 30px auto 25px 1%; */
                padding: 30px;
                max-width: 90%;
                position: relative;
                background: url("${applicationScope['context.resourcesBase']}/static/ewa/images/europass_logo-milestone-150M.png?version=${project.version}") no-repeat center center transparent;
                top: 1.5rem;
            }

            .message-area {
                border: 0 none;
                color: #737373;
                font-size: 16px;
                font-weight: 0,9rem;
                line-height: normal;
                display: block;
                vertical-align: baseline;
                text-align: left;
                padding: 5px 10px 5px 60px;
                border-radius: 4px;
                margin: auto;
                margin-top: 3%;
                position: relative;
                right: 25px;
                top: 10px;
                width: 65%;
                background: url("${applicationScope['context.resourcesBase']}/static/ewa/images/legacy/warning_icon.png?version=${project.version}") no-repeat scroll 20px center #ffdfc8;
            }

        </style>
    </head>
    <body>
        <div class="image-area">
            <div class="img-logo">

            </div>

        </div>
        <div class="message-area">
            <p>
                <b><span id="unsupported-browser-notice"><!-- GuiLabel: EWA.UnsupportedBrowser.Headline --></span></b><br/>
                <span id="unsupported-browser-message" class="description"><!-- GuiLabel: EWA.UnsupportedBrowser.Description --></span>
            </p>
        </div>
        <script type="text/javascript">
            window.ewaLocale = "${sessionScope['locale']}";
            if (window.ewaLocale === undefined
                    || window.ewaLocale === null
                    || window.ewaLocale === ""
                    || window.ewaLocale === "null") {
                window.ewaLocale = "en";
            }
            window.version = "${applicationScope['context.current.version']}";
            window.resourcesBase = "${applicationScope['context.resourcesBase']}";
        </script>
        <script type="text/javascript" 
                data-main="${applicationScope['context.resourcesBase']}/static/ewa/scripts/unsupportedBrowsers/main.js" 
                src="${applicationScope['context.resourcesBase']}/static/ewa/libraries/require/require-2.1.14-min.js"></script>		
    </body>
</html>
