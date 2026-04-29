# 📍 TrovaLocali

TrovaLocali è un'applicazione web full-stack sviluppata in **Java** utilizzando il framework **Quarkus**. Permette agli utenti di scoprire nuovi locali, lasciare recensioni e trovare i posti migliori vicini alla propria posizione.

Il progetto è stato sviluppato per coprire i requisiti fino al **Livello DIFFICILE (95/100)** della consegna.

## ✨ Funzionalità Implementate

* **Gestione Utenti & Sicurezza:**
  * Registrazione e Login.
  * Due ruoli: `UTENTE` e `MODERATORE`.
* **Gestione Locali:**
  * Inserimento di nuovi locali con nome, indirizzo, categorie multiple e **foto multiple** (visualizzate tramite carosello interattivo).
  * Modifica ed eliminazione dei locali (i moderatori hanno accesso globale a tutti i locali).
* **Sistema di Recensioni (Livello Intermedio + Extra):**
  * Inserimento recensioni (voto da 1 a 5 stelle e commento testuale).
  * **Moderazione:** Le nuove recensioni partono in stato "In attesa" (`PENDING`) e devono essere approvate da un moderatore tramite apposito pannello.
  * **Modifica:** Gli utenti possono aggiornare le proprie recensioni. Le modifiche vanno in stato `PENDING_UPDATE` e il moderatore può confrontare la vecchia e la nuova versione prima di approvarle.
* **Geolocalizzazione Automatica (Livello Difficile):**
  * Ricerca dei locali in base alla categoria o alla vicinanza (raggio di 2km).
  * Integrazione dell'API HTML5 Geolocation: le coordinate di ricerca vengono **dedotte in automatico dal browser** cliccando sull'apposito pulsante "Trova la mia posizione".

## 🚀 Come avviare il progetto

Assicurati di avere installato un JDK Java compatibile e Maven. Il progetto utilizza i Dev Services di Quarkus, quindi il database viene gestito in automatico all'avvio.

1. Clona il repository sul tuo computer.
2. Apri il terminale nella cartella principale del progetto.
3. Avvia l'applicazione in modalità sviluppo eseguendo:

   ```bash
   ./mvnw quarkus:dev
