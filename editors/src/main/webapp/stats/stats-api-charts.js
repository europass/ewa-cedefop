var charts = {

    elemID: '',
    xAxisSelectID: '',

    dataSets: {},
    arrayData: [],
    dimensionSize: 0,
    hasDateParameter: false,

    // array indexes in arraydata of known parameters

    yearIndex: -1,
    monthIndex: -1,
    dayIndex: -1,

    recCountIndex: -1,
    docTypeIndex: -1,
    docLangIndex: -1,

    mLangIndex: -1,
    oLangIndex: -1,

    addressCountryIndex: -1,
    nationalityIndex: -1,

    ageIndex: -1,
    workExpIndex: -1,

    monthLiterals: ["", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"],

    datasetsComputed: new Array(),

    initDataSetXAxis: function () {

        $("div#x_axis_select").attr("style", "display:none");

        this.dimensionSize = this.arrayData[0].length;

        this.yearIndex = this.arrayData[0].indexOf("year_no");
        this.monthIndex = this.arrayData[0].indexOf("month_no");
        this.dayIndex = this.arrayData[0].indexOf("day_no");

        this.recCountIndex = this.arrayData[0].indexOf("rec_count");

        this.hasDateParameter = (this.yearIndex >= 0 || this.monthIndex >= 0 || this.dayIndex >= 0);
        if (this.hasDateParameter) {
            this.dataSets.date = new Array();
            $(this.xAxisSelectID).append($('<option>', {
                value: "date",
                text: 'Date'
            }));
        }

        this.docLangIndex = this.arrayData[0].indexOf("doc_lang");
        if (this.docLangIndex >= 0) {
            this.dataSets.documentLanguage = new Array();
            $(this.xAxisSelectID).append($('<option>', {
                value: "doc_lang",
                text: 'Document Language'
            }));
        }

        this.mLangIndex = this.arrayData[0].indexOf("m_lang");
        if (this.mLangIndex >= 0) {
            this.dataSets.motherTongue = new Array();
            $(this.xAxisSelectID).append($('<option>', {
                value: "m_lang",
                text: 'Mother Tongue'
            }));
        }
        this.oLangIndex = this.arrayData[0].indexOf("f_lang");
        if (this.oLangIndex >= 0) {
            this.dataSets.foreignLanguage = new Array();
            $(this.xAxisSelectID).append($('<option>', {
                value: "f_lang",
                text: 'Foreign Language'
            }));
        }

        this.addressCountryIndex = this.arrayData[0].indexOf("address_country");
        if (this.addressCountryIndex >= 0) {
            this.dataSets.country = new Array();
            $(this.xAxisSelectID).append($('<option>', {
                value: "address_country",
                text: 'Country'
            }));
        }

        this.nationalityIndex = this.arrayData[0].indexOf("nationality");
        if (this.nationalityIndex >= 0) {
            this.dataSets.nationality = new Array();
            $(this.xAxisSelectID).append($('<option>', {
                value: "nationality",
                text: 'Nationality'
            }));
        }

        this.genderIndex = this.arrayData[0].indexOf("gender_group");
        if (this.genderIndex >= 0) {
            this.dataSets.gender = new Array();
            $(this.xAxisSelectID).append($('<option>', {
                value: "gender_group",
                text: 'Gender'
            }));
        }

        this.ageIndex = this.arrayData[0].indexOf("age");
        if (this.ageIndex >= 0) {
            this.dataSets.age = new Array();
            $(this.xAxisSelectID).append($('<option>', {
                value: "age",
                text: 'Age'
            }));
        }

        this.workExpIndex = this.arrayData[0].indexOf("work_years");
        if (this.workExpIndex >= 0) {
            this.dataSets.experience = new Array();
            $(this.xAxisSelectID).append($('<option>', {
                value: "work_years",
                text: 'Work Experience'
            }));
        }

        this.docTypeIndex = this.arrayData[0].indexOf("doc_type");
        if (this.docTypeIndex >= 0) {
            this.dataSets.documentType = new Array();
            $(this.xAxisSelectID).append($('<option>', {
                value: "doc_type",
                text: 'Document Type'
            }));
        }

        this.dataSets.generic = new Array();
    },

    /*	init: function(urlStr,id,selectID){
     
     this.elemID = id;
     this.xAxisSelectID = selectID;
     
     var that = this;
     
     console.log("Query (plot):'"+urlStr+"'");
     
     $("body").trigger("statistics:plot:loading:show");
     
     $.ajax({
     type: "GET",
     
     url: urlStr,
     data: "",
     success: function(response){        
     
     if(response == "Query returned: empty (No Results Found)")
     $(id).html("<p>Unable to plot, request returned empty data</p>");
     else{
     
     if(typeof(response.replace) === 'function')
     that.arrayData = $.csv.toArrays(response.replace(new RegExp('NULL', 'g'), '0'), {onParseValue: $.csv.hooks.castToScalar});
     
     that.initDataSetXAxis();
     
     var chartData = that.transformData();
     google.charts.setOnLoadCallback(that.drawData(chartData));
     }
     },
     error: function(jqXHR, textStatus, errorThrown ){
     $(id).html("Failed to get results from Statistics API");
     },
     complete: function(){
     $("body").trigger("statistics:plot:loading:hide");
     }
     });
     
     return this;
     },*/

    init: function (data, id, selectID) {

        this.elemID = id;
        this.xAxisSelectID = selectID;

//		console.log("Query (plot):'"+urlStr+"'");

        $("body").trigger("statistics:loading:show");

        if (data == "Query returned: empty (No Results Found)")
            $(id).html("<p>Unable to plot, request returned empty data</p>");
        else {

            try {
                if (typeof (data.replace) === 'function')
                    this.arrayData = $.csv.toArrays(data.replace(new RegExp('NULL', 'g'), '0'), {onParseValue: $.csv.hooks.castToScalar});

                this.initDataSetXAxis();

                var chartData = this.transformData();
                google.charts.setOnLoadCallback(this.drawData(chartData));
            } catch (err) {
                $(id).html("Failed to get results from Statistics API: '" + err + "'");
            }

        }

        $("body").trigger("statistics:loading:hide");

        return this;
    },

    destroy: function () {

        $(this.elemID).html("");
        $(this.xAxisSelectID).html("");
        $("div#x_axis_select").attr("style", "display:none");

        this.elemID = '';
        this.xAxisSelectID = '';

        this.dataSets = {};
        this.arrayData = [];
        this.dimensionSize = 0;
        this.hasDateParameter = false;

        this.yearIndex = -1;
        this.monthIndex = -1;
        this.dayIndex = -1;

        this.recCountIndex = -1;
        this.docTypeIndex = -1;
        this.docLangIndex = -1;

        this.mLangIndex = -1;
        this.oLangIndex = -1;

        this.addressCountryIndex = -1;
        this.nationalityIndex = -1;

        this.ageIndex = -1;
        this.workExpIndex = -1;

        this.datasetsComputed = new Array();
    },

    transformData: function (parameterName) {

        var byAxis = parameterName;
        if (byAxis === undefined || byAxis === null) {

            var xAxisSelect = $(this.xAxisSelectID).find("option:first");

            if (xAxisSelect != undefined && xAxisSelect.val() != undefined && xAxisSelect.val() != "")
                byAxis = xAxisSelect.val();
            else
                byAxis = "date";
        }

        if ($.inArray(byAxis, this.datasetsComputed) < 0) {

            for (var i = 0; i < this.arrayData.length; i++) {

                var rowHeadersElement = new Array();

                var rowElement = new Array();

                // DATE MANIPULATION - construct the date and append in first in the data array / or construct the remainings
                if (byAxis == "date") {

                    if (this.hasDateParameter) {
                        if (i == 0)
                            rowHeadersElement.push("Date");
                        else
                            rowElement.push(this.constructDateString(i));

                        this.constructRows(i, rowHeadersElement, rowElement);

                        if (i == 0)
                            this.dataSets.date.push(rowHeadersElement);
                        else if (rowElement.length > 0)
                            this.dataSets.date.push(rowElement);
                    } else if (this.docTypeIndex >= 0) {
                        if (i == 0)
                            rowHeadersElement.push("Document Type");
                        else
                            rowElement.push("" + this.arrayData[i][this.docTypeIndex]);

                        this.constructRows(i, rowHeadersElement, rowElement);

                        if (i == 0)
                            this.dataSets.documentType.push(rowHeadersElement);
                        else if (rowElement.length > 0)
                            this.dataSets.documentType.push(rowElement);
                    } else {
                        if (i == 0)
                            rowHeadersElement.push("Generic");
                        else
                            rowElement.push("Number");

                        this.constructRows(i, rowHeadersElement, rowElement);

                        if (i == 0)
                            this.dataSets.generic.push(rowHeadersElement);
                        else if (rowElement.length > 0)
                            this.dataSets.generic.push(rowElement);
                    }

                }

                if (byAxis == "doc_type") {

                    if (i == 0)
                        rowHeadersElement.push("Document Type");
                    else
                        rowElement.push("" + this.arrayData[i][this.docTypeIndex]);

                    this.constructRows(i, rowHeadersElement, rowElement);

                    if (i == 0)
                        this.dataSets.documentType.push(rowHeadersElement);
                    else if (rowElement.length > 0)
                        this.dataSets.documentType.push(rowElement);
                }

                if (byAxis == "doc_lang") {

                    if (i == 0)
                        rowHeadersElement.push("Document Language");
                    else
                        rowElement.push("" + this.arrayData[i][this.docLangIndex]);

                    this.constructRows(i, rowHeadersElement, rowElement);

                    if (i == 0)
                        this.dataSets.documentLanguage.push(rowHeadersElement);
                    else if (rowElement.length > 0)
                        this.dataSets.documentLanguage.push(rowElement);
                }

                if (byAxis == "m_lang") {

                    if (i == 0)
                        rowHeadersElement.push("Mother Tongue");
                    else
                        rowElement.push("" + this.arrayData[i][this.mLangIndex]);

                    this.constructRows(i, rowHeadersElement, rowElement);

                    if (i == 0)
                        this.dataSets.motherTongue.push(rowHeadersElement);
                    else if (rowElement.length > 0)
                        this.dataSets.motherTongue.push(rowElement);
                }

                if (byAxis == "f_lang") {

                    if (i == 0)
                        rowHeadersElement.push("Foreign Language");
                    else
                        rowElement.push("" + this.arrayData[i][this.oLangIndex]);

                    this.constructRows(i, rowHeadersElement, rowElement);

                    if (i == 0)
                        this.dataSets.foreignLanguage.push(rowHeadersElement);
                    else if (rowElement.length > 0)
                        this.dataSets.foreignLanguage.push(rowElement);
                }

                if (byAxis == "address_country") {

                    if (i == 0)
                        rowHeadersElement.push("Country");
                    else
                        rowElement.push("" + this.arrayData[i][this.addressCountryIndex]);

                    this.constructRows(i, rowHeadersElement, rowElement);

                    if (i == 0)
                        this.dataSets.country.push(rowHeadersElement);
                    else if (rowElement.length > 0)
                        this.dataSets.country.push(rowElement);
                }

                if (byAxis == "nationality") {

                    if (i == 0)
                        rowHeadersElement.push("Nationality");
                    else
                        rowElement.push("" + this.arrayData[i][this.nationalityIndex]);

                    this.constructRows(i, rowHeadersElement, rowElement);

                    if (i == 0)
                        this.dataSets.nationality.push(rowHeadersElement);
                    else if (rowElement.length > 0)
                        this.dataSets.nationality.push(rowElement);
                }

                if (byAxis == "gender_group") {

                    if (i == 0)
                        rowHeadersElement.push("Gender");
                    else
                        rowElement.push(this.arrayData[i][this.genderIndex]);

                    this.constructRows(i, rowHeadersElement, rowElement);

                    if (i == 0)
                        this.dataSets.gender.push(rowHeadersElement);
                    else if (rowElement.length > 0)
                        this.dataSets.gender.push(rowElement);
                }

                if (byAxis == "age") {

                    if (i == 0)
                        rowHeadersElement.push("Age");
                    else
                        rowElement.push(this.arrayData[i][this.ageIndex]);

                    this.constructRows(i, rowHeadersElement, rowElement);

                    if (i == 0)
                        this.dataSets.age.push(rowHeadersElement);
                    else if (rowElement.length > 0)
                        this.dataSets.age.push(rowElement);
                }

                if (byAxis == "work_years") {

                    if (i == 0)
                        rowHeadersElement.push("Work Experience");
                    else
                        rowElement.push(this.arrayData[i][this.workExpIndex]);

                    this.constructRows(i, rowHeadersElement, rowElement);

                    if (i == 0)
                        this.dataSets.experience.push(rowHeadersElement);
                    else if (rowElement.length > 0)
                        this.dataSets.experience.push(rowElement);
                }
            }
        }

        $(this.xAxisSelectID).val(byAxis);

        if (byAxis != "date")
            this.datasetsComputed.push(byAxis);

        if (byAxis == "date") {
            if (this.hasDateParameter) {
                this.datasetsComputed.push(byAxis);
                $(this.xAxisSelectID).val("date");
                return this.dataSets.date;
            } else if (this.docTypeIndex >= 0) {
                $(this.xAxisSelectID).val("doc_type");
                this.datasetsComputed.push("doc_type");
                return this.dataSets.documentType;
            } else {
                this.datasetsComputed.push("generic");
                return this.dataSets.generic;
            }
        }
        if (byAxis == "gender_group") {
            return this.dataSets.gender;
        }
        if (byAxis == "doc_lang") {
            return this.dataSets.documentLanguage;
        }
        if (byAxis == "doc_type") {
            return this.dataSets.documentType;
        }
        if (byAxis == "m_lang") {
            return this.dataSets.motherTongue;
        }
        if (byAxis == "f_lang") {
            return this.dataSets.foreignLanguage;
        }
        if (byAxis == "address_country") {
            return this.dataSets.country;
        }
        if (byAxis == "nationality") {
            return this.dataSets.nationality;
        }
        if (byAxis == "age") {
            return this.dataSets.age;
        }
        if (byAxis == "work_years") {
            return this.dataSets.experience;
        }

    },

    constructDateString: function (idx) {

        var dateString = "";

        var offset = (
                this.yearIndex >= 0 ?
                this.monthIndex >= 0 ?
                this.dayIndex >= 0 ? 3 : 2
                : 1
                : 0
                );

        switch (offset) {

            case 1:
                dateString += this.arrayData[idx][this.yearIndex];
                break;
            case 2:
                dateString += this.monthLiterals[this.arrayData[idx][this.monthIndex]] + " " + this.arrayData[idx][this.yearIndex];
                break;
            case 3:
                dateString += this.arrayData[idx][this.dayIndex] + " " + this.monthLiterals[this.arrayData[idx][this.monthIndex]] + " " + this.arrayData[idx][this.yearIndex];
                break;

            default:
                break;
        }

        return dateString;
    }

    , drawData: function (chartData) {

        if (chartData.length == 0) {
            $(this.elemID).hmtl("Unable to Plot Data...");
        } else {
            var dataSetSize = chartData[0].length;

            // this new DataTable object holds all the data
            var data = new google.visualization.arrayToDataTable(chartData);

            if (this.datasetColumnHasDuplicates(data, 0, chartData)) {

                var otherColumns = new Array();

                for (var i = 1; i < dataSetSize; i++) {
                    otherColumns.push({'column': i, 'aggregation': google.visualization.data.sum, 'type': 'number'});
                }
                data = google.visualization.data.group(
                        data,
                        [0],
                        otherColumns
                        );
            }

            vAxisOptions = {
                title: "# of generated Documents",
                format: 'long'
            };

            hAxisOptions = {
                title: chartData[0][0],
                gridlines: {count: data.getDistinctValues(0).length}
            };

            if (chartData[0][0] == "Work Experience" || chartData[0][0] == "Age") {

                var defaultMax = (chartData[0][0] == "Work Experience" ? 60 : 90);
                var defaultMin = (chartData[0][0] == "Work Experience" ? 0 : 12);

                hAxisOptions.viewWindow = {
                    max: (data.getColumnRange(0).max < defaultMax ? data.getColumnRange(0).max : defaultMax),
                    min: (data.getColumnRange(0).min > defaultMin ? data.getColumnRange(0).min : defaultMin)
                };

                hAxisOptions.gridlines = {count: hAxisOptions.viewWindow.max - hAxisOptions.viewWindow.min};
                hAxisOptions.format = 'short';
            }

            var options = {
//		             title: "A Chart from a Statistics API CSV response!",
                width: "800",
                height: "400",
                vAxis: vAxisOptions,
                hAxis: hAxisOptions,
                bar: {groupWidth: "95%"},
                seriesType: "bars",
                isStacked: dataSetSize > 2 ? true : false
            };

            var chart = new google.visualization.ComboChart(document.getElementById(this.elemID.substring(1, this.elemID.length)));
            chart.draw(data, options);

            if ($("div#x_axis_select").find("select").html() != "")
                $("div#x_axis_select").attr("style", "display:block");

        }
    }

    , datasetColumnHasDuplicates: function (data, idx, chartData) {

        // chartData : the returned array from the Statistics API call
        // data : the dataset created based on the chartData
        // idx: the column in which the comparission of duplicates will be applied

        return (data.getDistinctValues(idx).length !== (chartData.length - 1));
    }

    , constructRows: function (arrayDataIdx, rowHeadersElement, rowElement) {

        for (var i = 0; i <= this.dimensionSize - 1; i++) {

            // Avoid the date indexes to be inserted - if no date fields in the result, the condition will succeed
            if (this.isNotDeclaredParameterIndex(i)) {
                if (arrayDataIdx == 0) {

                    if (arrayDataIdx == this.recCountIndex)
                        rowHeadersElement.push("# of generated Documents");
                    else
                        rowHeadersElement.push(this.arrayData[0][i]);
                } else {
                    rowElement.push(this.arrayData[arrayDataIdx][i]);
                }
            }
        }
    }

    , isNotDeclaredParameterIndex: function (index) {

        return (index != this.yearIndex
                && index != this.monthIndex
                && index != this.dayIndex
                && index != this.docTypeIndex
                && index != this.docLangIndex
                && index != this.addressCountryIndex
                && index != this.nationalityIndex
                && index != this.mLangIndex
                && index != this.oLangIndex
                && index != this.genderIndex
                && index != this.ageIndex
                && index != this.workExpIndex);
    }
};