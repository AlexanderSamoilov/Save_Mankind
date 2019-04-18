package com.company.gamelogger;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.builder.api.*;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.config.Order;
import org.apache.logging.log4j.core.config.plugins.Plugin;

import java.net.URI;

import static com.company.gamelogger.LogOptions.*;

@Plugin(name = "LogConfFactory", category = ConfigurationFactory.CATEGORY)
@Order(50)
public class LogConfFactory extends ConfigurationFactory {
    private static Configuration createConfiguration(final String name, ConfigurationBuilder<BuiltConfiguration> builder) {
        builder.setConfigurationName(name);
        builder.setStatusLevel(LEVEL);

        String logFilePath = FILE_PATH + "/" + FILE_NAME;
        boolean debugging = LEVEL.equals(Level.DEBUG) || LEVEL.equals(Level.ALL);

        // ---->> FILTERS
        // Warning! Stupid Java programmers forgot to make multiple-add filters normally, like other components.
        //    That means you must add filter to Appender first, and add it to Logger second.
        //    I don't know alternative solution of this. Stackoverflow too.

        // Log regex filter. Instruction: http://logging.apache.org/log4j/2.x/manual/filters.html
        FilterComponentBuilder regexFilterBuilder = null;
        if (REGEX.length() > 0) {
            regexFilterBuilder = builder.newFilter(
                    "RegexFilter",
                    Filter.Result.ACCEPT,
                    Filter.Result.DENY
            ).addAttribute("regex", REGEX);
        }

        // Log Threshold filter. Instruction: http://logging.apache.org/log4j/2.x/manual/filters.html
        // It can filtering msg by lvl (from specified lvl to highest, if matching not reversal)
        //
        // Filter for messages level: from INFO to FATAL.
        FilterComponentBuilder infoAndHighestFilterBuilder = builder.newFilter(
                "ThresholdFilter",
                Filter.Result.ACCEPT,
                Filter.Result.DENY
        ).addAttribute("level", Level.INFO);

        // Inverted filter for messages level: from DEBUG to ALL (INFO not include).
        FilterComponentBuilder debugAndLowestFilterBuilder = builder.newFilter(
                "ThresholdFilter",
                Filter.Result.DENY,
                Filter.Result.ACCEPT
        ).addAttribute("level", Level.INFO);

        // ---->> CONSOLE BUILDER
        // Creating console output by appenderComponentBuilder
        AppenderComponentBuilder consoleBuilder = builder.newAppender(
                "stdout", "CONSOLE"
        ).addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT);

        // Log text layout. Instruction: https://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/PatternLayout.html
        consoleBuilder.add(builder.newLayout("PatternLayout").
                addAttribute("pattern", STDOUT_LAYOUT));

        consoleBuilder.addComponent(infoAndHighestFilterBuilder);

        // Adding consoleBuilder to configuration building
        builder.add(consoleBuilder);

        // ---->> FILE BUILDER
        if (WRITE_FILE) {
            // Creating file output by builder
            AppenderComponentBuilder fileBuilder = builder.newAppender(
                    "logfile", "FILE"
            ).addAttribute("fileName", logFilePath);

            fileBuilder.add(builder.newLayout("PatternLayout").
                    addAttribute("pattern", FILE_LAYOUT)
            );

            fileBuilder.add(infoAndHighestFilterBuilder);

            // Adding fileBuilder to configuration building
            builder.add(fileBuilder);
        }

        // ---->> DEBUG CONSOLE BUILDER
        // Creating console and file output by builder for debug type logger
        if (debugging) {
            AppenderComponentBuilder consoleDebugBuilder = builder.newAppender(
                "debug-stdout", "CONSOLE"
            ).addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT);

            consoleDebugBuilder.add(builder.newLayout("PatternLayout").
                    addAttribute("pattern", DEBUG_LAYOUT)
            );

            consoleDebugBuilder.add(debugAndLowestFilterBuilder);

            // Adding consoleDebugBuilder to configuration building
            builder.add(consoleDebugBuilder);
        }

        // ---->> DEBUG FILE BUILDER
        if (WRITE_FILE && debugging) {
            AppenderComponentBuilder fileDebugBuilder = builder.newAppender(
                    "debug-logfile", "FILE"
            ).addAttribute("fileName", logFilePath);

            fileDebugBuilder.add(builder.newLayout("PatternLayout").
                    addAttribute("pattern", DEBUG_LAYOUT)
            );

            fileDebugBuilder.add(debugAndLowestFilterBuilder);

            // Adding fileDebugBuilder to configuration building
            builder.add(fileDebugBuilder);
        }

        // ---->> LOGGER BUILDER
        // Adding Root Logger.
        // If not create this, log4j add it and configure anyway with ERROR lvl and SYSTEM_OUT appender.
        // We not need custom logger, because we must setting logger names in all classes.
        RootLoggerComponentBuilder gameLoggerBuilder = builder.newRootLogger(LEVEL);

        // Adding stdout as reference appender for logger
        gameLoggerBuilder.add(builder.newAppenderRef("stdout")).addAttribute("additivity", false);

        if (WRITE_FILE) {
            // Adding file appender as reference for logger
            gameLoggerBuilder.add(builder.newAppenderRef("logfile")).addAttribute("additivity", false);
        }

        if (debugging) {
            gameLoggerBuilder.add(builder.newAppenderRef("debug-stdout")).addAttribute("additivity", false);
        }

        if (WRITE_FILE && debugging) {
            gameLoggerBuilder.add(builder.newAppenderRef("debug-logfile")).addAttribute("additivity", false);
        }

        if (regexFilterBuilder != null) {
            // Add Regex Filter to logger
            gameLoggerBuilder.add(regexFilterBuilder);
        }

        builder.add(gameLoggerBuilder);

        return builder.build();
    }

    @Override
    public Configuration getConfiguration(
            final LoggerContext loggerContext,
            final ConfigurationSource source
    ) {
        return getConfiguration(
                loggerContext,
                source.toString(),
                null
        );
    }

    @Override
    public Configuration getConfiguration(
            final LoggerContext loggerContext,
            final String name,
            final URI configLocation
    ) {
        ConfigurationBuilder<BuiltConfiguration> builder = newConfigurationBuilder();
        return createConfiguration(name, builder);
    }

    @Override
    protected String[] getSupportedTypes() {
        return new String[] {"*"};
    }
}
