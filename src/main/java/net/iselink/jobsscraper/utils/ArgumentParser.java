package net.iselink.jobsscraper.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Parses arguments from command line.
 */
public class ArgumentParser {

	private final Command rootCommand = new Command();

	public void parse(String[] args) throws UndefinedFlagException, UndefinedCommandException {
		try {
			rootCommand.parse(args);
		} catch (UndefinedFlagException | UndefinedCommandException e) {
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

		public Command() {
		}

		public Command(String name, String description) {
			this.name = name;
			this.description = description;
		}

		public void addFlag(Flag f) {
			//TODO: throw exception if already defined
			flags.put(f.name, f);
		}

		public void addSubcommand(Command command) {
			//TODO: throw exception if already defined
			subcommands.put(command.name, command);
		}

		public Map<String, Command> getSubcommands() {
			return subcommands;
		}

		public Map<String, Flag> getFlags() {
			return flags;
		}

		public void parse(String[] args) throws UndefinedFlagException, UndefinedCommandException {
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
						throw new UndefinedFlagException(name);
					} else {
						flag.setValue(value);
					}
				} else {
					Command cmd = subcommands.get(argument);
					if (cmd == null) {
						throw new UndefinedCommandException(argument);
					} else {
						selectedCommand = cmd;
						cmd.parse(Arrays.copyOfRange(args, i + 1, args.length));
						return;
					}
				}

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
	}

	/**
	 * Represent flag, can contains value.
	 */
	public static class Flag {
		private String name = null;
		private String description = null;
		private String value = null;

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
	}

	public static class UndefinedFlagException extends Exception {

		public UndefinedFlagException(String flagName) {
			super(String.format("Flag with name %s is not defined.", flagName));
		}
	}

	public static class UndefinedCommandException extends Exception {
		public UndefinedCommandException(String commandName) {
			super(String.format("Command with name %s is not defined.", commandName));
		}
	}
}
