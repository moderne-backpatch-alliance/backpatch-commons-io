/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.io.input;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.output.XmlStreamWriter;
import org.junit.Test;

public class XmlStreamReaderEncodingPatternTest {

    private static boolean found(final String prolog) {
        return XmlStreamReader.ENCODING_PATTERN.matcher(prolog).find();
    }

    @Test
    public void patternIsAnchoredAtLineStart() {
        assertFalse(found("x <?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
    }

    @Test
    public void patternDoesNotSkipContentBeforeEncoding() {
        assertFalse(found("<?xml version=\"1.0\" other encoding=\"UTF-8\"?>"));
    }

    @Test
    public void patternAcceptsOnlyEncodingNameCharacters() {
        assertFalse(found("<?xml version=\"1.0\" encoding=\"UTF 8\"?>"));
    }

    @Test
    public void readerIgnoresEncodingAfterOtherContent() throws IOException {
        final byte[] xml = "<?xml version=\"1.0\" other encoding=\"ISO-8859-1\"?><a/>"
                .getBytes(StandardCharsets.UTF_8);
        try (final XmlStreamReader reader = new XmlStreamReader(new ByteArrayInputStream(xml))) {
            assertEquals("UTF-8", reader.getEncoding());
        }
    }

    @Test
    public void writerIgnoresEncodingAfterOtherContent() throws IOException {
        try (final XmlStreamWriter writer = new XmlStreamWriter(new ByteArrayOutputStream(), "UTF-8")) {
            writer.write("<?xml version=\"1.0\" other encoding=\"ISO-8859-1\"?><a/>");
            writer.flush();
            assertEquals("UTF-8", writer.getEncoding());
        }
    }
}
