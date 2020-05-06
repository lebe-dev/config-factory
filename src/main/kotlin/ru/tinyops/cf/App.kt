package ru.tinyops.cf

import org.apache.commons.cli.*
import org.slf4j.LoggerFactory
import ru.tinyops.cf.service.ConfigProducer
import ru.tinyops.cf.service.ConfigService
import ru.tinyops.cf.service.FileConfigProducer
import ru.tinyops.cf.service.FileConfigService
import java.io.File
import kotlin.system.exitProcess


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
                println("=".repeat(32))
                println("\n\tCONFIG FACTORY $VERSION\n")
                println("  Mass config file generator \n")
                println("-".repeat(32))

                val cmd = DefaultParser().parse(commandOptions, args)

                val configService: ConfigService = FileConfigService()
                val config = configService.load(File(CONFIG_FILE))

                if (config.isPresent) {
                    log.debug("+ config has been found")

                    val outputPath = cmd.getOptionValue(OUTPUT_PATH_OPTION) ?: OUTPUT_DIRECTORY

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

            } catch (e: MissingOptionException) {
                helpFormatter.printHelp("config-factory.jar", commandOptions)
                exitProcess(-1)

            } catch (e: Exception) {
                log.error("unexpected error, check logs for details")
                log.debug(e.message, e)
                exitProcess(-1)
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
