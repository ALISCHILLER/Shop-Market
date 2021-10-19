package com.varanegar.vaslibrary.base;

import androidx.annotation.NonNull;

import timber.log.Timber;

/**
 * Created by A.Torabi on 9/23/2018.
 */

public class TimberTracking {
    private static void addTrackingTree() {
        VasLogConfig.addTrackingTree();
    }

    private static void removeTrackingTree() {
        VasLogConfig.removeTrackingTree();
    }

    public static void v(@NonNull String message, Object... args) {
        addTrackingTree();
        Timber.v(message, args);
        removeTrackingTree();
    }

    /**
     * Log a verbose exception and a message with optional format args.
     */
    public static void v(Throwable t, @NonNull String message, Object... args) {
        addTrackingTree();
        Timber.v(t, message, args);
        removeTrackingTree();
    }

    /**
     * Log a verbose exception.
     */
    public static void v(Throwable t) {
        addTrackingTree();
        Timber.v(t);
        removeTrackingTree();
    }

    /**
     * Log a debug message with optional format args.
     */
    public static void d(@NonNull String message, Object... args) {
        addTrackingTree();
        Timber.d(message, args);
        removeTrackingTree();
    }

    /**
     * Log a debug exception and a message with optional format args.
     */
    public static void d(Throwable t, @NonNull String message, Object... args) {
        addTrackingTree();
        Timber.d(t, message, args);
        removeTrackingTree();
    }

    /**
     * Log a debug exception.
     */
    public static void d(Throwable t) {
        addTrackingTree();
        Timber.d(t);
        removeTrackingTree();
    }

    /**
     * Log an info message with optional format args.
     */
    public static void i(@NonNull String message, Object... args) {
        addTrackingTree();
        Timber.i(message, args);
        removeTrackingTree();
    }

    /**
     * Log an info exception and a message with optional format args.
     */
    public static void i(Throwable t, @NonNull String message, Object... args) {
        addTrackingTree();
        Timber.i(t, message, args);
        removeTrackingTree();
    }

    /**
     * Log an info exception.
     */
    public static void i(Throwable t) {
        addTrackingTree();
        Timber.i(t);
        removeTrackingTree();
    }

    /**
     * Log a warning message with optional format args.
     */
    public static void w(@NonNull String message, Object... args) {
        addTrackingTree();
        Timber.w(message, args);
        removeTrackingTree();
    }

    /**
     * Log a warning exception and a message with optional format args.
     */
    public static void w(Throwable t, @NonNull String message, Object... args) {
        addTrackingTree();
        Timber.w(t, message, args);
        removeTrackingTree();
    }

    /**
     * Log a warning exception.
     */
    public static void w(Throwable t) {
        addTrackingTree();
        Timber.w(t);
        removeTrackingTree();
    }

    /**
     * Log an error message with optional format args.
     */
    public static void e(@NonNull String message, Object... args) {
        addTrackingTree();
        Timber.e(message, args);
        removeTrackingTree();
    }

    /**
     * Log an error exception and a message with optional format args.
     */
    public static void e(Throwable t, @NonNull String message, Object... args) {
        addTrackingTree();
        Timber.e(t, message, args);
        removeTrackingTree();
    }

    /**
     * Log an error exception.
     */
    public static void e(Throwable t) {
        addTrackingTree();
        Timber.e(t);
        removeTrackingTree();
    }

    /**
     * Log an assert message with optional format args.
     */
    public static void wtf(@NonNull String message, Object... args) {
        addTrackingTree();
        Timber.wtf(message, args);
        removeTrackingTree();
    }

    /**
     * Log an assert exception and a message with optional format args.
     */
    public static void wtf(Throwable t, @NonNull String message, Object... args) {
        addTrackingTree();
        Timber.wtf(t, message, args);
        removeTrackingTree();
    }

    /**
     * Log an assert exception.
     */
    public static void wtf(Throwable t) {
        addTrackingTree();
        Timber.wtf(t);
        removeTrackingTree();
    }

    /**
     * Log at {@code priority} a message with optional format args.
     */
    public static void log(int priority, @NonNull String message, Object... args) {
        addTrackingTree();
        Timber.log(priority, message, args);
        removeTrackingTree();
    }

    /**
     * Log at {@code priority} an exception and a message with optional format args.
     */
    public static void log(int priority, Throwable t, @NonNull String message, Object... args) {
        addTrackingTree();
        Timber.log(priority, t, message, args);
        removeTrackingTree();
    }

    /**
     * Log at {@code priority} an exception.
     */
    public static void log(int priority, Throwable t) {
        addTrackingTree();
        Timber.log(priority, t);
        removeTrackingTree();
    }
}
