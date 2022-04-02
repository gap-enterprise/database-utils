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

import com.baudoliver7.jdbc.toolset.lockable.LocalLockedDataSource;
import com.baudoliver7.jdbc.toolset.wrapper.DataSourceWrap;
import java.sql.Connection;
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
     * Ctor.
     * @param src Data source
     */
    public DatabaseSetupExtension(final DataSource src) {
        super(
            new LocalLockedDataSource(
                src,
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
}
