/*
 * Copyright (C) 2011 Thomas Akehurst
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.tomakehurst.wiremock.extension;

import com.google.common.base.Function;

import java.util.List;

import static com.github.tomakehurst.wiremock.common.Exceptions.throwUnchecked;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.FluentIterable.from;
import static java.util.Arrays.asList;

public class ExtensionLoader {

    public static List<Extension> load(String... classNames) {
        return from(asList(classNames))
                .transform(toClasses())
                .transform(toExtensions())
                .toList();
    }

    public static List<Extension> load(Class<? extends Extension>... classes) {
        return from(asList(classes))
                .transform(toExtensions())
                .toList();
    }

    private static Function<Class<? extends Extension>, Extension> toExtensions() {
        return new Function<Class<? extends Extension>, Extension>() {
            @SuppressWarnings("unchecked")
            public Extension apply(Class<? extends Extension> extensionClass) {
                try {
                    checkArgument(ResponseTransformer.class.isAssignableFrom(extensionClass), "Extension classes must implement ResponseTransformer");
                    return extensionClass.newInstance();
                } catch (Exception e) {
                    return throwUnchecked(e, Extension.class);
                }

            }
        };
    }

    private static Function<String, Class<? extends Extension>> toClasses() {
        return new Function<String, Class<? extends Extension>>() {
            @SuppressWarnings("unchecked")
            public Class<? extends Extension> apply(String className) {
                try {
                    return (Class<? extends Extension>) Class.forName(className);
                } catch (ClassNotFoundException e) {
                    return throwUnchecked(e, Class.class);
                }
            }
        };
    }
}