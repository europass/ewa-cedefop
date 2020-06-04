define(
        [
            'jquery',
            'underscore',
            'backbone',
            'ckeditor'
        ],
        function ($, _, Backbone, CKEDITOR) {

            var RichTextEditorView2 = Backbone.View.extend({

                editor: null,
                editorTextarea: null,
                editorFocused: null,

                events: {
                    "europass:rte:clear": "clearEditorData"
                },

                initialize: function (options) {
                    this.enableRichTextEditor(options);
                },
                onClose: function () {
                    for (var name in CKEDITOR.instances) {
                        CKEDITOR.instances[name].removeAllListeners();
                        CKEDITOR.remove(CKEDITOR.instances[name]);
                    }
                },
                enableRichTextEditor: function (options) {
                    this.editorTextarea = options.el;

                    for (var instance in CKEDITOR.instances) {
                        if (CKEDITOR.instances[instance].name === options.el.attr('id')) {
                            CKEDITOR.instances[instance].removeAllListeners();
                            CKEDITOR.remove(CKEDITOR.instances[instance]);
                        }
                    }

                    if (options.rteConfig.applyTo.length === 2) {
                        this.editor = CKEDITOR.replace(options.el.attr('id'), {
                            language: ewaLocale,
                            height: '250px',
                            title: false,
                            extraPlugins: 'divarea,removeformat,epas,indentblock',
                            tabSpaces: 4,
                            keystrokes: [[9, 'indent'],
                                [CKEDITOR.SHIFT + 9, 'outdent']
                            ],
                            indentClasses: ['indent1', 'indent2', 'indent3'],
                            toolbar: [{
                                    name: 'insert',
                                    items: ['Bold', 'Italic', 'Underline', 'BulletedList', 'Outdent', 'Indent', 'Link', 'Unlink', 'RemoveFormat', 'Subscript', 'Superscript', 'Recommendations']
                                }]
                        });
                        // Close Action for ALL Custom Select Elements
                        // var menu = $(this.editorTextarea).closest('.editor').find('.help').find('.context-recommendations-list');
                        $(document).mouseup(function (e) {
                            var container = $('.context-recommendations-list');
                            var isButtonContainer = $(e.target);
                            var isButton = isButtonContainer.is('.cke_button__recommendations') ? isButtonContainer.is('.cke_button__recommendations') : isButtonContainer.is('.cke_button__recommendations_icon');
                            if (!isButton) {
                                container.hide();
                            }
                        });
                    } else {
                        this.editor = CKEDITOR.replace(options.el.attr('id'), {
                            extraPlugins: 'divarea,removeformat,indentblock',
                            tabSpaces: 4,
                            keystrokes: [[9, 'indent'],
                                [CKEDITOR.SHIFT + 9, 'outdent']
                            ],
                            language: ewaLocale,
                            indentClasses: ['indent1', 'indent2', 'indent3'],
                            height: '250px',
                            title: false,
                            toolbar: [{
                                    name: 'insert',
                                    items: ['Bold', 'Italic', 'Underline', 'BulletedList', 'Outdent', 'Indent', 'Link', 'Unlink', 'RemoveFormat', 'Subscript', 'Superscript']
                                }]
                        });
                    }

                    var that = this;

                    $(".cke").on("click", $.proxy(this.placeHolderClicked, this));

                    options.el.closest("fieldset").on("europass:contextmenu:added", $.proxy(this.recommendationsMenu, this));

                    this.editor.on('instanceReady', function (event) {
                        that.listenOnTyping();
                        that.listenOnPaste();
                        that.listenOnBlur();
                        $('.cke_wysiwyg_div').attr('spellcheck', 'true');
                    });

                    if (isTablet) {
                        this.$el.parent().on({'touchstart': function (e) {
                                $(that.editorTextarea).closest('.editor').find('.help.placeholder').hide();
                                for (var instance in CKEDITOR.instances) {
                                    if (CKEDITOR.instances[instance].name === options.el.attr('id')) {
                                        CKEDITOR.instances[instance].focus();
                                    }
                                }
                            }
                        });
                    }

                },

                listenOnTyping: function () {
                    var that = this;
                    $(that.editorTextarea).closest('.editor').on('keydown', function (event) {
                        $(that.editorTextarea).closest('.editor').find('.help.placeholder').hide();
                    });
                },
                listenOnPaste: function () {
                    var that = this;
                    var cke = $(that.editorTextarea).closest('.editor').find('.cke_wysiwyg_div');
                    cke.on('paste', function (e) {
                        $(that.editorTextarea).closest('.editor').find('.help.placeholder').hide();
                    });
                },
                listenOnBlur: function () {
                    var that = this;
                    this.editor.on('blur', function () {
                        if (that.editor.getData() === '') {
                            $(that.editorTextarea).closest('.editor').find('.help.placeholder').show();
                        }
                        this.editorFocused = false;
                    });
                },
                recommendationsMenu: function (event, menuOptions) {
                    var that = this;
                    $(that.editorTextarea).closest('.editor').on('click', '.cke_button__recommendations', function (e) {
                        that.extraButtonMenu(that, e);
                    });
                },

                extraButtonMenu: function (that, e) {
                    // console.log($(e.target));
                    var allMenus = $('.context-recommendations-list');
                    var menuList = $(that.editorTextarea).closest('.editor').find('.help').find('.context-recommendations-list');
                    $.each(allMenus, function (index, menu) {
                        if (!$(menu).attr('opened')) {
                            $(menu).hide();
                        }
                    });

                    var pageX = $('.cke_wysiwyg_div').offset().left - 20;
                    var pageY = $('.cke_wysiwyg_div').offset().top + 29;

                    menuList.css("width", "325px");

                    var tooltipDiv = menuList.find(".tooltip-inner").first();

                    var x = pageX - $('.cke_wysiwyg_div').offset().left;
                    var y = pageY - $('.cke_wysiwyg_div').offset().top;

                    var tooltipDiv = menuList.find(".tooltip-inner").first();

                    var rightSided = (x - 25 > $('.cke_wysiwyg_div').width() / 2);

                    menuList.css("top", (y + 45) + "px");

                    if (rightSided) {

                        x = x - menuList.width();
                        x = (x < 0 ? -x : x); // set |x| as x's value

                        if (tooltipDiv.hasClass("left")) {
                            tooltipDiv.toggleClass("left");
                            tooltipDiv.toggleClass("right");
                        }
                        menuList.css("left", (x - 10) + "px");
                    } else {
                        if (tooltipDiv.hasClass("right")) {
                            tooltipDiv.toggleClass("left");
                            tooltipDiv.toggleClass("right");
                        }
                        menuList.css("left", (x + 50) + "px");
                    }

                    menuList.attr('opened', !menuList.attr('opened'));
                    menuList.toggle();

                    $(that.editorTextarea).closest('.editor').find('.help').find('.context-recommendations-list')
                            .find('.tooltip-arrow').find('.context-list').find('.noListStyle > li').off().click(function (event) {
                        $(that.editorTextarea).closest('.editor').find('.help.placeholder').hide();
                        that.editor.insertText($(event.target).text());
                        menuList.hide();
                        menuList.attr('opened', false);

                    });

                },
                validateView: function () {
                    this.saveEditorData();
                    return true;
                },
                saveEditorData: function () {
                    if (this.editor) {
                        var editorText = this.editor.getData();
                        this.editorTextarea.val(editorText);
                    }
                },

                clearEditorData: function () {
                    for (var instance in CKEDITOR.instances) {
                        CKEDITOR.instances[instance].updateElement();
                        CKEDITOR.instances[instance].setData('');
                        $(this.editorTextarea).closest('.editor').find('.help.placeholder').show();
                    }
                }

            });

            return RichTextEditorView2;
        }
);
