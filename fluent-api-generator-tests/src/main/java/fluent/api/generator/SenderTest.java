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

import fluent.api.generator.impl.FixtureBeanSenderImpl;
import org.mockito.Mock;
import org.testng.annotations.Test;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class SenderTest extends TestBase {

    @Mock
    private FixtureBean fixtureBean;

    @Mock
    private FixtureInterface fixtureInterface;

    @Mock
    private List<FixtureBean> list;

    @Test
    public void testSimpleSender() {
        new FixtureInterfaceAcceptSender(fixtureInterface, fixtureBean).firstName("a").send();
        verify(fixtureInterface).accept(fixtureBean);
        verify(fixtureBean).setFirstName("a");
        verifyNoMoreInteractions(fixtureBean);
    }

    @Test
    public void testFullSender() {
        FixtureBeanSender sender = new FixtureBeanSenderImpl(fixtureInterface, fixtureBean);
        sender.children(list).age(5).send();
        verify(fixtureInterface).accept(fixtureBean);
        verify(fixtureBean).setAge(5);
        verify(fixtureBean).setChildren(list);
        verifyNoMoreInteractions(fixtureBean);
    }
}
