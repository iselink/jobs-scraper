import net.iselink.jobsscraper.utils.ArgumentParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ArgumentTest {


	@Test
	@DisplayName("Empty args")
	void emptyArguments() {
		String[] args = new String[]{};

		ArgumentParser ap = new ArgumentParser("test");
		ap.getRootCommand().addFlag(new ArgumentParser.Flag("root-flag", "Root flag 1", null));
		ap.getRootCommand().addFlag(new ArgumentParser.Flag("root-flag2", "Root flag 2", null));

		ArgumentParser.Command c1 = new ArgumentParser.Command("subc1", "Subcommand 1");
		ArgumentParser.Command c2 = new ArgumentParser.Command("subc2", "Subcommand 2");

		c1.addFlag(new ArgumentParser.Flag("subc1-flag1"));
		c1.addFlag(new ArgumentParser.Flag("subc1-flag2"));
		c2.addFlag(new ArgumentParser.Flag("subc2-flag1"));
		c2.addFlag(new ArgumentParser.Flag("subc2-flag2"));

		ap.getRootCommand().addSubcommand(c1);
		ap.getRootCommand().addSubcommand(c2);

		Assertions.assertDoesNotThrow(() -> ap.parse(args));


		Assertions.assertNull(ap.getRootCommand().getSelectedCommand());
		ap.getRootCommand().getFlags().forEach((s, flag) -> {
			Assertions.assertNull(flag.getValue());
		});
		c1.getFlags().forEach((s, flag) -> {
			Assertions.assertNull(flag.getValue());
		});
		c1.getFlags().forEach((s, flag) -> {
			Assertions.assertNull(flag.getValue());
		});

	}

	@Test
	@DisplayName("Root flags")
	void rootFlagsTest() {
		String[] args = new String[]{
				"--root-flag",
				"--root-flag2=xd",
		};

		ArgumentParser ap = new ArgumentParser("test");
		ap.getRootCommand().addFlag(new ArgumentParser.Flag("root-flag", "Root flag 1", null));
		ap.getRootCommand().addFlag(new ArgumentParser.Flag("root-flag2", "Root flag 2", null));

		ArgumentParser.Command c1 = new ArgumentParser.Command("subc1", "Subcommand 1");
		ArgumentParser.Command c2 = new ArgumentParser.Command("subc2", "Subcommand 2");

		c1.addFlag(new ArgumentParser.Flag("subc1-flag1"));
		c1.addFlag(new ArgumentParser.Flag("subc1-flag2"));
		c2.addFlag(new ArgumentParser.Flag("subc2-flag1"));
		c2.addFlag(new ArgumentParser.Flag("subc2-flag2"));

		ap.getRootCommand().addSubcommand(c1);
		ap.getRootCommand().addSubcommand(c2);

		Assertions.assertDoesNotThrow(() -> ap.parse(args));


		Assertions.assertNull(ap.getRootCommand().getSelectedCommand());
		ap.getRootCommand().getFlags().forEach((s, flag) -> {
			switch (s) {
				case "root-flag":
					Assertions.assertNull(flag.getValue());
					Assertions.assertTrue(flag.isSetFromCLI());
					break;
				case "root-flag2":
					Assertions.assertEquals("xd", flag.getValue());
					Assertions.assertTrue(flag.isSetFromCLI());
					break;
				default:
					Assertions.fail("Unknown flag name");
			}
		});

		c1.getFlags().forEach((s, flag) -> {
			Assertions.assertNull(flag.getValue());
			Assertions.assertFalse(flag.isSetFromCLI());
		});

		c1.getFlags().forEach((s, flag) -> {
			Assertions.assertNull(flag.getValue());
			Assertions.assertFalse(flag.isSetFromCLI());
		});

	}

	@Test
	@DisplayName("Subcommand")
	void subcommandTest() {
		String[] args = new String[]{
				"subc1"
		};

		ArgumentParser ap = new ArgumentParser("test");
		ap.getRootCommand().addFlag(new ArgumentParser.Flag("root-flag", "Root flag 1", null));
		ap.getRootCommand().addFlag(new ArgumentParser.Flag("root-flag2", "Root flag 2", null));

		ArgumentParser.Command c1 = new ArgumentParser.Command("subc1", "Subcommand 1");
		ArgumentParser.Command c2 = new ArgumentParser.Command("subc2", "Subcommand 2");

		c1.addFlag(new ArgumentParser.Flag("subc1-flag1"));
		c1.addFlag(new ArgumentParser.Flag("subc1-flag2"));
		c2.addFlag(new ArgumentParser.Flag("subc2-flag1"));
		c2.addFlag(new ArgumentParser.Flag("subc2-flag2"));

		ap.getRootCommand().addSubcommand(c1);
		ap.getRootCommand().addSubcommand(c2);

		Assertions.assertDoesNotThrow(() -> ap.parse(args));
		Assertions.assertEquals(c1, ap.getRootCommand().getSelectedCommand());

		ap.getRootCommand().getFlags().forEach((s, flag) -> {
			Assertions.assertNull(flag.getValue());
			Assertions.assertFalse(flag.isSetFromCLI());
		});

		c1.getFlags().forEach((s, flag) -> {
			Assertions.assertNull(flag.getValue());
			Assertions.assertFalse(flag.isSetFromCLI());
		});

		c1.getFlags().forEach((s, flag) -> {
			Assertions.assertNull(flag.getValue());
			Assertions.assertFalse(flag.isSetFromCLI());
		});

	}

	@Test
	@DisplayName("not existing subcommand")
	void notExistSubCmd() {
		String[] args = new String[]{
				"asdf"
		};

		ArgumentParser ap = new ArgumentParser("test");
		ap.getRootCommand().addFlag(new ArgumentParser.Flag("root-flag", "Root flag 1", null));
		ap.getRootCommand().addFlag(new ArgumentParser.Flag("root-flag2", "Root flag 2", null));

		ArgumentParser.Command c1 = new ArgumentParser.Command("subc1", "Subcommand 1");
		ArgumentParser.Command c2 = new ArgumentParser.Command("subc2", "Subcommand 2");

		c1.addFlag(new ArgumentParser.Flag("subc1-flag1"));
		c1.addFlag(new ArgumentParser.Flag("subc1-flag2"));
		c2.addFlag(new ArgumentParser.Flag("subc2-flag1"));
		c2.addFlag(new ArgumentParser.Flag("subc2-flag2"));

		ap.getRootCommand().addSubcommand(c1);
		ap.getRootCommand().addSubcommand(c2);


		ArgumentParser.UndefinedCommandException ex = Assertions.assertThrows(ArgumentParser.UndefinedCommandException.class, () -> {
			ap.parse(args);
		});
		Assertions.assertNull(ap.getRootCommand().getSelectedCommand());
		Assertions.assertEquals("asdf", ex.getUnrecoginzedCommnad());
		Assertions.assertSame(ex.getCommand(), ap.getRootCommand());

		ap.getRootCommand().getFlags().forEach((s, flag) -> {
			Assertions.assertNull(flag.getValue());
			Assertions.assertFalse(flag.isSetFromCLI());
		});

		c1.getFlags().forEach((s, flag) -> {
			Assertions.assertNull(flag.getValue());
			Assertions.assertFalse(flag.isSetFromCLI());
		});

		c1.getFlags().forEach((s, flag) -> {
			Assertions.assertNull(flag.getValue());
			Assertions.assertFalse(flag.isSetFromCLI());
		});

	}

	@Test
	@DisplayName("not existing flag")
	void notExistFlag() {
		String[] args = new String[]{
				"--asdf"
		};

		ArgumentParser ap = new ArgumentParser("test");
		ap.getRootCommand().addFlag(new ArgumentParser.Flag("root-flag", "Root flag 1", null));
		ap.getRootCommand().addFlag(new ArgumentParser.Flag("root-flag2", "Root flag 2", null));

		ArgumentParser.Command c1 = new ArgumentParser.Command("subc1", "Subcommand 1");
		ArgumentParser.Command c2 = new ArgumentParser.Command("subc2", "Subcommand 2");

		c1.addFlag(new ArgumentParser.Flag("subc1-flag1"));
		c1.addFlag(new ArgumentParser.Flag("subc1-flag2"));
		c2.addFlag(new ArgumentParser.Flag("subc2-flag1"));
		c2.addFlag(new ArgumentParser.Flag("subc2-flag2"));

		ap.getRootCommand().addSubcommand(c1);
		ap.getRootCommand().addSubcommand(c2);


		ArgumentParser.UndefinedFlagException ex = Assertions.assertThrows(ArgumentParser.UndefinedFlagException.class, () -> {
			ap.parse(args);
		});
		Assertions.assertEquals("asdf", ex.getUnrecognizedFlag());
		Assertions.assertNull(ap.getRootCommand().getSelectedCommand());

		ap.getRootCommand().getFlags().forEach((s, flag) -> {
			Assertions.assertNull(flag.getValue());
			Assertions.assertFalse(flag.isSetFromCLI());
		});

		c1.getFlags().forEach((s, flag) -> {
			Assertions.assertNull(flag.getValue());
			Assertions.assertFalse(flag.isSetFromCLI());
		});

		c1.getFlags().forEach((s, flag) -> {
			Assertions.assertNull(flag.getValue());
			Assertions.assertFalse(flag.isSetFromCLI());
		});

		ap.printHelp();

	}

	@Test
	@DisplayName("Required subcommand test")
	void checkRequiredSubcommandAttribute() {
		ArgumentParser parser = new ArgumentParser("test");
		parser.getRootCommand().setSubcommandRequired(true);

		ArgumentParser.Command cmd1 = parser.getRootCommand().addSubcommand(new ArgumentParser.Command("command1", "just command1"));
		ArgumentParser.Command cmd2 = parser.getRootCommand().addSubcommand(new ArgumentParser.Command("command2", "just command2"));

		ArgumentParser.SubcommandNotSelectedException ex = Assertions.assertThrows(ArgumentParser.SubcommandNotSelectedException.class, () -> {
			parser.parse(new String[]{});
		});
		Assertions.assertSame(ex.getCommand(), parser.getRootCommand());

	}

}
