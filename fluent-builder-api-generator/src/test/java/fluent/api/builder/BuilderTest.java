/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2019, Ondrej Fischer
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package fluent.api.builder;

import fluent.api.fixture.Fixture;
import fluent.api.fixture.FixtureGeter;
import fluent.api.fixture.FluentFixture;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static fluent.api.fixture.FixtureBuilder.fixtureWith;
import static java.time.LocalDateTime.now;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class BuilderTest {

    private final LocalDateTime localDateTime = now();
    private final int[] array = {1, 3, 4};

    @Test
    public void testAnnotatedFixture() {

        AnnotatedFixture fixture = new AnnotatedFixtureBuilder(mock(AnnotatedFixture.class))
                .intValue(10)
                .listValue(asList("A", "B"))
                .stringValue("V")
                .localDateTime(localDateTime)
                .arrayValue(array)
                .build();

        verify(fixture).setIntValue(10);
        verify(fixture).setStringValue("V");
        verify(fixture).setLocalDateTime(localDateTime);
        verify(fixture).setListValue(asList("A", "B"));
        verify(fixture).setArrayValue(array);
    }

    @Test
    public void testClassName() {
        Fixture fixture = new FluentFixture(mock(Fixture.class))
                .intValue(10)
                .listValue(asList("A", "B"))
                .stringValue("V")
                .localDateTime(localDateTime)
                .arrayValue(array)
                .build();

        verify(fixture).setIntValue(10);
        verify(fixture).setStringValue("V");
        verify(fixture).setLocalDateTime(localDateTime);
        verify(fixture).setListValue(asList("A", "B"));
        verify(fixture).setArrayValue(array);
    }

    @Test
    public void testMethodName() {
        Fixture fixture = new FixtureGeter(mock(Fixture.class))
                .intValue(10)
                .listValue(asList("A", "B"))
                .stringValue("V")
                .localDateTime(localDateTime)
                .arrayValue(array)
                .get();

        verify(fixture).setIntValue(10);
        verify(fixture).setStringValue("V");
        verify(fixture).setLocalDateTime(localDateTime);
        verify(fixture).setListValue(asList("A", "B"));
        verify(fixture).setArrayValue(array);

    }

    @Test
    public void testPackageName() {
        Fixture fixture = new fluent.api.builder.generated.FixtureBuilder(mock(Fixture.class))
                .intValue(10)
                .listValue(asList("A", "B"))
                .stringValue("V")
                .localDateTime(localDateTime)
                .arrayValue(array)
                .build();

        verify(fixture).setIntValue(10);
        verify(fixture).setStringValue("V");
        verify(fixture).setLocalDateTime(localDateTime);
        verify(fixture).setListValue(asList("A", "B"));
        verify(fixture).setArrayValue(array);

    }

    @Test
    public void testFactoryMethod() {
        Fixture fixture = fixtureWith(mock(Fixture.class))
                .intValue(10)
                .listValue(asList("A", "B"))
                .stringValue("V")
                .localDateTime(localDateTime)
                .arrayValue(array)
                .build();

        verify(fixture).setIntValue(10);
        verify(fixture).setStringValue("V");
        verify(fixture).setLocalDateTime(localDateTime);
        verify(fixture).setListValue(asList("A", "B"));
        verify(fixture).setArrayValue(array);

    }

}
