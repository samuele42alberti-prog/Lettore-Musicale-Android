# Lettore musicale

Applicazione Android per gestire e riprodurre una libreria musicale locale.

## Funzionalità

- Importazione di un file audio singolo o dei file audio direttamente contenuti in una cartella.
- Lettura dei metadati delle tracce e compilazione delle informazioni dell'album, dell'artista, dell'anno e del genere.
- Selezione della copertina dell'album dalla cartella importata.
- Libreria organizzata per album, singoli, canzoni, artisti e generi.
- Ricerca di album e canzoni per titolo e artista.
- Riproduzione audio in background con controlli play/pausa, traccia precedente e successiva.
- Coda di riproduzione consultabile, riordinabile e modificabile.
- Cronologia degli ascolti recenti.

## Tecnologie

- Kotlin
- Jetpack Compose e Material 3
- Room per il database locale
- Kotlin Coroutines e Flow
- AndroidX Media3 ed ExoPlayer
- Storage Access Framework per la selezione di file e cartelle
- Coil per il caricamento delle copertine

## Struttura

```text
app/
├── build.gradle.kts
└── src/main/
    ├── AndroidManifest.xml
    └── java/com/example/musicplayer/
        ├── UI/          # Schermate Compose, ViewModel e componenti del player
        ├── database/    # Database Room e DAO
        ├── model/       # Entità e modelli
        └── repository/  # Operazioni e accesso ai dati
```

## Come funziona

### Avvio e interfaccia

`MainActivity` avvia l'interfaccia Compose e crea il `MusicPlayerViewModel`, che conserva lo stato condiviso tra le schermate. `HomeScreen` mostra la libreria, la ricerca, l'importazione o la cronologia e gestisce la navigazione verso le schermate di album, artista, player e coda. Le schermate e i componenti in `UI/` leggono lo stato del ViewModel e gli inoltrano le azioni dell'utente.

### Importazione

I selettori Android per file e cartelle restituiscono URI ai contenuti scelti. `ImportazioneMusica.kt` legge i metadati audio e individua le tracce della cartella; `GestoreCopertine.kt` cerca un'immagine di copertina nella cartella selezionata. I dati vengono presentati nel modulo di importazione, dove si possono completare album, artisti, anno e generi. Dopo la conferma, il ViewModel valida i campi e delega il salvataggio al repository.

### Dati e persistenza

`model/` definisce album, canzoni, artisti, generi, ascolti e le relative associazioni. `MusicDatabase` configura il database Room; le interfacce in `database/` contengono le query e le operazioni sui singoli tipi di dati. `MusicRepository` coordina i DAO per ricerche, importazioni e modifiche che coinvolgono più entità. Gli album e le canzoni sono associati agli artisti tramite tabelle di collegamento.

Le liste della libreria sono osservate tramite `Flow`: quando i dati Room cambiano, il repository li espone al ViewModel e Compose aggiorna le schermate.

### Ricerca

La schermata `RicercaScreen` inoltra il testo digitato al `MusicPlayerViewModel`. Per evitare di eseguire una query a ogni carattere, il ViewModel attende 300 ms e annulla la ricerca precedente se nel frattempo il testo cambia. La frase viene divisa in parole e il repository interroga i DAO per ciascuna parola e per la frase completa.

La ricerca degli album considera il titolo e i nomi degli artisti associati. Quella delle canzoni considera titolo, artisti associati, metadati dell'artista e titolo dell'album. I risultati parziali vengono combinati: sono mantenuti gli elementi corrispondenti a tutte le parole inserite e ordinati in base ai punteggi di corrispondenza calcolati dalle query Room. La schermata mostra album, singoli e canzoni in sezioni separate; selezionando un album si apre la relativa schermata, mentre selezionando una canzone ne avvia la riproduzione.

### Riproduzione

`PlayService` mantiene un player ExoPlayer e una MediaSession per la riproduzione in background. `PlayerConnection` collega il ViewModel alla sessione; `PlayerController` converte le canzoni in elementi multimediali Media3 e offre operazioni per avvio, pausa, spostamento e rimozione di elementi dalla coda. Gli eventi del player aggiornano lo stato osservato dall'interfaccia, inclusa la canzone corrente e la cronologia degli ascolti.

## Requisiti

- Android Studio con Android SDK Platform 34
- JDK 17
- Gradle Wrapper incluso nel progetto (Gradle 8.5)

Configurazione Android: `minSdk 26`, `targetSdk 34`, `compileSdk 34`. Versione applicazione: `1.0`.

## Build

Da Windows, genera l'APK debug con:

```powershell
.\gradlew.bat assembleDebug
```

Il file APK viene prodotto in `app/build/outputs/apk/debug/app-debug.apk`.