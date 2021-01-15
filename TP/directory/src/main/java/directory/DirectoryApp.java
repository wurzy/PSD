package directory;

import directory.resources.DirectoryResource;
import directory.business.Directory;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class DirectoryApp extends Application<DirectoryConfiguration> {
    public static void main(String[] args) throws Exception {
        new DirectoryApp().run(args);
    }

    @Override
    public void initialize(Bootstrap<DirectoryConfiguration> bootstrap) { }

    @Override
    public void run(DirectoryConfiguration configuration, Environment environment) {
        environment.jersey().register(
                new DirectoryResource(Directory.getInstance()));
    }
}