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

import com.lightweight.db.DataSourceWrap;
import com.lightweight.db.EmbeddedPostgreSQLDataSource;
import com.lightweight.db.LiquibaseDataSource;
import io.surati.gap.database.utils.UncommittedDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * JUnit5 test template extension running test methods with all data source types.
 *
 * @since 0.3
 */
public final class DatabaseSetupExtension extends DataSourceWrap implements AfterEachCallback {

    /**
     * Connection in thread.
     */
    private static final ThreadLocal<Connection> CONNECTION = new ThreadLocal<>();

    /**
     * Ctor with embedded postgresql data source.
     * @param changelog Change log path
     */
    public DatabaseSetupExtension(final String changelog) {
        this(new EmbeddedPostgreSQLDataSource(), changelog);
    }

    /**
     * Ctor.
     * @param src Data source
     * @param changelog Change log path
     */
    public DatabaseSetupExtension(final DataSource src, final String changelog) {
        super(
            new UncommittedDataSource(
                DatabaseSetupExtension.upgrade(src, changelog),
                DatabaseSetupExtension.CONNECTION
            )
        );
    }

    @Override
    public void afterEach(final ExtensionContext context) throws Exception {
        if (DatabaseSetupExtension.CONNECTION.get() != null) {
            DatabaseSetupExtension.CONNECTION.get().rollback();
        }
    }

    /**
     * Upgrades data source.
     * @param src Data source to upgrade
     * @param changelog Change log path
     * @return Data source upgraded
     */
    private static DataSource upgrade(final DataSource src, final String changelog) {
        try {
            return new LiquibaseDataSource(
                src, changelog
            );
        } catch (final SQLException exe) {
            throw new IllegalStateException(exe);
        }
    }
}
