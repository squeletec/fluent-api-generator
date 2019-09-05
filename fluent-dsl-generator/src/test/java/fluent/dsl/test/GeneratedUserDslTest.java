/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2019, Ondrej Fischer
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

package fluent.dsl.test;

import org.testng.annotations.Test;

import static fluent.dsl.Bdd.When;
import static fluent.dsl.Bdd.then;
import static fluent.dsl.test.User.newUser;

public class GeneratedUserDslTest {

    private final User John = newUser(new Automation());

    private final String validUserName = "John Doe";
    private final String validPassword = "$3cr3T";
    private final String invalidPassword = "password";
    private final String loginPage = "http://my.server.com/login";

    @Test
    public void successfulLoginScreenTest() {
        When (John). entersUsername (validUserName). andPassword (validPassword). atUrl (loginPage);
        then (John). mustSeeMessage ("Welcome My Name!");
    }

    @Test
    public void unsuccessfulLoginScreenTest() {
        When (John). entersUsername (validUserName). andPassword (invalidPassword). atUrl (loginPage);
        then (John). mustSeeMessage ("Invalid username or password!");
    }

    @Test
    public void testDirectDsl() {
        John.entersUsername(validUserName).andPassword(validPassword).atUrl(loginPage);
        John.mustSeeMessage("Welcome " + validUserName + "!");
    }

}
