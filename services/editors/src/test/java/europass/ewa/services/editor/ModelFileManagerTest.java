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
package europass.ewa.services.editor;

import europass.ewa.enums.ContentTypes;
import europass.ewa.model.*;
import europass.ewa.model.wrapper.Feedback;
import europass.ewa.model.wrapper.Feedback.Code;
import europass.ewa.model.wrapper.Feedback.Level;
import europass.ewa.services.editor.files.SessionDiskFileManager;
import europass.ewa.services.files.FileRepository;
import europass.ewa.services.files.ImageType;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ModelFileManagerTest {

    private static SessionDiskFileManager manager;

    private static final String URI_PREFIX = "http://ewa/";

    private static final String ATT_EXISTS = "ATT111";

    private static final String ATT_NOT_EXISTS = "ATT222";

    private static final String ATT_FORBIDDEN = "ATT333";

    private static final byte[] photodataBytes = "photodata".getBytes();

    private static FileData photoData = new FileData("photo", ImageType.JPG.getBasicMimeType(), "http://ewa/123");

    @BeforeClass
    public static void prepare() {

        FileRepository mockRepo = mock(FileRepository.class);
        when(mockRepo.readPhotoData(photoData)).thenAnswer(new Answer<FileData>() {
            @Override
            public FileData answer(InvocationOnMock invocation) throws Throwable {
                //Answer that modifies the supplied object
                Object[] args = invocation.getArguments();
                FileData photo = (FileData) args[0];
                photo.setData(photodataBytes);
                return photo;
            }
        });
        when(mockRepo.readFile(ATT_EXISTS)).thenAnswer(new Answer<FileData>() {
            @Override
            public FileData answer(InvocationOnMock invocation) throws Throwable {
                //Answer that modifies the supplied object
                FileData file = new FileData();
                file.setMimeType(ContentTypes.PDF_CT.toString());
                file.setData(ATT_EXISTS.getBytes());
                return file;
            }
        });
        when(mockRepo.readFile(ATT_NOT_EXISTS)).thenAnswer(new Answer<FileData>() {
            @Override
            public FileData answer(InvocationOnMock invocation) {
                return null;
            }
        });
        when(mockRepo.readFile(ATT_FORBIDDEN)).thenAnswer(new Answer<FileData>() {
            @Override
            public FileData answer(InvocationOnMock invocation) throws Throwable {
                return null;
            }
        });

        manager = new SessionDiskFileManager(mockRepo);
    }

    @Test
    public void addBinaryPhoto() {

        assertNotNull(manager);

        SkillsPassport esp = new SkillsPassport();
        LearnerInfo l = new LearnerInfo();
        Identification i = new Identification();
        i.setPhoto(photoData);
        l.setIdentification(i);
        esp.setLearnerInfo(l);

        FileData photoBefore = esp.getModel().getLearnerInfo().getIdentification().getPhoto();
        assertThat("Photo uri", photoBefore.getTmpuri().toString(), is("http://ewa/123"));

        List<Feedback> feedback = manager.augmentWithPhotoData(esp);

        assertThat("Feedback Size", feedback.size(), is(1));
        assertThat("Feedback OK", feedback.get(0).getLevel(), is(Level.INFO));

        FileData photoAfter = esp.getModel().getLearnerInfo().getIdentification().getPhoto();

        assertThat("Photo name", photoAfter.getName(), is("photo"));
        assertNull("Photo uri", photoAfter.getTmpuri());

        byte[] data = photoAfter.getData();

        assertNotNull("Photo data not null", data);

        assertThat("Photo data", data, is(photodataBytes));

    }

    @Test
    public void addAtt1() throws URISyntaxException {
        SkillsPassport esp = new SkillsPassport();
        Attachment at = new Attachment();
        at.setTmpuri(new URI(URI_PREFIX + ATT_EXISTS));
        List<Attachment> ats = new ArrayList<>();
        ats.add(at);
        esp.setAttachmentList(ats);

        List<Feedback> feedback = manager.augmentWithData(esp);
        assertThat("Feedback Size", feedback.size(), is(1));
        assertThat("Feedback OK", feedback.get(0).getLevel(), is(Level.INFO));

        Attachment attAfter = esp.getAttachmentList().get(0);
        assertNull("Att uri", attAfter.getTmpuri());
        assertThat("Att data", attAfter.getData(), is(ATT_EXISTS.getBytes()));
    }

    @Test
    public void addAtt2() throws URISyntaxException {
        SkillsPassport esp = new SkillsPassport();
        Attachment at = new Attachment();
        at.setName(ATT_NOT_EXISTS);
        at.setTmpuri(new URI(URI_PREFIX + ATT_NOT_EXISTS));
        List<Attachment> ats = new ArrayList<>();
        ats.add(at);
        esp.setAttachmentList(ats);

        List<Feedback> feedback = manager.augmentWithData(esp);
        assertThat("Feedback Size", feedback.size(), is(1));
        assertThat("Feedback Warn", feedback.get(0).getLevel(), is(Level.WARN));
        assertThat("Feedback Code", feedback.get(0).getCode(), is(Code.DOWNLOAD_ATTACHMENT));
        assertThat("Feedback Section", feedback.get(0).getSection().getValue(), is(ATT_NOT_EXISTS));
    }

    @Test
    public void addAtt3() throws URISyntaxException {
        SkillsPassport esp = new SkillsPassport();
        Attachment at = new Attachment();
        at.setName(ATT_FORBIDDEN);
        at.setTmpuri(new URI(URI_PREFIX + ATT_FORBIDDEN));
        List<Attachment> ats = new ArrayList<>();
        ats.add(at);
        esp.setAttachmentList(ats);

        List<Feedback> feedback = manager.augmentWithData(esp);
        assertThat("Feedback Size", feedback.size(), is(1));
        assertThat("Feedback Warn", feedback.get(0).getLevel(), is(Level.WARN));
        assertThat("Feedback Code", feedback.get(0).getCode(), is(Code.DOWNLOAD_ATTACHMENT));
        assertThat("Feedback Section", feedback.get(0).getSection().getValue(), is(ATT_FORBIDDEN));
    }
}
