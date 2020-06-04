define(
        ['jquery']
        , function ($) {
            var GlobalHelpOption = function () {
                this.HELP_ON = "on";
                this.HELP_OFF = "off";

                this.isTablet = (/(iPhone|iPod|iPad)/.test(navigator.userAgent) || (/Android/.test(navigator.userAgent)) || (/BlackBerry/.test(navigator.userAgent)));
                this.defaultHELP = this.HELP_ON;
                if (this.isTablet) {
                    this.defaultHELP = this.HELP_OFF;
                }
                this.status = this.defaultHELP;
            };

            GlobalHelpOption.prototype.isOn = function () {
                var status = null;
                (this.status === this.HELP_ON) ? status = true : status = false;
                return status;
            };

            GlobalHelpOption.prototype.get = function () {
                return this.status;
            };

            GlobalHelpOption.prototype.set = function (status) {
                if (status !== undefined && status !== null) {
                    if (status === this.HELP_ON || status === this.HELP_OFF) {
                        this.status = status;
                    }
                }
            };

            GlobalHelpOption.prototype.switcher = function () {
                var status = this.get();
                switch (status) {
                    case this.HELP_OFF:
                        this.set(this.HELP_ON);
                        break;
                    case this.HELP_ON:
                        this.set(this.HELP_OFF);
                        break;
                    default:
                        this.set(this.defaultHELP);
                }
                return this.status;
            };
            return GlobalHelpOption;
        }
);