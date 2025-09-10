/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package framework;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import framework.config.AppConfig;
import framework.injector.ControllerManager;
import framework.injector.DIContainer;
import framework.model.network.NetworkRequest;
import framework.model.network.NetworkResponse;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import framework.orm.Entitet;
import framework.simplelogger.LogLevel;
import framework.simplelogger.SimpleLogger;
import iznajmljivanjeapp.domain.*;
import org.reflections.Reflections;

/**
 *
 * @author Djurkovic
 */
public class RequestDispatcher implements Runnable {

    private static Set<Class<?>> st;

    static {
        st = new HashSet<>();
        Reflections r = new Reflections("iznajmljivanjeapp.domain");
        st.addAll(r.getSubTypesOf(Entitet.class));
        st.add(NetworkRequest.class);
        st.add(NetworkResponse.class);
    }


    private final Socket clientSocket;
    private final InetAddress ip;
    private final AppConfig config;
    private final DIContainer container;

    public RequestDispatcher(Socket s, AppConfig config, DIContainer container) {
        clientSocket = s;
        ip = clientSocket.getInetAddress();
        this.config = config;
        this.container = container;
    }

    //proveri da li zahtev nije besmislen i prosledi ga odgovarajucem kontroleru
    @Override
    public void run() {
        try /*(ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream()); ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());)*/ {

            Kryo kryo = new Kryo();
            kryo.setReferences(true);
            kryo.setRegistrationRequired(false);

            Input kryoInput = new Input(clientSocket.getInputStream());
            Output kryoOutput = new Output(clientSocket.getOutputStream());

            //output.flush();
            SimpleLogger.log(LogLevel.LOG_INFO, "Krecem obradu zahteva klijenta [" + ip + "]");
            //Object obj = input.readObject();
            Object obj = kryo.readClassAndObject(kryoInput);
            SimpleLogger.log(LogLevel.LOG_INFO,"Procitan objekat"); //never gets to here, magically stops running no exceptions nothing
            if (!(obj instanceof NetworkRequest zahtev)) {
                SimpleLogger.log(LogLevel.LOG_FATAL, "Obustavljam zahtev od [" + ip + "], nepoznat objekat poslat kao zahtev.");
                //output.writeObject(new NetworkResponse("Nepoznat zahtev",false,null));
                kryo.writeClassAndObject(kryoOutput,new NetworkResponse("Nepoznat zahtev",false,null));
                kryoOutput.flush();
                return;
            }
            //uzimamo i dispatch pod trajanje jednog zahteva
            long pocetakZahteva = System.nanoTime();
            //output.writeObject(ControllerManager.dispatch(zahtev,config,container));
            kryo.writeClassAndObject(kryoOutput,ControllerManager.dispatch(zahtev,config,container));
            long trajanjeZahteva = System.nanoTime() - pocetakZahteva;
            SimpleLogger.log(LogLevel.LOG_INFO, "Zavrseno opsluzivanje klijenta [" + ip + "] - " + trajanjeZahteva/1_000_000.0 + " ms");
            kryoOutput.flush();
        } catch (IOException e) {
            SimpleLogger.log(LogLevel.LOG_FATAL, "Neuspesno opsluzen klijent: " + e);
        }
        catch(Exception e) {
            SimpleLogger.log(LogLevel.LOG_FATAL, "o ne: " + e);
            e.printStackTrace();
        }
//        catch (ClassNotFoundException ex) {
//            SimpleLogger.log(LogLevel.LOG_FATAL, "Nepostojeca klasa (mismatch verzija?): " + ex);
//        }
        finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                SimpleLogger.log(LogLevel.LOG_FATAL, "Greska pri zatvaranju klijentskog soketa.[" + ip + "]: " + e);
            }
        }
    }

}
