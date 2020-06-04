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
package europass.ewa.model.wrapper;

import europass.ewa.model.FileData;

/**
 * Wrapper class for grouping FileData with Feedback information in case a non
 * blocking exception occurs String errCode exists to trace multiple log entries
 * from suppressed exceptions
 *
 */
public class FiledataWrapper {

    String errCode;

    FileData FileData;

    Feedback Feedback;

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public FileData getFiledata() {
        return FileData;
    }

    public void setFiledata(FileData filedata) {
        this.FileData = filedata;
    }

    public Feedback getFeedback() {
        return Feedback;
    }

    public void setFeedback(Feedback feedback) {
        this.Feedback = feedback;
    }

}
