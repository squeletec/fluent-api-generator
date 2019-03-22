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

import fluent.api.FluentParameters;
import fluent.api.FluentParametersApi;

import java.time.LocalDateTime;
import java.util.List;

public class ParametersFixtureClass {

    public static ParametersFixtureInterface fixtureInterface;

    private final int anInt;
    private final String aString;
    private final LocalDateTime aTime;
    private final List<Double> aList;

    @FluentParameters(className = "ParametersFixtureClassSimpleCaller")
    @FluentParametersApi
    public ParametersFixtureClass(int anInt, String aString, LocalDateTime aTime, List<Double> aList) {
        this.anInt = anInt;
        this.aString = aString;
        this.aTime = aTime;
        this.aList = aList;
        fixtureInterface.call(anInt, aString, aTime, aList);
    }

    @FluentParameters(factoryMethod = "createObjectWith")
    @FluentParametersApi(methodName = "andSend")
    public static void call(int anInt, String aString, LocalDateTime aTime, List<Double> aList) {
        fixtureInterface.call(anInt, aString, aTime, aList);
    }

    @FluentParameters(packageName = "fluent.api.generator.parameters.simple", factoryMethod = "fixtureClass")
    @FluentParametersApi
    public static Integer create(int anInt, String aString, LocalDateTime aTime, List<Double> aList) {
        return anInt;
    }

}
