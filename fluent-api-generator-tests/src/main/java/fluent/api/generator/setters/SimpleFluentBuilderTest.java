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

package fluent.api.generator.setters;

import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import fluent.api.generator.setters.simple.GenericPojoFixtureBuilder;

import static fluent.api.generator.setters.PojoFixtureBuilder.pojo;
import static org.testng.Assert.assertEquals;

public class SimpleFluentBuilderTest {

    @Test
    public void testSimplePojoBuilder() {
        LocalDate now = LocalDate.now();
        List<String> list  = new LinkedList<>();
        PojoFixture fixture = pojo().anInt(5).aLong(6L).aString("a").aLocalDate(now).aList(list).build();
        assertEquals(fixture.getAnInt(), 5);
        assertEquals(fixture.getaLong(), Long.valueOf(6L));
        assertEquals(fixture.getaString(), "a");
        assertEquals(fixture.getaLocalDate(), now);
        assertEquals(fixture.getaList(), list);
    }

    @Test
    public void testSimpleStringPojoBuilder() {
        LocalDate now = LocalDate.now();
        List<String> list  = new LinkedList<>();
        GenericPojoFixture<String> fixture = new GenericPojoFixtureBuilder().anInt(5).aLong(6L).aString("a").aLocalDate(now).aList(list).aT("B").build();
        assertEquals(fixture.getAnInt(), 5);
        assertEquals(fixture.getaLong(), Long.valueOf(6L));
        assertEquals(fixture.getaString(), "a");
        assertEquals(fixture.getaLocalDate(), now);
        assertEquals(fixture.getaList(), list);
        assertEquals(fixture.getaT(), "B");
    }

    @Test
    public void testSimpleGenericPojoBuilder() {
        LocalDate now = LocalDate.now();
        List<String> list  = new LinkedList<>();
        GenericPojoFixture<Integer> fixture = new GenericPojoFixtureCreator<Integer>().anInt(5).aLong(6L).aString("a").aLocalDate(now).aList(list).aT(8).create();
        assertEquals(fixture.getAnInt(), 5);
        assertEquals(fixture.getaLong(), Long.valueOf(6L));
        assertEquals(fixture.getaString(), "a");
        assertEquals(fixture.getaLocalDate(), now);
        assertEquals(fixture.getaList(), list);
        assertEquals(fixture.getaT(), Integer.valueOf(8));
    }

}
