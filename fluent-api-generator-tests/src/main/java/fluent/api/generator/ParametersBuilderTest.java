/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2018, Ondrej Fischer
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

package fluent.api.generator;

import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static java.time.LocalDateTime.now;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;

public class ParametersBuilderTest {

    @Mock
    private FixtureInterface fixtureInterface;

    private final ZonedDateTime birth = now().atZone(ZoneId.systemDefault());

    @BeforeMethod
    public void mocks() {
        initMocks(this);
    }

    @Test
    public void testInstanceMethodBuilder() {
        when(fixtureInterface.createName("a", "b", 5)).thenReturn(1);
        assertEquals(new IntegerSender(fixtureInterface).prefix("a").suffix("b").padding(5).send(), Integer.valueOf(1));
        verify(fixtureInterface).createName("a", "b", 5);
    }

    @Test
    public void testStaticMethodCaller() {
        FixtureClass.fixtureInterface = fixtureInterface;
        assertEquals(new FixtureBuilder().first("a").last("b").age(5).birth(birth).build(), birth);
    }

    @Test
    public void testConstructorBuilder() {
        FixtureClass.fixtureInterface = fixtureInterface;
        FixtureClass fixtureClass = new FixtureClassBuilder().first("a").last("b").age(5).birth(birth).build();
        verify(fixtureInterface).myMethod("a", "b", 5, birth);
        assertEquals(fixtureClass.first, "a");
        assertEquals(fixtureClass.last, "b");
        assertEquals(fixtureClass.age, 5);
        assertEquals(fixtureClass.birth, birth);
    }

}
