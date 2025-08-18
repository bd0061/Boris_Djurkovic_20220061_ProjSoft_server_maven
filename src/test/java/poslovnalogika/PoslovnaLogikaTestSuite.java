package poslovnalogika;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import framework.App;
import framework.config.AppConfig;
import framework.injector.TypeToken;
import framework.model.network.NetworkRequest;
import framework.model.network.NetworkResponse;
import framework.simplelogger.LogLevel;
import framework.simplelogger.SimpleLogger;
import iznajmljivanjeapp.Main;
import iznajmljivanjeapp.domain.*;
import iznajmljivanjeapp.repositories.Repository;
import iznajmljivanjeapp.repositories.inmemoryrepositories.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        DozvolaTest.class,
        VoziloTest.class,
        ZaposleniTest.class,
        IznajmljivanjeTest.class,
        SmenaTest.class
})
public class PoslovnaLogikaTestSuite {

    private static final int PORT = 9999;

    public static NetworkResponse sendRequest(NetworkRequest req) throws Exception {
        try (Socket sock = new Socket("localhost", PORT)) {
            Kryo kryo = new Kryo();
            kryo.setReferences(true);
            kryo.setRegistrationRequired(false);

            Output out = new Output(sock.getOutputStream());
            Input in = new Input(sock.getInputStream());
            kryo.writeClassAndObject(out, req);
            out.flush();

            //out.writeObject(req);
            //out.flush();
            return (NetworkResponse) kryo.readClassAndObject(in);
            //return (NetworkResponse) in.readObject();
        }
    }

    @org.junit.BeforeClass
    public static void startApp() throws Exception {
        Thread serverThread = new Thread(() -> {
            try {
                SimpleLogger.LOG_SENSITIVITY = LogLevel.LOG_INFO;
                App app = new App();

                app.setAppConfig(new AppConfig.Builder()
                        .addListeningPort(9999)
                        .usingDatabase(false)
                        .build()
                );
                var container = app.getContainer();
                container.register(new TypeToken<Repository<Dozvola>>() {}.getType(), DozvolaInMemoryRepository::new);
                container.register(new TypeToken<Repository<Vozilo>>() {}.getType(), VoziloInMemoryRepository::new);
                container.register(new TypeToken<Repository<Zaposleni>>() {}.getType(), ZaposleniInMemoryRepository::new);
                container.register(new TypeToken<Repository<StavkaIznajmljivanja>>() {}.getType(), StavkaIznajmljivanjaInMemoryRepository::new);
                container.register(new TypeToken<Repository<Iznajmljivanje>>() {}.getType(), IznajmljivanjeInMemoryRepository::new);
                container.register(new TypeToken<Repository<Vozac>>() {}.getType(), VozacInMemoryRepository::new);
                container.register(new TypeToken<Repository<Smena>>() {}.getType(), SmenaInMemoryRepository::new);
                container.register(new TypeToken<Repository<TerminDezurstva>>() {}.getType(), TerminDezurstvaInMemoryRepository::new);
                app.run(Main.class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, "test-server");
        serverThread.setDaemon(true);
        serverThread.start();

        // cekaj da se server inicijalizuje
        Thread.sleep(2000);
    }


}