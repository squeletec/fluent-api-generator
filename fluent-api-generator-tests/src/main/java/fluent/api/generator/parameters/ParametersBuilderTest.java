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

import fluent.api.generator.*;
import fluent.api.generator.impl.GenericImmutableFixtureBuilderImpl;
import fluent.api.generator.parameters.full.impl.ParametersFixtureInterfaceCreatorImpl;
import fluent.api.generator.parameters.impl.*;
import org.mockito.Mock;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static fluent.api.generator.parameters.ParametersFixtureInterfaceCreator.fixtureInterface;
import static fluent.api.generator.parameters.simple.ParametersFixtureClassCreator.fixtureClass;
import static java.time.LocalDateTime.now;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

public class ParametersBuilderTest extends TestBase {

    @Mock
    private ParametersFixtureInterface parametersFixtureInterface;

    @Mock
    private List<Double> list;

    @Mock
    private GenericFixtureInterface<String> genericFixtureInterface;

    private final ZonedDateTime zonedDateTime = now().atZone(ZoneId.systemDefault());

    @Test
    public void testSimpleInstanceMethodCaller() {
        LocalDateTime time = LocalDateTime.now();
        new ParametersFixtureInterfaceCaller(parametersFixtureInterface).anInt(5).aString("value").aTime(time).aList(list).call();
        verify(parametersFixtureInterface).call(5, "value", time, list);
    }

    @Test
    public void testFullInstanceMethodCaller() {
        LocalDateTime time = LocalDateTime.now();
        ParametersFixtureInterfaceFullCallerImpl caller = new ParametersFixtureInterfaceFullCallerImpl(parametersFixtureInterface);
        caller.anInt(5).aString("value").aTime(time).aList(list).fullCall();
        verify(parametersFixtureInterface).call(5, "value", time, list);
    }

    @Test
    public void testSimpleInstanceMethodCalculator() {
        when(parametersFixtureInterface.calculate(5, "value", null, null)).thenReturn(6);
        ParametersFixtureInterfaceCalculator calculator = new ParametersFixtureInterfaceCalculator(parametersFixtureInterface);
        Assert.assertEquals(calculator.anInt(5).aString("value").calculate(), 6);
    }

    @Test
    public void testFullInstanceMethodCalculator() {
        when(parametersFixtureInterface.calculate(5, "value", null, null)).thenReturn(6);
        ParametersFixtureInterfaceFullCalculator calculator = new ParametersFixtureInterfaceFullCalculatorImpl(parametersFixtureInterface);
        Assert.assertEquals(calculator.anInt(5).aString("value").calculate(), 6);
    }

    @Test
    public void testSimpleInstanceMethodCreator() {
        when(parametersFixtureInterface.create(5, "value", null, null)).thenReturn(zonedDateTime);
        Assert.assertEquals(fixtureInterface(parametersFixtureInterface).anInt(5).aString("value").create(), zonedDateTime);
    }

    @Test
    public void testFullInstanceMethodCreator() {
        when(parametersFixtureInterface.create(5, "value", null, null)).thenReturn(zonedDateTime);
        fluent.api.generator.parameters.full.ParametersFixtureInterfaceCreator calculator = new ParametersFixtureInterfaceCreatorImpl(parametersFixtureInterface);
        Assert.assertEquals(calculator.anInt(5).aString("value").create(), zonedDateTime);
    }

    @Test
    public void testSimpleStaticMethodCaller() {
        ParametersFixtureClass.fixtureInterface = parametersFixtureInterface;
        LocalDateTime time = LocalDateTime.now();
        new ParametersFixtureClassSimpleCaller().anInt(5).aString("value").aTime(time).aList(list).call();
        verify(parametersFixtureInterface).call(5, "value", time, list);
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
    public void testSimpleConstructorBuilder() {
        ParametersFixtureClass.fixtureInterface = parametersFixtureInterface;
        LocalDateTime time = LocalDateTime.now();
        ParametersFixtureClass fixtureClass = new ParametersFixtureClassSimpleBuilder().anInt(5).aString("value").aTime(time).aList(list).simpleBuild();
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
    public void testSimpleStaticMethodCreator() {
        ParametersFixtureClass.fixtureInterface = parametersFixtureInterface;
        LocalDateTime time = LocalDateTime.now();
        assertEquals(fixtureClass().anInt(5).aString("value").aTime(time).aList(list).create(), Integer.valueOf(5));
    }

    @Test
    public void testFullStaticMethodCreator() {
        ParametersFixtureClass.fixtureInterface = parametersFixtureInterface;
        LocalDateTime time = LocalDateTime.now();
        ParametersFixtureClassCreator creator = new ParametersFixtureClassCreatorImpl();
        assertEquals(creator.anInt(5).aString("value").aTime(time).aList(list).create(), Integer.valueOf(5));
    }

    @Test void testGenericParameterBuilder() {
        new GenericFixtureInterfaceInvoker<>(genericFixtureInterface).input("Aha").invoke();
        verify(genericFixtureInterface).myGenericMethod("Aha", null, 0, null, null);
    }
/*
    @Test void testGenericStaticParameterBuilder() {
        GenericFixtureInterfaceStaticGenericMethodBuilder<String> builder = new GenericFixtureInterfaceStaticGenericMethodBuilderImpl<>();
        assertEquals(builder.input("Aha").age(5).build(), "Aha");
    }
*/
    @Test void testGenericConstructorParmateresBuilder() {
        GenericImmutableFixtureBuilder<Integer, String> builder = new GenericImmutableFixtureBuilderImpl<>();
        GenericImmutableFixture<Integer, String> result = builder.t(5).u(Collections.singletonList("Ua")).build();
        assertEquals(result.t, (Integer) 5);
        assertEquals(result.u, Collections.singletonList("Ua"));
    }

}
