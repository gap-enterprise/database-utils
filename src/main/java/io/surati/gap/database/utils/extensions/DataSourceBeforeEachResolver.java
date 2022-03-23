/*
 * Copyright (c) 2022 Surati
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to read
 * the Software only. Permissions is hereby NOT GRANTED to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.surati.gap.database.utils.extensions;

import java.util.Optional;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.engine.execution.BeforeEachMethodAdapter;
import org.junit.jupiter.engine.extension.ExtensionRegistry;

/**
 * Data source parameter before each resolver.
 *
 * @since 0.1
 */
public final class DataSourceBeforeEachResolver
    implements BeforeEachMethodAdapter, ParameterResolver {

    /**
     * Data source extension resolver.
     */
    private ParameterResolver resolver;

    @Override
    public void invokeBeforeEachMethod(
        final ExtensionContext context, final ExtensionRegistry registry
    ) {
        final Optional<ParameterResolver> optres =
            registry.getExtensions(ParameterResolver.class)
                .stream()
                .filter(
                    parameterResolver -> parameterResolver.getClass()
                        .getName()
                        .contains("Resolver")
                )
                .findFirst();
        if (optres.isPresent()) {
            this.resolver = optres.get();
        } else {
            throw new IllegalStateException(
                "Resolver missed in the registry. Probably it's not a TestTemplate Test"
            );
        }
    }

    @Override
    public boolean supportsParameter(
        final ParameterContext pctx, final ExtensionContext ectx
    ) throws ParameterResolutionException {
        return this.resolver.supportsParameter(pctx, ectx);
    }

    @Override
    public Object resolveParameter(
        final ParameterContext pctx, final ExtensionContext ectx
    ) throws ParameterResolutionException {
        return this.resolver.resolveParameter(pctx, ectx);
    }
}
