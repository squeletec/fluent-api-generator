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

import java.time.ZonedDateTime;

public class FixtureClass {

    public static FixtureInterface fixtureInterface;
    public final String first;
    public final String last;
    public final int age;
    public final ZonedDateTime birth;

    @GenerateParametersBuilder
    @GenerateMethodCaller
    public FixtureClass(String first, String last, int age, ZonedDateTime birth) {
        this.first = first;
        this.last = last;
        this.age = age;
        this.birth = birth;
        fixtureInterface.myMethod(first, last, age, birth);
    }

    @GenerateMethodCaller(methodName = "send")
    public static void staticMethod(String first, String last, int age, ZonedDateTime birth) {
        fixtureInterface.myMethod(first, last, age, birth);
    }

    @GenerateParametersBuilder(className = "FixtureBuilder")
    public static ZonedDateTime stringMethod(String first, String last, int age, ZonedDateTime birth) {
        return birth;
    }

}
