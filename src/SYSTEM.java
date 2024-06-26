import Clients.Klient;
import Clients.Types.Firma;
import Clients.Types.Osoba;
import Flights.Lot;
import Reservation.*;

import Resources.Lotnisko;
import Resources.Samolot;
import Resources.SamolotTyp.Typ1;
import Resources.SamolotTyp.Typ2;
import Resources.SamolotTyp.Typ3;


import java.time.DayOfWeek;
import java.time.LocalTime;
import java.text.DateFormatSymbols;
import java.util.*;

public class SYSTEM {
    private ArrayList<Lotnisko> lotniska = new ArrayList<>();
    private ArrayList<Lot> loty = new ArrayList<>();

    public void daneTEST(){
        Lotnisko lotnisko1 = new Lotnisko("Warszawa", "Warszawa", 1, 100);
        Lotnisko lotnisko2 = new Lotnisko("Berlin", "Berlin", 1, 20);
        Lotnisko lotnisko3 = new Lotnisko("Paryz", "Paryz", 1, 3);
        Lotnisko lotnisko4 = new Lotnisko("HongKong", "HongKong", 1, 25);

        lotniska.add(lotnisko1);
        lotniska.add(lotnisko2);
        lotniska.add(lotnisko3);
        lotniska.add(lotnisko4);

        /// Testy
        Typ1 krotko_dystansowiec = new Typ1("Airbus A319", 1500, 40, 20, "Warszawa", "Warszawa");
        Typ2 srednio_dystansowiec = new Typ2("Jak-42", 5000, 30,50 , "Warszawa", "Warszawa");
        Typ3 daleko_dystansowiec = new Typ3("Boeing 7771", 100000, 20, 70, "Warszawa", "Warszawa");

        Typ1 krotko_dystansowiec2 = new Typ1("Airbus A3192", 1500, 20, 20, "Berlin", "Berlin");
        Typ2 srednio_dystansowiec2 = new Typ2("Jak-422", 5000, 30, 40, "Berlin", "Berlin");
        Typ3 daleko_dystansowiec2 = new Typ3("Boeing 7772", 100000, 20, 80, "Berlin", "Berlin");

        Typ1 krotko_dystansowiec3 = new Typ1("Airbus A3193", 1500, 20, 20, "Paryz", "Paryz");
        Typ2 srednio_dystansowiec3 = new Typ2("Jak-423", 5000, 30, 40, "Paryz", "Paryz");
        Typ3 daleko_dystansowiec3 = new Typ3("Boeing 7773", 100000, 20, 90, "Paryz", "Paryz");

        Typ1 krotko_dystansowiec4 = new Typ1("Airbus A3194", 1500, 20, 23, "HongKong", "HongKong");
        Typ2 srednio_dystansowiec4 = new Typ2("Jak-424", 5000, 30, 42, "HongKong", "HongKong");
        Typ3 daleko_dystansowiec4 = new Typ3("Boeing 7774", 100000, 20, 65,"HongKong", "HongKong" );

        lotnisko1.dodajSamolot(krotko_dystansowiec);
        lotnisko1.dodajSamolot(srednio_dystansowiec);
        lotnisko1.dodajSamolot(daleko_dystansowiec);

        lotnisko2.dodajSamolot(krotko_dystansowiec2);
        lotnisko2.dodajSamolot(srednio_dystansowiec2);
        lotnisko2.dodajSamolot(daleko_dystansowiec2);

        lotnisko3.dodajSamolot(krotko_dystansowiec3);
        lotnisko3.dodajSamolot(srednio_dystansowiec3);
        lotnisko3.dodajSamolot(daleko_dystansowiec3);

        lotnisko4.dodajSamolot(krotko_dystansowiec4);
        lotnisko4.dodajSamolot(srednio_dystansowiec4);
        lotnisko4.dodajSamolot(daleko_dystansowiec4);

        generujLoty();
        Klient klient1 = new Osoba("Krzysztof", "S");
        Klient klient2 = new Firma("Marek", "T");
        Klient klient3 = new Firma("Dawid", "S");
        klient1.dodajKlienta();
        klient2.dodajKlienta();
        klient3.dodajKlienta();
    }

