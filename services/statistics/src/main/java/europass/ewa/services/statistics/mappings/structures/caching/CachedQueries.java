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
package europass.ewa.services.statistics.mappings.structures.caching;

import java.util.HashMap;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.DateTime;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import europass.ewa.services.statistics.structures.QueryResults;

@Singleton
public class CachedQueries {

	private final int CACHED_QUERIES_NUMBER;
	private final int CACHED_QUERIES_LIFE_MILLIS;
	private HashMap<String, CachedQuery> cachedQueriesMap;

	@Inject
	public CachedQueries(
		@Named("europass-ewa-services-statistics.cached.queries.number") String cachedQueriesSize,
		@Named("europass-ewa-services-statistics.cached.queries.life") String cachedQueriesLifeMillis
	) {
		cachedQueriesMap = new HashMap<String, CachedQuery>();

		CACHED_QUERIES_NUMBER = Integer.parseInt(cachedQueriesSize);
		CACHED_QUERIES_LIFE_MILLIS = Integer.parseInt(cachedQueriesLifeMillis);
	}

	public void cacheQuery(String query, QueryResults results) {

		boolean isAlreadyCached = isAlreadyCached(query);
		if (!isAlreadyCached) {
			cachedQueriesMap.put(makeMD5CacheKey(query), new CachedQuery(results));
		}
	}

	public QueryResults getCachedQueryResults(String query) {

		boolean isAlreadyCached = isAlreadyCached(query);
		if (isAlreadyCached) {
			CachedQuery cached = cachedQueriesMap.get(makeMD5CacheKey(query));
			return cached.getResults();
		}

		return null;
	}

	public CachedQuery getCachedQuery(String query) {

		return cachedQueriesMap.get(makeMD5CacheKey(query));
	}

	public boolean isAlreadyCached(String query) {

		String hashedKey = makeMD5CacheKey(query);
		return cachedQueriesMap.containsKey(hashedKey);
	}

	public void cleanUp() {

		/**
		 * Clean Up the cached queries map and remove once the maximum cached queries size reached 
		 *
		 * Evaluate the points of the cached queries and decide which are going to be cleared
		 * The more points they have the more likely to be cleared
		 *
		 * The approach is to calculate "weights" based on two factors:
		 * 1. The creation date (the factor will have extra points once it is closer to the time expiration threshold)
		 * 2. The times queried (the factor will have extra points once it has fewer
		 */
		//check against size to see which 
		if (cachedQueriesMap.size() == CACHED_QUERIES_NUMBER) {

			Set<String> cachedItemsKeys = cachedQueriesMap.keySet();
			Object[] cachedItemsKeysArray = cachedItemsKeys.toArray();

			Object keyToRemove = null;
			double currentHighestGrade = 0.0;

			for (Object itemKey : cachedItemsKeysArray) {

				CachedQuery cachedItem = cachedQueriesMap.get((String) itemKey);
				double itemGrade = calculateRemovalGrade(cachedItem);

				if (itemGrade < 0.0) {
					cachedQueriesMap.remove(itemKey);
					break;
				} else {
					if (currentHighestGrade < itemGrade) {
						currentHighestGrade = itemGrade;
						keyToRemove = itemKey;
					}
				}
			}

			if (keyToRemove != null) {
				cachedQueriesMap.remove(keyToRemove);
			}
		}
	}

	private double calculateRemovalGrade(CachedQuery item) {

		double totalGrade = 0;

		double expirationGrade = 0;

		/**
		 * Part 1: calculation of the expiration date grade
		 */
		// calculate the date of expiration
		DateTime expirationDate = new DateTime().minus(CACHED_QUERIES_LIFE_MILLIS);
		long lifeDifference = item.getCreated().getMillis() - expirationDate.getMillis();

		if (lifeDifference <= 0) { // lifeDiffernce less or equal to 0, means expiration of the cached query
			return -1.0;
		} else {

			// Calculate factor depending on the CACHED_QUERIES_LIFE_MILLIS
			// (the biggest CACHED_QUERIES_LIFE_MILLIS configured, the more likely the query needs to be decached due to newst results)
			int expirationFactor;
			if (CACHED_QUERIES_LIFE_MILLIS / (60 * 1000) <= 1) {	// less than minute
				expirationFactor = 100;
			} else if (CACHED_QUERIES_LIFE_MILLIS / (60 * 60 * 1000) <= 1) { // less than hour
				expirationFactor = 200;
			} else if (CACHED_QUERIES_LIFE_MILLIS / (24 * 60 * 60 * 1000) <= 1) { // less than day
				expirationFactor = 400;
			} else if (CACHED_QUERIES_LIFE_MILLIS / (7 * 24 * 60 * 60 * 1000) <= 1) { // less than week
				expirationFactor = 800;
			} else {
				expirationFactor = 16000;
			}

			expirationGrade = ((double) lifeDifference / (double) CACHED_QUERIES_LIFE_MILLIS) * (double) expirationFactor;
		}

		/**
		 * Part 2: calculation of the times queried grade
		 */
		int timesQueried = item.getTimesQueried();

		int timesQueriedFactor;
		if (timesQueried <= 10) {
			timesQueriedFactor = 20;
		} else if (timesQueried <= 20) {
			timesQueriedFactor = 40;
		} else if (timesQueried <= 40) {
			timesQueriedFactor = 80;
		} else if (timesQueried <= 80) {
			timesQueriedFactor = 160;
		} else if (timesQueried <= 160) {
			timesQueriedFactor = 320;
		} else if (timesQueried <= 320) {
			timesQueriedFactor = 640;
		} else if (timesQueried <= 640) {
			timesQueriedFactor = 1280;
		} else {
			timesQueriedFactor = 3000;
		}

		// total grade is the expiration grade divided by the times queried factor 
		//(so queries that are used mostly are more unlikely to be removed
		totalGrade = (double) expirationGrade / (double) timesQueriedFactor;

		return totalGrade;
	}

	private static String makeMD5CacheKey(String query) {

		String hash = DigestUtils.md5Hex(query);
		return hash == null ? query : hash;
	}

}
