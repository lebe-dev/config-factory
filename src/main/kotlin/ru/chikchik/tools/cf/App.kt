package ru.chikchik.tools.cf

import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Option
import org.apache.commons.cli.Options
import org.slf4j.LoggerFactory
import ru.chikchik.tools.cf.service.ConfigProducer
import ru.chikchik.tools.cf.service.ConfigService
import ru.chikchik.tools.cf.service.FileConfigProducer
import ru.chikchik.tools.cf.service.FileConfigService
import java.io.File


class App {

    companion object {
        private val log = LoggerFactory.getLogger(App::class.java)

        private const val VERSION = "1.0.1"

        const val OUTPUT_DIRECTORY = "output"

        internal const val CONFIG_FILE = "config-factory.conf"
        private const val TEMPLATE_FILE_OPTION = "t"
        private const val OUTPUT_PATH_OPTION = "o"

        @JvmStatic
        fun main(args: Array<String>) {
            val commandOptions = getCommandLineOptions()
            val helpFormatter = HelpFormatter()

            try {
                log.info("=".repeat(32))
                log.info("\n\tCONFIG FACTORY $VERSION\n")
                log.info("  Mass config file generator \n")
                log.info("-".repeat(32))

                val cmd = DefaultParser().parse(commandOptions, args)

                val configService: ConfigService = FileConfigService()
                val config = configService.load(File(CONFIG_FILE))

                if (config.isPresent) {
                    log.debug("+ config has been found")

                    val outputPath = cmd.getOptionValue(TEMPLATE_FILE_OPTION) ?: OUTPUT_DIRECTORY

                    val configProducer: ConfigProducer = FileConfigProducer(cmd.getOptionValue(TEMPLATE_FILE_OPTION))

                    val configFiles = configProducer.produce(
                        profiles = config.get().profiles,
                        variables = config.get().variables,
                        outputFileFormat = config.get().outputFileFormat,
                        outputPath = outputPath
                    )

                    if (configFiles.isPresent) {
                        log.info("[+] created config files (${configFiles.get().size}) at path '$outputPath'")
                    }

                } else {
                    log.error("unable to load application config from file '$CONFIG_FILE'")
                }

            } catch (e: Exception) {
                helpFormatter.printHelp("config-factory.jar", commandOptions)
                log.debug(e.message, e)
            }
        }

        private fun getCommandLineOptions(): Options =
                Options().apply {
                    addOption(
                            Option.builder(TEMPLATE_FILE_OPTION).argName("file-name")
                                                                .longOpt("template-file")
                                                                .desc("template file")
                                                                .optionalArg(false)
                                                                .hasArg(true)
                                                                .required().build()
                    )
                    addOption(
                            Option.builder(OUTPUT_PATH_OPTION).argName("path").desc("output path")
                                                              .longOpt("output-path")
                                                              .hasArg(true)
                                                              .optionalArg(true)
                                                              .required(false)
                                                              .build()
                    )
                }

    }

}