package net.iselink.jobsscraper.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Parses arguments from command line.
 */
public class ArgumentParser {

	private final String programName;
	private final Command rootCommand;

	public ArgumentParser(String programName) {
		this.programName = programName;
		this.rootCommand = new Command(this.programName, "");
	}

	public void parse(String[] args) throws UndefinedFlagException, UndefinedCommandException, SubcommandNotSelectedException {
		try {
			rootCommand.parse(args);
		} catch (UndefinedFlagException | UndefinedCommandException | SubcommandNotSelectedException e) {
			throw e;
		}
	}

	public void printHelp() {
		StringBuilder builder = new StringBuilder("Help:\n");
		rootCommand.generateVisualisation(builder);
		System.err.println(builder);
	}

	public Command getRootCommand() {
		return rootCommand;
	}

	public static class Command {

		private String name = null;
		private String description = null;

		private Map<String, Command> subcommands = new HashMap<>();
		private Map<String, Flag> flags = new HashMap<>();

		private Command selectedCommand = null;
		private boolean subcommandRequired = false;

		public Command() {
		}

		public Command(String name, String description) {
			this.name = name;
			this.description = description;
		}

		public Command(String name, String description, boolean subcommandRequired) {
			this.name = name;
			this.description = description;
			this.subcommandRequired = subcommandRequired;
		}

		public Flag addFlag(Flag flag) {
			//TODO: throw exception if already defined
			flags.put(flag.name, flag);
			return flag;
		}

		public Command addSubcommand(Command command) {
			//TODO: throw exception if already defined
			subcommands.put(command.name, command);
			return command;
		}

		public Map<String, Command> getSubcommands() {
			return subcommands;
		}

		public Map<String, Flag> getFlags() {
			return flags;
		}

		/**
		 * Get changed flags which has been set from command line argument.
		 *
		 * @return Map of set flags
		 */
		public Map<String, Flag> getSetFlags() {
			Map<String, Flag> map = new HashMap<>();
			flags.forEach((name, flag) -> {
				if (flag.isSetFromCLI()) {
					map.put(name, flag);
				}
			});
			return map;
		}

		/**
		 * Parse arguments from command line.
		 *
		 * @param args Arguments.
		 * @throws UndefinedFlagException         thrown if found unknown flag.
		 * @throws UndefinedCommandException      thrown if found unknown (sub)command.
		 * @throws SubcommandNotSelectedException thrown if subcommand is required to be selected.
		 */
		public void parse(String[] args) throws UndefinedFlagException, UndefinedCommandException, SubcommandNotSelectedException {
			if (subcommandRequired && subcommands.isEmpty())
				throw new IllegalStateException("Subcommand is required but there is no subcommands.");

			for (int i = 0; i < args.length; i++) {
				String argument = args[i];
				if (argument.startsWith("--")) {
					int divider = argument.indexOf('=');
					String name;
					String value;
					if (divider == -1) {
						//flag without value
						name = argument.substring(2);
						value = null;
					} else {
						//flag with value
						name = argument.substring(2, divider);
						value = argument.substring(divider + 1);
					}

					Flag flag = flags.get(name);
					if (flag == null) {
						throw new UndefinedFlagException(this, name);
					} else {
						flag.setFromCLI = true;
						flag.setValue(value);
					}
				} else {
					Command cmd = subcommands.get(argument);
					if (cmd == null) {
						throw new UndefinedCommandException(this, argument);
					} else {
						selectedCommand = cmd;
						cmd.parse(Arrays.copyOfRange(args, i + 1, args.length));
						return;
					}
				}
			}

			if (subcommandRequired && selectedCommand == null) {
				throw new SubcommandNotSelectedException(this);
			}
		}

		public void generateVisualisation(StringBuilder builder) {
			StringBuilder flagBuilder = new StringBuilder();
			flags.forEach((name, flag) -> {
				flagBuilder.append("--").append(name);
				if (flag.value != null) {
					flagBuilder.append('=').append(flag.value);
				}
				flagBuilder.append(' ');
			});

			if (subcommands.isEmpty()) {
				builder.append(flagBuilder);
				return;
			}

			subcommands.forEach((name, command) -> {
				builder.append(flagBuilder);
				builder.append(name).append(' ');
				command.generateVisualisation(builder);
				builder.append('\n');
			});
		}

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}

		public Command getSelectedCommand() {
			return selectedCommand;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public void setSubcommandRequired(boolean subcommandRequired) {
			this.subcommandRequired = subcommandRequired;
		}
	}

	/**
	 * Represent flag, can contains value.
	 */
	public static class Flag {
		private String name = null;
		private String description = null;
		private String value = null;
		private boolean setFromCLI = false;

		public Flag(String name, String description, String value) {
			this.name = name;
			this.description = description;
			this.value = value;
		}

		public Flag(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		/**
		 * Return if this flag has been specified between command line arguments.
		 * Flags without value are market as well.
		 *
		 * @return true if flag is set from CLI
		 */
		public boolean isSetFromCLI() {
			return setFromCLI;
		}
	}

	/**
	 * Base exception for all other exceptions.
	 */
	public static class ArgumentParserException extends Exception {
		public ArgumentParserException() {
		}

		public ArgumentParserException(String message) {
			super(message);
		}

		public ArgumentParserException(String message, Throwable cause) {
			super(message, cause);
		}

		public ArgumentParserException(Throwable cause) {
			super(cause);
		}

		public ArgumentParserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
			super(message, cause, enableSuppression, writableStackTrace);
		}
	}

	/**
	 * Not defined flag exception.
	 */
	public static class UndefinedFlagException extends ArgumentParserException {
		private final Command command;
		private final String unrecognizedFlag;

		public UndefinedFlagException(Command command, String unrecognizedFlag) {
			super(String.format("Flag with name %s is not defined (on command %s).", unrecognizedFlag, command.getName()));
			this.command = command;
			this.unrecognizedFlag = unrecognizedFlag;
		}


		public Command getCommand() {
			return command;
		}

		public String getUnrecognizedFlag() {
			return unrecognizedFlag;
		}
	}

	/**
	 * Not defined (sub)command exception.
	 * Thrown when given subcommand doesn't exist.
	 */
	public static class UndefinedCommandException extends ArgumentParserException {
		private final Command command;
		private final String unrecoginzedCommnad;

		public UndefinedCommandException(Command command, String unrecoginzedCommnad) {
			super(String.format("Command with name %s is not defined (on command %s).", unrecoginzedCommnad, command.getName()));
			this.command = command;
			this.unrecoginzedCommnad = unrecoginzedCommnad;
		}

		public Command getCommand() {
			return command;
		}

		public String getUnrecoginzedCommnad() {
			return unrecoginzedCommnad;
		}
	}

	/**
	 * Subcommand not selected exception.
	 * Thrown when subcommand is required to be selected from subcommands, but none of them has been.
	 */
	public static class SubcommandNotSelectedException extends ArgumentParserException {
		private final Command command;

		public SubcommandNotSelectedException(Command command) {
			super(String.format("Subcommand not selected on command: '%s'", command.getName()));
			this.command = command;
		}

		public Command getCommand() {
			return command;
		}
	}
}
