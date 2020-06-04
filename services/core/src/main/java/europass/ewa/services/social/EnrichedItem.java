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
package europass.ewa.services.social;

import java.util.Locale;

import org.springframework.social.linkedin.api.LinkedInProfile;
import org.springframework.social.linkedin.api.Recommendation;
import org.springframework.social.linkedin.api.Recommendation.RecommendationType;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

import europass.ewa.model.JDate;

public class EnrichedItem<A> {

    private A item;
    private JDate startDate;
    private JDate endDate;

    private Recommendation recommendationItem = null;

    private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

    public EnrichedItem(A item) {
        this.item = item;
    }

    private static final String DATE_FORMAT = "numeric/short";

    public void setStartDate(JDate startDate) {
        this.startDate = startDate;
    }

    public JDate getStartDate() {
        return startDate;
    }

    public void setEndDate(JDate endDate) {
        this.endDate = endDate;
    }

    public JDate getEndDate() {
        return endDate;
    }

    public String formatStartDate() {
        String formatted = startDate.format(DATE_FORMAT, DEFAULT_LOCALE);
        return formatted;
    }

    public String formatEndDate() {
        String formatted = endDate.format(DATE_FORMAT, DEFAULT_LOCALE);
        return formatted;
    }

    public A getItem() {
        return item;
    }

    public void setItem(A item) {
        this.item = item;
    }

    /**
     * ** RECOMMENDATION ***
     */
    public String formatRecommenderName() {
        if (!(item instanceof Recommendation)) {
            return null;
        }
        if (recommendationItem == null) {
            recommendationItem = (Recommendation) item;
        }
        LinkedInProfile recommender = recommendationItem.getRecommender();
        if (recommender == null) {
            return null;
        }
        String[] names = {recommender.getFirstName(), recommender.getLastName()};
        return Joiner.on(" ").join(names);
    }

    public String formatRecommenderType() {
        if (!(item instanceof Recommendation)) {
            return null;
        }
        if (recommendationItem == null) {
            recommendationItem = (Recommendation) item;
        }
        RecommendationType type = recommendationItem.getRecommendationType();
        if (type == null) {
            return null;
        }
        String name = type.name();
        if (Strings.isNullOrEmpty(name)) {
            return null;
        }
        if ("SERVICE_PROVIDER".equals(name)) {
            return null;
        }
        //TODO: fetch from Taxonomy
        return name;
    }

    public String formatRecommenderText() {
        if (!(item instanceof Recommendation)) {
            return null;
        }
        if (recommendationItem == null) {
            recommendationItem = (Recommendation) item;
        }
        return recommendationItem.getRecommendationText();
    }
}
