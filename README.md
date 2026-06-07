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
- **Service** (`service/`) — logica di business: `LibraryService`, `PlaylistService`
- **Command** (`command/`) — pattern **Command** per le operazioni undo su tracce e playlist
- **Util** (`util/`) — utilità trasversali (validazione input)

### Pattern utilizzati

- **Command** — incapsula ogni operazione mutante (add/remove traccia/playlist) come oggetto, abilitando undo tramite `CommandHistory`
- **Strategy** — la modalità di riproduzione (sequenziale, shuffle, loop) è selezionabile a runtime tramite l'interfaccia `PlaybackStrategy` e le sue implementazioni concrete
- **Factory** — `PlaybackStrategyFactory` centralizza la creazione della strategy corretta a partire dal `PlaybackMode`
- **MVC** — separazione Model/View/Controller tramite JavaFX e FXML
- **Service Layer** — i Controller delegano la logica di business ai Service, mantenendo basso il coupling
- **Observer** — le property osservabili di `PlaybackService` (`currentTrack`, `elapsed`, `state`, `mode`) si propagano automaticamente alla UI tramite il meccanismo di binding nativo di JavaFX

---

## Struttura del progetto

```
Gruppo2-SAD/
├── docs/                       ← Documenti
├── src/
│   ├── main/
│   │   ├── java/com/gruppo2/musicplaylistmanager/
│   │   │   ├── Main.java
│   │   │   ├── command/      ← Command pattern (undo)
│   │   │   ├── controller/   ← JavaFX controllers
│   │   │   ├── model/        ← Domain model
│   │   │   ├── service/      ← Business logic
│   │   │   └── util/
│   │   └── resources/.../view/  ← FXML views
│   └── test/
│       └── java/             ← JUnit 5 test suite
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
```

---

## Funzionalità implementate

- Gestione tracce (CRUD): titolo, autore, durata, genere, anno
- Creazione e gestione playlist
- Riproduzione simulata: **sequenziale**, **shuffle**, **loop**
- Modifica playlist in tempo reale (anche durante la riproduzione)
- Evidenziazione in tempo reale della traccia in riproduzione (libreria e playlist)
- Operazioni **undo** su aggiunta/rimozione tracce e playlist
