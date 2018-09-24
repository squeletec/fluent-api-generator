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

import fluent.api.generator.impl.FixtureBeanFullBuilderImpl;
import org.mockito.Mock;
import org.testng.annotations.Test;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class SettersBuilderTest extends TestBase {

    @Mock
    private FixtureBean fixtureBean;

    @Mock
    private List<FixtureBean> list;

    @Mock
    private FixtureInterface fixtureInterface;

    @Mock
    private GenericFixture<String> genericFixture;

    @Test
    public void testSimpleBuilder() {
        FixtureBean fixtureBean = new FixtureBeanBuilder(this.fixtureBean)
                .firstName("a")
                .lastName("b")
                .children(list)
                .build();
        verify(fixtureBean).setFirstName("a");
        verify(fixtureBean).setLastName("b");
        verify(fixtureBean).setChildren(list);
        verifyNoMoreInteractions(fixtureBean);
    }

    @Test
    public void testFullBuilder() {
        int[] array = new int[] {};
        FixtureBeanFullBuilder builder = new FixtureBeanFullBuilderImpl(this.fixtureBean);
        FixtureBean fixtureBean = builder.age(4).array(array).build();
        verify(fixtureBean).setAge(4);
        verify(fixtureBean).setArray(array);
        verifyNoMoreInteractions(fixtureBean);
    }

    @Test
    public void testGenericSender() {
        new GenericFixtureGenericSender<>(fixtureInterface, genericFixture).value("").genericSend();
        verify(genericFixture).setValue("");
        verify(fixtureInterface).otherGeneric(genericFixture);
    }
}
