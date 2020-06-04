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
package europass.ewa.services.editor.jobs;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.util.Date;
import java.util.zip.ZipOutputStream;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CreateZipFromExportedJsonJobTest {

    @Mock
    private File zipFile1;
    @Mock
    private File zipFile2;
    @Mock
    private File jsonFile;
    @Mock
    private File folder;

    @InjectMocks
    private CreateZipFromExportedJsonJob job = new CreateZipFromExportedJsonJob();

    @Before
    public void setUp() {

        when(zipFile1.isFile()).thenReturn(Boolean.TRUE);
        when(zipFile2.isFile()).thenReturn(Boolean.TRUE);
        when(jsonFile.isFile()).thenReturn(Boolean.TRUE);

        when(zipFile1.getName()).thenReturn("Exported-JSON-123123123.zip");
        when(zipFile2.getName()).thenReturn("Exported-JSON-56756756756.zip");
        when(jsonFile.getName()).thenReturn("107e3c8c-36e1-4b72-9542-3e68ad15fa1c-20170723233523.json");
        when(jsonFile.getAbsolutePath()).thenReturn("/tmp/exported-json-documents/107e3c8c-36e1-4b72-9542-3e68ad15fa1c-20170723233523.json");
        when(folder.listFiles()).thenReturn(new File[]{zipFile1, zipFile2, jsonFile});
    }

    @Test
    public void testNoOldZipFilesDeleted() throws Exception {

        final long timeNow = new Date().getTime();

        // 2 days in milliseconds ::
        final long daysInMilliseconds = 2 * 24 * 60 * 60 * 1000;

        when(zipFile1.lastModified()).thenReturn(timeNow - daysInMilliseconds);
        when(zipFile2.lastModified()).thenReturn(timeNow - daysInMilliseconds);

        // set 3 days date limit to delete zips..
        job.cleanupOldZipFiles("3", folder);

        verify(zipFile1, never()).delete();
        verify(zipFile2, never()).delete();
    }

    @Test
    public void testDeleteSomeOfZipFiles() throws Exception {

        final long timeNow = new Date().getTime();

        // 2 days in milliseconds ::
        final long daysInMillisecondsContained = 2 * 24 * 60 * 60 * 1000;
        // 4 days in milliseconds ::
        final long daysInMillisecondsOlder = 4 * 24 * 60 * 60 * 1000;

        when(zipFile1.lastModified()).thenReturn(timeNow - daysInMillisecondsContained);
        when(zipFile2.lastModified()).thenReturn(timeNow - daysInMillisecondsOlder);

        // set 3 days date limit to delete zips..
        job.cleanupOldZipFiles("3", folder);

        verify(zipFile1, never()).delete();
        verify(zipFile2, Mockito.times(1)).delete();
    }

    @Test
    public void testCleanupExtractedOnlyJsonFiles() throws Exception {

        job.cleanupExtractedDocuments(new File[]{jsonFile});

        verify(jsonFile, Mockito.times(1)).delete();
    }

}
