# Music Playlist Manager

Progetto per il corso di **Software Architecture Design** – A.A. 2025/2026 – Gruppo 02.

Applicazione desktop in **Java/JavaFX** per la creazione e gestione di playlist musicali con riproduzione simulata e filtri avanzati, sviluppata seguendo il processo **Scrum**.

---

## Team

| Membro | Ruolo |
|---|---|
| Francesco Apicella | Sviluppatore |
| Lorenzo Campagna | Sviluppatore |
| Errico Gioia | Sviluppatore |
| Raffaele Cutolo | Sviluppatore |

---

## Tech Stack

| Componente | Tecnologia |
|---|---|
| Linguaggio | Java 17 |
| UI Framework | JavaFX 21.0.4 (FXML) |
| Build | Maven |
| Testing | JUnit 5.10.2 |
| Serializzazione | Jackson 2.17.1 |
| Versioning | Git / GitHub |
| Project Management | Trello (Scrum board) |

---

## Architettura

Il progetto adotta un'architettura **MVC + Service Layer**, con separazione netta tra:

- **Model** (`model/`) — entità di dominio: `Track`, `Playlist`, `Library`, `Tag`, `PlayState`
- **View** (`resources/.../view/`) — interfacce FXML dichiarative
- **Controller** (`controller/`) — gestione degli eventi UI e coordinamento con i Service
- **Service** (`service/`) — logica di business: `LibraryService`, `PlaylistService`, `PlaybackService`
- **Playback** (`playback/`) — Strategy pattern per la riproduzione: `PlaybackStrategy`, `SequentialStrategy`, `ShuffleStrategy`, `LoopStrategy`, `PlaybackStrategyFactory`, `PlaybackMode`
- **Command** (`command/`) — pattern **Command** per le operazioni undo su tracce e playlist
- **Persistence** (`persistence/`) — pattern **DAO** per la persistenza JSON: `TrackDAO`, `PlaylistDAO`, `JsonTrackDAO`, `JsonPlaylistDAO`
- **Util** (`util/`) — utilità trasversali (validazione input)

### Pattern utilizzati

- **Command** — incapsula ogni operazione mutante (add/remove traccia/playlist, generazione playlist automatica, riordino tracce) come oggetto, abilitando undo tramite `CommandHistory`
- **Strategy** — la modalità di riproduzione (sequenziale, shuffle, loop) è selezionabile a runtime tramite l'interfaccia `PlaybackStrategy` e le sue implementazioni concrete
- **Factory** — `PlaybackStrategyFactory` centralizza la creazione della strategy corretta a partire dal `PlaybackMode`
- **DAO** — `TrackDAO` e `PlaylistDAO` astraggono la persistenza; le implementazioni `JsonTrackDAO` e `JsonPlaylistDAO` incapsulano la serializzazione Jackson, rendendo il formato di storage sostituibile senza toccare Model o Service
- **MVC** — separazione Model/View/Controller tramite JavaFX e FXML
- **Service Layer** — i Controller delegano la logica di business ai Service, mantenendo basso il coupling
- **Observer** — le property osservabili di `PlaybackService` (`currentTrack`, `elapsed`, `state`, `mode`) si propagano automaticamente alla UI tramite il meccanismo di binding nativo di JavaFX

---

## Struttura del progetto

```
Gruppo2-SAD/
├── docs/                       ← Documenti (pre-game, sprint1, sprint2)
├── src/
│   ├── main/
│   │   ├── java/com/gruppo2/musicplaylistmanager/
│   │   │   ├── Main.java
│   │   │   ├── command/      ← Command pattern (undo)
│   │   │   ├── controller/   ← JavaFX controllers
│   │   │   ├── model/        ← Domain model
│   │   │   ├── persistence/  ← DAO JSON (TrackDAO, PlaylistDAO)
│   │   │   ├── playback/     ← Strategy + Factory riproduzione
│   │   │   ├── service/      ← Business logic
│   │   │   └── util/         ← Validazione input
│   │   └── resources/.../view/  ← FXML views + CSS
│   └── test/
│       └── java/             ← JUnit 5 test suite (218 test)
└── pom.xml
```

---

## Prerequisiti

- **Java 17** o superiore
- **Maven 3.8+**

> JavaFX è incluso come dipendenza Maven e non richiede installazione separata.

---

## Build e avvio

```bash
# Compilazione e test
mvn clean install

# Avvio dell'applicazione
mvn javafx:run

# Solo test
mvn test

# JavaDoc
mvn javadoc:javadoc
```

> Dopo aver eseguito il comando per la produzione della JavaDoc, tale documentazione verrà salvata nella cartella `target/reports/apidocs/`.

---

## Funzionalità implementate

### Gestione libreria e playlist
- Gestione tracce (CRUD): titolo, autore, durata, genere, anno di pubblicazione
- Tag visivi sulle tracce: **Preferito**, **Esplicito**, **Nuova uscita** — attivabili/disattivabili con un click ovunque la traccia compaia
- Creazione, eliminazione e gestione playlist
- Aggiunta e rimozione di tracce nelle playlist, anche durante la riproduzione
- Riordino delle tracce in una playlist (su/giù), anche durante la riproduzione
- Generazione automatica di playlist filtrate per **genere**, **anno** o **tag**

### Riproduzione simulata
- Riproduzione di tracce singole e playlist: modalità **sequenziale**, **shuffle**, **loop**
- Cambio di modalità di riproduzione a runtime senza interruzioni
- Pannello **Now Playing** con titolo, autore, tempo trascorso e barra di avanzamento aggiornati in tempo reale
- Evidenziazione della traccia corrente nella lista playlist durante la riproduzione

### Home page
- Vista **Top Playlist**: le playlist più ascoltate della sessione, ordinate per numero di avvii, cliccabili per mostrarne le tracce
- Vista **Top Tracks**: tutte le tracce della libreria ordinate per numero di riproduzioni, con contatore di ascolti e tag cliccabili

### Undo
- Operazioni **undo** su: aggiunta/rimozione tracce dalla libreria, aggiunta/rimozione tracce dalle playlist, creazione/eliminazione playlist, generazione automatica playlist, riordino tracce
- Pulsante "Annulla" disabilitato automaticamente quando non ci sono operazioni da annullare

### Persistenza
- Salvataggio automatico di tracce e playlist su disco (JSON) alla chiusura dell'applicazione
- Caricamento automatico all'avvio per ripristinare lo stato della sessione precedente
