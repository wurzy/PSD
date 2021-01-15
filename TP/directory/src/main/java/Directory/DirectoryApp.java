package Directory;

import Directory.*;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class DiretorioApp extends Application<DirectoryConfiguration> {
    public static void main(String[] args) throws Exception {
        new DiretorioApp().run(args);
    }

    @Override
    public void initialize(Bootstrap<DirectoryConfiguration> bootstrap) { }

    @Override
    public void run(DirectoryConfiguration configuration, Environment environment) {
        environment.jersey().register(
                new DiretorioResource(Diretorio.getInstance()));
        environment.healthChecks().register("template",
                new DiretorioHealthCheck(configuration.getVersion(), Directory.getInstance()));
    }
}