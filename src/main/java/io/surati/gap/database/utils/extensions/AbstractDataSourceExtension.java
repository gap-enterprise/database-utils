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

import com.lightweight.db.EmbeddedOracleDataSource;
import com.lightweight.db.EmbeddedPostgreSQLDataSource;
import com.lightweight.db.LiquibaseDataSource;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;
import javax.sql.DataSource;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;

/**
 * JUnit5 test template extension running test methods with all data source types.
 *
 * @since 0.1
 */
public abstract class AbstractDataSourceExtension
    implements TestTemplateInvocationContextProvider {

    /**
     * Liquibase change log filename.
     */
    private final String changelog;

    /**
     * Ctor.
     * @param changelog Liquibase change log filename
     */
    public AbstractDataSourceExtension(final String changelog) {
        this.changelog = changelog;
    }

    @Override
    public final boolean supportsTestTemplate(final ExtensionContext context) {
        return context.getTestMethod().isPresent();
    }

    @Override
    public final Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(
        final ExtensionContext context
    ) {
        try {
            return new LinkedList<>(
                Arrays.asList(
                    new LiquibaseDataSource(
                        new EmbeddedPostgreSQLDataSource(),
                        this.changelog
                    ),
                    new LiquibaseDataSource(
                        new EmbeddedOracleDataSource(),
                        this.changelog
                    )
                )
            ).stream().map(DataSourceContext::new);
        } catch (final SQLException exe) {
            throw new IllegalStateException(exe);
        }
    }

    /**
     * Test template context with bound data source.
     *
     * @since 0.1
     */
    private static class DataSourceContext implements TestTemplateInvocationContext {

        /**
         * Data source bound.
         */
        private final DataSource src;

        /**
         * Ctor.
         * @param src Data source to bind
         */
        DataSourceContext(final DataSource src) {
            this.src = src;
        }

        @Override
        public String getDisplayName(final int index) {
            return String.format("[%s]", this.src.getClass().getSimpleName());
        }

        @Override
        public List<Extension> getAdditionalExtensions() {
            return Collections.singletonList(new Resolver());
        }

        /**
         * Resolver for {@link DataSource} parameter.
         *
         * @since 0.1
         */
        private class Resolver implements ParameterResolver {

            @Override
            public boolean supportsParameter(
                final ParameterContext parameter,
                final ExtensionContext extension) throws ParameterResolutionException {
                return parameter.getParameter().getType().equals(DataSource.class);
            }

            @Override
            public Object resolveParameter(
                final ParameterContext parameter,
                final ExtensionContext extension) throws ParameterResolutionException {
                return DataSourceContext.this.src;
            }
        }
    }
}