    public void generujLoty() {
        DateFormatSymbols dni = new DateFormatSymbols(new Locale("en", "US"));
        String[] daysOfWeek = dni.getWeekdays();
        for (Lotnisko lotnisko_p : lotniska) {
            for (Lotnisko lotnisko_k : lotniska) {
                if (lotnisko_k.equals(lotnisko_p)) { continue; }
                for (int i = 2; i <= 7; i++) {
                    // lot pierwszy
                    double odleglosc = Odleglosc(lotnisko_p, lotnisko_k);
                    LocalTime godzinaOdlotu = RandomHour();
                    DayOfWeek dzienOdlotu = DayOfWeek.valueOf(daysOfWeek[i].toUpperCase(Locale.ENGLISH));

                    Samolot samolot = PrzydzielSamolot(lotnisko_p, godzinaOdlotu, dzienOdlotu, odleglosc);
                    if (samolot == null) {
                        continue;
                    }
                    Lot lot_out = new Lot(godzinaOdlotu, dzienOdlotu, samolot, lotnisko_p, lotnisko_k);

                    // lot powrotny
                    LocalTime godzinaPrzylotu = GodzinaPrzylotu(godzinaOdlotu, odleglosc, samolot);
                    boolean isAfterMidnight = godzinaPrzylotu.isAfter(LocalTime.MIDNIGHT) && godzinaPrzylotu.isBefore(godzinaOdlotu);
                    if (isAfterMidnight) {
                        dzienOdlotu = dzienOdlotu.plus(1);
                    }
                    Lot lot_in = new Lot(godzinaPrzylotu, dzienOdlotu, samolot, lotnisko_k, lotnisko_p);

                    loty.add(lot_out);
                    loty.add(lot_in);
                }
            }
        }
    }


    public Samolot PrzydzielSamolot(Lotnisko lotnisko_p, LocalTime godzina, DayOfWeek dzien, double odleglosc) {
        for (Samolot samolot : lotnisko_p.getFlota()) {

            boolean isAvailable  = (samolot.getZasieg() >= odleglosc && samolot.getIloscMiejsc() > 0);
            if (!isAvailable) {
                continue;
            }

            boolean DoesOverlap = Overlap(samolot, lotnisko_p, godzina, dzien, odleglosc);
            if(!DoesOverlap){
                return samolot;
            }
        }
        return null;
    }


    public Boolean Overlap(Samolot samolot, Lotnisko lotnisko_p, LocalTime godzina, DayOfWeek dzien, double odleglosc) {
        if (loty.isEmpty()) {
            return false;
        }

        for (int i = loty.size() - 1; i >= 0; i--) {
            Lot lot = loty.get(i);
            if (lot.GetSamolot() == samolot && lot.GetLotniskoP() == lotnisko_p) {

                boolean doesDayOverlap = godzina.isBefore(lot.GetGodzinaOdlotu()); // sprawdzamy czy nie ma konfliktu z innym lotem tego samego dnia
                boolean isOnTime = GodzinaPrzylotu(godzina, odleglosc, samolot).isBefore(lot.GetGodzinaOdlotu()) && dzien.equals(lot.GetDzien()); // czy samolot zdazy przyleciec przed odlotem i czy jest to tego samego dnia
                boolean canHaveBreak = godzina.isAfter(lot.GetGodzinaOdlotu().plusHours(3)) && dzien.equals(lot.GetDzien()); // czy samolot bedzie mogl miec 3 godzinna przerwe przed kolejnym wylotem

                if(!doesDayOverlap && isOnTime || !canHaveBreak){
                    return true;
                }
            }
        }
        return false;
    }

    public double Odleglosc(Lotnisko lotnisko_p, Lotnisko lotnisko_k){
        double dx = lotnisko_k.getX() - lotnisko_p.getX();
        double dy = lotnisko_k.getY() - lotnisko_p.getY();
        return Math.sqrt(dx*dx + dy*dy)*1000;
    }

