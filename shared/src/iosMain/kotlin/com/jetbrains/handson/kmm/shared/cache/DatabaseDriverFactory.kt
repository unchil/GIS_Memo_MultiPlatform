package com.jetbrains.handson.kmm.shared.cache

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.drivers.native.NativeSqliteDriver

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(GisMemoDatabase.Schema, "gismemo.db")
    }
}