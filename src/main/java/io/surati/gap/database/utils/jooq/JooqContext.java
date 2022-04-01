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
package io.surati.gap.database.utils.jooq;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;
import javax.sql.DataSource;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DefaultDSLContext;

/**
 * Jooq context.
 *
 * @since 0.4
 */
public final class JooqContext extends DefaultDSLContext {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 2681360188806309513L;

    /**
     * Ctor.
     * @param src Data source
     */
    public JooqContext(final DataSource src) {
        super(
            src,
            JooqContext.dialectOf(src),
            new Settings().withRenderSchema(false)
        );
    }

    /**
     * Identifies dialect to use for data source.
     * @param src Data source
     * @return SQLDialect
     */
    private static SQLDialect dialectOf(final DataSource src) {
        try (Connection conn = src.getConnection()) {
            final String driver = conn.getMetaData().getDriverName();
            final SQLDialect dialect;
            if (driver.toLowerCase(Locale.ENGLISH).contains("postgres")) {
                dialect = SQLDialect.POSTGRES;
            } else if (driver.toLowerCase(Locale.ENGLISH).contains("h2")) {
                dialect = SQLDialect.H2;
            } else {
                dialect = SQLDialect.DEFAULT;
            }
            return dialect;
        } catch (final SQLException sqle) {
            throw new IllegalStateException(sqle);
        }
    }
}