    public int TravelTimeHours(double odleglosc, int speed){
        double hours = odleglosc/(double)speed;
        return (int)Math.floor(hours);
    }

    public int TravelTimeMinutes(double odleglosc, int speed){
        double minutes = odleglosc/(double)speed;
        minutes = (minutes - (int)minutes)*100;
        return (int) minutes;
    }

    public LocalTime RandomHour(){
        Random random = new Random();
        int hour = random.nextInt(24);
        int minute = random.nextInt(60);
        return LocalTime.of(hour, minute);
    }

    public LocalTime GodzinaPrzylotu(LocalTime godzinaPrzylotu, double odleglosc, Samolot samolot){
        godzinaPrzylotu = godzinaPrzylotu.plusHours(TravelTimeHours(odleglosc, samolot.getPredkosc()));
        godzinaPrzylotu = godzinaPrzylotu.plusMinutes(TravelTimeMinutes(odleglosc, samolot.getPredkosc()));
        return godzinaPrzylotu;
    }

     public void rezerwacjaBiletu(Klient klient, Lot lot){
        Ticket ticket = new Ticket(lot, klient);
        ticket.kupBilet(lot,klient);
        System.out.println("Kupiono bilet na lot z lotniska " + lot.GetLotniskoP().getNazwa() +" do " + lot.GetLotniskoK().getNazwa() + " dnia " + lot.GetDzien() + " o godzinie: " + lot.GetGodzinaOdlotu());
        klient.dodajBilet(ticket);
        System.out.println();
    }

    public void odwolywanieBiletu(Klient klient, Ticket ticket){
        ticket.usunBilet(klient,ticket);
    }

    // Method used for debugging and testing
    public void wyswietlanieBiletu(Klient klient){
        List<Ticket>BiletyKlienta = klient.getBilety();
        if(klient instanceof Osoba) {
            for(int i=0; i<BiletyKlienta.size();i++){
                Ticket ticket=BiletyKlienta.get(i);
                System.out.println("Bilety klienta: " + klient.getNazwa() + " " + klient.getNazwiskoKrs());
                System.out.println((i + 1) + ". " + ticket.getLot().GetLotniskoP().getNazwa() + " -> " + ticket.getLot().GetLotniskoK().getNazwa());
                System.out.println("    Dnia: " + ticket.getLot().GetDzien() + " o godzinie: " + ticket.getLot().GetGodzinaOdlotu());
                System.out.println();
            }
            if (BiletyKlienta.isEmpty()) {
                System.out.println("Klient nie posiada biletow");
                System.out.println();
            }
        }
        else{
            if(klient instanceof Firma){
                for(int i=0; i<BiletyKlienta.size();i++){
                    Ticket ticket=BiletyKlienta.get(i);
                    System.out.println("Bilety firmy: " + klient.getNazwa() + " " + klient.getNazwiskoKrs());
                    System.out.println((i + 1) + ". " + ticket.getLot().GetLotniskoP().getNazwa() + " -> " + ticket.getLot().GetLotniskoK().getNazwa());
                    System.out.println("    Dnia: " + ticket.getLot().GetDzien() + " o godzinie: " + ticket.getLot().GetGodzinaOdlotu());
                    System.out.println();
                }
                if (BiletyKlienta.isEmpty()) {
                    System.out.println("Firma nie posiada biletow");
                    System.out.println();
                }
            }
        }
    }

    public void StworzOsobe(String Imie, String Nazwisko){
        Osoba osoba = new Osoba(Imie,Nazwisko);
        osoba.dodajKlienta();
    }

    public void StworzFirme(String Nazwa, String KRS) {
        Firma firma = new Firma(Nazwa, KRS);
        firma.dodajKlienta();
    }
    public ArrayList<Lot> getLoty() {
        return loty;
    }

    public ArrayList<Lotnisko> getLotniska() { return lotniska; }
}

