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
package europass.ewa.services.files;

import europass.ewa.services.exception.FileExceedsCummulativeLimitException;
import europass.ewa.services.exception.FileExceedsLimitException;
import europass.ewa.services.files.FileRepository.Types;

public interface SizeLimitation {

    /**
     * Checks whether with the upload of the new file the upper limit will be
     * reached.
     *
     * @param fileSize
     * @return boolean
     */
    boolean isWithinSizeLimit(int fileSize, int cumulativeSize);

    /**
     * Controls whether the specific size is below the individual allowed limit
     * and the cumulative allowed limit
     *
     * @param newSize
     * @param type
     * @param cumulativeSize
     * @return
     * @throws FileExceedsLimitException, when individual limit is exceeded
     * @throws FileExceedsCummulativeLimitException, when cumulative limit is
     * exceeded
     */
    boolean isWithinLimits(int newSize, Types type, int cumulativeSize) throws FileExceedsLimitException, FileExceedsCummulativeLimitException;

}
