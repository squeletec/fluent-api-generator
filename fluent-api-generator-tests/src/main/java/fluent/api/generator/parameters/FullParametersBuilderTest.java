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

package fluent.api.generator.parameters;

import fluent.api.generator.TestBase;
import fluent.api.generator.parameters.full.impl.ParametersFixtureInterfaceCreatorImpl;
import fluent.api.generator.parameters.impl.*;
import org.mockito.Mock;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static java.time.LocalDateTime.now;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

public class FullParametersBuilderTest extends TestBase {

    @Mock
    private ParametersFixtureInterface parametersFixtureInterface;

    @Mock
    private List<Double> list;

    private final ZonedDateTime zonedDateTime = now().atZone(ZoneId.systemDefault());

    @Test
    public void testFullInstanceMethodCaller() {
        LocalDateTime time = LocalDateTime.now();
        ParametersFixtureInterfaceFullCallerImpl caller = new ParametersFixtureInterfaceFullCallerImpl(parametersFixtureInterface);
        caller.anInt(5).aString("value").aTime(time).aList(list).fullCall();
        verify(parametersFixtureInterface).call(5, "value", time, list);
    }

    @Test
    public void testFullInstanceMethodCalculator() {
        when(parametersFixtureInterface.calculate(5, "value", null, null)).thenReturn(6);
        ParametersFixtureInterfaceFullCalculator calculator = new ParametersFixtureInterfaceFullCalculatorImpl(parametersFixtureInterface);
        Assert.assertEquals(calculator.anInt(5).aString("value").calculate(), 6);
    }

    @Test
    public void testFullInstanceMethodCreator() {
        when(parametersFixtureInterface.create(5, "value", null, null)).thenReturn(zonedDateTime);
        fluent.api.generator.parameters.full.ParametersFixtureInterfaceCreator calculator = new ParametersFixtureInterfaceCreatorImpl(parametersFixtureInterface);
        Assert.assertEquals(calculator.anInt(5).aString("value").create(), zonedDateTime);
    }

    @Test
    public void testFullStaticMethodCaller() {
        ParametersFixtureClass.fixtureInterface = parametersFixtureInterface;
        LocalDateTime time = LocalDateTime.now();
        ParametersFixtureClassCaller caller = new ParametersFixtureClassCallerImpl();
        caller.anInt(5).aString("value").aTime(time).aList(list).call();
        verify(parametersFixtureInterface).call(5, "value", time, list);
    }

    @Test
    public void testFullConstructorBuilder() {
        ParametersFixtureClass.fixtureInterface = parametersFixtureInterface;
        LocalDateTime time = LocalDateTime.now();
        ParametersFixtureClassBuilder builder = new ParametersFixtureClassBuilderImpl();
        ParametersFixtureClass fixtureClass = builder.anInt(5).aString("value").aTime(time).aList(list).build();
        verify(parametersFixtureInterface).call(5, "value", time, list);
    }

    @Test
    public void testFullStaticMethodCreator() {
        ParametersFixtureClass.fixtureInterface = parametersFixtureInterface;
        LocalDateTime time = LocalDateTime.now();
        ParametersFixtureClassCreator creator = new ParametersFixtureClassCreatorImpl();
        assertEquals(creator.anInt(5).aString("value").aTime(time).aList(list).create(), Integer.valueOf(5));
    }

}
