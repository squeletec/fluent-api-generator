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

import fluent.api.dsl.impl.FixtureBeanSenderImpl;
import fluent.api.generator.impl.FixtureSenderImpl;
import org.mockito.Mock;
import org.testng.annotations.Test;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.testng.Assert.assertEquals;

public class SenderTest extends TestBase {

    @Mock
    private FixtureBean fixtureBean;

    @Mock
    private FixtureInterface fixtureInterface;

    @Mock
    private List<FixtureBean> list;

    @Mock
    private GenericFixture<String> genericFixture;

    @Test
    public void testSimpleSender() {
        new FixtureBeanAccepter(fixtureInterface, fixtureBean).firstName("a").accept();
        verify(fixtureInterface).accept(fixtureBean);
        verify(fixtureBean).setFirstName("a");
        verifyNoMoreInteractions(fixtureBean);
    }

    @Test
    public void testFullSender() {
        FixtureSender sender = new FixtureSenderImpl(fixtureInterface, fixtureBean);
        sender.children(list).age(5).send();
        verify(fixtureInterface).accept(fixtureBean);
        verify(fixtureBean).setAge(5);
        verify(fixtureBean).setChildren(list);
        verifyNoMoreInteractions(fixtureBean);
    }

    @Test
    public void testConstructorSender() {
        FixtureClass.fixtureInterface = fixtureInterface;
        FixtureClass fixtureClass = new FixtureBeanSender(fixtureBean).lastName("a").send();
        verify(fixtureBean).setLastName("a");
        verify(fixtureBean).getFirstName();
        verify(fixtureBean).getLastName();
        verify(fixtureBean).getAge();
        verifyNoMoreInteractions(fixtureBean);
    }

    @Test
    public void testStaticMethodSender() {
        fluent.api.dsl.FixtureBeanSender sender = new FixtureBeanSenderImpl("1", fixtureBean);
        assertEquals(sender.age(5).send(), "1");
        verify(fixtureBean).setAge(5);
        verifyNoMoreInteractions(fixtureBean);
    }


    @Test
    public void testGenericSender() {
        new GenericFixtureGenericSender<>(fixtureInterface, genericFixture).value("").genericSend();
        verify(genericFixture).setValue("");
        verify(fixtureInterface).otherGeneric(genericFixture);
    }

}
