/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.vertx.ext.web.annotations.impl;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;

import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.annotations.*;

/**
 * @author <a href="mailto:pmlopes@gmail.com">Paulo Lopes</a>
 */
public class RouterProcessorHandler extends AbstractAnnotationHandler<Router> {

    public RouterProcessorHandler() {
        super(Router.class);
    }

    @Override
    public void process(final Router router, final Object instance, final Class<?> clazz, final Method method) {
        if (isValidMethod(method)) {
            // create a base route
            Route route = router.route();

            // route handling annotations
            if (Processor.isCompatible(method, Handler.class, RoutingContext.class)) {
                MethodHandle methodHandle = Processor.getMethodHandle(method, RoutingContext.class);

                if (Processor.getAnnotation(method, Handler.class).blocking()) {
                    route.blockingHandler(wrap(instance, methodHandle));
                } else {
                    route.handler(wrap(instance, methodHandle));
                }
            }
            if (Processor.isCompatible(method, FailureHandler.class, RoutingContext.class)) {
                MethodHandle methodHandle = Processor.getMethodHandle(method, RoutingContext.class);
                route.failureHandler(wrap(instance, methodHandle));
            }

            // disabling routes annotations
            if (Processor.isCompatible(method, Disable.class, RoutingContext.class)) {
                route.disable();
            }

            // process http request path annotations
            if (Processor.isCompatible(method, Path.class, RoutingContext.class)) {
                route.path(Processor.getAnnotation(method, Path.class).value());
            }

            // process http request verb annotations
            if (Processor.isCompatible(method, CONNECT.class, RoutingContext.class)) {
                route.method(HttpMethod.CONNECT);
            }
            if (Processor.isCompatible(method, OPTIONS.class, RoutingContext.class)) {
                route.method(HttpMethod.OPTIONS);
            }
            if (Processor.isCompatible(method, HEAD.class, RoutingContext.class)) {
                route.method(HttpMethod.HEAD);
            }
            if (Processor.isCompatible(method, GET.class, RoutingContext.class)) {
                route.method(HttpMethod.GET);
            }
            if (Processor.isCompatible(method, POST.class, RoutingContext.class)) {
                route.method(HttpMethod.POST);
            }
            if (Processor.isCompatible(method, PUT.class, RoutingContext.class)) {
                route.method(HttpMethod.PUT);
            }
            if (Processor.isCompatible(method, PATCH.class, RoutingContext.class)) {
                route.method(HttpMethod.PATCH);
            }
            if (Processor.isCompatible(method, DELETE.class, RoutingContext.class)) {
                route.method(HttpMethod.DELETE);
            }
            if (Processor.isCompatible(method, TRACE.class, RoutingContext.class)) {
                route.method(HttpMethod.TRACE);
            }

            // process http request order annotations
            if (Processor.isCompatible(method, Order.class, RoutingContext.class)) {
                route.order(Processor.getAnnotation(method, Order.class).value());
            }

            // process http content negotiation annotations
            if (Processor.isCompatible(method, Consumes.class, RoutingContext.class)) {
                final String[] mimeTypes = Processor.getAnnotation(method, Consumes.class).value();
                for (String mimeType : mimeTypes) {
                    route.consumes(mimeType);
                }
            }
            if (Processor.isCompatible(method, Produces.class, RoutingContext.class)) {
                final String[] mimeTypes = Processor.getAnnotation(method, Produces.class).value();
                route.produces(String.join(", ", mimeTypes));
            }
        }
    }

    private static boolean isValidMethod(final Method method) {
        return Processor.isCompatible(method, Handler.class, RoutingContext.class)
            || Processor.isCompatible(method, FailureHandler.class, RoutingContext.class);
    }

    private static io.vertx.core.Handler<RoutingContext> wrap(final Object instance, final MethodHandle m) {
        return ctx -> {
            try {
                m.invoke(instance, ctx);
            } catch (Throwable e) {
                ctx.fail(e);
            }
        };
    }
}
