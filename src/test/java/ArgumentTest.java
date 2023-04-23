import net.iselink.jobsscraper.utils.ArgumentParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ArgumentTest {


	@Test
	void emptyArguments() {
		String[] args = new String[]{};

		ArgumentParser ap = new ArgumentParser();

		ap.parse(args);

		Assertions.assertEquals(0, ap.getFlags().size());
		Assertions.assertEquals(0, ap.getFlagsWithValue().size());
	}

	@Test
	void flagsOnly() {
		String[] args = new String[]{
				"--test",
				"--test2",
		};

		ArgumentParser ap = new ArgumentParser();

		ap.parse(args);

		Assertions.assertEquals(2, ap.getFlags().size());
		Assertions.assertEquals(0, ap.getFlagsWithValue().size());
	}

	@Test
	void flagsWithValueOnly() {
		String[] args = new String[]{
				"--test=x",
				"--test2=6",
		};

		ArgumentParser ap = new ArgumentParser();

		ap.parse(args);

		Assertions.assertEquals(0, ap.getFlags().size());
		Assertions.assertEquals(2, ap.getFlagsWithValue().size());

		Assertions.assertEquals("x", ap.getFlagsWithValue().get("test"));
		Assertions.assertEquals("6", ap.getFlagsWithValue().get("test2"));
	}


}
