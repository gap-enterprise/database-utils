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
package io.surati.gap.database.utils;

import com.lightweight.db.DataSourceWrap;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

/**
 * Data source that places automatically all database operations into a transaction.
 * <p>It returns the same connection during this transaction.
 * You have to commit or rollback before passing to another connection</p>
 *
 * @since 0.3
 */
public final class UncommittedDataSource extends DataSourceWrap {

    /**
     * Thread connection.
     */
    private final ThreadLocal<Connection> connection;

    /**
     * Ctor.
     * @param origin Data source to wrap
     * @param connection Thread connection
     */
    public UncommittedDataSource(
        final DataSource origin, final ThreadLocal<Connection> connection
    ) {
        super(origin);
        this.connection = connection;
    }

    @Override
    public Connection getConnection() throws SQLException {
        final Connection conn;
        if (this.connection.get() == null) {
            conn = this.newConnection();
        } else {
            if (this.connection.get().isClosed()) {
                conn = this.newConnection();
            } else {
                conn = this.connection.get();
            }
        }
        return new ClosedShieldConnection(conn);
    }

    @Override
    public Connection getConnection(
        final String username, final String password
    ) throws SQLException {
        final Connection conn;
        if (this.connection.get() == null) {
            conn = this.newConnection(username, password);
        } else {
            if (this.connection.get().isClosed()) {
                conn = this.newConnection(username, password);
            } else {
                conn = this.connection.get();
            }
        }
        return new ClosedShieldConnection(conn);
    }

    /**
     * Generate new connection.
     * @return Connection
     * @throws SQLException If fails
     */
    private Connection newConnection() throws SQLException {
        synchronized (this.connection) {
            final Connection connect =  super.getConnection();
            connect.setAutoCommit(false);
            connect.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            this.connection.set(connect);
            return connect;
        }
    }

    /**
     * Generate new connection.
     * @param username Username
     * @param password Password
     * @return Connection
     * @throws SQLException If fails
     */
    private Connection newConnection(
        final String username, final String password
    ) throws SQLException {
        synchronized (this.connection) {
            final Connection connect =  super.getConnection(username, password);
            connect.setAutoCommit(false);
            connect.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            this.connection.set(connect);
            return connect;
        }
    }
}
