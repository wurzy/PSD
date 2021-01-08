import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import resources.CompanyResource;

public class StockDirectory extends Application<Configuration> {
    public static void main(String[] args) throws Exception {
        new StockDirectory().run(args);
    }

    @Override
    public void initialize(Bootstrap<Configuration> bootstrap) {}

    @Override
    public void run(Configuration stockDirectoryConfiguration, Environment environment) throws Exception {
        environment.jersey().register(new CompanyResource());
    }
}
