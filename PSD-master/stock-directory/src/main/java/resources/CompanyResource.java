package resources;

import models.Company;
import models.Exchange;
import models.Transaction;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CompanyResource {
    private final Map<String, Company> companies;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public CompanyResource() {
        companies = new HashMap<>();

        Exchange cpI = new Exchange("CP I", "localhost", 5000);
        Exchange cpII = new Exchange("CP II", "localhost", 5001);
        Exchange cpIII = new Exchange("CP III", "localhost", 5002);

        companies.put("Gota", new Company("Gota", cpI));
        companies.put("Peões", new Company("Peões", cpI));
        companies.put("Speedy", new Company("Speedy", cpI));

        companies.put("Diobar", new Company("Diobar", cpII));
        companies.put("Carpe Noctem", new Company("Carpe Noctem", cpII));
        companies.put("Stephane", new Company("Stephane", cpII));

        companies.put("Bar Académico", new Company("Bar Académico", cpIII));
        companies.put("Pão de Forma", new Company("Pão de Forma", cpIII));

        schedule();
    }

    @GET
    @Path("/companies")
    public List<String> getCompanies() {
        return companies.values()
                .stream()
                .map(Company::getName)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/company/{name}")
    public Company getCompany(@PathParam("name") String name) {
        Company company = companies.get(name);

        if (company == null) {
            final String errorMessage = String.format("Company %s does not exist", name);
            throw new WebApplicationException(errorMessage, Response.Status.NOT_FOUND);
        }

        return company;
    }

    @PUT
    @Path("/company/{name}")
    public Response registerTransaction(@PathParam("name") String name, @NotNull Transaction tr) {
        Company company = companies.get(name);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime opening = LocalDate.now().atTime(9,0);
        LocalDateTime closing = LocalDate.now().atTime(17,0);

        if (now.isBefore(opening) || now.isAfter(closing))
            throw new WebApplicationException("Transactions only accepted between 9AM and 5PM.", Response.Status.FORBIDDEN);

        company.accept(tr);
        return Response.noContent().build();
    }

    private void schedule() {
        int alreadyClosed = (LocalDateTime.now().isAfter(LocalDate.now().atTime(17, 00))) ? 1 : 0;

        long closingTime = LocalDateTime
                .now()
                .until(LocalDate.now().plusDays(alreadyClosed).atTime(17, 00), ChronoUnit.MINUTES);

        long midnightTime = LocalDateTime
                .now()
                .until(LocalDate.now().plusDays(1).atStartOfDay(), ChronoUnit.MINUTES);

        Runnable closeAction = () -> companies.values().forEach(Company::close);
        scheduler.scheduleAtFixedRate(closeAction, closingTime, TimeUnit.DAYS.toMinutes(1), TimeUnit.MINUTES);

        Runnable newDayAction = () -> companies.values().forEach(Company::nextDay);
        scheduler.scheduleWithFixedDelay(newDayAction, midnightTime, TimeUnit.DAYS.toMinutes(1), TimeUnit.MINUTES);
    }
}