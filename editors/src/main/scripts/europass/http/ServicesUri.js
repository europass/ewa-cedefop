define(
        ['europass/http/ServicesUri'],
        function (Self) {
            if (Self === undefined || Self === null) {

                var apiBaseUri = window.config.api;
                var base = (apiBaseUri !== undefined && apiBaseUri !== null) ? apiBaseUri : '';

                var lastIndex = base.length - 1;
                var lastChar = base.substr(lastIndex);
                if (lastChar === '/') {
                    base = base.substring(0, lastIndex);
                }
                if (base === '') {
                    base = '/api';
                }
                var loggingBase = '/logging';
                var loadBase = '/load';
                var contactBase = '/contact/email';
                var shareReviewBase = '/share/email/review';
                var shareReviewPostbackBase = '/share/email/postback';

                var filesBase = base + '/files';
                var documentConversion = base + '/document';

                var social = base + '/social';
                var social_import = social + '/import';

                var Self = {
                    file_post: filesBase + '/file/upload',
                    file_get_delete: filesBase + '/file',
                    photo_post: filesBase + '/photo/upload',
                    signature_post: filesBase + '/signature/upload',
                    photo_get_delete: filesBase + '/photo',
                    scopeValidity: filesBase + '/scope',

                    document_conversion_to: {
                        available: documentConversion + "/to",
                        preview_pdf: documentConversion + '/to/pdf/preview',
                        local_xml: documentConversion + '/to/xml',
                        local_pdf: documentConversion + '/to/pdf',
                        local_word: documentConversion + '/to/word',
                        local_odt: documentConversion + '/to/opendoc',
                        email_xml: documentConversion + '/email/xml',
                        email_pdf: documentConversion + '/email/pdf',
                        email_word: documentConversion + '/email/word',
                        email_odt: documentConversion + '/email/opendoc',
                        googledrive_xml: documentConversion + '/cloud/googledrive/xml',
                        googledrive_pdf: documentConversion + '/cloud/googledrive/pdf',
                        googledrive_word: documentConversion + '/cloud/googledrive/word',
                        googledrive_odt: documentConversion + '/cloud/googledrive/opendoc',
                        dropbox_xml: documentConversion + '/cloud/dropbox/xml',
                        dropbox_pdf: documentConversion + '/cloud/dropbox/pdf',
                        dropbox_word: documentConversion + '/cloud/dropbox/word',
                        dropbox_odt: documentConversion + '/cloud/dropbox/opendoc',
                        onedrive_xml: documentConversion + '/cloud/onedrive/xml',
                        onedrive_pdf: documentConversion + '/cloud/onedrive/pdf',
                        onedrive_word: documentConversion + '/cloud/onedrive/word',
                        onedrive_odt: documentConversion + '/cloud/onedrive/opendoc',
                        googledrive_share: documentConversion + '/cloud/share/googledrive/xml',
                        dropbox_share: documentConversion + '/cloud/share/dropbox/xml',
                        onedrive_share: documentConversion + '/cloud/share/onedrive/xml',
                        partners: documentConversion + '/to/xml',
                        proxy_xml: documentConversion + '/to/proxy-xml',
                        post_to_eures: documentConversion + '/to/eures',
                        post_to_xing: documentConversion + '/to/xing',
                        check_cv_size: documentConversion + '/to/monster/checkCVSize',
                        post_to_monster: documentConversion + '/to/monster',
                        post_to_cvLibrary: documentConversion + '/to/cvLibrary',
                        post_to_anpal: documentConversion + '/to/anpal',
                        post_to_indeed: documentConversion + '/to/indeed'
                    },

                    document_upload: base + loadBase,

                    document_upload_from_cloud: base + loadBase + '/from-cloud',

                    is_file_Europass: base + loadBase + '/isEuroDoc',

                    logging: base + loggingBase,

                    contact: base + contactBase,

                    shareReview: base + shareReviewBase,
                    shareReviewPostback: base + shareReviewPostbackBase,

                    social_import_services: {
                        linkedIn: social_import + '/linkedin'
                    },

                    social_import_helpers: {
                        linkedIn: social_import + '/helper/linkedin'
                    }
                };
            }
            return Self;
        }
);