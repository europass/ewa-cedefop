define(
        [
            'jquery'
                    , 'europass/http/Header'
                    , 'europass/http/Verb'
        ],
        function ($, Header, Verb) {
            var Resource = function (uri) {
                this.uri = uri;
                this._params = {};
                this._headers = {};
            };
            /*
             * path = function ( String path ) 
             */
            Resource.prototype.path = function (path) {
                var that = {};
                $.extend(true, that, this);
                that.uri = this.uri + this.normalizePath(path);
                return that;
            };
            /*
             * Normalise path
             * If it does not start with a '/', force it
             */
            Resource.prototype.normalizePath = function (path) {
                return (path.substr(0, 1) == '/' ? path : '/' + path);
            };
            /*
             * accept = function ( String acceptType ) 
             */
            Resource.prototype.accept = function (acceptType) {
                this.header(Header.accept, acceptType);
            };
            /*
             * contentType= function ( String contentType ) 
             */
            Resource.prototype.contentType = function (contentType) {
                this.header(Header.contentType, contentType);
            };
            Resource.prototype.contentLanguage = function (locale) {
                this.header(Header.contentLanguage, locale);
            };
            Resource.prototype.acceptLanguage = function (locale) {
                this.header(Header.acceptLanguage, locale);
            };
            /*
             * header = function ( String name, String value) 
             */
            Resource.prototype.header = function (name, value) {
                this._headers[name] = value;
            };
            /*
             * header = function ( Object headers ) 
             */
            Resource.prototype.headers = function (headers) {
                this._headers = headers;
            };
            /*
             * param = function ( String name, String value) 
             */
            Resource.prototype.param = function (name, value) {
                var that = {};
                $.extend(true, that, this);
                that._params[name] = value;
                return that;
            };
            /*
             * params = function ( Object params) 
             */
            Resource.prototype.params = function (params) {
                var that = {};
                $.extend(true, that, this);
                that._params = params;
                return that;
            };
            Resource.prototype.listener = function () {};

            Resource.prototype.listeners = function () {};

            /* ===================== REST VERBS ===============================*/
            /*
             * _get = function ( Object options ) 
             * options:
             * 	success: Object
             * 				callback: Function
             * 				scope: Object
             * 				args : Array of String
             * 	error: Object
             * 				callback: Function
             * 				scope: Object
             * 				args : Array of String
             * 	fetch  : Array of Object
             * 				typeName    : String
             * 				locale      : String
             * 				propertyName: String
             */
            Resource.prototype._get = function (options) {
                this.method(Verb._get, null, options);
            };
            /*
             * _put = function ( Object entity, Object options ) 
             */
            Resource.prototype._put = function (entity, options) {
                this.method(Verb._put, entity, options);
            };
            /*
             * _post = function ( Object entity, Object options ) 
             */
            Resource.prototype._post = function (entity, options) {
                this.method(Verb._post, entity, options);
            };
            /*
             * _delete = function ( Object options ) 
             */
            Resource.prototype._delete = function (options) {
                this.method(Verb._delete, null, options);
            };
            /*
             * _header = function ( Object options ) 
             */
            Resource.prototype._header = function (options) {
                this.method(Verb._header, null, options);
            };
            /*
             * _options = function ( Object options ) 
             */
            Resource.prototype._options = function (options) {
                this.method(Verb._options, null, options);
            };
            /*
             * method = function ( String method, Object entity, Object options ) 
             */
            Resource.prototype.method = function (method, entity, options) {
                try {
                    if ($.isEmptyObject(options)) {
                        throw 'Empty-Options-Object';
                    }
                    var opt = this.normalizeOptions(options);

                    var requestConfiguration = {
                        type: method,
                        //dataType : this.header[Header.accept],
                        async: options.async !== undefined ? options.async : true,
                        url: this.uri,
                        context: this,
                        success: function (data, textStatus, jqXHR) {
                            this.success.apply(this, [data, textStatus, jqXHR, opt]);
                        },
                        error: function (jqXHR, textStatus, errorThrown) {
                            this.error.apply(this, [jqXHR, textStatus, errorThrown, opt]);
                        },
                        complete: function (jqXHR, textStatus) {
                            this.complete.apply(this, [jqXHR, textStatus, opt]);
                        },
                        headers: this._headers,
                        data: this._params
                    };

                    // Add POST data
                    if (entity !== undefined && entity !== null && entity !== "") {
                        requestConfiguration.data = JSON.stringify(entity);
                    }
                    //Ajax Call
                    $.ajax(requestConfiguration);
                } catch (error) {
                    if (error == 'Empty-Options-Object') {
                        alert('Error: The options Object cannot be left empty.');
                    } else {
                        alert('Exception [' + error.name + ']: Message: ' + error.message + '\n at: ' + error.location);
                    }
                }
            };

            Resource.prototype.success = function (data, textStatus, jqXHR, opts) {
                var entityInfo = data;
                //Call Success Callback
                if (opts.success.args && entityInfo !== undefined) {
                    opts.success.args.unshift(entityInfo);
                }
                opts.success.callback.apply(opts.success.scope, opts.success.args);
            };

            Resource.prototype.error = function (jqXHR, textStatus, errorThrown, opts) {
                //Call error callback
                var responseText = jqXHR.responseText;
                if (opts.error.args && responseText !== undefined) {
                    opts.error.args.unshift(responseText);
                }
                var status = jqXHR.status;
                if (opts.error.args && status !== undefined) {
                    opts.error.args.unshift(status);
                }
                opts.error.callback.apply(opts.error.scope, opts.error.args);
            };

            Resource.prototype.complete = function (jqXHR, textStatus, opts) {
                //Call Complete callback
                opts.complete.callback.apply(opts.complete.scope, opts.complete.args);
            };

            /**
             * Used by Resource.js
             */
            Resource.prototype.normalizeOptions = function (options) {
                try {
                    // === Initialize ==
                    var opt = {
                        success: {},
                        error: {},
                        complete: {}
                    };
                    var defaultFunction = function () {};
                    // === SUCCESS == 
                    if ($.isFunction(options.success)) {
                        opt.success.callback = options.success,
                                opt.success.scope = options.scope || window;
                        opt.success.args = ((options.args === undefined || $.isEmptyObject(options.args)) ?
                                []
                                : (($.isArray(options.args) ? options.args : [options.args])));
                    } else {
                        //if it is object
                        if (!$.isEmptyObject(options.success)) {
                            opt.success = options.success || {};
                            opt.success.scope = options.success.scope || options.scope || window;
                            if (options.success.args !== undefined && options.success.args !== null && !$.isEmptyObject(options.success.args)) {
                                opt.success.args = $.isArray(options.success.args) ? options.success.args : [options.success.args];
                            } else if (options.args !== undefined && options.args !== null && !$.isEmptyObject(options.args)) {
                                opt.success.args = $.isArray(options.args) ? options.args : [options.args];
                            } else {
                                opt.success.args = [];
                            }
                        } else {
                            throw 'Success-No-Function-Or-Object';
                        }
                    }
                    // === ERROR ===
                    if (options.error === undefined) {
                        opt.error.scope = window;
                        opt.error.callback = defaultFunction;
                        opt.error.args = [];
                    } else if ($.isFunction(options.error)) {
                        opt.error.callback = options.error,
                                opt.error.scope = options.scope || window;
                        opt.error.args = ((options.args === undefined || $.isEmptyObject(options.args)) ?
                                []
                                : (($.isArray(options.args) ? options.args : [options.args])));
                    } else {
                        opt.error = options.error || {};
                        opt.error.callback = options.error.callback || defaultFunction;
                        opt.error.scope = options.error.scope || options.scope || window;
                        if (options.error.args !== undefined && options.error.args !== null && !$.isEmptyObject(options.error.args)) {
                            opt.error.args = $.isArray(options.error.args) ? options.error.args : [options.error.args];
                        } else if (options.args !== undefined && options.args !== null && !$.isEmptyObject(options.args)) {
                            opt.error.args = $.isArray(options.args) ? options.args : [options.args];
                        } else {
                            opt.error.args = [];
                        }
                    }

                    // === COMPLETE ===
                    if (options.complete === undefined) {
                        opt.complete.scope = window;
                        opt.complete.callback = defaultFunction;
                        opt.complete.args = [];
                    } else if ($.isFunction(options.complete)) {
                        opt.complete.callback = options.complete,
                                opt.complete.scope = options.scope || window;
                        opt.complete.args = ((options.args === undefined || $.isEmptyObject(options.args)) ?
                                []
                                : (($.isArray(options.args) ? options.args : [options.args])));
                    } else {
                        opt.complete = options.complete || {};
                        opt.complete.callback = options.complete.callback || defaultFunction;
                        opt.complete.scope = options.complete.scope || options.scope || window;
                        if (options.complete.args !== undefined && options.complete.args !== null && !$.isEmptyObject(options.complete.args)) {
                            opt.complete.args = $.isArray(options.complete.args) ? options.complete.args : [options.complete.args];
                        } else if (options.args !== undefined && options.args !== null && !$.isEmptyObject(options.args)) {
                            opt.complete.args = $.isArray(options.args) ? options.args : [options.args];
                        } else {
                            opt.complete.args = [];
                        }
                    }
                    //=== Return ==
                    return opt;

                } catch (error) {
                    if (error == 'Success-No-Function-Or-Object') {
                        alert('Error', 'There exists no success callback.');
                    }
                }
            };

            return Resource;
        }
);