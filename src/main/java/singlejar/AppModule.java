package singlejar;

import com.google.common.collect.ImmutableMap;
import restx.ResourcesRoute;
import restx.config.ConfigLoader;
import restx.config.ConfigSupplier;
import restx.factory.Provides;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import restx.security.*;
import restx.factory.Module;
import javax.inject.Named;

import java.nio.file.Paths;

@Module
public class AppModule {
    @Provides
    public SignatureKey signatureKey() {
         return new SignatureKey("singlejar SingleJar -8774294261987174997 381863a5-25a7-4c56-ae40-7cb6a864c28f".getBytes(Charsets.UTF_8));
    }

    @Provides
    @Named("restx.admin.password")
    public String restxAdminPassword() {
        return "juma";
    }

    @Provides(priority = 2000) // We set priority to 2000 to make sure static assets are matched only if no other route is matched.
    public ResourcesRoute assetsRoute() {
        return new ResourcesRoute(
                "assets", // route name, for log and debug only
                "/",      // assets are served on / HTTP path
                "assets", // assets are searched in /assets directory in classpath (src/main/resources/assets in sources)
                ImmutableMap.of("", "index.html") // alias empty string to index.html, so asking for / will look for /index.html
        );
    }

    @Provides
    public ConfigSupplier appConfigSupplier(ConfigLoader configLoader) {
        // Load settings.properties in singlejar package as a set of config entries
        return configLoader.fromResource("singlejar/settings");
    }

    @Provides
    public CredentialsStrategy credentialsStrategy() {
        return new BCryptCredentialsStrategy();
    }

    @Provides
    public BasicPrincipalAuthenticator basicPrincipalAuthenticator(
            SecuritySettings securitySettings, CredentialsStrategy credentialsStrategy,
            @Named("restx.admin.passwordHash") String defaultAdminPasswordHash, ObjectMapper mapper) {
        return new StdBasicPrincipalAuthenticator(new StdUserService<>(
                // use file based users repository.
                // Developer's note: prefer another storage mechanism for your users if you need real user management
                // and better perf
                new FileBasedUserRepository<>(
                        StdUser.class, // this is the class for the User objects, that you can get in your app code
                        // with RestxSession.current().getPrincipal().get()
                        // it can be a custom user class, it just need to be json deserializable
                        mapper,

                        // this is the default restx admin, useful to access the restx admin console.
                        // if one user with restx-admin role is defined in the repository, this default user won't be
                        // available anymore
                        new StdUser("admin", ImmutableSet.<String>of("*")),

                        // the path where users are stored
                        Paths.get("data/users.json"),

                        // the path where credentials are stored. isolating both is a good practice in terms of security
                        // it is strongly recommended to follow this approach even if you use your own repository
                        Paths.get("data/credentials.json"),

                        // tells that we want to reload the files dynamically if they are touched.
                        // this has a performance impact, if you know your users / credentials never change without a
                        // restart you can disable this to get better perfs
                        true),
                credentialsStrategy, defaultAdminPasswordHash),
                securitySettings);
    }
}
