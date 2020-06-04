/* 
 * Copyright (c) 2002-2020 Cedefop.
 * 
 * This file is part of EWA (Cedefop).
 * 
 * EWA (Cedefop) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * EWA (Cedefop) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with EWA (Cedefop). If not, see <http ://www.gnu.org/licenses/>.
 */
package europass.ewa.services.statistics.resources;

import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;


import net.hamnaberg.funclite.Optional;
import net.hamnaberg.jsonstat.Category;
import net.hamnaberg.jsonstat.Data;
import net.hamnaberg.jsonstat.Dataset;
import net.hamnaberg.jsonstat.Dimension;
import net.hamnaberg.jsonstat.Role;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.sun.jersey.core.spi.factory.ResponseBuilderImpl;

import europass.ewa.model.conversion.json.EWAJsonMapper;
import europass.ewa.services.ResponseUtils;
import europass.ewa.services.statistics.api.info.QueryInfo;
import europass.ewa.services.statistics.api.process.StatisticsApiRequestProcess;
import europass.ewa.services.statistics.constants.ServicesStatisticsConstants;
import europass.ewa.services.statistics.enums.request.ResponseFormats;
import europass.ewa.services.statistics.enums.response.ResponseStatusMessage;
import europass.ewa.services.statistics.enums.validation.ResponseResult;
import europass.ewa.services.statistics.mappings.structures.caching.CachedQueries;
import europass.ewa.services.statistics.structures.QueryParameter;
import europass.ewa.services.statistics.structures.QueryProperties;
import europass.ewa.services.statistics.structures.ValueProperties;
import europass.ewa.services.statistics.util.StringHelper;

/**
 * Statistics API Endpoint services 
 * 
 * Request with Statistics API compliant syntax query
 * 
 * @author pgia
 * 
 */
@Path(ServicesStatisticsConstants.RESPONSE_TYPE_BASE)
public class StatisticsAPIResource {
	
	private static StatisticsApiRequestProcess process;
	
	private QueryInfo statisticsApiInfo;
	private QueryProperties qprops;

	private StringBuilder sb;
	
	@Inject
	public StatisticsAPIResource(
			StatisticsApiRequestProcess proc,
			QueryInfo info,
			QueryProperties properties,
			CachedQueries cQueries) {
		
		process = proc;
		statisticsApiInfo = info;
		qprops = properties;
		
		sb = new StringBuilder();
	}

	@GET
	@Produces("text/plain")
	public String getGreeting() {
		return "Europass: This is the Statistics API Service";
	}

	// ---- Request with response format options CSV, JSON (forwards the request to queryRequest()) ----
	@GET
	@Produces("text/plain;charset=utf-8")
	@Path("/{format}")
	public Response queryRequestNoQuery(@Context UriInfo ui, @PathParam("format") String format) {

		return this.queryRequest(ui, format, new PathSegment() {

			@Override
			public String getPath() {
				return "";
			}

			@Override
			public MultivaluedMap<String, String> getMatrixParameters() {
				return null;
			}
		});
	}

	@GET
	@Produces({MediaType.APPLICATION_JSON + ";charset=utf-8", "text/csv;charset=utf-8", "text/plain;charset=utf-8"})
	@Path("/{format}/{params}")
	public Response queryRequest(@Context UriInfo ui, @PathParam("format") String format, @PathParam("params") PathSegment params) {

		// Process run and begin of steps execution 
		// - info holds the results and the validation object
		
		process.process(statisticsApiInfo, qprops, format, params);

		if (statisticsApiInfo.isValidated() && qprops != null && qprops.getResults() != null && qprops.getResults().getResponseResult().getDetails().equals(ResponseStatusMessage.RESPONSE_200)) {
			List<Object> results = qprops.getResults().getResultData();
			if (results != null) {
				return responseValidRequest(results, format);
			}
		} else {
			return responseInvalidRequest(format);
		}

		return Response.serverError().status(500).build();
	}

