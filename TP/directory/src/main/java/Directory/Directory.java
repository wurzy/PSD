package Directory;

import Directory.business.DirectoryClass;
import Directory.resources.DirectoryResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class Directory extends Application<DirectoryConfiguration> {
    public static void main(String[] args) throws Exception {
        new Directory().run(args);
    }

    @Override
    public void initialize(Bootstrap<DirectoryConfiguration> bootstrap) { }

    @Override
    public void run(DirectoryConfiguration configuration, Environment environment) {
        environment.jersey().register(
                new DirectoryResource(DirectoryClass.getInstance()));
    }
}