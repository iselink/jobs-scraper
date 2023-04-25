import net.iselink.jobsscraper.utils.ArgumentParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIf;

public class ArgumentTest {


	@Test
	@DisplayName("Empty args")
	void emptyArguments() {
		String[] args = new String[]{};

		ArgumentParser ap = new ArgumentParser();
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

		ArgumentParser ap = new ArgumentParser();
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
					break;
				case "root-flag2":
					Assertions.assertEquals("xd", flag.getValue());
					break;
				default:
					Assertions.fail("Unknown flag name");
			}
		});

		c1.getFlags().forEach((s, flag) -> {
			Assertions.assertNull(flag.getValue());
		});

		c1.getFlags().forEach((s, flag) -> {
			Assertions.assertNull(flag.getValue());
		});

	}

	@Test
	@DisplayName("Subcommand")
	void subcommandTest() {
		String[] args = new String[]{
				"subc1"
		};

		ArgumentParser ap = new ArgumentParser();
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
		});

		c1.getFlags().forEach((s, flag) -> {
			Assertions.assertNull(flag.getValue());
		});

		c1.getFlags().forEach((s, flag) -> {
			Assertions.assertNull(flag.getValue());
		});

	}

	@Test
	@DisplayName("not existing subcommand")
	void notExistSubCmd() {
		String[] args = new String[]{
				"asdf"
		};

		ArgumentParser ap = new ArgumentParser();
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


		Assertions.assertThrows(ArgumentParser.UndefinedCommandException.class, () -> {
			ap.parse(args);
		});
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
	@DisplayName("not existing flag")
	void notExistFlag() {
		String[] args = new String[]{
				"--asdf"
		};

		ArgumentParser ap = new ArgumentParser();
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


		Assertions.assertThrows(ArgumentParser.UndefinedFlagException.class, () -> {
			ap.parse(args);
		});
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

		ap.printHelp();

	}


}