	private Response responseValidRequest(List<Object> results, String responseFromat) {

		boolean isJSON = responseFromat.equals(ResponseFormats.PATH_RESPONSE_TYPE_JSON.getDescription());
		boolean isCSV = responseFromat.equals(ResponseFormats.PATH_RESPONSE_TYPE_CSV.getDescription());

		// In case of empty result, return a
		if (results.isEmpty()) {

			ResponseResult.ResponseInfo responseInfo = new ResponseResult.ResponseInfo(200,
					ResponseStatusMessage.RESPONSE_200.getStatus(),
					"Query returned: " + ResponseStatusMessage.RESPONSE_204.getMessage() + " (No Results Found)",
					statisticsApiInfo.getValidationResult().resultMessage());

			return ResponseUtils.buildResponse(responseInfo.getMessage(), MediaType.TEXT_HTML, 200);
		}

		// JSON
		if (isJSON) {

			List<Data> values = new ArrayList<>();

			for (Object cubeRow : results) {
				if (cubeRow.getClass().isArray()) {
					int length = Array.getLength(cubeRow);

					List<Object> dataRowValues = new LinkedList<>();

					for (int i = 0; i < length; i++) {
						Object fieldResult = Array.get(cubeRow, i);
						dataRowValues.add(fieldResult);
					}

					values.add(new Data(dataRowValues, null));
				}
			}

			// Create empty Category
			Map<String, Integer> indices = new HashMap<>();
			Map<String, String> labels = new HashMap<>();
			Map<String, List<String>> children = new HashMap<>();
			Category category = new Category(indices, labels, children);

			// Create Dimensions
			List<Dimension> dimensions = new ArrayList<>();

			List<String> headersList = qprops.getResults().getResultHeaders();
			int index = 0;
			for (String header : headersList) {

				Dimension dim = new Dimension(index++, header, 1, Optional.some("metric"), category, Optional.some(Role.valueOf("geo")));
				dimensions.add(dim);
			}

			// Create Dataset
			Dataset dataset = new Dataset("metric", null, values, null, dimensions);

			try {
				ObjectMapper mapper = EWAJsonMapper.get();
				Writer writer = new StringWriter();

//				JsonContents contents = new JsonContents(headersList,datasets);
//				mapper.writeValue(writer, contents);
				mapper.writeValue(writer, dataset);
				sb.append(writer.toString());
			} catch (Exception e) {
				e.printStackTrace();

				return Response.serverError().build();
			}

			return ResponseUtils.buildResponse(sb.toString(), MediaType.APPLICATION_JSON, 200);
		}

		// CSV
		if (isCSV) {

			Joiner commaJoiner = Joiner.on(",");

			List<String> headersList = qprops.getResults().getResultHeaders();
			sb.append(commaJoiner.join(headersList) + "\n");

			for (Object cubeRow : results) {

				if (cubeRow == null) {
					sb.append("NULL\n");
				} else if (cubeRow.getClass().isArray()) {

					List<Object> dataRowValues = new LinkedList<>();

					for (int i = 0; i < Array.getLength(cubeRow); i++) {
						Object fieldResult = Array.get(cubeRow, i);

						if (fieldResult == null) {
							dataRowValues.add("NULL");
						} else if (Strings.isNullOrEmpty(fieldResult.toString()) || fieldResult.toString().matches("^(\\s)*$")) {
							dataRowValues.add("NULL");
						} else {
							dataRowValues.add(fieldResult.toString().replaceAll(",", " ").replaceAll("\"", "'").replaceAll("\\n", " "));
						}
					}

					if (!dataRowValues.isEmpty()) {
						sb.append(commaJoiner.join(dataRowValues) + "\n");
					}
				} else {
					sb.append(cubeRow + "\n");
				}
			}
			
			//Remove last line break
			String csvString = StringHelper.replaceLast(sb.toString(), "\n", "");
			
			//return ResponseUtils.buildResponse(csvString, MediaType.TEXT_PLAIN, 200);
			return ResponseUtils.buildResponse(csvString, "text/csv", 200);
		}
		return Response.serverError().status(500).build();
	}

