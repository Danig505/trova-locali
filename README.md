# 📍 TrovaLocali

TrovaLocali è un'applicazione web full-stack sviluppata in **Java** utilizzando il framework **Quarkus**. Permette agli utenti di scoprire nuovi locali, lasciare recensioni e trovare i posti migliori vicini alla propria posizione.

## ✨ Funzionalità Implementate

* **Gestione Utenti & Sicurezza:**
  * Registrazione e Login.
  * Due ruoli: `UTENTE` e `MODERATORE`.
* **Gestione Locali:**
  * Inserimento di nuovi locali con nome, indirizzo, categorie multiple e **foto multiple** (visualizzate tramite carosello interattivo).
  * Modifica ed eliminazione dei locali (i moderatori hanno accesso globale a tutti i locali).
* **Sistema di Recensioni:**
  * Inserimento recensioni (voto da 1 a 5 stelle e commento testuale).
  * **Moderazione:** Le nuove recensioni partono in stato "In attesa" (`PENDING`) e devono essere approvate da un moderatore tramite apposito pannello.
  * **Modifica:** Gli utenti possono aggiornare le proprie recensioni. Le modifiche vanno in stato `PENDING_UPDATE` e il moderatore può confrontare la vecchia e la nuova versione prima di approvarle.
* **Geolocalizzazione Automatica:**
  * Ricerca dei locali in base alla categoria o alla vicinanza (raggio personalizzabile).
  * Integrazione dell'API HTML5 Geolocation: le coordinate di ricerca vengono **dedotte in automatico dal browser** cliccando sull'apposito pulsante "Trova la mia posizione".

---

## 🛠️ Requisiti fondamentali (Cosa ti serve prima di iniziare)

Per far partire questo progetto senza errori, il tuo computer deve avere installato:
1. **Java JDK** (versione 17 o superiore).
2. **Docker Desktop** installato e **avviato**. (Il progetto usa i *Quarkus Dev Services*: significa che Quarkus userà Docker per creare e accendere il database in automatico al posto tuo, ma Docker deve essere in esecuzione in background prima di procedere).

*(Non serve installare Maven, il progetto include già il comando `mvnw` che scaricherà tutto il necessario in automatico).*

---

## 🚀 Come avviare il progetto (Guida Passo-Passo)

Segui **esattamente** questi passaggi in ordine:

**Passo 1: Prepara la cartella**
* Se hai scaricato il progetto come file `.zip`, **estrai tutta la cartella** (ad esempio sul Desktop). *Attenzione: non provare ad avviare i comandi cliccando i file direttamente da dentro lo zip chiuso, altrimenti fallirà tutto!*
* Se usi Git, clona semplicemente il repository.

**Passo 2: Apri il Terminale nella cartella giusta**
Devi aprire il terminale esattamente dentro la cartella principale del progetto (quella che contiene il file `pom.xml`).
* **Metodo facile per Windows:** Apri la cartella del progetto. Clicca in alto sulla barra degli indirizzi della cartella (dove c'è scritto il percorso), cancella tutto, scrivi `cmd` e premi Invio. Si aprirà una finestra nera già posizionata nel posto giusto.
* **Metodo facile per Mac:** Apri l'app "Terminale". Scrivi `cd ` (importante: metti uno spazio dopo cd) e poi trascina la cartella del progetto dal Finder direttamente dentro la finestra del Terminale. Poi premi Invio.

**Passo 3: Dai il comando di avvio**
Nella finestra del terminale, scrivi il comando corretto per il tuo sistema operativo e premi **Invio**:
* Se usi **Windows**: `mvnw.cmd quarkus:dev`
* Se usi **Mac o Linux**: `./mvnw quarkus:dev`

**Passo 4: Attendi il caricamento**
La primissima volta ci vorranno alcuni minuti perché il sistema deve scaricare le dipendenze di Java e accendere il database tramite Docker. 
Non toccare nulla finché non vedi scorrere i log e compare una scritta simile a questa:
✅ `Listening on: http://localhost:8080`
*(Questo significa che il server è acceso e pronto a ricevere visite).*

**Passo 5: Apri il sito!**
Ora che il server sta girando in background (non chiudere la finestra nera del terminale!), apri il tuo browser preferito (Chrome, Edge, Safari...) e digita esattamente questo indirizzo nella barra in alto:
👉 **http://localhost:8080/venues**

---

## 🗄️ Struttura del Database (Script SQL per DataGrip)

Il progetto utilizza i Dev Services di Quarkus (Hibernate), il che significa che le tabelle vengono generate in automatico all'avvio. 

Tuttavia, come richiesto, nella cartella principale del repository è presente il file **`progettocondello.sql`**. Questo file contiene l'intero schema relazionale scritto per **PostgreSQL** (con l'uso di `SERIAL`, `DOUBLE PRECISION` e vincoli `ON DELETE CASCADE`).

Se desideri visionare o inizializzare il database manualmente usando **DataGrip** (o software simili come DBeaver o pgAdmin), ecco i passaggi esatti:

**1. Collega il Database su DataGrip:**
* Apri DataGrip e clicca sul pulsante `+` in alto a sinistra nel pannello Database.
* Seleziona **Data Source -> PostgreSQL**.
* Inserisci le tue credenziali (Host: `localhost`, User: `postgres`, Password) e assicurati di puntare a un database vuoto. Clicca su **Test Connection** e poi su **OK**.

**2. Apri lo Script SQL:**
* Torna alla cartella di questo progetto che hai scaricato.
* Prendi il file **`progettocondello.sql`** e trascinalo fisicamente dentro la finestra principale di DataGrip (oppure da DataGrip fai *File -> Open* e cercalo).

**3. Esegui lo Script:**
* Una volta che il file SQL è aperto sullo schermo, controlla in alto a destra che sia selezionato il database corretto.
* Clicca sul pulsante verde **Play (Execute All)** in alto a sinistra (oppure premi `Ctrl + Enter` o `Cmd + Enter`).
* Fai clic con il tasto destro sul tuo database a sinistra e premi **Refresh**: vedrai apparire tutte e 5 le tabelle perfettamente relazionate!

💡 **Nota:** Se si sceglie di usare questo script per creare le tabelle a mano, bisogna ricordare che Quarkus in modalità sviluppo tenta di rigenerare il database di default (`drop-and-create`). Se si vuole usare il database inizializzato da DataGrip senza che Quarkus lo sovrascriva, basterà impostare `quarkus.hibernate-orm.database.generation=validate` (oppure `none`) nel file `application.properties`.
