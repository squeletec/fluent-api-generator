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
import fluent.api.generator.parameters.impl.ParametersFixtureInterfaceFullCalculatorImpl;
import org.mockito.Mock;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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

    private final ZonedDateTime birth = now().atZone(ZoneId.systemDefault());

    @Test
    public void testInstanceMethodCaller() {
        LocalDateTime time = LocalDateTime.now();
        List<Double> list = new LinkedList<>();
        new ParametersFixtureInterfaceCaller(parametersFixtureInterface).anInt(5).aString("value").aTime(time).aList(list).call();
        verify(parametersFixtureInterface).call(5, "value", time, list);
    }

    @Test
    public void testInstanceMethodCalculator() {
        when(parametersFixtureInterface.calculate(5, "value", null, null)).thenReturn(6);
        ParametersFixtureInterfaceFullCalculator calculator = new ParametersFixtureInterfaceFullCalculatorImpl(parametersFixtureInterface);
        Assert.assertEquals(calculator.anInt(5).aString("value").calculate(), 6);
    }
/*
    @Test
    public void testStaticMethodBuilder() {
        FixtureClass.fixtureInterface = parametersFixtureInterface;
        assertEquals(new FactoryFixtureCaller(parametersFixtureInterface).first("a").last("b").age(5).birth(birth).build(), birth);
    }

    @Test
    public void testConstructorBuilder() {
        FixtureClass.fixtureInterface = parametersFixtureInterface;
        FixtureClass fixtureClass = new FixtureClassBuilder().first("a").last("b").age(5).birth(birth).list(list).build();
        verify(parametersFixtureInterface).myMethod("a", "b", 5, birth, list);
        assertEquals(fixtureClass.first, "a");
        assertEquals(fixtureClass.last, "b");
        assertEquals(fixtureClass.age, 5);
        assertEquals(fixtureClass.birth, birth);
    }


    @Test
    public void testStaticMethodCaller() {
        FixtureClass.fixtureInterface = parametersFixtureInterface;
        new FixtureClassStaticMethodSender().first("a").last("b").age(5).birth(birth).list(list).send();
        verify(parametersFixtureInterface).myMethod("a", "b", 5, birth, list);
    }

    @Test void testFullParametersBuilder() {
        when(parametersFixtureInterface.create("a", "b", 5)).thenReturn("c");
        FixtureInterfaceCreateBuilder builder = new FixtureInterfaceCreateBuilderImpl(parametersFixtureInterface);
        assertEquals(builder.age(5).first("a").last("b").build(), "c");
        verify(parametersFixtureInterface).create("a", "b", 5);
    }

    @Test void testGenericParameterBuilder() {
        new GenericFixtureInterfaceInvoker<>(genericFixtureInterface).input("Aha").invoke();
        verify(genericFixtureInterface).myGenericMethod("Aha", null, 0, null, null);
    }

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