	private Response responseInvalidRequest(String responseFromat) {

		ResponseResult.ResponseInfo responseInfo;
		if (qprops.getResults() != null) {
			responseInfo = qprops.getResults().getResponseResult().getResponseInfo();
		} else {
			responseInfo = new ResponseResult.ResponseInfo(400,
					ResponseStatusMessage.RESPONSE_400.getStatus(),
					ResponseStatusMessage.RESPONSE_400.getMessage(),
					statisticsApiInfo != null && statisticsApiInfo.getValidationResult() != null
						? statisticsApiInfo.getValidationResult().resultMessage()
						: "");
		}

		// JSON
		if (responseFromat.equals(ResponseFormats.PATH_RESPONSE_TYPE_JSON.getDescription())) {
			try {
				ObjectMapper mapper = EWAJsonMapper.get();
				String json = mapper.writeValueAsString(responseInfo);
				sb.append(json);

				ResponseBuilderImpl builder = new ResponseBuilderImpl();
				return builder.type(MediaType.APPLICATION_JSON).status(responseInfo.getCode()).entity(json).build();

			} catch (Exception e) {
				e.printStackTrace();
				return Response.serverError().status(500).build();
			}
		}

		// CSV
		if (responseFromat.equals(ResponseFormats.PATH_RESPONSE_TYPE_CSV.getDescription())) {

			sb.append(ResponseResult.ResponseInfo.getCSVHeaders() + "\n");
			sb.append(
					responseInfo.getCode() + ","
					+ responseInfo.getStatus() + ","
					+ responseInfo.getMessage() + ","
					+ responseInfo.getCause()
			);

			//return ResponseUtils.buildResponse(sb.toString(), MediaType.TEXT_PLAIN, responseInfo.getCode());
			return ResponseUtils.buildResponse(sb.toString(), "text/csv", 200);
		}

		// Used only in case of invalid response format
		String[] errorHeaders = ResponseResult.ResponseInfo.getCSVHeaders().split(",");
		sb.append(errorHeaders[0] + ": " + responseInfo.getCode() + "\n");
		sb.append(errorHeaders[1] + ": " + responseInfo.getStatus() + "\n");
		sb.append(errorHeaders[2] + ": " + responseInfo.getMessage() + "\n");
		sb.append(errorHeaders[3] + ": " + responseInfo.getCause() + "\n");

		return ResponseUtils.buildResponse(sb.toString(), MediaType.TEXT_PLAIN, responseInfo.getCode());
	}

	@SuppressWarnings("unused")
	@Deprecated
	private void preparePrintInformation(StringBuilder sb) {

		sb.append("GET /stats/to/\n");
		sb.append("Response Format '" + qprops.getResponseFormat() + "'\n");
		sb.append("Query Prefix '" + qprops.getQueryPrefix() + "'\n");
		sb.append("Matching Entity Table: '" + qprops.getTableProperties() + "'\n");
		sb.append("HSQL: '" + qprops.getHsqlBuilder().toString() + "'\n");
		sb.append("Data Results: '" + qprops.getResults().getResultData().size() + "'\n");

		List<QueryParameter> parameters = qprops.getParameterList();
		for (QueryParameter parameter : parameters) {

			sb.append("\tParameter: " + parameter.getParameterName());

			ValueProperties properties = parameter.getValueProperties();

			sb.append("\n\t\tValueType: " + properties.getValueType().name());
			sb.append("\n\t\tOperator: " + properties.getValueOperator().name());

			if (properties.getValue() != null) {
				sb.append("\n\t\tString Parameter: " + properties.getValue());
			}

			if (properties.getIntegerValue() != null) {
				sb.append("\n\t\tInt Value: " + properties.getIntegerValue());
			}

			if (properties.getDateValue() != null) {
				sb.append("\n\t\tDate Value: " + properties.getDateValue().toString());
			}

			if (properties.getIntValueList() != null) {
				sb.append("\n\t\tInt List Value: " + properties.getIntValueList());
			}

			if (properties.getStrValueList() != null) {
				sb.append("\n\t\tString List Value: " + properties.getStrValueList());
			}

			if (properties.getDateValueList() != null) {
				sb.append("\n\t\tDate List Value: " + properties.getDateValueList().toString());
			}

			if (properties.getIntValueRangeList() != null) {
				sb.append("\n\t\tInt Range Value: " + properties.getIntValueRangeList());
			}

			if (properties.getDateValueRangeList() != null) {
				sb.append("\n\t\tDate Range Value: " + properties.getDateValueRangeList().toString());
			}

			sb.append("\n");
		}
	}
}
