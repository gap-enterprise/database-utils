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

import com.lightweight.db.EmbeddedPostgreSQLDataSource;
import com.lightweight.db.LiquibaseDataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import javax.sql.DataSource;
import org.jooq.codegen.GenerationTool;
import org.jooq.meta.jaxb.Configuration;
import org.jooq.meta.jaxb.Database;
import org.jooq.meta.jaxb.Generator;
import org.jooq.meta.jaxb.Jdbc;
import org.jooq.meta.jaxb.Target;

/**
 * JOOQ generator.
 *
 * @since 0.1
 */
public final class JooqGenerator {

    /**
     * Data source.
     */
    private final DataSource src;

    /**
     * Package where to generate classes.
     * <p>For example {@code io.surati.gap.admin.jooq.generated}</p>
     */
    private final String pkg;

    /**
     * Tables to include.
     * <p>
     *     To include all public tables, write: .*
     *     To include all public tables prefixed with ad, write: ad_(.*)
     * </p>
     */
    private final String inclusions;

    /**
     * Liquibase changelog filename.
     */
    private final String changelog;

    /**
     * Target directory.
     */
    private final String target;

    /**
     * Ctor.
     * @param changelog Liquibase change log filename
     * @param pkg Package where to generate classes
     * @param inclusions Database tables to include
     * @param target Target directory
     * @checkstyle ParameterNumberCheck (4 lines)
     */
    public JooqGenerator(
        final String changelog, final String pkg,
        final String inclusions, final String target
    ) {
        this(new EmbeddedPostgreSQLDataSource("db_test"), changelog, pkg, inclusions, target);
    }

    /**
     * Ctor.
     * @param src Data source
     * @param changelog Liquibase change log filename
     * @param pkg Package where to generate classes
     * @param inclusions Database tables to include
     * @param target Target directory
     * @checkstyle ParameterNumberCheck (4 lines)
     */
    public JooqGenerator(
        final DataSource src, final String changelog, final String pkg,
        final String inclusions, final String target
    ) {
        this.src = src;
        this.changelog = changelog;
        this.pkg = pkg;
        this.inclusions = inclusions;
        this.target = target;
    }

    /**
     * Starts generation.
     *
     * @throws Exception If fails
     */
    public void start() throws Exception {
        new LiquibaseDataSource(
            this.src,
            this.changelog
        ).getConnection().close();
        final String driver;
        final String url;
        try (Connection conn = this.src.getConnection()) {
            url = conn.getMetaData().getURL();
            driver = DriverManager.getDriver(url).getClass().getName();
        }
        GenerationTool.generate(
            new Configuration()
                .withJdbc(
                    new Jdbc()
                        .withDriver(driver)
                        .withUrl(url)
                ).withGenerator(
                    new Generator()
                        .withDatabase(
                            new Database()
                                .withIncludes(this.inclusions)
                                .withExcludes("databasechangelog|databasechangeloglock")
                                .withInputSchema("public")
                        ).withTarget(
                            new Target()
                                .withPackageName(this.pkg)
                                .withDirectory(this.target)
                        )
                )
        );
    }
}
