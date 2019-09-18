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

import fluent.api.FluentBuilder;
import fluent.api.generator.validation.FluentCheck;
import fluent.dsl.Dsl;
import fluent.dsl.Keyword;
import fluent.dsl.Prefix;
import fluent.dsl.Suffix;
import fluent.validation.Assert;
import fluent.validation.Check;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import static fluent.validation.CollectionChecks.blockingQueue;
import static fluent.validation.CollectionChecks.equalTo;
import static java.util.Collections.singletonList;

@Dsl(className = "User", factoryMethod = "newUser")
public class Automation {

    public enum Side {BUY, SELL}

    @Prefix public @interface enters {}
    @Prefix public @interface and {}
    @Prefix public @interface at {}
    @Prefix public @interface mustSee {}
    @Suffix public @interface inDatabase {}
    @Suffix public @interface in {}
    @Suffix public @interface database {}
    @Suffix public @interface only {}
    @Keyword public @interface must {}
    @Keyword public @interface see {}
    @Keyword public @interface with {}
    @Keyword public @interface order {}
    @Target(ElementType.TYPE_USE)
    public @interface entity {}

    private final BlockingQueue<Order> orders = new LinkedBlockingDeque<>();

    public static void userLogin(@Automation.enters String username, @and String password, @at String url) {
    }

    @in @database
    public static void verification(@mustSee String message) {
    }

    public String injectOrder(@enters @FluentBuilder @FluentCheck(factoryMethod = "with") Order order, @at String topic) {
        orders.add(order);
        return "A";
    }

    @only
    public void exactOrderVerification(@must @see @order @with String orderId, @and Check<Order> criteria) {
        Assert.that(orders, blockingQueue(equalTo(singletonList(criteria)), Duration.ofSeconds(1)));
    }

    //public void databaseVerification(@must @see @in @database String message) {
    //}

}
